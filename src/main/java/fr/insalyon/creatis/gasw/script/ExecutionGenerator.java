/* Copyright CNRS-CREATIS
 *
 * Rafael Ferreira da Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.rafaelsilva.com
 *
 * This software is governed by the CeCILL  license under French law and
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

import java.net.URI;
import java.util.List;
import java.util.Map;

import fr.insalyon.creatis.gasw.GaswConfiguration;
import fr.insalyon.creatis.gasw.GaswConstants;
import fr.insalyon.creatis.gasw.GaswException;
import fr.insalyon.creatis.gasw.GaswUpload;
import fr.insalyon.creatis.gasw.util.VelocityUtil;

/**
 *
 * @author Rafael Ferreira da Silva
 */
public class ExecutionGenerator {

    private static ExecutionGenerator instance;
    private GaswConfiguration conf;

    public static ExecutionGenerator getInstance() throws GaswException {
        if (instance == null) {
            instance = new ExecutionGenerator();
        }
        return instance;
    }

    private ExecutionGenerator() throws GaswException {

        conf = GaswConfiguration.getInstance();
    }

    /**
     * Generates the job header (function declarations and variable settings).
     *
     * @param serviceCall
     * @return A string containing the header
     * @throws Exception
     */
    public String loadHeader(String serviceCall) throws Exception {

        VelocityUtil velocity = new VelocityUtil("vm/script/execution/header.vm");

        velocity.put("minorStatusEnabled", conf.isMinorStatusEnabled());
        velocity.put("serviceCall", serviceCall);
        velocity.put("defaultEnvironment", conf.getDefaultEnvironment());
        velocity.put("voDefaultSE", conf.getVoDefaultSE());
        velocity.put("voUseCloseSE", conf.getVoUseCloseSE());
        velocity.put("boshCVMFSPath", conf.getBoshCVMFSPath());
        velocity.put("boutiquesProvenanceDir", conf.getBoutiquesProvenanceDir());
        velocity.put("containersCVMFSPath", conf.getContainersCVMFSPath());
        velocity.put("udockerTag", conf.getUdockerTag());
        velocity.put("simulationID", conf.getSimulationID());

        return velocity.merge().toString();
    }

    /**
     * Prints code printing the host configuration.
     *
     * @return A String containing the code
     * @throws Exception
     */
    public String loadHostConfiguration() throws Exception {

        VelocityUtil velocity = new VelocityUtil("vm/script/execution/hostConfiguration.vm");

        velocity.put("cacheDir", GaswConstants.CACHE_DIR);

        return velocity.merge().toString();
    }

    /**
     * Returns the code downloading and launching the background script.
     *
     * @param serviceCall
     * @return A String containing the code
     * @throws Exception
     */
    public String loadBackgroundScript(String serviceCall) throws Exception {

        if (!conf.getDefaultBackgroundScript().isEmpty()) {

            VelocityUtil velocity = new VelocityUtil("vm/script/execution/backgroundScript.vm");

            velocity.put("minorStatusEnabled", conf.isMinorStatusEnabled());
            velocity.put("serviceCall", serviceCall);
            velocity.put("backgroundScript", conf.getDefaultBackgroundScript());

            return velocity.merge().toString();
        }
        return "";
    }

    /**
     * Generates the code to perform an upload test before the job is executed.
     *
     * @param uploads The list of URIs to be uploaded
     * @return The code, in a String
     * @throws Exception
     */
    public String loadUploadTest(List<GaswUpload> uploads) throws Exception {

        if (!uploads.isEmpty()) {

            GaswUpload upload = uploads.get(0);
            VelocityUtil velocity = new VelocityUtil("vm/script/execution/uploadTest.vm");

            velocity.put("cacheDir", GaswConstants.CACHE_DIR);
            velocity.put("uri", upload.getURI().toString());
            velocity.put("nrep", upload.getNumberOfReplicas());

            return velocity.merge().toString();
        }
        return "";
    }

    /**
     * Generates the code to set application custom environment.
     *
     * @param release
     * @return A string containing the code
     * @throws Exception
     */
    public String loadApplicationEnvironment(Map<String, String> envVariables) throws Exception {

        VelocityUtil velocity = new VelocityUtil("vm/script/execution/variables.vm");

        velocity.put("variables", envVariables);

        return velocity.merge().toString();
    }

    /**
     * Generates the code to download all the inputs.
     *
     * @param serviceCall
     * @param downloads The list of URIs to be downloaded
     * @return A string containing the code
     * @throws Exception
     */
    public String loadInputs(String serviceCall, List<URI> downloads) throws Exception {

        VelocityUtil velocity = new VelocityUtil("vm/script/execution/inputs.vm");

        velocity.put("minorStatusEnabled", conf.isMinorStatusEnabled());
        velocity.put("serviceCall", serviceCall);
        velocity.put("downloads", downloads);

        return velocity.merge().toString();
    }

    /**
     * Generates the code executing the application command line.
     *
     * @param serviceCall
     * @param parameters List of parameters
     * @return
     * @throws Exception
     */
    public String loadApplicationExecution(String serviceCall,
            String executableName, List<String> parameters) throws Exception {

        VelocityUtil velocity = new VelocityUtil("vm/script/execution/execution.vm");

        velocity.put("minorStatusEnabled", conf.isMinorStatusEnabled());
        velocity.put("serviceCall", serviceCall);
        velocity.put("executableName", executableName);
        velocity.put("params", parameters);

        return velocity.merge().toString();
    }

    /**
     * Generates the code to upload the results.
     *
     * @param serviceCall
     * @param Uploads the list of URIs to be uploaded
     * @return A string containing the code
     * @throws Exception
     */
    public String loadResultsUpload(String serviceCall, List<GaswUpload> uploads) 
            throws Exception {

        VelocityUtil velocity = new VelocityUtil("vm/script/execution/result.vm");

        velocity.put("minorStatusEnabled", conf.isMinorStatusEnabled());
        velocity.put("serviceCall", serviceCall);
        velocity.put("uploads", uploads);
        
        return velocity.merge().toString();
    }

    /**
     * Generates the job footer.
     *
     * @return A string containing the footer
     * @throws Exception
     */
    public String loadFooter(String serviceCall) throws Exception {

        VelocityUtil velocity = new VelocityUtil("vm/script/execution/footer.vm");

        velocity.put("minorStatusEnabled", conf.isMinorStatusEnabled());
        velocity.put("serviceCall", serviceCall);

        return velocity.merge().toString();
    }
}