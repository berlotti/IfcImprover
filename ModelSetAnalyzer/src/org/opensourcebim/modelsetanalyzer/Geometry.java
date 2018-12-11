package org.opensourcebim.modelsetanalyzer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.bimserver.models.geometry.GeometryData;

public class Geometry extends Excellable {

	private GeometryData geometryData;
	private int revisionId;
	private String type;

	public Geometry(GeometryData geometryData, String type) {
		this.geometryData = geometryData;
		this.type = type;
	}

	public void addToSheet(Sheet sheet, int rowId) {
		Row row = sheet.createRow(rowId);
		add(row, 0, getRevisionId());
		add(row, 1, geometryData.getOid());
		add(row, 2, geometryData.getReused());
		add(row, 3, (geometryData.getNrIndices() / 3));
		add(row, 4, geometryData.getSaveableTriangles());
		int estimatedBytes = 
			geometryData.getNrIndices() * 4 +
			geometryData.getNrVertices() * 2 +
			geometryData.getNrNormals() +
			geometryData.getNrVertices() + // colors
			geometryData.getNrVertices() / 3 * 4 // picking
			;
		add(row, 5, estimatedBytes);
		add(row, 6, type);
	}
	
	public void setRevisionId(int revisionId) {
		this.revisionId = revisionId;
	}
	
	public int getRevisionId() {
		return revisionId;
	}
}
