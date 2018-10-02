package org.opensourcebim.modelsetanalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.bimserver.client.ClientIfcModel;
import org.bimserver.database.queries.om.Include;
import org.bimserver.database.queries.om.Include.TypeDef;
import org.bimserver.database.queries.om.JsonQueryObjectModelConverter;
import org.bimserver.database.queries.om.Query;
import org.bimserver.database.queries.om.QueryException;
import org.bimserver.database.queries.om.QueryPart;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.ModelMetaData;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.models.geometry.Bounds;
import org.bimserver.models.geometry.GeometryPackage;
import org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Package;
import org.bimserver.models.ifc2x3tc1.IfcClassification;
import org.bimserver.models.ifc2x3tc1.IfcProduct;
import org.bimserver.models.ifc2x3tc1.IfcRelAssociatesClassification;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.models.store.IfcHeader;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.utils.IfcTools3d;
import org.bimserver.utils.IfcUtils;

public class Task implements Callable<AnalyzedModel> {
	private SProject project;
	private long roid;
	private BimServerClientInterface client;
	private AnalyzedModel analyzedModel = new AnalyzedModel();
	private int revisionId;
	private AnalyzedModelSet analyzedModelSet;

	public Task(AnalyzedModelSet analyzedModelSet, BimServerClientInterface client, SProject project, long roid, int revisionId) {
		this.analyzedModelSet = analyzedModelSet;
		this.client = client;
		this.project = project;
		this.roid = roid;
		this.revisionId = revisionId;
	}
	
	@Override
	public AnalyzedModel call() throws Exception {
		System.out.println("Loading " + project.getName());
		long start = System.nanoTime();
		IfcModelInterface model = client.getModel(project, roid, false, false, true);
		
		Query preloadQuery = new Query(model.getPackageMetaData());
		QueryPart objectsPart = preloadQuery.createQueryPart();
		
		try {
			objectsPart.addType(new TypeDef(Ifc2x3tc1Package.eINSTANCE.getIfcProduct(), true));
			Include include = objectsPart.createInclude();
			include.addType(Ifc2x3tc1Package.eINSTANCE.getIfcProduct(), true);
			include.addField("geometry");
			Include include2 = include.createInclude();
			include2.addType(new TypeDef(GeometryPackage.eINSTANCE.getGeometryInfo(), false));
			include2.addField("data");

			// Properties
			Include createInclude = objectsPart.createInclude();
			createInclude.addType(new TypeDef(Ifc2x3tc1Package.eINSTANCE.getIfcProduct(), true));
			createInclude.addField("IsDefinedBy");
			Include isDefinedBy = createInclude.createInclude();
			isDefinedBy.addType(new TypeDef(Ifc2x3tc1Package.eINSTANCE.getIfcRelDefinesByProperties(), false));
			isDefinedBy.addField("RelatingPropertyDefinition");
			Include relatingPropertyDefinition = isDefinedBy.createInclude();
			relatingPropertyDefinition.addType(Ifc2x3tc1Package.eINSTANCE.getIfcPropertySet(), false);
			relatingPropertyDefinition.addField("HasProperties");
			
			// Materials
			Include associations = objectsPart.createInclude();
			associations.addType(Ifc2x3tc1Package.eINSTANCE.getIfcProduct(), true);
			associations.addField("HasAssociations");
			Include associatesMaterial = associations.createInclude();
			associatesMaterial.addType(Ifc2x3tc1Package.eINSTANCE.getIfcRelAssociatesMaterial(), true);
			associatesMaterial.addField("RelatingMaterial");
			
			Include ifcMaterialLayerSetUsage = associatesMaterial.createInclude();
			ifcMaterialLayerSetUsage.addType(Ifc2x3tc1Package.eINSTANCE.getIfcMaterialLayerSetUsage(), false);
			ifcMaterialLayerSetUsage.addField("ForLayerSet");
			Include ifcMaterialLayer = ifcMaterialLayerSetUsage.createInclude();
			ifcMaterialLayer.addType(Ifc2x3tc1Package.eINSTANCE.getIfcMaterialLayerSet(), true);
			ifcMaterialLayer.addField("MaterialLayers");
			Include layers = ifcMaterialLayer.createInclude();
			layers.addType(new TypeDef(Ifc2x3tc1Package.eINSTANCE.getIfcMaterialLayer(), false));
			layers.addField("Material");
			
			Include ifcMaterialLayer3 = associatesMaterial.createInclude();
			ifcMaterialLayer3.addType(Ifc2x3tc1Package.eINSTANCE.getIfcMaterialLayerSet(), true);
			ifcMaterialLayer3.addField("MaterialLayers");
			Include layers3 = ifcMaterialLayer.createInclude();
			layers3.addType(new TypeDef(Ifc2x3tc1Package.eINSTANCE.getIfcMaterialLayer(), false));
			layers3.addField("Material");
			
			Include ifcMaterialList = associatesMaterial.createInclude();
			ifcMaterialList.addType(Ifc2x3tc1Package.eINSTANCE.getIfcMaterialList(), false);
			ifcMaterialList.addField("Materials");

			Include ifcMaterialLayer2 = associatesMaterial.createInclude();
			ifcMaterialLayer2.addType(Ifc2x3tc1Package.eINSTANCE.getIfcMaterialLayer(), false);
			ifcMaterialLayer2.addField("Material");
			
		} catch (QueryException e) {
			e.printStackTrace();
		}
		
		QueryPart classifications = preloadQuery.createQueryPart();
		classifications.addType(new TypeDef(Ifc2x3tc1Package.eINSTANCE.getIfcClassificationReference(), false));
		classifications.addType(new TypeDef(Ifc2x3tc1Package.eINSTANCE.getIfcRelAssociatesClassification(), false));
		
		model.query(new JsonQueryObjectModelConverter(model.getPackageMetaData()).toJson(preloadQuery), true);
		
		addMeta(model);
		addObjects(model);
		addAggregations(model);
		
		((ClientIfcModel)model).getClientDebugInfo().dump();
		long end = System.nanoTime();
		System.out.println(((end - start) / 1000000) + " ms");
		analyzedModelSet.add(analyzedModel);
		return analyzedModel;
	}

