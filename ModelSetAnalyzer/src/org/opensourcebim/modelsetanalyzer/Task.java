package org.opensourcebim.modelsetanalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.bimserver.database.queries.om.Include;
import org.bimserver.database.queries.om.Include.TypeDef;
import org.bimserver.database.queries.om.JsonQueryObjectModelConverter;
import org.bimserver.database.queries.om.Query;
import org.bimserver.database.queries.om.QueryPart;
import org.bimserver.emf.IdEObject;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.ModelMetaData;
import org.bimserver.emf.PackageMetaData;
import org.bimserver.emf.Schema;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.models.geometry.Bounds;
import org.bimserver.models.geometry.GeometryInfo;
import org.bimserver.models.geometry.GeometryPackage;
import org.bimserver.models.ifc4.IfcClassificationReference;
import org.bimserver.models.ifc4.IfcObjectDefinition;
import org.bimserver.models.ifc4.IfcRelDefinesByType;
import org.bimserver.models.ifc4.IfcTypeProduct;
import org.bimserver.models.store.IfcHeader;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.utils.IfcTools3d;
import org.bimserver.utils.IfcUtils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

public class Task implements Callable<AnalyzedModel> {
	private SProject project;
	private long roid;
	private BimServerClientInterface client;
	private AnalyzedModel analyzedModel;
	private int revisionId;
	private AnalyzedModelSet analyzedModelSet;

	public Task(AnalyzedModelSet analyzedModelSet, BimServerClientInterface client, SProject project, long roid, int revisionId) {
		this.analyzedModelSet = analyzedModelSet;
		this.client = client;
		this.project = project;
		this.roid = roid;
		this.revisionId = revisionId;
		this.analyzedModel = new AnalyzedModel(revisionId);
	}
	
	@Override
	public AnalyzedModel call() throws Exception {
		System.out.println("Loading " + project.getName());
		try {
			IfcModelInterface model = client.getModel(project, roid, false, false, true);
			
			PackageMetaData packageMetaData = model.getPackageMetaData();
			Query preloadQuery = new Query(packageMetaData);
			QueryPart objectsPart = preloadQuery.createQueryPart();
			objectsPart.addType(new TypeDef(packageMetaData.getEClass("IfcProduct"), true));
			Include include = objectsPart.createInclude();
			include.addType(packageMetaData.getEClass("IfcProduct"), true);
			include.addField("geometry");
			Include include2 = include.createInclude();
			include2.addType(new TypeDef(GeometryPackage.eINSTANCE.getGeometryInfo(), false));
			include2.addField("data");

			// Properties
			Include createInclude = objectsPart.createInclude();
			createInclude.addType(new TypeDef(packageMetaData.getEClass("IfcProduct"), true));
			createInclude.addField("IsDefinedBy");
			Include isDefinedBy = createInclude.createInclude();
			isDefinedBy.addType(new TypeDef(packageMetaData.getEClass("IfcRelDefinesByProperties"), false));
			isDefinedBy.addField("RelatingPropertyDefinition");
			Include relatingPropertyDefinition = isDefinedBy.createInclude();
			relatingPropertyDefinition.addType(packageMetaData.getEClass("IfcPropertySet"), false);
			relatingPropertyDefinition.addField("HasProperties");
			Include relatingPropertyDefinition2 = isDefinedBy.createInclude();
			relatingPropertyDefinition2.addType(packageMetaData.getEClass("IfcElementQuantity"), false);
			relatingPropertyDefinition2.addField("Quantities");
			
			// Materials
			Include associations = objectsPart.createInclude();
			associations.addType(packageMetaData.getEClass("IfcProduct"), true);
			associations.addField("HasAssociations");
			Include associatesMaterial = associations.createInclude();
			associatesMaterial.addType(packageMetaData.getEClass("IfcRelAssociatesMaterial"), true);
			associatesMaterial.addField("RelatingMaterial");
			
			Include ifcMaterialLayerSetUsage = associatesMaterial.createInclude();
			ifcMaterialLayerSetUsage.addType(packageMetaData.getEClass("IfcMaterialLayerSetUsage"), false);
			ifcMaterialLayerSetUsage.addField("ForLayerSet");
			Include ifcMaterialLayer = ifcMaterialLayerSetUsage.createInclude();
			ifcMaterialLayer.addType(packageMetaData.getEClass("IfcMaterialLayerSet"), true);
			ifcMaterialLayer.addField("MaterialLayers");
			Include layers = ifcMaterialLayer.createInclude();
			layers.addType(new TypeDef(packageMetaData.getEClass("IfcMaterialLayer"), false));
			layers.addField("Material");
			
			Include ifcMaterialLayer3 = associatesMaterial.createInclude();
			ifcMaterialLayer3.addType(packageMetaData.getEClass("IfcMaterialLayerSet"), true);
			ifcMaterialLayer3.addField("MaterialLayers");
			Include layers3 = ifcMaterialLayer.createInclude();
			layers3.addType(new TypeDef(packageMetaData.getEClass("IfcMaterialLayer"), false));
			layers3.addField("Material");
			
			Include ifcMaterialList = associatesMaterial.createInclude();
			ifcMaterialList.addType(packageMetaData.getEClass("IfcMaterialList"), false);
			ifcMaterialList.addField("Materials");

			Include ifcMaterialLayer2 = associatesMaterial.createInclude();
			ifcMaterialLayer2.addType(packageMetaData.getEClass("IfcMaterialLayer"), false);
			ifcMaterialLayer2.addField("Material");
			
			QueryPart classifications = preloadQuery.createQueryPart();
			classifications.addType(new TypeDef(packageMetaData.getEClass("IfcClassificationReference"), false));
			classifications.addType(new TypeDef(packageMetaData.getEClass("IfcRelAssociatesClassification"), false));
			Include relatedObjects = classifications.createInclude();
			relatedObjects.addType(packageMetaData.getEClass("IfcRelAssociatesClassification"), false);
			relatedObjects.addField("RelatedObjects");
			Include referencedBy = relatedObjects.createInclude();
			referencedBy.addType(packageMetaData.getEClass("IfcTypeProduct"), true);
			if (packageMetaData.getSchema() == Schema.IFC4) {
				referencedBy.addField("Types");
				Include relatedObjects2 = referencedBy.createInclude();
				relatedObjects2.addType(packageMetaData.getEClass("IfcRelDefinesByType"), true);
				relatedObjects2.addField("RelatedObjects");
			} else {
				// In ifc2x3tc1 this relation is missing, which is annoying
			}


			addMeta(model);

			model.query(new JsonQueryObjectModelConverter(packageMetaData).toJson(preloadQuery), true);
			
			addObjects(model);
			addAggregations(model);
			
//			model.dumpDebug();
		} catch (Throwable e) {
			System.err.println(project.getName());
			e.printStackTrace();
		}

		analyzedModelSet.add(analyzedModel);
		return analyzedModel;
	}

