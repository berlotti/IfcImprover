package org.opensourcebim.modelsetanalyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

public class AnalyzedModel {

	private final List<ProductResult> productResults = new ArrayList<>();
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

	public void setAggregation(Aggregation aggregation) {
		this.aggregation = aggregation;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}
	
	public int getNrResults() {
		return productResults.size();
	}
	
	public int toExcel(Sheet metaSheet, Sheet aggregationsSheet, Sheet objectsSheet, int startRow) {
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
}
