package org.opensourcebim.modelsetanalyzer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class AnalyzedModelSet {
	private final List<AnalyzedModel> analyzedModels = new ArrayList<>();
	private Workbook workbook;
	private Sheet metaSheet;
	private Sheet aggregationsSheet;
	private List<Sheet> objectsSheets = new ArrayList<>();
	private int rowId = 2;

	public AnalyzedModelSet() {
	}

	public void toExcel(Path path) throws FileNotFoundException, IOException {
		workbook = new SXSSFWorkbook(100);

		metaSheet = workbook.createSheet("Meta");

		Row row = metaSheet.createRow(0);
		row.createCell(0).setCellValue("Model ID");
		row.createCell(1).setCellValue("Filename");
		row.createCell(2).setCellValue("Ifc Schema");
		row.createCell(3).setCellValue("Implementation level");
		row.createCell(4).setCellValue("Originating syste");
		row.createCell(5).setCellValue("Preprocessor version");
		row.createCell(6).setCellValue("Authorization");
		row.createCell(7).setCellValue("Organization");
		row.createCell(8).setCellValue("Author");
		row.createCell(9).setCellValue("Description");
		row.createCell(10).setCellValue("Timestamp");
		row.createCell(11).setCellValue("Classification reference");

		aggregationsSheet = workbook.createSheet("Aggregations");
		row = aggregationsSheet.createRow(0);
		row.createCell(0).setCellValue("Model ID");
		row.createCell(1).setCellValue("Number of objects (Ifc entities)");
		row.createCell(2).setCellValue("Number of relations (IfcRelationship)");
		row.createCell(3).setCellValue("Number of objects (IfcProduct)");
		row.createCell(4).setCellValue("Number of assemblies (IfcElementAssembly)");
		row.createCell(5).setCellValue("M2");
		row.createCell(6).setCellValue("M3");
		row.createCell(7).setCellValue("Bounding box M2");
		row.createCell(8).setCellValue("Bounding box M3");

		analyzedModels.sort((AnalyzedModel o1, AnalyzedModel o2) -> o1.getModelId() - o2.getModelId());

		for (AnalyzedModel analyzedModel : analyzedModels) {
			Sheet objectSheet = getObjectSheet(rowId + analyzedModel.getNrResults());
			rowId = analyzedModel.toExcel(metaSheet, aggregationsSheet, objectSheet, rowId);
		}

		try (OutputStream fileOut = new FileOutputStream(path.toFile())) {
			workbook.write(fileOut);
		}
	}
	
	public Sheet getObjectSheet(int rowId) {
		if (rowId >= 100 || objectsSheets.isEmpty()) {
			Sheet objectsSheet = workbook.createSheet("Objects (" + (objectsSheets.size() + 1) + ")");
			objectsSheets.add(objectsSheet);

			Row row = objectsSheet.createRow(0);
			row.createCell(0).setCellValue("Model ID");
			row.createCell(1).setCellValue("Type");
			row.createCell(2).setCellValue("Name");
			row.createCell(3).setCellValue("Description");
			row.createCell(4).setCellValue("GUID");
			row.createCell(5).setCellValue("Material");
			row.createCell(6).setCellValue("Classification AssociationName");
			row.createCell(7).setCellValue("Classification Identification");
			row.createCell(8).setCellValue("Classification ItemReference");
			row.createCell(9).setCellValue("Classification Location");
			row.createCell(10).setCellValue("Classification Name");
			row.createCell(11).setCellValue("Triangles");
			row.createCell(12).setCellValue("Points");
			row.createCell(13).setCellValue("Property sets");
			row.createCell(14).setCellValue("Psets");
			row.createCell(15).setCellValue("Properties");
			row.createCell(16).setCellValue("Geometric M2");
			row.createCell(17).setCellValue("Geometric M3");
			row.createCell(18).setCellValue("Quantity M2");
			row.createCell(19).setCellValue("Quantity M3");
			
			this.rowId = 2;
			
			return objectsSheet;
		} else {
			return objectsSheets.get(objectsSheets.size() - 1);
		}
	}

	public void add(AnalyzedModel analyzedModel) {
		this.analyzedModels.add(analyzedModel);
	}
}
