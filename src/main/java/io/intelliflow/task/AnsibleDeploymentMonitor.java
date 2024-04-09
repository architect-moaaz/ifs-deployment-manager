package io.intelliflow.task;

import io.intelliflow.db.AnsibleDeployApp;
import io.intelliflow.db.Deployment;
import io.intelliflow.db.DeploymentQueue;
import io.intelliflow.model.ansible.BuildRequest;
import io.intelliflow.repository.AnsibleDeployAppRepository;
import io.intelliflow.rest.BuildManagementService;
import io.intelliflow.rest.BuildQueueService;
import io.intelliflow.service.AnsibleManagerService;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AnsibleDeploymentMonitor {

    @Inject
    BuildQueueService buildQueueService;

    @Inject
    BuildManagementService buildManagementService;

    @Inject
    @RestClient
    AnsibleManagerService ansibleManagerService;

    @Inject
    AnsibleDeployAppRepository ansibleDeployAppRepository;

    private Map<String, String> runningBuilds = new ConcurrentHashMap<>();

    private static int concurrentBuildCount = 2;

    @Scheduled(every = "5s")
    public void deploymentMaintainer() {

        //logging the queue
        Log.info("Running Builds Count : " + runningBuilds.size());
        List<AnsibleDeployApp> byUserId = new ArrayList<>();

        //Updated Logic for concurrent Builds
        if (runningBuilds.size() == concurrentBuildCount) {
            //If there are 2 running builds, update the status or remove it if it's done
            for (Map.Entry<String, String> runningEntry : runningBuilds.entrySet()) {
                byUserId = ansibleDeployAppRepository.findByUserId(runningEntry.getKey());

                for (AnsibleDeployApp row : byUserId) {
                    String currentStatusAnsible = row.getStatus();
                    Log.info("Current Ansible Deploy App Status:::" + currentStatusAnsible);
                    DeploymentQueue currentQueue = buildQueueService.find(runningEntry.getKey());
                    if (Objects.nonNull(currentQueue) && !currentStatusAnsible.equals(currentQueue.getBuildstatus())) {
                        //Updating status
                        Deployment deployment = new Deployment();
                        deployment.setActionedby("SYSTEM");
                        deployment.setActiontime(Date.from(Instant.now()));
                        deployment.setComment("Build Status Update");
                        deployment.setAction("BUILD_RUN_STATE");

                        deployment.setBuildno(runningEntry.getKey());
                        deployment.setOutcome(currentStatusAnsible);

                        buildManagementService.save(deployment);

                        if ("COMPLETED".equals(currentStatusAnsible) || "FAILED".equals(currentStatusAnsible)) {
                            buildQueueService.delete(currentQueue);
                            Log.info("Build deleted for workspace: " + row.getWorkspace() + " miniapp: " + row.getMiniapp() + " " + currentQueue.getBuildno());
                            //Removing from the running builds
                            runningBuilds.remove(runningEntry.getKey());
                        } else {
                            currentQueue.setBuildstatus(currentStatusAnsible);
                            buildQueueService.save(currentQueue);
                        }

                    } else {
                        System.out.println("Current Queue is empty");
                    }
                    break;
                }
            }
        }

        //Enters if running Builds are less than the currentCount
        if (runningBuilds.size() < concurrentBuildCount) {

            List<DeploymentQueue> q = buildQueueService.getAll();
            //Sorting the queue in ascending order on queue time so that the first request added to the queue should be processed first.
            q.sort(Comparator.comparing(DeploymentQueue::getQueuetime));

            System.out.println("Ansible Build Queue:  " + q.size());

            //Adding new builds into the deployment builds array
            for (int i = 0; i < q.size(); i++) {
                DeploymentQueue deploymentQueueEntry = q.get(i);
                //Case to avoid duplicate entry coming in running builds
                if(runningBuilds.containsKey(deploymentQueueEntry.getBuildno())) {
                    byUserId = ansibleDeployAppRepository.findByUserId(deploymentQueueEntry.getBuildno());
                    AnsibleDeployApp deployApp = byUserId.get(0);
                    if ("COMPLETED".equals(deployApp.getStatus()) || "FAILED".equals(deployApp.getStatus())) {
                        buildQueueService.delete(deploymentQueueEntry);
                        Log.info("Build deleted for workspace: " + deployApp.getWorkspace() + " miniapp: " + deployApp.getMiniapp() + " " + deploymentQueueEntry.getBuildno());
                        //Removing from the running builds
                        runningBuilds.remove(deploymentQueueEntry.getBuildno());
                    } else {
                        deploymentQueueEntry.setBuildstatus(deployApp.getStatus());
                        buildQueueService.save(deploymentQueueEntry);
                    }
                    continue;
                }
                byUserId = ansibleDeployAppRepository.findByUserId(deploymentQueueEntry.getBuildno());
                AnsibleDeployApp deployApp = byUserId.get(0);

                //Triggering a Build in Ansible
                BuildRequest buildRequest = new BuildRequest();
                buildRequest.setWorkspaceName(deployApp.getWorkspace());
                buildRequest.setMiniappName(deployApp.getMiniapp());
                buildRequest.setDeployId(deploymentQueueEntry.getBuildno());

                try {
                    runningBuilds.put(deploymentQueueEntry.getBuildno(), deploymentQueueEntry.getBuildstatus());
                    Log.info("Build request sending for workspace: " + deployApp.getWorkspace() + " miniapp: " + deployApp.getMiniapp() + " " + deploymentQueueEntry.getBuildno());
                    Object deployResponse = ansibleManagerService.triggerAnsibleScriptDeployment(buildRequest);
                    System.out.println("Ansible Response:: " + deployResponse.toString());
                } catch (Exception e) {
                    Log.error("Error Occured in Ansible API ::: \n" + e);
                    break;
                }
                if (runningBuilds.size() == concurrentBuildCount) {
                    break;
                }
            }
        }
    }
}
