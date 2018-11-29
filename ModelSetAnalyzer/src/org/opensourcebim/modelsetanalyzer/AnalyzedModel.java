package org.opensourcebim.modelsetanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

public class AnalyzedModel {

	private final List<ProductResult> productResults = new ArrayList<>();
	private final Map<Long, String> geometryDataIdToType = new HashMap<>();
	private final List<Geometry> geometry = new ArrayList<>();
	private Aggregation aggregation;
	private MetaData metaData;
	private int modelId;
	
	public AnalyzedModel(int modelId) {
		this.modelId = modelId;
	}
	
	public int getModelId() {
		return modelId;
	}
	
	public void addProduct(ProductResult productResult) {
		productResults.add(productResult);
	}
	
	public String getGeometryDataType(long oid) {
		return geometryDataIdToType.get(oid);
	}
	
	public void addGeometryDataIdToType(long oid, String type) {
		geometryDataIdToType.put(oid, type);
	}

	public void setAggregation(Aggregation aggregation) {
		this.aggregation = aggregation;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}
	
	public int getNrProducts() {
		return productResults.size();
	}
	
	public int objectsToExcel(Sheet metaSheet, Sheet aggregationsSheet, Sheet objectsSheet, int startRow) {
		if (metaData != null) {
			metaData.toExcel(metaSheet.createRow(metaData.getRevisionId()));
		}
		if (aggregation != null) {
			aggregation.toExcel(aggregationsSheet, aggregation.getRevisionId());
		}
		int rowId = startRow;
		for (ProductResult productResult : productResults) {
			productResult.addToSheet(objectsSheet, rowId++);
		}
		return rowId;
	}

	public int geometryToExcel(Sheet metaSheet, Sheet aggregationsSheet, Sheet geometrySheet, int startRow) {
		if (metaData != null) {
			metaData.toExcel(metaSheet.createRow(metaData.getRevisionId()));
		}
		if (aggregation != null) {
			aggregation.toExcel(aggregationsSheet, aggregation.getRevisionId());
		}
		int rowId = startRow;
		for (Geometry geometry : this.geometry) {
			geometry.addToSheet(geometrySheet, rowId++);
		}
		return rowId;
	}

	public void addGeometryData(Geometry geometry) {
		this.geometry.add(geometry);
	}

	public int getNrGeometries() {
		return geometry.size();
	}
}
