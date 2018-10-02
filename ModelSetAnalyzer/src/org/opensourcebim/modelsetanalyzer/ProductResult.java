package org.opensourcebim.modelsetanalyzer;

import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.google.common.base.Joiner;

public class ProductResult {
	private String type;
	private String description;
	private String globalId;
	private String name;
	private Set<String> classifications;
	private int nrPSets;
	private int nrPropertySets;
	private int nrProperties;
	private float area;
	private float volume;
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

	public Set<String> getClassifications() {
		return classifications;
	}

	public void setClassifications(Set<String> classifications) {
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

	public float getArea() {
		return area;
	}

	public void setArea(float area) {
		this.area = area;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
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
			row.createCell(6).setCellValue(Joiner.on(", ").join(classifications));
		}
		row.createCell(7).setCellValue("" + nrTriangles);
		row.createCell(8).setCellValue("TODO");
		row.createCell(9).setCellValue("" + nrPropertySets);
		row.createCell(10).setCellValue("" + nrPSets);
		row.createCell(11).setCellValue("" + nrProperties);
		row.createCell(12).setCellValue("" + area);
		row.createCell(13).setCellValue("" + volume);
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
}
