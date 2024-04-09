package io.intelliflow.db;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;

@MongoEntity(collection = "ansible_deploy_app")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnsibleDeployApp {
    ObjectId id;
    Date endtime;
    String errorlog;
    Date initiatetime;
    String miniapp;
    String status;
    String userid;
    String workspace;
    String version;
    String actioned_by;
}
