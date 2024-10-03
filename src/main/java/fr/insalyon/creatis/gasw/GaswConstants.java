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
package fr.insalyon.creatis.gasw;

/**
 *
 * @author Rafael Ferreira da Silva
 */
public class GaswConstants {

    // Configuration File Labels
    public static final String LAB_DEFAULT_BACKGROUD_SCRIPT = "default.background.script";
    public static final String LAB_DEFAULT_CPUTIME = "default.cputime";
    public static final String LAB_DEFAULT_ENVIRONMENT = "default.environment";
    public static final String LAB_DEFAULT_EXECUTOR = "default.executor";
    public static final String LAB_DEFAULT_REQUIREMENTS = "default.requirements";
    public static final String LAB_DEFAULT_RETRY_COUNT = "default.retry.count";
    public static final String LAB_DEFAULT_SLEEPTIME = "default.sleeptime";
    public static final String LAB_DEFAULT_TIMEOUT = "default.timeout";
    public static final String LAB_FAILOVER_ENABLED = "failover.server.enabled";
    public static final String LAB_FAILOVER_HOME = "failover.server.home";
    public static final String LAB_FAILOVER_HOST = "failover.server.host";
    public static final String LAB_FAILOVER_PORT = "failover.server.port";
    public static final String LAB_FAILOVER_RETRY = "failover.max.retry";
    public static final String LAB_MINORSTATUS_ENABLED = "minorstatus.service.enabled";
    public static final String LAB_PLUGIN_DB = "plugin.db";
    public static final String LAB_PLUGIN_EXECUTOR = "plugin.executor";
    public static final String LAB_PLUGIN_LISTENER = "plugin.listener";
    public static final String LAB_VO_DEFAULT_SE = "vo.default.se";
    public static final String LAB_VO_NAME = "vo.name";
    public static final String LAB_VO_USE_CLOSE_SE = "vo.use.close.se";
    public static final String LAB_BOSH_CVMFS_PATH = "bosh.cvmfs.path";
    public static final String LAB_APPTAINER_PATH = "apptainer.path";
    public static final String LAB_CONTAINERS_CVMFS_PATH = "containers.cvmfs.path";
    public static final String LAB_UDOCKER_TAG = "udocker.tag";
    public static final String LAB_BOUTIQUES_PROV_DIR = "boutiques.provenance.dir";
    public static final String LAB_BOUTIQUES_FILE_NAME = "boutiques.filename";
    // timeouts used in lcg-c*
    //public static final int SEND_RECEIVE_TIMEOUT = 900;
    public static final String LAB_MIN_AVG_DOWNLOAD_THROUGHPUT = "min.avg.download.throughput";
    public static final int CONNECT_TIMEOUT = 10;
    public static final int BDII_TIMEOUT = 10;
    public static final int SRM_TIMEOUT = 30;
    // Directories
    public static final String SCRIPT_ROOT = "./sh";
    public static final String JDL_ROOT = "./jdl";
    public static final String OUT_ROOT = "./out";
    public static final String ERR_ROOT = "./err";
    public static final String PROVENANCE_ROOT = "./provenance";
    public static final String CACHE_DIR = "${BASEDIR}/cache";
    public static final String CACHE_FILE = "cache.txt";
    public static final String PROVENANCE_FILE = "provenance.json";
    // Extensions
    public static final String OUT_EXT = ".out";
    public static final String OUT_APP_EXT = ".app" + OUT_EXT;
    public static final String ERR_EXT = ".err";
    public static final String ERR_APP_EXT = ".app" + ERR_EXT;
    public static final String PROVENANCE_EXT = ".provenance.json";
    // Environment Variables
    public static final String ENV_EXECUTOR = "executor";
    // moteur-lite constants
    public static final int numberOfReplicas = 1;
    public static final String INVOCATION_DIR = "./inv";
    public static final String CONFIG_DIR = "./config";
}