	private void addObjects(IfcModelInterface model) {
		PackageMetaData packageMetaData = model.getPackageMetaData();
		List<IdEObject> products = model.getAllWithSubTypes(packageMetaData.getEClass("IfcProduct"));
		Map<Long, Set<Classification>> classifications = new HashMap<>();
		EClass ifcRelAssociatesClassificationEClass = packageMetaData.getEClass("IfcRelAssociatesClassification");
		List<IdEObject> all = model.getAllWithSubTypes(ifcRelAssociatesClassificationEClass);
		for (IdEObject ifcRelAssociatesClassification : all) {
			for (IdEObject ifcRoot : (List<IdEObject>)ifcRelAssociatesClassification.eGet(ifcRelAssociatesClassificationEClass.getEStructuralFeature("RelatedObjects"))) {
				if (ifcRoot instanceof IfcTypeProduct) {
					IfcTypeProduct ifcTypeProduct = (IfcTypeProduct)ifcRoot;
					for (IfcRelDefinesByType ifcRelAssignsToProduct : ifcTypeProduct.getTypes()) {
						for (IfcObjectDefinition ifcObjectDefinition : ifcRelAssignsToProduct.getRelatedObjects()) {
							if (model.getPackageMetaData().getEClass("IfcProduct").isSuperTypeOf(ifcObjectDefinition.eClass())) {
								addProductToSet(classifications, ifcRelAssociatesClassification, ifcObjectDefinition);
							} else {
								System.out.println("Unimplemented " + ifcObjectDefinition);
							}
						}
					}
				}
				addProductToSet(classifications, ifcRelAssociatesClassification, ifcRoot);
			}
		}
		for (IdEObject ifcProduct : products) {
			ProductResult productResult = new ProductResult();
			
			productResult.setRevisionId(revisionId);
			productResult.setType(ifcProduct.eClass().getName());
			productResult.setName((String)ifcProduct.eGet(ifcProduct.eClass().getEStructuralFeature("Name")));
			productResult.setDescription((String)ifcProduct.eGet(ifcProduct.eClass().getEStructuralFeature("Description")));
			productResult.setGlobalId((String)ifcProduct.eGet(ifcProduct.eClass().getEStructuralFeature("GlobalId")));
			productResult.setMaterial(IfcUtils.getMaterial(ifcProduct));
			productResult.setClassifications(classifications.get(ifcProduct.getOid()));
			productResult.setNrPropertySets(IfcUtils.getNrOfPropertySets(ifcProduct));
			productResult.setNrPSets(IfcUtils.getNrOfPSets(ifcProduct));
			productResult.setNrProperties(IfcUtils.getNrOfProperties(ifcProduct));
			
			analyzedModel.addProduct(productResult);

			Double area = IfcUtils.getIfcQuantityArea(ifcProduct);
			if (area != null) {
				productResult.setQuantityArea(area);
			}
			Double volume = IfcUtils.getIfcQuantityVolume(ifcProduct);
			if (volume != null) {
				productResult.setQuantityVolume(volume);
			}
			
			GeometryInfo geometry = (GeometryInfo) ifcProduct.eGet(ifcProduct.eClass().getEStructuralFeature("geometry"));
			if (geometry != null) {
				productResult.setNrTriangles(geometry.getPrimitiveCount());
				productResult.setGeometricArea((float) geometry.getArea());
				productResult.setGeometricVolume((float) geometry.getVolume());
 			}
		}
	}