	private void addObjects(IfcModelInterface model) {
		List<IfcProduct> products = model.getAllWithSubTypes(IfcProduct.class);
		Map<IfcRoot, Set<String>> classifications = new HashMap<>();
		for (IfcRelAssociatesClassification ifcRelAssociatesClassification : model.getAll(IfcRelAssociatesClassification.class)) {
			for (IfcRoot ifcRoot : ifcRelAssociatesClassification.getRelatedObjects()) {
				Set<String> set = classifications.get(ifcRoot);
				if (set == null) {
					set = new HashSet<>();
					classifications.put(ifcRoot, set);
				}
				if (ifcRelAssociatesClassification.getName() != null) {
					set.add(ifcRelAssociatesClassification.getName());
				}
			}
		}
		for (IfcProduct ifcProduct : products) {
			ProductResult productResult = new ProductResult();
			
			productResult.setRevisionId(revisionId);
			productResult.setType(ifcProduct.eClass().getName());
			productResult.setName(ifcProduct.getName());
			productResult.setDescription(ifcProduct.getDescription());
			productResult.setGlobalId(ifcProduct.getGlobalId());
			productResult.setMaterial(IfcUtils.getMaterial(ifcProduct));
			productResult.setClassifications(classifications.get(ifcProduct));
			productResult.setNrPropertySets(IfcUtils.getNrOfPropertySets(ifcProduct));
			productResult.setNrPSets(IfcUtils.getNrOfPSets(ifcProduct));
			productResult.setNrProperties(IfcUtils.getNrOfProperties(ifcProduct));
			
			analyzedModel.addProduct(productResult);

			if (ifcProduct.getGeometry() != null) {
				productResult.setNrTriangles(ifcProduct.getGeometry().getPrimitiveCount());
				productResult.setArea((float) ifcProduct.getGeometry().getArea());
				productResult.setVolume((float) ifcProduct.getGeometry().getVolume());
 			}
		}
	}

	private void addAggregations(IfcModelInterface model) {
		Aggregation aggregation = new Aggregation();
		
		aggregation.setRevisionId(revisionId);
		aggregation.setModelSize(model.size());
		aggregation.setIfcRelationsShipCount(model.countWithSubtypes(Ifc2x3tc1Package.eINSTANCE.getIfcRelationship()));
		aggregation.setIfcProductCount(model.countWithSubtypes(Ifc2x3tc1Package.eINSTANCE.getIfcProduct()));
		
		float m2 = 0;
		float m3 = 0;
		float m2bb = 0;
		float m3bb = 0;
		
		for (IfcProduct ifcProduct : model.getAllWithSubTypes(IfcProduct.class)) {
			if (ifcProduct.getGeometry() != null) {
				m2 += ifcProduct.getGeometry().getArea();
				m3 += ifcProduct.getGeometry().getVolume();
				Bounds boundsMm = ifcProduct.getGeometry().getBoundsMm();
				
				m2bb += IfcTools3d.getArea(boundsMm);
				m3bb += IfcTools3d.getVolume(boundsMm);
			}
		}
		
		aggregation.setM2(m2);
		aggregation.setM3(m3);
		aggregation.setM2AABB(m2bb);
		aggregation.setM3AABB(m3bb);
		
		analyzedModel.setAggregation(aggregation);
	}

	private void addMeta(IfcModelInterface model) {
		MetaData metaData = new MetaData();
		
		ModelMetaData modelMetaData = model.getModelMetaData();
		IfcHeader ifcHeader = modelMetaData.getIfcHeader();
		
		metaData.getIfcHeader(ifcHeader);
		metaData.setRevisionId(revisionId);
		
		Set<String> classificationsSet = new HashSet<>();
		List<IfcClassification> classifications = model.getAll(IfcClassification.class);
		for (IfcClassification ifcClassificationReference : classifications) {
			if (ifcClassificationReference.getName() != null) {
				classificationsSet.add(ifcClassificationReference.getName());
			}
		}
		metaData.setClassifications(classificationsSet);
		
		analyzedModel.setMetaData(metaData);
	}
}
