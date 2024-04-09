package io.intelliflow.model;

import java.util.ArrayList;

import io.intelliflow.model.blueOcean._links;

public class BuildTriggerResponseModel {
	 private String _class;
	 _links _linksObject;
	 ArrayList < Object > actions = new ArrayList < Object > ();
	 private String artifactsZipFile = null;
	 private String causeOfBlockage;
	 ArrayList < Object > causes = new ArrayList < Object > ();
	 ArrayList < Object > changeSet = new ArrayList < Object > ();
	 private String description = null;
	 private String durationInMillis = null;
	 private String enQueueTime = null;
	 private String endTime = null;
	 private String estimatedDurationInMillis = null;
	 private String id;
	 private String name = null;
	 private String organization;
	 private String pipeline;
	 private boolean replayable;
	 private String result;
	 private String runSummary = null;
	 private String startTime = null;
	 private String state;
	 private String type;
	 private String queueId;


	 // Getter Methods 

	 public String get_class() {
	  return _class;
	 }

	 public _links get_links() {
	  return _linksObject;
	 }

	 public String getArtifactsZipFile() {
	  return artifactsZipFile;
	 }

	 public String getCauseOfBlockage() {
	  return causeOfBlockage;
	 }

	 public String getDescription() {
	  return description;
	 }

	 public String getDurationInMillis() {
	  return durationInMillis;
	 }

	 public String getEnQueueTime() {
	  return enQueueTime;
	 }

	 public String getEndTime() {
	  return endTime;
	 }

	 public String getEstimatedDurationInMillis() {
	  return estimatedDurationInMillis;
	 }

	 public String getId() {
	  return id;
	 }

	 public String getName() {
	  return name;
	 }

	 public String getOrganization() {
	  return organization;
	 }

	 public String getPipeline() {
	  return pipeline;
	 }

	 public boolean getReplayable() {
	  return replayable;
	 }

	 public String getResult() {
	  return result;
	 }

	 public String getRunSummary() {
	  return runSummary;
	 }

	 public String getStartTime() {
	  return startTime;
	 }

	 public String getState() {
	  return state;
	 }

	 public String getType() {
	  return type;
	 }

	 public String getQueueId() {
	  return queueId;
	 }

	 // Setter Methods 

	 public void set_class(String _class) {
	  this._class = _class;
	 }

	 public void set_links(_links _linksObject) {
	  this._linksObject = _linksObject;
	 }

	 public void setArtifactsZipFile(String artifactsZipFile) {
	  this.artifactsZipFile = artifactsZipFile;
	 }

	 public void setCauseOfBlockage(String causeOfBlockage) {
	  this.causeOfBlockage = causeOfBlockage;
	 }

	 public void setDescription(String description) {
	  this.description = description;
	 }

	 public void setDurationInMillis(String durationInMillis) {
	  this.durationInMillis = durationInMillis;
	 }

	 public void setEnQueueTime(String enQueueTime) {
	  this.enQueueTime = enQueueTime;
	 }

	 public void setEndTime(String endTime) {
	  this.endTime = endTime;
	 }

	 public void setEstimatedDurationInMillis(String estimatedDurationInMillis) {
	  this.estimatedDurationInMillis = estimatedDurationInMillis;
	 }

	 public void setId(String id) {
	  this.id = id;
	 }

	 public void setName(String name) {
	  this.name = name;
	 }

	 public void setOrganization(String organization) {
	  this.organization = organization;
	 }

	 public void setPipeline(String pipeline) {
	  this.pipeline = pipeline;
	 }

	 public void setReplayable(boolean replayable) {
	  this.replayable = replayable;
	 }

	 public void setResult(String result) {
	  this.result = result;
	 }

	 public void setRunSummary(String runSummary) {
	  this.runSummary = runSummary;
	 }

	 public void setStartTime(String startTime) {
	  this.startTime = startTime;
	 }

	 public void setState(String state) {
	  this.state = state;
	 }

	 public void setType(String type) {
	  this.type = type;
	 }

	 public void setQueueId(String queueId) {
	  this.queueId = queueId;
	 }
}
