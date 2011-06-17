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
     * @parameter default-value="${basedir}/src/main/marklogic/configuration.xml" expression="${marklogic.install.configuration}
     * @required
     */
    protected File installConfigurationFile;

    /**
     * The XML controlling installation
     *
     * @parameter
     */
    protected PlexusConfiguration install;

    /**
     * @parameter
     */
    protected MLInstallEnvironment[] environments;

    protected ResultSequence executeInstallAction(String action, String module) throws RequestException {
    	Session session = this.getXccSession();
		Request request = session.newModuleInvoke(module);
		request.setNewStringVariable("action", action);
		request.setNewStringVariable("environ", environment);
		request.setNewVariable("delete-data", ValueType.XS_BOOLEAN, false);
        try {
            request.setNewStringVariable("configuration-string", getInstallConfiguration());
        } catch (IOException e) {
            throw new RequestException("Cannot load configuration file", request, e);
        }
        ResultSequence rs = session.submitRequest(request);
		return rs;
    }

    protected Document getInstallConfigurationDocument() throws IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(installConfigurationFile);
        } catch (ParserConfigurationException e) {
            throw new IOException("Invalid configuration file", e);
        } catch (SAXException e) {
            throw new IOException("Invalid configuration file", e);
        }
    }

    private String getInstallConfiguration() throws IOException {
        if(install != null) {
            try {
                return getFileAsString(writeTargetToConfigurationFile());
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

    protected File writeTargetToConfigurationFile() throws IOException, PlexusConfigurationException {
        StringWriter writer = new StringWriter();
        MarklogicXmlPlexusConfigurationWriter xmlWriter = new MarklogicXmlPlexusConfigurationWriter();
        xmlWriter.write( install, writer );

        StringBuffer installConfigurationXml = writer.getBuffer();

        final String xmlHeader = "<?xml version=\"1.0\" encoding=\"" + UTF_8 + "\" ?>\n";
        installConfigurationXml.insert(0, xmlHeader);

        int index = installConfigurationXml.indexOf( "<install" );
        installConfigurationXml.replace(index, index + "<install".length(), "<install xmlns=\"" + INSTALL_NS + "\" ");

        // The fileName should probably use the plugin executionId instead of the targetName
        String fileName = "install-" + environment + ".xml";
        File buildFile = new File( project.getBuild().getDirectory(), "/marklogic/" + fileName );

        buildFile.getParentFile().mkdirs();
        FileUtils.fileWrite(buildFile.getAbsolutePath(), UTF_8, installConfigurationXml.toString());

        return buildFile;
    }
}
