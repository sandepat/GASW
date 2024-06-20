package fr.insalyon.creatis.gasw.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.hibernate.SessionFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import fr.insalyon.creatis.gasw.GaswConfiguration;
import fr.insalyon.creatis.gasw.GaswConstants;
import fr.insalyon.creatis.gasw.GaswException;
import fr.insalyon.creatis.gasw.GaswUpload;
import fr.insalyon.creatis.gasw.plugin.ExecutorPlugin;
import fr.insalyon.creatis.gasw.plugin.ListenerPlugin;

/**
 * 
 * Author: Sandesh Patil [https://github.com/sandepat]
 * 
 */

public class JsonConfigurationFile {

    public static Path createJsonConfiguration(String jobId) throws IOException {
        // Specify the file path
        File confDir = new File(GaswConstants.CONFIG_DIR);
        if (!confDir.exists()) {
            confDir.mkdir();
        }
        jobId = jobId.endsWith(".sh") ? jobId.substring(0, jobId.length() - 3) : jobId;
        Path jsonConfigurationFile = Paths.get(confDir + "/"+ jobId+"-configuration.json");
       
        // Check if the file exists, create it if not
        if (!Files.exists(jsonConfigurationFile)) {
            Files.createFile(jsonConfigurationFile);
        }
        return jsonConfigurationFile;
    }

    public static void appendJobConfiguration(String serviceCall, List<URI> downloads, String executableName,
    String invocationString, Map<String, String> envVariables, List<String> parameters, List<GaswUpload> uploads, String jobId, String applicationName, List<URI> DownloadFiles, String outputDirName) throws IOException, GaswException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();       

