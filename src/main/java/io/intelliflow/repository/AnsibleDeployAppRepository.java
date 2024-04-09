package io.intelliflow.repository;

import io.intelliflow.db.AnsibleDeployApp;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class AnsibleDeployAppRepository implements PanacheMongoRepository<AnsibleDeployApp> {
    public List<AnsibleDeployApp> findByUserId(String userId) {
        return find("userid", userId).list();
    }

    public void updateErrorLogsAndEndTime(Instant now, String errorLog, String id) {
        update("endtime = :endtime, errorlog = :errorlog ", now, errorLog, id);
    }

    public void updateDeployStatus(String status, String id) {
        update("status = ?1 where userid = ?2", status, id);
    }
}
