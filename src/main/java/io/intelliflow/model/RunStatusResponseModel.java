package io.intelliflow.model;

import java.util.List;

import io.intelliflow.db.Deployment;

public class RunStatusResponseModel {
	
	private String logs;
	
	private String runStatus;
	
	private String runState;

	
	 private List<Deployment> history;

	public String getLogs() {
		return logs;
	}

	public void setLogs(String logs) {
		this.logs = logs;
	}

	public String getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}

	public String getRunState() {
		return runState;
	}

	public void setRunState(String runState) {
		this.runState = runState;
	}

	public List<Deployment> getHistory() {
		return history;
	}

	public void setHistory(List<Deployment> history) {
		this.history = history;
	}
	
	
	

}
