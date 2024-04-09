package io.intelliflow.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.intelliflow.db.Deployment;
import io.intelliflow.db.DeploymentDto;


@Path("/build")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

@ApplicationScoped
public class BuildManagementResource {

	@Inject
	BuildManagementService buildManagementService;
	
	  @GET
	  public List<DeploymentDto> getAll() {
		 
	    return buildManagementService.getAll().stream().map(this::convertToDto).collect(Collectors.toList());
	  }

	  @POST
	  public void add(DeploymentDto deploymentDto) {
		  buildManagementService.save(convertFromDto(deploymentDto));
	  }
	  
	  private Deployment convertFromDto(DeploymentDto deploymentDto) {
		  
		  Deployment deployment = new Deployment();
		  deployment.setBuildno(deploymentDto.getBuildNo().toString());
		  
		  return deployment;
	  }
	  
	  private DeploymentDto convertToDto(Deployment deployment) {
		  DeploymentDto deploymentDto = new DeploymentDto();
		  return deploymentDto;
	  }

}
