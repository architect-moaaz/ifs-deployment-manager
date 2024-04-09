package io.intelliflow.model.blueOcean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class BuildParameter {

	private String name;
	private String value;

	// Getter Methods

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	// Setter Methods

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
}