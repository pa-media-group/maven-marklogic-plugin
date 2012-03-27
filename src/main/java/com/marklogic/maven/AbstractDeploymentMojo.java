package com.marklogic.maven;

import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.types.ValueType;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.FileUtils;
import org.jfrog.maven.annomojo.annotations.MojoParameter;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public abstract class AbstractDeploymentMojo extends AbstractMarkLogicMojo {

    protected static final String ACTION_RESTART = "restart";

    /**
     * Namespace for the install configuration block
     */
    public static final String INSTALL_NS = "http://www.marklogic.com/ps/install/config.xqy";

    /**
     * The default encoding to use for the generated Ant build.
     */
    public static final String UTF_8 = "UTF-8";

    private MLInstallEnvironment currentEnvironment;

    /**
     * Sequence of environments, containing configuration instructions
     * <p/>
     * For example
     * <p/>
     * <environments>
     *     <environment>
     *         <name>development</name>
     *         <applicationName>Demo-Development</applicationName>
     *         <title>Demo (Development)</title>
     *         <databases>
     *             { database nodes for configuration xml }
     *         </databases>
     *         <servers>
     *             { server nodes for configuration xml }
     *         </servers>
     *         <pipeline-resources>
     *             <resource>
     *                 <database>Triggers</database>
     *                 <directory>${basedir}/src/main/marklogic/pipelines</directory>
     *                 <includes>
     *                     <include>demp-pipeline.xml</include>
     *                 </includes>
     *             </resource>
     *         </pipeline-resources>
     *         <resources>
     *             <resource>
     *                 <database>Content</database>
     *                 <directory>${basedir}/src/main/xquery</directory>
     *                 <includes>
     *                     <include>*.xqy</include>
     *                 </includes>
     *             </resource>
     *         </resources>
     *         
     *         <module-invokes>
     *         		<module-invoke>
     *     				<server>XCC</server>			
     *     				<module>static/load-lookups.xqy</module>
     *     			</module-invoke>	
     *         </module-invokes>
     *         
     *     </environment>
     *     ...
     * </environments>
     */
    @MojoParameter
    protected MLInstallEnvironment[] environments;

    /**
     * The path to the file controlling installation.
     * <p/>
     * The configuration defined in the specified file is used instead of that defined in the environments block
     * with the exception of the pipeline-resources and resources elements.
     */
    @MojoParameter
    protected File installConfigurationFile;

    /**
     * The installation module path.  This path is relative to the
     * xdbcModuleRoot.
     * <p/>
     * For example if the xdbcModuleRoot is set to /modules/,
     * and the installation module is deployed at /modules/install/install.xqy,
     * set the installModule to install/install.xqy.
     * <p/>
     * The default value is just "install.xqy" since we assume that the xdbc server
     * will point directly to the installation xquery.
     */
    @MojoParameter(defaultValue = "install.xqy", expression = "${marklogic.install.module}")
    protected String installModule;

    protected Map<String, Session> sessions = new HashMap<String, Session>();

    protected Session getSession(final String database) {
        Session s = sessions.get(database);
        if (s == null) {
            s = getXccSession(database);
            sessions.put(database, s);
        }
        return s;
    }

    protected void executeAction(final String action) throws MojoExecutionException {
        getLog().info("Executing ".concat(action));
        try {
            ResultSequence rs = executeInstallAction(action, installModule);
            getLog().debug(rs.asString());
        } catch (RequestException e) {
            throw new MojoExecutionException("xcc request error", e);
        }
    }

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
        return session.submitRequest(request);
    }

    /**
     * Converts a list into a comma separated String list
     *
     * @param list The list to be converted
     * @return Comma separated representation of list
     */
    protected String getCommaSeparatedList(List list) {
        StringBuilder buffer = new StringBuilder();
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            Object element = iterator.next();
            buffer.append(element.toString());
            if (iterator.hasNext()) {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }

    /**
     * Gets the current environment object
     *
     * @return MLInstallEnvironment instance
     */
    public MLInstallEnvironment getCurrentEnvironment() {
        if (currentEnvironment == null) {
            for (MLInstallEnvironment e : environments) {
                if (environment.equalsIgnoreCase(e.getName())) {
                    currentEnvironment = e;
                    break;
                }
            }
            if (currentEnvironment == null) {
                currentEnvironment = new DefaultMLInstallEnvironment(environment);
            }
        }
        return currentEnvironment;
    }

    /**
     * Returns the string representation of the specified file
     *
     * @param file The file to be loaded
     * @return
     * @throws IOException
     */
    protected String getFileAsString(final File file) throws IOException {
        StringBuilder buffer = new StringBuilder((int) file.length());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            buffer.append(buf, 0, numRead);
        }
        reader.close();
        return buffer.toString();
    }

    private String getInstallConfiguration() throws IOException {
        if (installConfigurationFile == null) {
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

    private StringBuffer plexusConfigurationToStringBuffer(PlexusConfiguration config) throws IOException {
        StringWriter writer = new StringWriter();
        MarklogicXmlPlexusConfigurationWriter xmlWriter = new MarklogicXmlPlexusConfigurationWriter();
        xmlWriter.write(config, writer);
        return writer.getBuffer();
    }

    /**
     * Restart marklogic HTTP, XDBC servers.
     *
     * @throws MojoExecutionException
     */
    protected void restartServers() throws MojoExecutionException {
        executeAction(ACTION_RESTART);

        /**
         * Ensure server is ready
         */
        int count = 10;
        boolean success = false;
        RequestException lastException = null;
        while (count-- > 0) {
            /* Try and get session */
            Session session = getXccSession();
            if (session != null) {
                /* Attempt simple xquery */
                AdhocQuery q = session.newAdhocQuery("xquery version \"1.0-ml\";\n1");
                try {
                    session.submitRequest(q);
                    success = true;
                } catch (RequestException e) {
                    lastException = e;
                } finally {
                    session.close();
                }
            }

            if (success) {
                break;
            } else {
                try {
                    getLog().info("Waiting for server to be ready.");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Interrupted
                }
            }
        }

        if (!success) {
            throw new MojoExecutionException("Job timed out waiting for servers on host to restart.", lastException);
        }
    }

    /**
     * Get the server configuration with name attribute of value parameter 
     * 
     * @param value
     * @return
     * @throws PlexusConfigurationException
     */
    protected PlexusConfiguration getServer(String value) throws PlexusConfigurationException
  	{
  		PlexusConfiguration[] servers = getCurrentEnvironment().getServers().getChildren();

  		for (PlexusConfiguration cfg : servers)
  		{
  			if (value != null && value.equals( cfg.getAttribute("name") ) )
  			{
  				return cfg;
  			}
  		}
  		throw new PlexusConfigurationException("Unknown server configuration: " + value);
  	}
    
    
    /**
     * Creates a configuration file based on the specification defined in the environment block
     *
     * @return
     * @throws IOException
     * @throws PlexusConfigurationException
     */
    private File writeEnvironmentToConfigurationFile() throws IOException, PlexusConfigurationException {

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
        File buildFile = new File(project.getBuild().getDirectory(), "/marklogic/" + fileName);

        buildFile.getParentFile().mkdirs();
        FileUtils.fileWrite(buildFile.getAbsolutePath(), UTF_8, builder.toString());

        return buildFile;
    }

}