package io.intelliflow.service;

import io.intelliflow.db.AppsByName;
import io.intelliflow.repository.AppsByNameRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;

@ApplicationScoped
public class AppsByNameService {

    @Inject
    AppsByNameRepository appsByNameRepository;
    public void updateAppData(String id, String state, String workspaceName, String appName) {
        AppsByName appsByName = appsByNameRepository.findByAppNameAndWorkSpaceName(appName,workspaceName);
        appsByName.setAlldeployment(Set.of(id));
        appsByName.setDeploymentid(id);
        appsByName.setStatus(state);
        appsByNameRepository.update(appsByName);
    }

    public void updateDeployAppData(Set<String> allDeployment, String deploymentId, String state, String workspaceName, String appName) {
        appsByNameRepository.updateDeployAppData(allDeployment,deploymentId,state,workspaceName,appName);
    }

    public void updateVersionByDeployment(String version, String workspaceName, String appName) {
        appsByNameRepository.updateVersionByDeployment(version,workspaceName,appName);
    }
}
