package io.intelliflow.rest;

import io.intelliflow.db.AnsibleDeployApp;
import io.intelliflow.db.Deployment;
import io.intelliflow.db.DeploymentQueue;
import io.intelliflow.model.ansible.BuildRequest;
import io.intelliflow.model.ansible.BuildUpdate;
import io.intelliflow.model.ansible.StatusResponse;
import io.intelliflow.repository.AnsibleDeployAppRepository;
import io.intelliflow.repository.AppsByNameRepository;
import io.intelliflow.service.AppsByNameService;
import org.eclipse.microprofile.config.ConfigProvider;
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
import java.io.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
@Path("/ansible/build")
public class AnsibleDeploymentService {

    @Inject
    BuildManagementService buildManagementService;

    @Inject
    BuildQueueService buildQueueService;

    @Inject
    AnsibleDeployAppRepository ansibleDeployAppRepository;

    @Inject
    AppsByNameRepository appsByNameRepository;

    @Inject
    AppsByNameService appsByNameService;
    private String INITIAL_VERSION="1.0.0";
    String repositoryFolderPath = ConfigProvider.getConfig().getValue("repo.path", String.class);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/initiate")
    public BuildUpdate initiateAnsibleScriptBuild(BuildRequest buildRequest) {

        String user = buildRequest.getUser() == null ? "SYSTEM" : buildRequest.getUser();
        String deployId = UUID.randomUUID().toString();

        Deployment deployment = new Deployment();
        deployment.setBuildno(deployId);
        deployment.setActionedby(user);

        deployment.setActiontime(Date.from(Instant.now()));
        deployment.setComment(buildRequest.getComment());
        deployment.setAction("BUILD_RUN_START");
        deployment.setOutcome("QUEUED");
        deployment.setActionedby(user);
        buildRequest.setDeployId(deployId);

        //Adding into deployment table
        buildManagementService.save(deployment);

        //Adding into deployment queue
        DeploymentQueue deploymentQueue = new DeploymentQueue();
        deploymentQueue.setBuildno(deployId);
        deploymentQueue.setQueuetime(Date.from(Instant.now()));
        deploymentQueue.setBuildstatus("QUEUED");
        buildQueueService.save(deploymentQueue);

        BuildUpdate response = new BuildUpdate();

        this.saveAnsibleDeployApp(buildRequest.getWorkspaceName(),buildRequest.getMiniappName(),deployId,Instant.now(),"QUEUED",buildRequest.getVersion(),user);
        response.setWorkspaceName(buildRequest.getWorkspaceName());
        response.setMiniappName(buildRequest.getMiniappName());
        response.setUserId(deployId);

        return response;

    }

    private void saveAnsibleDeployApp(String workspaceName, String miniappName, String deployId, Instant now, String state, String version, String user) {
        AnsibleDeployApp ansibleDeployApp = new AnsibleDeployApp();
        ansibleDeployApp.setMiniapp(miniappName);;
        ansibleDeployApp.setStatus(state);
        ansibleDeployApp.setInitiatetime(Date.from(now));
        ansibleDeployApp.setWorkspace(workspaceName);
        ansibleDeployApp.setUserid(deployId);
        ansibleDeployApp.setActioned_by(user);
        if(Objects.nonNull(version)) {
            ansibleDeployApp.setVersion(version);
        }else{
            ansibleDeployApp.setVersion(INITIAL_VERSION);
        }
        ansibleDeployAppRepository.persist(ansibleDeployApp);
    }

    @GET
    @Path("/status/{userid}")
    public BuildUpdate statusOfAnsibleBuild(@PathParam("userid") String userId) {

        BuildUpdate response = new BuildUpdate();
        List<AnsibleDeployApp> byUserId = ansibleDeployAppRepository.findByUserId(userId);
        for(AnsibleDeployApp ansibleDeployApp: byUserId){
            response.setStatus(ansibleDeployApp.getStatus());
            response.setInitiateTime(ansibleDeployApp.getInitiatetime().toInstant());
            response.setWorkspaceName(ansibleDeployApp.getWorkspace());
            response.setMiniappName(ansibleDeployApp.getMiniapp());
            if(Objects.nonNull(ansibleDeployApp.getErrorlog())) {
                response.setErrorLog(ansibleDeployApp.getErrorlog());
            }
            if(Objects.nonNull(ansibleDeployApp.getEndtime())) {
                response.setEndTime(ansibleDeployApp.getEndtime().toInstant());
            }
            response.setUserId(userId);
            response.setActioned_by(ansibleDeployApp.getActioned_by());
            break;
        }
        return response;
    }

    @POST
    @Path("/update-status")
    public StatusResponse updateBuildStatusAnsible(StatusResponse statusResponse) {


        String status = "QUEUED";
        String errorLog = null;
        AnsibleDeployApp deployedApp = null;
        System.out.println("Build Status::" + statusResponse.getStatus());
        System.out.println("Build Id::" + statusResponse.getId());
        List<AnsibleDeployApp> byUserId = ansibleDeployAppRepository.findByUserId(statusResponse.getId());
        if(byUserId.size() > 0) {
            deployedApp = byUserId.get(0);
        }
        if(statusResponse.getStatus().equals("Cloning Repository")) {
            status = "INITIATED";
        } else if(statusResponse.getStatus().equals("Building Quarkus project") ||
                (statusResponse.getStatus().equals("Deleting existing files"))) {
            status = "BUILDING";
        } else if(statusResponse.getStatus().equals("Deploying to Kubernetes")) {
            status = "DEPLOYING";
        } else if(statusResponse.getStatus().equals("Deployment completed")) {
            status = "COMPLETED";
        } else if (statusResponse.getStatus().contains("failed")) {
            status = "FAILED";
        }

        if(Objects.nonNull(statusResponse.getLog())) {
            System.out.println("Error Occurred in Build. Log is being shared!!!");
            errorLog = statusResponse.getLog().toString();
        }

        if(status.equals("FAILED") || status.equals("COMPLETED")) {
            if(Objects.nonNull(deployedApp)) {
                deployedApp.setErrorlog(errorLog);
                deployedApp.setEndtime(Date.from(Instant.now()));
            }

            if(status.equals("COMPLETED")) {
                BuildRequest appData = statusOfAnsibleBuild(statusResponse.getId());
                appsByNameService.updateAppData(statusResponse.getId(),"FINISHED",appData.getWorkspaceName(),appData.getMiniappName());
                //TODO:Uncomment when app version update is being deployed
                //updateVersion(deployedApp);
            }

        }
        if(Objects.nonNull(deployedApp)) {
            deployedApp.setStatus(status);
            ansibleDeployAppRepository.update(deployedApp);
        }

        return statusResponse;
    }

    private void updateVersion(AnsibleDeployApp deployedApp) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            File tempFile = new File(
                    repositoryFolderPath+deployedApp.getWorkspace() +"/"+ deployedApp.getMiniapp() +"/pom.xml");
            InputStream inputStream = new FileInputStream(tempFile);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            String versionpara = deployedApp.getVersion();
            versionpara = versionpara != null ? versionpara : null;

            //String version = updateElementValue(document, versionpara);
            this.writeXMLFile(document, tempFile);
           // System.out.println("New Version :" + version);
            inputStream.close();
           // appsByNameService.updateVersionByDeployment(version,deployedApp.getWorkspace(),deployedApp.getMiniapp());

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
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
}
