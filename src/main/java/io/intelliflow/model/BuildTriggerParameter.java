package io.intelliflow.model;

import java.util.ArrayList;

import io.intelliflow.model.blueOcean.BuildParameter;

public class BuildTriggerParameter {

	ArrayList<BuildParameter> parameters = new ArrayList<BuildParameter>();

	public ArrayList<BuildParameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<BuildParameter> parameters) {
		this.parameters = parameters;
	}
	
}
