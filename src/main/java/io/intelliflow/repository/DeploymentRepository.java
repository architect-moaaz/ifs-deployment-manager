package io.intelliflow.repository;

import io.intelliflow.db.Deployment;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DeploymentRepository implements PanacheMongoRepository<Deployment> {
    public List<Deployment> findAllByBuildNo(String buildNo) {
        return find("buildNo", buildNo).list();
    }
}
