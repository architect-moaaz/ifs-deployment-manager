package io.intelliflow.model.ansible;

import java.time.Instant;

public class BuildUpdate extends BuildRequest{

    private String status;
    private Instant initiateTime;
    private Instant endTime;
    private String userId;

    private String errorLog;
    String actioned_by;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getInitiateTime() {
        return initiateTime;
    }

    public void setInitiateTime(Instant initiateTime) {
        this.initiateTime = initiateTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

    public String getActioned_by() {
        return actioned_by;
    }

    public void setActioned_by(String actioned_by) {
        this.actioned_by = actioned_by;
    }
}
