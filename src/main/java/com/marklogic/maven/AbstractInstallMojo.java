package com.marklogic.maven;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.types.ValueType;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Iterator;
import java.util.List;


public abstract class AbstractInstallMojo extends AbstractMarkLogicMojo {

    /**
     * The default encoding to use for the generated Ant build.
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * Namespace for the install configuration block
     */
    public static final String INSTALL_NS = "http://www.marklogic.com/ps/install/config.xqy";

    /**
     * The installation module path.  This path is relative to the 
     * xdbcModuleRoot.
     * 
     * For example if the xdbcModuleRoot is set to /modules/,
     * and the installation module is deployed at /modules/install/install.xqy,
     * set the installModule to install/install.xqy.
     * 
     * The default value is just "install.xqy" since we assume that the xdbc server 
     * will point directly to the installation xquery.
     * 
     * @parameter default-value="install.xqy" expression="${marklogic.install.module}"
     */    
    protected String installModule;

    /**
     * The path to the file controlling installation.
     *
     * @parameter
     */
    protected File installConfigurationFile;

    /**
     * @parameter
     */
    protected MLInstallEnvironment[] environments;

    private MLInstallEnvironment currentEnvironment;

    protected ResultSequence executeInstallAction(String action, String module) throws RequestException {
    	Session session = this.getXccSession();
		Request request = session.newModuleInvoke(module);
		request.setNewStringVariable("action", action);
		request.setNewStringVariable("environ", environment);
		request.setNewVariable("delete-data", ValueType.XS_BOOLEAN, false);
        try {
            request.setNewStringVariable("configuration-string", getInstallConfiguration());
            getLog().debug("Using configuration : ".concat(installConfigurationFile.getPath()));
        } catch (IOException e) {
            throw new RequestException("Cannot load configuration file", request, e);
        }
        ResultSequence rs = session.submitRequest(request);
		return rs;
    }

    private String getInstallConfiguration() throws IOException {
        if(installConfigurationFile == null) {
            try {
                installConfigurationFile = writeEnvironmentToConfigurationFile();
                return getFileAsString(installConfigurationFile);
            } catch (PlexusConfigurationException e) {
                throw new IOException("Unable to configuration from project pom.", e);
            }
        } else {
            return getFileAsString(installConfigurationFile);
        }
    }

    protected String getFileAsString(final File file) throws IOException {
        StringBuffer buffer = new StringBuffer((int)file.length());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead = 0;
        while((numRead = reader.read(buf))!= -1) {
            buffer.append(buf, 0, numRead);
        }
        reader.close();
        return buffer.toString();
    }

    /**
     * @param list
     * @return
     */
    protected String getCommaSeparatedList(List list) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Object element = iterator.next();
            buffer.append(element.toString());
            if (iterator.hasNext()) {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }

    protected File writeEnvironmentToConfigurationFile() throws IOException, PlexusConfigurationException {

        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"" + UTF_8 + "\" ?>\n");
        builder.append("<install xmlns=\"" + INSTALL_NS + "\">\n");
        builder.append("<").append(environment).append(">");
        builder.append("<application name=\"")
                .append(getCurrentEnvironment().getApplicationName()).append("\" title=\"")
                .append(getCurrentEnvironment().getTitle()).append("\" filesystem-root=\"")
                .append(getCurrentEnvironment().getFilesystemRoot()).append("\" />\n");

        for (PlexusConfiguration db : getCurrentEnvironment().getDatabases()) {
            builder.append(plexusConfigurationToStringBuffer(db).toString()).append("\n");
        }
        builder.append(plexusConfigurationToStringBuffer(getCurrentEnvironment().getServers()).toString()).append("\n");
        builder.append("</").append(environment).append(">");
        builder.append("</install>");

        // The fileName should probably use the plugin executionId instead of the targetName
        String fileName = "install-" + environment + ".xml";
        File buildFile = new File( project.getBuild().getDirectory(), "/marklogic/" + fileName );

        buildFile.getParentFile().mkdirs();
        FileUtils.fileWrite(buildFile.getAbsolutePath(), UTF_8, builder.toString());

        return buildFile;
    }

    private StringBuffer plexusConfigurationToStringBuffer(PlexusConfiguration config) throws IOException {
        StringWriter writer = new StringWriter();
        MarklogicXmlPlexusConfigurationWriter xmlWriter = new MarklogicXmlPlexusConfigurationWriter();
        xmlWriter.write( config, writer );
        return writer.getBuffer();
    }


    public MLInstallEnvironment getCurrentEnvironment() {
        if(currentEnvironment == null) {
            for (MLInstallEnvironment e : environments) {
                if(environment.equalsIgnoreCase(e.getName())) {
                    currentEnvironment = e;
                    break;
                }
            }
            if(currentEnvironment == null) {
                currentEnvironment = new DefaultMLInstallEnvironment(environment);
            }
        }
        return currentEnvironment;
    }
}