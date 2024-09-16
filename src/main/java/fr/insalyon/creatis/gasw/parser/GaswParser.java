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
package fr.insalyon.creatis.gasw.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.insalyon.creatis.gasw.GaswConstants;
import fr.insalyon.creatis.gasw.GaswException;
import fr.insalyon.creatis.gasw.GaswInput;
import fr.insalyon.creatis.gasw.GaswUpload;
import fr.insalyon.creatis.gasw.GaswUtil;

/**
 *
 * @author Rafael Ferreira da Silva
 */
public class GaswParser extends DefaultHandler {

    private static final Logger logger = Logger.getLogger("fr.insalyon.creatis.gasw");

    private XMLReader reader;
    private boolean parsing;
    private boolean parsingSandbox;
    private String executableName;
    private List<URI> downloads;
    private Map<String, String> gaswVariables;
    private Map<String, String> envVariables;
    private List<GaswArgument> arguments;
    private GaswInputArg inputArg;
    private GaswOutputArg outputArg;
    private List<String> inputsList;

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final String LFN_PREFIX = "lfn://";
    private static final String FILE_PREFIX = "file:/";

    public GaswParser() throws GaswException {

        this.parsing = false;
        this.parsingSandbox = false;
        this.downloads = new ArrayList<URI>();
        this.gaswVariables = new HashMap<String, String>();
        this.envVariables = new HashMap<String, String>();
        this.arguments = new ArrayList<GaswArgument>();
        this.inputsList = new ArrayList<String>();
    }

