package io.intelliflow.rest;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.intelliflow.db.Deployment;
import io.intelliflow.repository.DeploymentRepository;

@ApplicationScoped
public class BuildManagementService {


	@Inject
	DeploymentRepository deploymentRepository;

	public void save(Deployment deployment) {
		deploymentRepository.persist(deployment);
	}

	public List<Deployment> getAll() {
		return deploymentRepository.listAll();
	}

	public List<Deployment> getAll(String buildNo){
		return deploymentRepository.findAllByBuildNo(buildNo);
	}
	
}
