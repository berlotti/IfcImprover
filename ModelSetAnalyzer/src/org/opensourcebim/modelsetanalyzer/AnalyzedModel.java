package org.opensourcebim.modelsetanalyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

public class AnalyzedModel {

	private final List<ProductResult> productResults = new ArrayList<>();
	private Aggregation aggregation;
	private MetaData metaData;
	
	public void addProduct(ProductResult productResult) {
		productResults.add(productResult);
	}

	public void setAggregation(Aggregation aggregation) {
		this.aggregation = aggregation;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}
	
	public void toExcel(Sheet metaSheet, Sheet aggregationsSheet, Sheet objectsSheet) {
		metaData.toExcel(metaSheet.createRow(metaData.getRevisionId()));
		aggregation.toExcel(aggregationsSheet, aggregation.getRevisionId());
		int rowId = 2;
		for (ProductResult productResult : productResults) {
			productResult.addToSheet(objectsSheet, rowId++);
		}
	}
}
