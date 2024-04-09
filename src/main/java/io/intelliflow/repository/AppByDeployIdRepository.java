package io.intelliflow.repository;

import io.intelliflow.db.AppByDeployId;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
@ApplicationScoped

public class AppByDeployIdRepository implements PanacheMongoRepository<AppByDeployId> {
    public void updateStatusByDeployId(String id, String state) {
        update("status = ?1 where deployid = ?2", state, id);
    }

    public List<AppByDeployId> findAllByDeployId(String id) {
        return find("deployid", id).list();
    }
}
