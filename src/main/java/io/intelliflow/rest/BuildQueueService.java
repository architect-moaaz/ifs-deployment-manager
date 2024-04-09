package io.intelliflow.rest;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.intelliflow.db.DeploymentQueue;
import io.intelliflow.repository.DeploymentQueueRepository;

@ApplicationScoped
public class BuildQueueService {

	@Inject
	DeploymentQueueRepository deploymentQueueRepository;
	
	public void save(DeploymentQueue deploymentQ) {
		deploymentQueueRepository.persistOrUpdate(deploymentQ);
	}

	public List<DeploymentQueue> getAll() {
		return deploymentQueueRepository.listAll();
	}
	
	public DeploymentQueue find(String buildNo) {
		return deploymentQueueRepository.findByBuildNo(buildNo);
	}
	
	public void delete(DeploymentQueue deploymentQ) {
		deploymentQueueRepository.delete(deploymentQ);
		
	}


}
