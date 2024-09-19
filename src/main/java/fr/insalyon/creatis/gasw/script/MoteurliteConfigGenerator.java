/* Copyright CNRS-CREATIS
 *
 * Rafael Ferreira da Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.rafaelsilva.com
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

 package fr.insalyon.creatis.gasw.script;

 import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fr.insalyon.creatis.gasw.GaswConfiguration;
import fr.insalyon.creatis.gasw.GaswConstants;
import fr.insalyon.creatis.gasw.GaswException;
import fr.insalyon.creatis.gasw.GaswInput;
import fr.insalyon.creatis.gasw.execution.GaswMinorStatusServiceGenerator;
 
 /**
  *
  * Author: Sandesh Patil [https://github.com/sandepat]
  *
  */
 public class MoteurliteConfigGenerator {
 
     private static final Logger logger = Logger.getLogger("fr.insalyon.creatis.gasw");
     private static MoteurliteConfigGenerator instance;
     private GaswConfiguration conf;
 
     public synchronized static MoteurliteConfigGenerator getInstance() throws GaswException {
         if (instance == null) {
             instance = new MoteurliteConfigGenerator();
         }
         return instance;
     }
 
     private MoteurliteConfigGenerator() throws GaswException {
         conf = GaswConfiguration.getInstance();
     }
 
     // Generates the configuration based on the input and minor status service
     public Map<String, String> generateConfig(GaswInput gaswInput, GaswMinorStatusServiceGenerator minorStatusService)
             throws IOException {
         Map<String, String> config = new HashMap<>();
         if (gaswInput.getExecutableName() != null) {
            String workflowFile = (gaswInput.getExecutableName().contains("."))
                    ? gaswInput.getExecutableName().substring(0, gaswInput.getExecutableName().lastIndexOf(".")) + ".json"
                    : "";
            // Extract and append the current date and time to the first upload URI
            URI uploadURI = gaswInput.getUploads() != null && !gaswInput.getUploads().isEmpty()
            ? URI.create(gaswInput.getUploads().get(0).getURI().toString() + "/" +
                new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date()) + "_" +
                gaswInput.getJobId().substring(0, gaswInput.getJobId().lastIndexOf(".")))
            : null;    
            String invocationJson = gaswInput.getJobId().substring(0, gaswInput.getJobId().lastIndexOf(".")) + "-invocation.json";
            String downloads = gaswInput.getDownloads().stream()
                .map(URI::toString)
                .collect(Collectors.joining(" "));
 
             config.put("minorStatusEnabled", String.valueOf(conf.isMinorStatusEnabled()));
             config.put("serviceCall", minorStatusService.getServiceCall());
             config.put("defaultEnvironment", conf.getDefaultEnvironment());
             config.put("voDefaultSE", conf.getVoDefaultSE());
             config.put("voUseCloseSE", conf.getVoUseCloseSE());
             config.put("boshCVMFSPath", conf.getBoshCVMFSPath());
             config.put("boutiquesProvenanceDir", conf.getBoutiquesProvenanceDir());
             config.put("containersCVMFSPath", conf.getContainersCVMFSPath());
             config.put("udockerTag", conf.getUdockerTag());
             config.put("simulationID", conf.getSimulationID());
             config.put("cacheDir", GaswConstants.CACHE_DIR);
             config.put("backgroundScript", conf.getDefaultBackgroundScript());
             config.put("nrep", String.valueOf(GaswConstants.numberOfReplicas));
             config.put("cacheFile", GaswConstants.CACHE_FILE);
             config.put("timeout", String.valueOf(GaswConstants.CONNECT_TIMEOUT));
             config.put("minAvgDownloadThroughput", String.valueOf(conf.getMinAvgDownloadThroughput()));
             config.put("bdiiTimeout", String.valueOf(GaswConstants.BDII_TIMEOUT));
             config.put("srmTimeout", String.valueOf(GaswConstants.SRM_TIMEOUT));
             config.put("downloads", downloads);
             config.put("workflowFile", workflowFile);
             config.put("uploadURI", uploadURI.toString());
             config.put("invocationJson", invocationJson);
         }
 
         for (String key : gaswInput.getEnvVariables().keySet()) {
             config.put(key, gaswInput.getEnvVariables().get(key));
         }
 
         return config;
     }
 }