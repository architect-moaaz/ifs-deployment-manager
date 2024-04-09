package io.intelliflow.repository;

import io.intelliflow.db.DeploymentQueue;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DeploymentQueueRepository implements PanacheMongoRepository<DeploymentQueue> {
    public DeploymentQueue findByBuildNo(String buildNo) {
        return find("buildno", buildNo).firstResult();
    }

    public void delete(DeploymentQueue deploymentQueue) {
        delete("buildno", deploymentQueue.getBuildno());
    }
}
