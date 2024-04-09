package io.intelliflow.service;

import java.util.Base64;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.intelliflow.model.BuildTriggerParameter;
import io.intelliflow.model.BuildTriggerResponseModel;

@RegisterRestClient(configKey = "deploymentmanager-api")
@ClientHeaderParam(name = "Authorization", value = "{auth}")
public interface DeploymentManagerService {

//	@GET
//	@Path("/crumbIssuer/api/json")
//	public AuthResponseModel getAuthCrumb();

	@POST
	@Path("/{pipeline}/runs/")
	@ClientHeaderParam(name = "Authorization", value = "{auth}")
	public BuildTriggerResponseModel triggerOneClickDeployment(
			@PathParam("pipeline") @DefaultValue("oneclick-deployment-pipeline") String pipeline,
			BuildTriggerParameter buildTriggerRequestModel);

	@GET
	@Path("/{pipeline}/runs/{buildNo}")
	@ClientHeaderParam(name = "Authorization", value = "{auth}")
	@ClientHeaderParam(name = "Content-Type", value = "application/json")
	public BuildTriggerResponseModel fetchBuildRunStatus(
			@PathParam("pipeline") @DefaultValue("oneclick-deployment-pipeline") String pipeline,
			@PathParam("buildNo") String buildNo);

	@PUT
	@Path("/{pipeline}/runs/{buildNo}/stop")
	@ClientHeaderParam(name = "Authorization", value = "{auth}")
	@ClientHeaderParam(name = "Content-Type", value = "application/json")
	public BuildTriggerResponseModel stopDeployment(
			@PathParam("pipeline") @DefaultValue("oneclick-deployment-pipeline") String pipeline,
			@PathParam("buildNo") String buildNo);

	@GET
	@Path("/{pipeline}/runs/{buildNo}/log")
	@ClientHeaderParam(name = "Authorization", value = "{auth}")
	public String fetchLog(
			@PathParam("pipeline") @DefaultValue("oneclick-deployment-pipeline") String pipeline,
			@PathParam("buildNo") String buildNo);

	default String auth() {
		final Config config = ConfigProvider.getConfig();
		String token = config.getValue("if.jenkins.token", String.class);
		return "Basic " + Base64.getEncoder().encodeToString(("admin:"+token).getBytes());
	}

}
