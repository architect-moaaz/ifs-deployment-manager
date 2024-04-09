package io.intelliflow.service;

import io.intelliflow.model.ansible.BuildRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@RegisterRestClient(configKey = "ansiblemanager-api")
public interface AnsibleManagerService {

    @POST
    @Path("/deploy")
    Object triggerAnsibleScriptDeployment(BuildRequest buildRequest);
}
