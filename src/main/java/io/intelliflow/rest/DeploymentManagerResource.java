package io.intelliflow.rest;

import io.intelliflow.db.Deployment;
import io.intelliflow.db.DeploymentDto;
import io.intelliflow.db.DeploymentQueue;
import io.intelliflow.model.BuildTriggerRequestModel;
import io.intelliflow.model.BuildTriggerResponseModel;
import io.intelliflow.model.RunStatusResponseModel;
import io.intelliflow.model.StopTriggerResponseModel;
import io.intelliflow.service.AppByDeployIdService;
import io.intelliflow.service.AppsByNameService;
import io.intelliflow.service.DeploymentManagerService;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

@ApplicationScoped
@Path("/deployment/build")
public class DeploymentManagerResource {
    @Inject
    @RestClient
    DeploymentManagerService deploymentManagerService;

    @Inject
    BuildManagementService buildManagementService;

    @Inject
    BuildQueueService buildQueueService;

    @Inject
    AppByDeployIdService appByDeployIdService;

    @Inject
    AppsByNameService appsByNameService;


    String workspaceName = "";
    String appName = "";

    public void writeXMLFile(Document doc, File tempFile)
            throws TransformerFactoryConfigurationError, TransformerException {
        doc.getDocumentElement().normalize();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(tempFile);

        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

        transformer.transform(source, result);
        System.out.println("POM.xml file updated successfully");
    }

    static int versionCompare(String v1, String v2) {

        int vnum1 = 0, vnum2 = 0;

        for (int i = 0, j = 0; (i < v1.length()
                || j < v2.length());) {

            while (i < v1.length()
                    && v1.charAt(i) != '.') {
                vnum1 = vnum1 * 10
                        + (v1.charAt(i) - '0');
                i++;
            }
            while (j < v2.length()
                    && v2.charAt(j) != '.') {
                vnum2 = vnum2 * 10
                        + (v2.charAt(j) - '0');
                j++;
            }

            if (vnum1 > vnum2)
                return 1;
            if (vnum2 > vnum1)
                return -1;
            vnum1 = vnum2 = 0;
            i++;
            j++;
        }
        return 0;
    }

