package org.opensourcebim.modelsetanalyzer;

import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ProductResult {
	private String type;
	private String description;
	private String globalId;
	private String name;
	private Set<Classification> classifications;
	private int nrPSets;
	private int nrPropertySets;
	private int nrProperties;
	private float geometricArea;
	private float geometricVolume;
	private int revisionId;
	private String material;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Classification> getClassifications() {
		return classifications;
	}

	public void setClassifications(Set<Classification> classifications) {
		this.classifications = classifications;
	}

	public int getNrPSets() {
		return nrPSets;
	}

	public void setNrPSets(int nrPSets) {
		this.nrPSets = nrPSets;
	}

	public int getNrPropertySets() {
		return nrPropertySets;
	}

	public void setNrPropertySets(int nrPropertySets) {
		this.nrPropertySets = nrPropertySets;
	}

	public int getNrProperties() {
		return nrProperties;
	}

	public void setNrProperties(int nrProperties) {
		this.nrProperties = nrProperties;
	}

	public float getGeometricArea() {
		return geometricArea;
	}

	public void setGeometricArea(float area) {
		this.geometricArea = area;
	}

	public float getGeometricVolume() {
		return geometricVolume;
	}

	public void setGeometricVolume(float volume) {
		this.geometricVolume = volume;
	}

	public int getNrTriangles() {
		return nrTriangles;
	}

	public void setNrTriangles(int nrTriangles) {
		this.nrTriangles = nrTriangles;
	}

	public String getType() {
		return type;
	}

	private int nrTriangles;
	private Double quantityArea;
	private Double quantityVolume;

	public void setType(String type) {
		this.type = type;
	}

	public void addToSheet(Sheet sheet, int rowId) {
		Row row = sheet.createRow(rowId);
		row.createCell(0).setCellValue("" + getRevisionId());
		row.createCell(1).setCellValue(getType());
		row.createCell(2).setCellValue(getName());
		row.createCell(3).setCellValue(getDescription());
		row.createCell(4).setCellValue(getGlobalId());

//		String material = IfcUtils.getMaterial(ifcProduct);
		if (getMaterial() != null) {
			row.createCell(5).setCellValue(getMaterial());
		}
//		Set<String> classification = classifications.get(ifcProduct);
		if (classifications != null) {
			Classification first = classifications.iterator().next();
			row.createCell(6).setCellValue(first.getAssociationName());
			row.createCell(7).setCellValue(first.getIdentification());
			row.createCell(8).setCellValue(first.getItemReference());
			row.createCell(9).setCellValue(first.getLocation());
			row.createCell(10).setCellValue(first.getName());
		}
		row.createCell(11).setCellValue("" + nrTriangles);
		row.createCell(12).setCellValue("TODO");
		row.createCell(13).setCellValue("" + nrPropertySets);
		row.createCell(14).setCellValue("" + nrPSets);
		row.createCell(15).setCellValue("" + nrProperties);
		row.createCell(16).setCellValue("" + geometricArea);
		row.createCell(17).setCellValue("" + geometricVolume);
		row.createCell(18).setCellValue("" + (quantityArea == null ? "" : quantityArea));
		row.createCell(19).setCellValue("" + (quantityVolume == null ? "" : quantityVolume));
	}

	public int getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(int revisionId) {
		this.revisionId = revisionId;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public void setQuantityArea(Double quantityArea) {
		this.quantityArea = quantityArea;
	}
	
	public Double getQuantityArea() {
		return quantityArea;
	}

	public void setQuantityVolume(Double quantityVolume) {
		this.quantityVolume = quantityVolume;
	}
	
	public Double getQuantityVolume() {
		return quantityVolume;
	}
}
