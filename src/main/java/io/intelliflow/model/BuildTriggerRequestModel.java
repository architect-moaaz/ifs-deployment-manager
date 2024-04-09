package io.intelliflow.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class BuildTriggerRequestModel {
	
	String user;
	
	String comment;
	String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	BuildTriggerParameter buildParameters ;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public BuildTriggerParameter getBuildParameters() {
		return buildParameters;
	}

	public void setBuildParameters(BuildTriggerParameter buildParameters) {
		this.buildParameters = buildParameters;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	


}