    private static String updateElementValue(Document doc, String versionarg) {
        NodeList users = doc.getElementsByTagName("project");
        Element user = null;
        String version = null;
        for (int i = 0; i < users.getLength(); i++) {
            user = (Element) users.item(i);
            Node name = user.getElementsByTagName("version").item(0).getFirstChild();
            String currentVersion = name.getTextContent().toString();
            String[] versionsplit = currentVersion.split("\\.");
            int x = Integer.parseInt(versionsplit[2]) + 1;
            versionsplit[2] = Integer.toString(x);

            version = String.join(".", versionsplit);
            if (versionarg != null && versionCompare(versionarg, version) > 0)
                name.setTextContent(versionarg);
            else
                name.setTextContent(version);
        }
        return versionCompare(versionarg, version) > 0?versionarg:version;

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/start")
    public BuildTriggerResponseModel startDeployment(BuildTriggerRequestModel buildTriggerRquest)
            throws IOException, SAXException, XmlPullParserException {
        BuildTriggerResponseModel buildTriggerResponseModel =
                deploymentManagerService
                        .triggerOneClickDeployment("oneclick-deployment-pipeline",
                                buildTriggerRquest.getBuildParameters());

        Deployment deployment = new Deployment();

        deployment.setBuildno(buildTriggerResponseModel.getId());
        deployment.setActionedby(buildTriggerRquest.getUser());

        deployment.setActiontime(Date.from(Instant.now()));
        deployment.setComment(buildTriggerRquest.getComment());
        deployment.setAction("BUILD_RUN_START");
        deployment.setOutcome(buildTriggerResponseModel.getState());

        buildManagementService.save(deployment);

        DeploymentQueue deploymentQueue = new DeploymentQueue();
        deploymentQueue.setBuildno(buildTriggerResponseModel.getId());
        deploymentQueue.setQueuetime(Date.from(Instant.now()));
        deploymentQueue.setBuildstatus(buildTriggerResponseModel.getState());
        buildQueueService.save(deploymentQueue);

        for(int i = 0; i <
                buildTriggerRquest.getBuildParameters().getParameters().size(); i++) {
            if(buildTriggerRquest.getBuildParameters().getParameters().get(i).getName().equalsIgnoreCase("workspace")){
                workspaceName =
                        buildTriggerRquest.getBuildParameters().getParameters().get(i).getValue();
            } else {
                appName =
                        buildTriggerRquest.getBuildParameters().getParameters().get(i).getValue();
            }
        }

        appByDeployIdService.save(buildTriggerResponseModel.getId(),workspaceName,appName,buildTriggerResponseModel.getState());

        updateAppData(
                buildTriggerResponseModel.getId(),
                buildTriggerResponseModel.getState(),
                workspaceName,
                appName
        );
        for (int i = 0; i < buildTriggerRquest.getBuildParameters().getParameters().size(); i++) {
            if (buildTriggerRquest.getBuildParameters().getParameters().get(i).getName()
                    .equalsIgnoreCase("workspace")) {
                workspaceName = buildTriggerRquest.getBuildParameters().getParameters().get(i).getValue();
            } else {
                appName = buildTriggerRquest.getBuildParameters().getParameters().get(i).getValue();
            }
        }
        String repositoryFolderPath = ConfigProvider.getConfig().getValue("repo.path", String.class);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            File tempFile = new File(
                    repositoryFolderPath+workspaceName +"/"+ appName +"/pom.xml");
            InputStream inputStream = new FileInputStream(tempFile);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            String versionpara = buildTriggerRquest.getVersion();
            versionpara = versionpara != null ? versionpara : null;

            //String version = updateElementValue(document, versionpara);
            this.writeXMLFile(document, tempFile);
           // System.out.println("New Version :" + version);
            inputStream.close();
            //appsByNameService.updateVersionByDeployment(version,workspaceName,appName);

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

        return buildTriggerResponseModel;

    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/stop/{buildID}")
    public StopTriggerResponseModel stopDeployment(@PathParam("buildID") String buildID,
                                                   BuildTriggerRequestModel buildTriggerRquest) {

        StopTriggerResponseModel stopTriggerResponse = new StopTriggerResponseModel();

        DeploymentQueue deploymentQueue = buildQueueService.find(buildID);

        if((deploymentQueue != null) && (!"FINISHED".equals(deploymentQueue.getBuildstatus()))  ) {

            BuildTriggerResponseModel buildTriggerResponseModel = deploymentManagerService
                    .stopDeployment("oneclick-deployment-pipeline", buildID);

            Deployment deployment = new Deployment();

            deployment.setBuildno(buildTriggerResponseModel.getId());
            deployment.setActionedby(buildTriggerRquest.getUser());
            deployment.setActiontime(Date.from(Instant.now()));
            deployment.setComment(buildTriggerRquest.getComment());
            deployment.setOutcome(buildTriggerResponseModel.getState());
            deployment.setAction("BUILD_RUN_STOP");

            buildManagementService.save(deployment);

            stopTriggerResponse.setState(buildTriggerResponseModel.getState());

            if("ABORTED".equals(buildTriggerResponseModel.getResult())) {
                stopTriggerResponse.setMessage("Successfully aborted the build "+buildID);
                appByDeployIdService.updateStatus(buildTriggerResponseModel.getId(),buildTriggerResponseModel.getState());

                updateAppData(
                        buildTriggerResponseModel.getId(),
                        buildTriggerResponseModel.getState(),
                        workspaceName,
                        appName
                );
            }else {

                stopTriggerResponse.setMessage(buildTriggerResponseModel.getResult());

            }


        }else {

            stopTriggerResponse.setState("UNKNOWN");

            stopTriggerResponse.setMessage("Build is either ABORTED or FINISHED");

        }



        return stopTriggerResponse;

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/status/{buildID}")
    public BuildTriggerResponseModel runStatusUpdate(@PathParam("buildID") String buildID,
                                                     BuildTriggerRequestModel buildTriggerRquest) {

        BuildTriggerResponseModel buildTriggerResponseModel = deploymentManagerService
                .stopDeployment("oneclick-deployment-pipeline", buildID);

        return buildTriggerResponseModel;

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{artifact}/{buildID}")
    public RunStatusResponseModel runStatus(@PathParam("artifact") String artifactType ,@PathParam("buildID") String buildID) {


        RunStatusResponseModel responseModel = new RunStatusResponseModel() ;
        BuildTriggerResponseModel buildTriggerResponseModel = null;

        switch(artifactType) {

            case "logs":

                responseModel.setLogs(deploymentManagerService.fetchLog("oneclick-deployment-pipeline", buildID));
                break;

            case "status":

                buildTriggerResponseModel = deploymentManagerService
                        .fetchBuildRunStatus("oneclick-deployment-pipeline", buildID);

                responseModel.setRunState(buildTriggerResponseModel.getState());
                responseModel.setRunState(buildTriggerResponseModel.getResult());

                break;
            case "history":
                responseModel.setHistory(buildManagementService.getAll(buildID));
                break;

            case "all":

                buildTriggerResponseModel = deploymentManagerService
                        .fetchBuildRunStatus("oneclick-deployment-pipeline", buildID);

                responseModel.setRunState(buildTriggerResponseModel.getState());
                responseModel.setRunStatus(buildTriggerResponseModel.getResult());

                responseModel.setLogs(deploymentManagerService.fetchLog("oneclick-deployment-pipeline", buildID));

                responseModel.setHistory(buildManagementService.getAll(buildID));

                break;

            default:
                break;

        }


        return responseModel;

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    // @Consumes(MediaType.TEXT_PLAIN)
    @Path("/log/{buildID}")
    public String getRunLog(@PathParam("buildID") String buildID, BuildTriggerRequestModel buildTriggerRquest) {

        String response = deploymentManagerService.fetchLog("oneclick-deployment-pipeline", buildID);

        return response;

    }

    private Deployment convertFromDto(DeploymentDto deploymentDto) {

        Deployment deployment = new Deployment();
        // deployment.setStartedBy(deploymentDto.getStartedBy());

        return deployment;
    }

    private DeploymentDto convertToDto(Deployment deployment) {
        DeploymentDto deploymentDto = new DeploymentDto();

        // deploymentDto.setStartedBy(deployment.getStartedBy());

        return deploymentDto;
    }

    private void updateAppData(String id, String state, String workspaceName, String appName) {
        appsByNameService.updateDeployAppData(Collections.singleton(id),id,state,workspaceName,appName);
    }
}
