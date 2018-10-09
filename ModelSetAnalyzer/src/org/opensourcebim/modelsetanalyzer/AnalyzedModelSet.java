package org.opensourcebim.modelsetanalyzer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AnalyzedModelSet {
	private final List<AnalyzedModel> analyzedModels = new ArrayList<>();
	private Workbook workbook;
	private Sheet metaSheet;
	private Sheet aggregationsSheet;
	private Sheet objectsSheet;

	public AnalyzedModelSet() {
	}

	public void toExcel(Path path) throws FileNotFoundException, IOException {
		workbook = new XSSFWorkbook();

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

		objectsSheet = workbook.createSheet("Objects");
		row = objectsSheet.createRow(0);
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

		analyzedModels.sort((AnalyzedModel o1, AnalyzedModel o2) -> o1.getModelId() - o2.getModelId());

		int newRowId = 2;
		for (AnalyzedModel analyzedModel : analyzedModels) {
			newRowId = analyzedModel.toExcel(metaSheet, aggregationsSheet, objectsSheet, newRowId);
		}

		try (OutputStream fileOut = new FileOutputStream(path.toFile())) {
			workbook.write(fileOut);
		}
	}

	public void add(AnalyzedModel analyzedModel) {
		this.analyzedModels.add(analyzedModel);
	}
}
