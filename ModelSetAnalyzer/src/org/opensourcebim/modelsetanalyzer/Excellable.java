package org.opensourcebim.modelsetanalyzer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class Excellable {
	public void add(Row row, int column, Object value) {
		Cell cell = row.createCell(column);
		if (value == null) {
			return;
		}
		if (value instanceof String) {
			cell.setCellValue((String)value);
		} else if (value instanceof Integer) {
			cell.setCellValue((Integer)value);
			cell.setCellType(CellType.NUMERIC);
		} else if (value instanceof Long) {
			cell.setCellValue((Long)value);
			cell.setCellType(CellType.NUMERIC);
		}
	}
}