	private void addProductToSet(Map<Long, Set<Classification>> classifications, IdEObject ifcRelAssociatesClassification, IdEObject ifcObjectDefinition) {
		Set<Classification> set = classifications.get(ifcObjectDefinition.getOid());
		if (set == null) {
			set = new HashSet<>();
			classifications.put(ifcObjectDefinition.getOid(), set);
		}
		Classification classification = new Classification();
		IdEObject relatingClassification = (IdEObject) ifcRelAssociatesClassification.eGet(ifcRelAssociatesClassification.eClass().getEStructuralFeature("RelatingClassification"));
		if (relatingClassification != null) {
			classification.setLocation((String) relatingClassification.eGet(relatingClassification.eClass().getEStructuralFeature("Location")));
			if (relatingClassification instanceof IfcClassificationReference) {
				classification.setIdentification((String) relatingClassification.eGet(relatingClassification.eClass().getEStructuralFeature("Identification")));
			} else {
				classification.setItemReference((String) relatingClassification.eGet(relatingClassification.eClass().getEStructuralFeature("ItemReference")));
			}
			classification.setName((String) relatingClassification.eGet(relatingClassification.eClass().getEStructuralFeature("Name")));
		}
		EStructuralFeature nameFeature = ifcRelAssociatesClassification.eClass().getEStructuralFeature("Name");
		classification.setAssociationName((String)ifcRelAssociatesClassification.eGet(nameFeature));
		set.add(classification);
	}

	private void addAggregations(IfcModelInterface model) {
		PackageMetaData packageMetaData = model.getPackageMetaData();
		
		Aggregation aggregation = new Aggregation();
		
		aggregation.setRevisionId(revisionId);
		aggregation.setModelSize(model.size());
		aggregation.setIfcRelationsShipCount(model.countWithSubtypes(packageMetaData.getEClass("IfcRelationship")));
		aggregation.setIfcProductCount(model.countWithSubtypes(packageMetaData.getEClass("IfcProduct")));
		aggregation.setNrOfAssemblies(model.countWithSubtypes(packageMetaData.getEClass("IfcElementAssembly")));
		
		float m2 = 0;
		float m3 = 0;
		float m2bb = 0;
		float m3bb = 0;
		
		for (IdEObject ifcProduct : model.getAllWithSubTypes(packageMetaData.getEClass("IfcProduct"))) {
			GeometryInfo geometry = (GeometryInfo) ifcProduct.eGet(ifcProduct.eClass().getEStructuralFeature("geometry"));
			if (geometry != null) {
				m2 += geometry.getArea();
				m3 += geometry.getVolume();
				Bounds boundsMm = geometry.getBoundsMm();
				
				m2bb += IfcTools3d.getArea(boundsMm);
				m3bb += IfcTools3d.getVolume(boundsMm);
			}
		}
		
		aggregation.setM2(m2);
		aggregation.setM3(m3);
		aggregation.setM2AABB(m2bb / (1000 * 1000));
		aggregation.setM3AABB(m3bb / (1000 * 1000 * 1000));
		
		analyzedModel.setAggregation(aggregation);
	}

	private void addMeta(IfcModelInterface model) {
		PackageMetaData packageMetaData = model.getPackageMetaData();
		MetaData metaData = new MetaData();
		
		ModelMetaData modelMetaData = model.getModelMetaData();
		IfcHeader ifcHeader = modelMetaData.getIfcHeader();
		
		metaData.getIfcHeader(ifcHeader);
		metaData.setRevisionId(revisionId);
		
		Set<String> classificationsSet = new HashSet<>();
		List<IdEObject> classifications = model.getAll(packageMetaData.getEClass("IfcClassification"));
		for (IdEObject ifcClassificationReference : classifications) {
			String name = (String) ifcClassificationReference.eGet(ifcClassificationReference.eClass().getEStructuralFeature("Name"));
			if (name != null) {
				classificationsSet.add(name);
			}
		}
		metaData.setClassifications(classificationsSet);
		
		analyzedModel.setMetaData(metaData);
	}
}
