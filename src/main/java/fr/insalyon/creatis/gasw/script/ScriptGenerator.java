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

import java.io.IOException;

import org.apache.log4j.Logger;

import fr.insalyon.creatis.gasw.GaswConfiguration;
import fr.insalyon.creatis.gasw.GaswException;
import fr.insalyon.creatis.gasw.GaswInput;
import fr.insalyon.creatis.gasw.execution.GaswMinorStatusServiceGenerator;

/**
 *
 * @author Rafael Ferreira da Silva, Tristan Glatard
 */
public class ScriptGenerator {

    private static final Logger logger = Logger.getLogger("fr.insalyon.creatis.gasw");
    private static ScriptGenerator instance;
    private BasicGenerator basic;
    private DataManagementGenerator dataManagement;
    private ExecutionGenerator execution;

    public synchronized static ScriptGenerator getInstance() throws GaswException {

        if (instance == null) {
            instance = new ScriptGenerator();
        }
        return instance;
    }

    private ScriptGenerator() throws GaswException {

        basic = BasicGenerator.getInstance();
        dataManagement = DataManagementGenerator.getInstance();
        execution = ExecutionGenerator.getInstance();
    }

    /**
     * Generates the complete bash script for a job.
     *
     * @param downloads
     * @param uploads
     * @param command
     * @param regexs List of regular expressions to match with results
     * @param defaultDir Default directory to store files matched against regexp
     * @param parameters
     * @param minorStatusService
     * @return A string containing the bash script source
     * @throws GaswException 
     * @throws IOException 
     */
    public String generateScript(GaswInput gaswInput, 
            GaswMinorStatusServiceGenerator minorStatusService) throws IOException, GaswException {

        StringBuilder sb = new StringBuilder();

        try {
            sb.append("#!/bin/bash -l\n\n");
            sb.append(basic.loadLogFunctions());
            sb.append(basic.loadCleanupFunction());
            sb.append(dataManagement.loadCheckCacheDownloadAndCacheLFNFunction());
            sb.append(dataManagement.loadRefreshTokenFunctions());
            sb.append(dataManagement.loadDownloadFunctions());
            sb.append(dataManagement.loadAddToCacheFunction());
            sb.append(dataManagement.loadAddToFailOverFunction());
            sb.append(dataManagement.loadUploadFunctions());
            sb.append(dataManagement.loadDeleteFunctions());

            // Minor Status Service
            if (GaswConfiguration.getInstance().isMinorStatusEnabled()
                    && minorStatusService.getClient() != null) {
                sb.append(minorStatusService.getClient());
            }

            sb.append(execution.loadHeader(minorStatusService.getServiceCall()));
            sb.append(execution.loadHostConfiguration());
            sb.append(execution.loadBackgroundScript(minorStatusService.getServiceCall()));
            sb.append(execution.loadUploadTest(gaswInput.getUploads()));
            sb.append(execution.loadInputs(minorStatusService.getServiceCall(), gaswInput.getDownloads()));
            sb.append(execution.loadApplicationEnvironment(gaswInput.getEnvVariables()));
            sb.append(execution.loadApplicationExecution(minorStatusService.getServiceCall(), gaswInput.getExecutableName(), gaswInput.getParameters()));
            sb.append(execution.loadResultsUpload(minorStatusService.getServiceCall(), gaswInput.getUploads()));
            sb.append(execution.loadFooter(minorStatusService.getServiceCall()));

        } catch (Exception ex) {
            logger.error(ex);
        }
        return sb.toString();
    }
}