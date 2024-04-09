package io.intelliflow.service;

import io.intelliflow.db.AppByDeployId;
import io.intelliflow.repository.AppByDeployIdRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AppByDeployIdService {
    @Inject
    AppByDeployIdRepository appByDeployIdRepository;

    public void save(String deployId, String workspaceName, String appName, String state) {
        AppByDeployId appByDeployId = new AppByDeployId();

        if (appName != null && !appName.isEmpty()) {
            appByDeployId.setAppname(appName);
        }

        if (state != null && !state.isEmpty()) {
            appByDeployId.setStatus(state);
        }

        if (workspaceName != null && !workspaceName.isEmpty()) {
            appByDeployId.setWorkspacename(workspaceName);
        }

        if (deployId != null && !deployId.isEmpty()) {
            appByDeployId.setDeployid(deployId);
        }

        appByDeployIdRepository.persist(appByDeployId);
    }

    public void updateStatus(String id, String state) {
        appByDeployIdRepository.updateStatusByDeployId(id,state);
    }

    public List<AppByDeployId> findAllByDeployId(String id) {
        return appByDeployIdRepository.findAllByDeployId(id);
    }
}