        if (executableName != null) {
            String jsonFileName = (executableName.contains(".")) ? executableName.substring(0, executableName.lastIndexOf(".")) + ".json" : "";
            List<URI> uploadUris = (uploads != null) ? uploads.stream().map(GaswUpload::getURI).collect(Collectors.toList()) : new ArrayList<>();
            String invocationJson = jobId.substring(0, jobId.lastIndexOf(".")) + "-invocation.json";

            applicationName = (applicationName != null) ? applicationName : ""; 
            parameters = (parameters != null) ? parameters : new ArrayList<>();
            envVariables = (envVariables != null) ? envVariables : new HashMap<>();
            serviceCall = (serviceCall != null) ? serviceCall : "";
            invocationString = (invocationString != null) ? invocationString : "";
            jsonFileName = (jsonFileName != null) ? jsonFileName : "";

            Set<URI> uniqueDownloads = new HashSet<>(downloads);
            downloads = new ArrayList<>(uniqueDownloads);
            
            Set<URI> uniqueDownloadFiles = new HashSet<>(DownloadFiles);
            DownloadFiles = new ArrayList<>(uniqueDownloadFiles);
                        
            jsonObject.addProperty("applicationName", applicationName);
            jsonObject.addProperty("jsonFileName", jsonFileName);
            jsonObject.addProperty("jobId", jobId);
            jsonObject.addProperty("invocationJson", invocationJson);
            jsonObject.addProperty("serviceCall", serviceCall);
            jsonObject.addProperty("downloads", downloads.toString());
            jsonObject.addProperty("invocationString", invocationString);
            jsonObject.addProperty("envVariables", envVariables.toString());
            jsonObject.addProperty("parameters", parameters.toString());
            jsonObject.addProperty("uploads", uploadUris.toString());
            jsonObject.addProperty("downloadFiles", DownloadFiles.toString());
            jsonObject.addProperty("outputDirName", outputDirName);

            JsonObject masterJsonObject = new JsonObject();
            masterJsonObject.add("jobConfiguration", jsonObject);

            Path jsonConfigurationFile = createJsonConfiguration(jobId);
            try (FileWriter fileWriter = new FileWriter(jsonConfigurationFile.toFile())) {
                gson.toJson(masterJsonObject, fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void appendGaswConstants(JsonObject gaswConstantsObj, String jobId) throws IOException {
        Path jsonConfigurationFile = createJsonConfiguration(jobId);
        String existingContent = Files.readString(jsonConfigurationFile);

        // Parse existing JSON content
        JsonObject existingJsonObject = new Gson().fromJson(existingContent, JsonObject.class);

        // Add gaswConstantsObj to the existing JSON object
        existingJsonObject.add("gaswConstants", gaswConstantsObj);

        // Convert the JSON object back to a string with proper formatting
        String updatedContent = new GsonBuilder().setPrettyPrinting().create().toJson(existingJsonObject);

        // Write updated JSON content to file
        try (FileWriter fileWriter = new FileWriter(jsonConfigurationFile.toFile())) {
            fileWriter.write(updatedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void appendGaswConfigurations(String jobId) throws GaswException, IOException {
        GaswConfiguration gaswConfig = GaswConfiguration.getInstance();
        PropertiesConfiguration config = gaswConfig.getPropertiesConfiguration();
        List<ExecutorPlugin> executorPlugins = gaswConfig.getExecutorPlugins();
        List<ListenerPlugin> listenerPlugins = gaswConfig.getListenerPlugins();
        SessionFactory sessionFactory = gaswConfig.getSessionFactory();
        int defaultSleeptime = gaswConfig.getDefaultSleeptime();
        String simulationID = gaswConfig.getSimulationID();
        String workflowID = gaswConfig.getWorkflowID();
        String executionPath = gaswConfig.getExecutionPath();
        String defaultBackgroundScript = gaswConfig.getDefaultBackgroundScript();
        int defaultCPUTime = gaswConfig.getDefaultCPUTime();
        String defaultEnvironment = gaswConfig.getDefaultEnvironment();
        String defaultExecutor = gaswConfig.getDefaultExecutor();
        String voDefaultSE = gaswConfig.getVoDefaultSE();
        String voUseCloseSE = gaswConfig.getVoUseCloseSE();
        String boshCVMFSPath = gaswConfig.getBoshCVMFSPath();
        String containersCVMFSPath = gaswConfig.getContainersCVMFSPath();
        String udockerTag = gaswConfig.getUdockerTag();
        boolean failOverEnabled = gaswConfig.isFailOverEnabled();
        String failOverHome = gaswConfig.getFailOverHome();
        String failOverHost = gaswConfig.getFailOverHost();
        int failOverMaxRetry = gaswConfig.getFailOverMaxRetry();
        int failOverPort = gaswConfig.getFailOverPort();
        boolean minorStatusEnabled = gaswConfig.isMinorStatusEnabled();
        int minAvgDownloadThroughput = gaswConfig.getMinAvgDownloadThroughput();
        int defaultRetryCount = gaswConfig.getDefaultRetryCount();
        String apptainerPath = gaswConfig.getApptainerPath();
        String boutiquesProvenanceDir = gaswConfig.getBoutiquesProvenanceDir();

        Path jsonConfigurationFile = createJsonConfiguration(jobId);
        String existingContent = Files.readString(jsonConfigurationFile);

        // Parse existing JSON content
        JsonObject jsonObject = new Gson().fromJson(existingContent, JsonObject.class);

        // Create the gaswConfiguration object
        JsonObject gaswConfiguration = new JsonObject();
        gaswConfiguration.addProperty("defaultSleeptime", defaultSleeptime);
        gaswConfiguration.addProperty("simulationID", simulationID);
        gaswConfiguration.addProperty("workflowID", workflowID);
        gaswConfiguration.addProperty("executionPath", executionPath);
        gaswConfiguration.addProperty("defaultBackgroundScript", defaultBackgroundScript);
        gaswConfiguration.addProperty("defaultCPUTime", defaultCPUTime);
        gaswConfiguration.addProperty("defaultEnvironment", defaultEnvironment);
        gaswConfiguration.addProperty("defaultExecutor", defaultExecutor);
        gaswConfiguration.addProperty("voDefaultSE", voDefaultSE);
        gaswConfiguration.addProperty("voUseCloseSE", voUseCloseSE);
        gaswConfiguration.addProperty("boshCVMFSPath", boshCVMFSPath);
        gaswConfiguration.addProperty("containersCVMFSPath", containersCVMFSPath);
        gaswConfiguration.addProperty("udockerTag", udockerTag);
        gaswConfiguration.addProperty("failOverEnabled", failOverEnabled);
        gaswConfiguration.addProperty("failOverHost", failOverHost);
        gaswConfiguration.addProperty("failOverPort", failOverPort);
        gaswConfiguration.addProperty("failOverHome", failOverHome);
        gaswConfiguration.addProperty("minorStatusEnabled", minorStatusEnabled);
        gaswConfiguration.addProperty("minAvgDownloadThroughput", minAvgDownloadThroughput);
        gaswConfiguration.addProperty("defaultRetryCount", defaultRetryCount);
        gaswConfiguration.addProperty("apptainerPath", apptainerPath);
        gaswConfiguration.addProperty("boutiquesProvenanceDir", boutiquesProvenanceDir);
        gaswConfiguration.addProperty("defaultExecutor", defaultExecutor);
        gaswConfiguration.addProperty("executorPlugins", executorPlugins.toString());
        gaswConfiguration.addProperty("listenerPlugins", listenerPlugins.toString());
        gaswConfiguration.addProperty("sessionFactory", sessionFactory.toString());
        gaswConfiguration.addProperty("failOverMaxRetry", failOverMaxRetry);
        gaswConfiguration.addProperty("config", config.toString());

        // Add gaswConfiguration to the master object
        jsonObject.add("gaswConfiguration", gaswConfiguration);

        String updatedContent = new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);

        try (FileWriter fileWriter = new FileWriter(jsonConfigurationFile.toFile())) {
            fileWriter.write(updatedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


