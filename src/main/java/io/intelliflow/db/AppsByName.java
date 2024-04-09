package io.intelliflow.db;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Set;

@MongoEntity(collection = "apps_by_name")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppsByName {
    ObjectId id;
    Set<String> alldeployment;
    String appdisplayname;
    String appname;
    String colorscheme;
    String deploymentid;
    String description;
    String devicesupport;
    Set<String> files;
    String logourl;
    String status;
    String userid;
    String workspacename;
    Date creationtime;
    Date lastupdatedtime;
    String version;
}
