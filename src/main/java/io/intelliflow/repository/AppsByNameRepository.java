package io.intelliflow.repository;

import io.intelliflow.db.AppsByName;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class AppsByNameRepository implements PanacheMongoRepository<AppsByName> {
    public AppsByName findByAppNameAndWorkSpaceName(String appName, String workspaceName) {
        return find("appname = :appname and workspacename = :workspacename", Map.of("appname", appName, "workspacename", workspaceName)).firstResult();
    }

    public void updateDeployAppData(Set<String> allDeployment, String deploymentId, String state, String workspaceName, String appName) {
        AppsByName app = find("workspacename = ?1 and appname = ?2", workspaceName, appName).firstResult();
        if (app != null) {
            app.setAlldeployment(allDeployment);
            app.setDeploymentid(deploymentId);
            app.setStatus(state);
            update(app);
        }
    }

    public void updateVersionByDeployment(String version, String workspaceName, String appName) {
        AppsByName app = find("workspacename = ?1 and appname = ?2", workspaceName, appName).firstResult();
        if (app != null) {
            app.setVersion(version);
            update(app);
        }
    }
}
