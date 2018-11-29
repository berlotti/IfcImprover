package org.opensourcebim.modelsetanalyzer;

import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ProductResult extends Excellable {
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
	private long oid;
	private long geometryDataOid;

	public void setType(String type) {
		this.type = type;
	}

	public void addToSheet(Sheet sheet, int rowId) {
		Row row = sheet.createRow(rowId);
		add(row, 0, getRevisionId());
		add(row, 1, getOid());
		add(row, 2, getType());
		add(row, 3, getName());
		add(row, 4, getDescription());
		add(row, 5, getGlobalId());

//		String material = IfcUtils.getMaterial(ifcProduct);
		if (getMaterial() != null) {
			add(row, 6, getMaterial());
		}
//		Set<String> classification = classifications.get(ifcProduct);
		if (classifications != null) {
			Classification first = classifications.iterator().next();
			add(row, 7, first.getAssociationName());
			add(row, 8, first.getIdentification());
			add(row, 9, first.getItemReference());
			add(row, 10, first.getLocation());
			add(row, 11, first.getName());
		}
		add(row, 12, nrTriangles);
		add(row, 13, "TODO");
		add(row, 14, nrPropertySets);
		add(row, 15, nrPSets);
		add(row, 16, nrProperties);
		add(row, 17, geometricArea);
		add(row, 18, geometricVolume);
		add(row, 19, (quantityArea == null ? "" : quantityArea));
		add(row, 20, (quantityVolume == null ? "" : quantityVolume));
		add(row, 21, geometryDataOid);
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

	public long getOid() {
		return oid;
	}

	public void setOid(long oid) {
		this.oid = oid;
	}

	public void setGeometryDataOid(long geometryDataOid) {
		this.geometryDataOid = geometryDataOid;
	}
	
	public long getGeometryDataOid() {
		return geometryDataOid;
	}
}
