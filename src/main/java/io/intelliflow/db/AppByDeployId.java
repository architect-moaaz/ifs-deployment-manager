package io.intelliflow.db;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;


@MongoEntity(collection = "app_by_deployid")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppByDeployId {

    ObjectId id;
    String deployid;
    String appname;
    String status;
    String version;
    String workspacename;
}
