package org.opensourcebim.modelsetanalyzer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.bimserver.models.store.IfcHeader;

import com.google.common.base.Joiner;

public class MetaData {

	private IfcHeader ifcHeader;
	private int revisionId;
	private Set<String> classifications;

	public void getIfcHeader(IfcHeader ifcHeader) {
		this.ifcHeader = ifcHeader;
	}
	
	public void toExcel(Row row) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		row.createCell(0).setCellValue("" + revisionId);
		row.createCell(1).setCellValue(ifcHeader.getFilename());
		row.createCell(2).setCellValue(ifcHeader.getIfcSchemaVersion());
		row.createCell(3).setCellValue(ifcHeader.getImplementationLevel());
		row.createCell(4).setCellValue(ifcHeader.getOriginatingSystem());
		row.createCell(5).setCellValue(ifcHeader.getPreProcessorVersion());
		row.createCell(6).setCellValue(ifcHeader.getAuthorization());
		row.createCell(7).setCellValue(Joiner.on(", ").join(ifcHeader.getOrganization()));
		row.createCell(8).setCellValue(Joiner.on(", ").join(ifcHeader.getAuthor()));
		row.createCell(9).setCellValue(Joiner.on(", ").join(ifcHeader.getDescription()));
		row.createCell(10).setCellValue(dateFormat.format(ifcHeader.getTimeStamp()));
		if (classifications != null) {
			row.createCell(11).setCellValue(Joiner.on(", ").join(classifications));
		}
	}

	public int getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(int revisionId) {
		this.revisionId = revisionId;
	}

	public Set<String> getClassifications() {
		return classifications;
	}

	public void setClassifications(Set<String> classifications) {
		this.classifications = classifications;
	}
}