    public void parse(String descriptorFileName) throws GaswException {

        try {
            File descriptor = new File(descriptorFileName);
            logger.info("Parsing GASW descriptor: " + descriptor.getAbsolutePath());
            reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(this);
            reader.parse(new InputSource(new FileReader(descriptor)));

        } catch (IOException ex) {
            logger.error(ex);
            throw new GaswException(ex);
        } catch (SAXException ex) {
            logger.error(ex);
            throw new GaswException(ex);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("description")) {
            if (parsing) {
                throw new SAXException("Nested <description> tags.");
            }
            parsing = true;

        } else if (localName.equals("executable")) {
            if (executableName != null) {
                throw new SAXException("Nested <executable> tags.");
            }
            executableName = getAttributeValue(attributes, "name", "No executable name defined.");

        } else if (localName.equals("value") && !parsingSandbox) {
            try {
                String value = getAttributeValue(attributes, "value", "No value defined.");
                if (value.contains("lfn:/")) {
                    value = LFN_PREFIX + new URI(value + ".tar.gz").getPath();
                }else if(value.contains("file:/")){
                    value = FILE_PREFIX + new URI(value + ".tar.gz").getPath();
                }else {
                    value = LFN_PREFIX + value + ".tar.gz";
                }
                downloads.add(new URI(value));

            } catch (URISyntaxException ex) {
                throw new SAXException(ex);
            }

        } else if (localName.equals("nodeNumber")) {
            String value = getAttributeValue(attributes, "value", "No value defined.");
            gaswVariables.put("nodeNumber", value);
            gaswVariables.put(GaswConstants.ENV_EXECUTOR, "gLite");

        } else if (localName.equals("executor")) {
            String value = getAttributeValue(attributes, "value", "No value defined.");
            gaswVariables.put(GaswConstants.ENV_EXECUTOR, value);

        } else if (localName.equals("requirement")) {
            String value = getAttributeValue(attributes, "value", "No value defined.");

            if (value.toLowerCase().startsWith("dirac")) {
                String[] v = value.split(":");
                envVariables.put(v[0], v[1]);

            } else {
                envVariables.put("gLiteRequirement", value);
            }

        } else if (localName.equals("input")) {
            String name = getAttributeValue(attributes, "name", "No input name defined.");
            String option = getAttributeValue(attributes, "option", "No input option defined.");

            boolean implicit = false;
            if (option.startsWith("no")) {
                option = null;

            } else if (option.startsWith("invisible")) {
                option = null;
                implicit = true;
            }
            inputArg = new GaswInputArg(name, option, implicit);
            arguments.add(inputArg);
            inputsList.add(name);

        } else if (localName.equals("access")) {

            String type = getAttributeValue(attributes, "type", "No access type defined.");
            if (inputArg != null && (type.equals("LFN") || type.equals("URI"))) {
                inputArg.setType(GaswArgument.Type.URI);
            }

        } else if (localName.equals("output")) {
            String name = getAttributeValue(attributes, "name", "No input name defined.");
            String option = attributes.getValue("option");

            boolean implicit = option.startsWith("invisible");

            if (option.startsWith("no")) {
                option = null;
            }

            outputArg = new GaswOutputArg(name, option, implicit);
            arguments.add(outputArg);

        } else if (localName.equals("template")) {
            if (outputArg == null) {
                throw new SAXException("<template> tags are just allowed inside <output> tags.");
            }
            String value = getAttributeValue(attributes, "value", "No template value defined.");
            String stripExtns = attributes.getValue("strip-extensions");
            Set<String> stripExtensions=new HashSet<>();
            if(stripExtns!=null) {
                String[] stripExtnArr = stripExtns.split(",");
                for(String str : stripExtnArr) {
                    stripExtensions.add(str.trim());
                }
            }
            outputArg.setTemplate(true);

            if (value.contains("$rep-")) {
                outputArg.setReplicas(new Integer(value.substring(value.lastIndexOf("-") + 1)));
                value = value.replaceAll("\\$rep-[0-9]*", "");
            }

            outputArg.setTemplateParts(templateParts(value, inputsList, stripExtensions));
        } else if (localName.equals("sandbox")) {
            parsingSandbox = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (localName.equals("input")) {
            inputArg = null;

        } else if (localName.equals("output")) {
            outputArg = null;

        } else if (localName.equals("sandbox")) {
            parsingSandbox = false;
        }
    }

    private String getAttributeValue(Attributes attributes, String valueName,
            String errorMessage) throws SAXException {

        String attributeValue = attributes.getValue(valueName);
        if (attributeValue == null || attributeValue.length() == 0) {
            throw new SAXException(errorMessage);
        }
        return attributeValue;
    }

    public String getInputName(int index) {
        return inputsList.get(index);
    }

    /**
     *
     * @param inputsMap
     * @return
     */
    public GaswInput getGaswInput(Map<String, String> inputsMap)
            throws URISyntaxException {


        List<String> parameters = new ArrayList<String>();
        List<GaswUpload> uploads = new ArrayList<GaswUpload>();

        for (GaswArgument argument : arguments) {

            StringBuilder param = new StringBuilder();

            if (argument.getOption() != null) {
                param.append(argument.getOption());
                param.append(" ");
            }

            if (argument.getHookup() == GaswArgument.Hookup.Input) {
                String value = inputsMap.get(argument.getName());
                if (argument.getType() == GaswArgument.Type.URI) {
                    // If the value already is a URI, use it as is.  If not, it
                    // is a lfn and the prefix is added.
                    URI valueURI = new URI(
                        GaswUtil.isUri(value) ? value : LFN_PREFIX + value);
                    param.append(new File(valueURI.getPath()).getName());
                    downloads.add(valueURI);
                } else {
                    // Need to escape special characters to avoid bash errors.
                    param.append(escapeSpecialBashCharacters(value));
                }

            } else {
                GaswOutputArg output = (GaswOutputArg) argument;
                String value = parseOutputTemplate(
                    output.getTemplateParts(), inputsMap);
                // If the value already is a URI, use it as is.  If not, it is a
                // lfn and the prefix is added.
                URI valueURI = new URI(
                    GaswUtil.isUri(value) ? value : LFN_PREFIX + value);
                uploads.add(new GaswUpload(valueURI, output.getReplicas()));
                param.append(new File(valueURI.getPath()).getName());
            }

            if (!argument.isImplicit()) {
                parameters.add(param.toString());
            }
        }

        return new GaswInput(executableName, parameters, downloads, uploads,
                gaswVariables, envVariables);
    }

    static String escapeSpecialBashCharacters(String stringToEscape) {
        // The \ char must be in the first place, so that the \ included by
        // following replacements are not touched.
        // For the list of characters, see:
        // https://stackoverflow.com/questions/19177076/list-of-characters-which-needs-to-be-escaped-in-a-linux-shell-command/19177228#19177228
        String[] specialChars =
            {"\\", "|", "&", ";", "<", ">", "(", ")",
             "$", "`", "\"", "'", " ", "\t", "\n"};
        return Arrays.stream(specialChars)
            .reduce(stringToEscape, (string, s) -> string.replace(s, "\\" + s));
    }

    static List<GaswOutputTemplatePart> templateParts(
        String value, List<String> inputsList,Set<String> stripExtensions) throws SAXException {

        // $dirX/$naX is treated as a special case, because if $na1 is an empty
        // string, the / should not be inserted.
        Pattern p = Pattern.compile("\\$dir(\\d+)/\\$na\\1");  // the "\\1" at the end references the first group
        Matcher m = p.matcher(value);
        List<GaswOutputTemplatePart> list;
        if (m.find()) {
            // everything before the dirX/naX is treated normally, the dirX/naX is added as DIR_AND_NAME,
            // and then it looks again for dirX/naX recursively 
            list =
                templateSimpleParts(value.substring(0, m.start()), inputsList, stripExtensions);
            int n = Integer.parseInt(m.group(1));
            list.add(new GaswOutputTemplatePart(
                         GaswOutputTemplateType.DIR_AND_NAME,
                         inputsList.get(n - 1),stripExtensions));
            list.addAll(templateParts(
                            value.substring(m.end()),
                            inputsList,stripExtensions));
        } else {
            list = templateSimpleParts(value, inputsList, stripExtensions);
        }
        return list;
    }

    static List<GaswOutputTemplatePart> templateSimpleParts(
        String value, List<String> inputsList, Set<String> stripExtensions) throws SAXException {

        LinkedList<GaswOutputTemplatePart> list = new LinkedList<>();
        Pattern p = Pattern.compile("\\$(prefix|dir|na|options)(\\d+)");
        Matcher m = p.matcher(value);
        int start = 0;
        while (m.find()) {
            if (m.start() > start) {
                list.addLast(new GaswOutputTemplatePart(
                                 GaswOutputTemplateType.STRING,
                                 value.substring(start, m.start()),stripExtensions));
            }
            GaswOutputTemplateType type = null;
            int n = Integer.parseInt(m.group(2));
            switch (m.group(1)) {
            case "prefix":
                type = GaswOutputTemplateType.PREFIX;
                break;
            case "dir":
                type = GaswOutputTemplateType.DIR;
                break;
            case "na":
                type = GaswOutputTemplateType.NAME;
                break;
            case "options":
                type = GaswOutputTemplateType.OPTIONS;
                break;
            default:
                throw new IllegalArgumentException(
                    "Unhandled type: " + m.group(1));
            }
            try {
                list.addLast(
                    new GaswOutputTemplatePart(type, inputsList.get(n - 1),stripExtensions));
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new SAXException(
                    "The index used in the output template does not exist.");
            }

            start = m.end();
        }
        if (value.length() > start) {
            list.addLast(new GaswOutputTemplatePart(
                             GaswOutputTemplateType.STRING,
                             value.substring(start),stripExtensions));
        }

        return list;
    }

    static String parseOutputTemplate(
        List<GaswOutputTemplatePart> output,
        Map<String, String> inputsMap) {

        StringBuilder content = new StringBuilder();

        for (GaswOutputTemplatePart part : output) {
            try {
                switch(part.getType()) {
                case STRING:
                    content.append(part.getValue());
                    break;
                case PREFIX:
                {
                    URI u = new URI(inputsMap.get(part.getValue()));
                    String scheme = u.getScheme();
                    if (scheme != null) {
                        content.append(scheme).append(":");
                    }
                    String authority = u.getAuthority();
                    if (authority != null) {
                        content.append("//").append(authority);
                    }
                }
                break;
                case DIR_AND_NAME:
                {
                    addDir(inputsMap.get(part.getValue()), content);
                    addName("/", inputsMap.get(part.getValue()), content);
                }
                break;
                case DIR:
                {
                    addDir(inputsMap.get(part.getValue()), content);
                }
                break;
                case NAME:
                {
                	String inputValue = inputsMap.get(part.getValue());
                	if (part.getStripExtensions()!=null) {
                		for (String extn: part.getStripExtensions()) {
                    		if (inputValue.endsWith(extn)) {
                                inputValue=inputValue.substring(0, inputValue.length() - extn.length());;
                    		}
                    	}
                	}
                    addName("", inputValue, content);
                }
                break;
                case OPTIONS:
                {
                    URI u = new URI(inputsMap.get(part.getValue()));
                    String query = u.getQuery();
                    if (query != null) {
                        content.append('?').append(query);
                    }
                }
                break;
                default:
                    throw new IllegalArgumentException(
                        "Unhandled type: " + part.getType());
                }
            } catch (URISyntaxException ex) {
                content.append(inputsMap.get(part.getValue()));
            }
        }

        return content.toString();
    }

    private static void addDir(String s, StringBuilder content)
        throws URISyntaxException {

        URI u = new URI(s);
        File f = new File(u.getPath());
        String dir = f.getParent();
        if (dir != null && !dir.equals("/")) {
            content.append(dir);
        }
    }

    private static void addName(
        String separator, String s, StringBuilder content)
        throws URISyntaxException {

        URI u = new URI(s);
        File f = new File(u.getPath());
        if (f.getName().length() > 0) {
            content.append(separator).append(f.getName());
        }
    }

    public GaswInput getGaswInput(String applicationName, Map<String, String> inputsMap, String executableName,HashMap<Integer, String> inputid , HashMap<Integer, String> outputid, String invocationString, 
    Map<String, String> resultDirectory, String jobId, List<URI> DownloadFiles, String boutiquesFilePath)
            throws URISyntaxException, FileNotFoundException, IOException, GaswException, SAXException {

        getArgument(executableName, inputid, outputid);
        List<String> parameters = new ArrayList<>();
        List<GaswUpload> uploads = new ArrayList<>();

        URI boutiquesFilePathURI = new URI(boutiquesFilePath);
        //DownloadFiles.add(boutiquesFilePathURI);

        for (GaswArgument argument : arguments) {
            StringBuilder param = new StringBuilder();
            if (argument.getOption() != null) {
                param.append(argument.getOption());
                param.append(" ");
            }
            if (argument.getHookup() == GaswArgument.Hookup.Input) {
                String value = inputsMap.get(argument.getName());
                logger.info("inputMaps:" + inputsMap);
                logger.info("Value:" + value);
                if (argument.getType() == GaswArgument.Type.URI) {
                    // If the value already is a URI, use it as is.  If not, it
                    // is a lfn and the prefix is added.
                    URI valueURI = new URI(
                        GaswUtil.isUri(value) ? value : LFN_PREFIX + value);
                    param.append(new File(valueURI.getPath()).getName());
                    downloads.add(valueURI);
                } else {
                    // Need to escape special characters to avoid bash errors.
                    param.append(escapeSpecialBashCharacters(value));
                }

            } else {
                //System.out.println(ANSI_BLUE + "inside Else " +ANSI_RESET);
                GaswOutputArg output = (GaswOutputArg) argument;
                String value = parseOutputTemplate(
                    output.getTemplateParts(), inputsMap);
                // If the value already is a URI, use it as is.  If not, it is a
                // lfn and the prefix is added.
                //System.out.println(ANSI_GREEN + "value: " + value +ANSI_RESET);
                URI valueURI = new URI(
                    GaswUtil.isUri(value) ? value : LFN_PREFIX + value);
                //System.out.println(ANSI_BLUE + "valueURI: " +valueURI +ANSI_RESET);
                uploads.add(new GaswUpload(valueURI, output.getReplicas()));
                //System.out.println(ANSI_GREEN + "uploads: " + uploads.getURI() +ANSI_RESET);
                param.append(new File(valueURI.getPath()).getName());
            }

            if (!argument.isImplicit()) {
                parameters.add(param.toString());
            }
        }
        return new GaswInput(applicationName, executableName, parameters, downloads, uploads,
                gaswVariables, envVariables, invocationString, jobId, DownloadFiles);
    }

        public String getArgument(String executableName, HashMap<Integer, String> inputid, HashMap<Integer, String> outputid) 
                throws FileNotFoundException, IOException, GaswException, URISyntaxException, SAXException {

            // Check the value of LAB_DEFAULT_EXECUTOR
            String executorType = GaswConstants.LAB_DEFAULT_EXECUTOR;
            String download;

            // Check if the executor is DIRAC or local
            if ("DIRAC".equalsIgnoreCase(executorType)) {
                // If executor is DIRAC, construct the download string using the lfn prefix
                download = "lfn:/" + System.getProperty("user.dir") + "/" + executableName;
            } else {
                // If executor is local, use the executable name directly and make it a symlink
                String workflowFile = System.getProperty("user.dir") + "/workflow.json";
                download = "file:/" + workflowFile;

                // Create a symlink for the executableName pointing to the workflow.json file
                File symlink = new File(System.getProperty("user.dir"), executableName);
                File targetFile = new File(workflowFile);
                if (!symlink.exists()) {
                    Files.createSymbolicLink(symlink.toPath(), targetFile.toPath());
                }
            }

            // Add the constructed download URI to the downloads list
            downloads.add(new URI(download.replace("]", "").replace("[", "")));

            inputArg = new GaswInputArg("results-directory", null, false);
            arguments.add(inputArg);
            inputsList.add("results-directory");

            for (int i = 0; i < inputid.size(); i++) {
                inputArg = new GaswInputArg(inputid.get(i), "--" + inputid.get(i), false);
                arguments.add(inputArg);
                inputsList.add(inputid.get(i));
            }

            for (int i = 0; i < outputid.size(); i++) {
                outputArg = new GaswOutputArg(outputid.get(i), "--" + outputid.get(i), false);
                String templateValue = "$prefix1$dir1/$na1";

                Set<String> stripExtensions = new HashSet<>();
                outputArg.setTemplate(true);

                if (templateValue.contains("$rep-")) {
                    outputArg.setReplicas(new Integer(templateValue.substring(templateValue.lastIndexOf("-") + 1)));
                    templateValue = templateValue.replaceAll("\\$rep-[0-9]*", "");
                }

                outputArg.setTemplateParts(templateParts(templateValue, inputsList, stripExtensions));
                outputArg.setReplicas(GaswConstants.numberOfReplicas);
                arguments.add(outputArg);
                inputsList.add(outputid.get(i));
            }
            return executableName;
        }
        }