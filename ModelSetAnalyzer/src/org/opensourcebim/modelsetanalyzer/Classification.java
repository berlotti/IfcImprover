package org.opensourcebim.modelsetanalyzer;

public class Classification {

	private String identification;
	private String name;
	private String associationName;
	private String location;
	private String itemReference;

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAssociationName(String associationName) {
		this.associationName = associationName;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setItemReference(String itemReference) {
		this.itemReference = itemReference;
	}

	public String getIdentification() {
		return identification;
	}

	public String getName() {
		return name;
	}

	public String getAssociationName() {
		return associationName;
	}

	public String getLocation() {
		return location;
	}

	public String getItemReference() {
		return itemReference;
	}
}
