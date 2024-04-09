package io.intelliflow.model.ansible;

public class BuildRequest {

    private String workspaceName;

    private String miniappName;

    private String deployId;

    private String user;

    private String comment;
    private String version;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getMiniappName() {
        return miniappName;
    }

    public void setMiniappName(String miniappName) {
        this.miniappName = miniappName;
    }

    public String getDeployId() {
        return deployId;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
