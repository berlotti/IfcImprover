package org.opensourcebim.modelsetanalyzer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Aggregation {
	private int revisionId;
	private long size;
	private int countWithSubtypes;
	private int productCount;
	private float m2;
	private float m3;
	private float m2bb;
	private float m3bb;

	public Aggregation() {
		
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getCountWithSubtypes() {
		return countWithSubtypes;
	}

	public void setCountWithSubtypes(int countWithSubtypes) {
		this.countWithSubtypes = countWithSubtypes;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public float getM2bb() {
		return m2bb;
	}

	public void setM2bb(float m2bb) {
		this.m2bb = m2bb;
	}

	public float getM3bb() {
		return m3bb;
	}

	public void setM3bb(float m3bb) {
		this.m3bb = m3bb;
	}

	public int getRevisionId() {
		return revisionId;
	}

	public float getM2() {
		return m2;
	}

	public float getM3() {
		return m3;
	}

	public void setRevisionId(int revisionId) {
		this.revisionId = revisionId;
	}

	public void setModelSize(long size) {
		this.size = size;
	}

	public void setIfcRelationsShipCount(int countWithSubtypes) {
		this.countWithSubtypes = countWithSubtypes;
	}

	public void setIfcProductCount(int productCount) {
		this.productCount = productCount;
	}

	public void setM2(float m2) {
		this.m2 = m2;
	}

	public void setM3(float m3) {
		this.m3 = m3;
	}

	public void setM2AABB(float m2bb) {
		this.m2bb = m2bb;
	}

	public void setM3AABB(float m3bb) {
		this.m3bb = m3bb;
	}
	
	public void toExcel(Sheet sheet, int rowId) {
		Row row = sheet.createRow(rowId);
		row.createCell(0).setCellValue(revisionId);
		row.createCell(1).setCellValue(size);
		row.createCell(2).setCellValue(countWithSubtypes);
		row.createCell(3).setCellValue(productCount);
		row.createCell(4).setCellValue(m2);
		row.createCell(5).setCellValue(m3);
		row.createCell(6).setCellValue(m2bb);
		row.createCell(7).setCellValue(m3bb);
	}
}
