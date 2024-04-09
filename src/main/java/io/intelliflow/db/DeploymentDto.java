package io.intelliflow.db;

import java.util.Date;

public class DeploymentDto {
	
	Integer buildNo;
	
	String startedBy;
	
	String stoppedBy;
	
	Date completedDate;
	
	Date startedDate;
	
	public DeploymentDto() {}

	public Integer getBuildNo() {
		return buildNo;
	}

	public void setBuildNo(Integer buildNo) {
		this.buildNo = buildNo;
	}

	public String getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}

	public String getStoppedBy() {
		return stoppedBy;
	}

	public void setStoppedBy(String stoppedBy) {
		this.stoppedBy = stoppedBy;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}
	
	

}
