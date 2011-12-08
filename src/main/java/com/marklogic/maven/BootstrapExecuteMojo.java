package com.marklogic.maven;

import com.marklogic.maven.xquery.XQueryDocumentBuilder;
import com.marklogic.maven.xquery.XQueryModule;
import com.marklogic.maven.xquery.XQueryModuleXDMP;
import org.apache.http.HttpResponse;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Remove the bootstrap configuration created by the marklogic:bootstrap goal.
 *
 * @author <a href="mailto:bob.browning@pressassociation.com">Bob Browning</a>
 * @goal execute
 */
public class BootstrapExecuteMojo extends AbstractBootstrapMojo {

    /**
     * @parameter expression="${marklogic.xquery}"
     */
    protected File executeXQuery;

    /**
     * @parameter expression="${marklogic.executeDatabase}"
     */
    protected String executeDatabase;

    /**
     * @parameter expression="${marklogic.executeProperties}"
     */
    protected Map executeProperties;

    /**
     * @parameter
     */
    protected Execution xqueryExecutions[];

    /**
     * Returns the string representation of the specified file
     *
     * @param file The file to be loaded
     * @return
     * @throws java.io.IOException
     */
    protected String getFileAsString(final File file) throws IOException {
        StringBuilder builder = new StringBuilder((int) file.length());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            builder.append(buf, 0, numRead);
        }
        reader.close();
        return builder.toString();
    }

    protected String getBootstrapExecuteQuery() throws MojoExecutionException {
        throw new UnsupportedOperationException("Not Implemented.");
    }

    private String getExecutionXQuery(File xquery, String database, Map properties) throws MojoExecutionException {
        try {
            if (StringUtils.isBlank(database) && (properties == null || properties.isEmpty())) {
                return getFileAsString(xquery);
            } else {
                XQueryDocumentBuilder builder = new XQueryDocumentBuilder();
                String values = "";
                if (properties != null && !properties.isEmpty()) {
                    List vars = new ArrayList(properties.size());
                    for (Object key : properties.keySet()) {
                        vars.add(XQueryModule.createQName(((String) key).replace('.', '_')));
                        vars.add(XQueryModule.quote((String) properties.get(key)));
                    }
                    values = StringUtils.join(vars.toArray(), ",");
                }
                StringBuilder options = new StringBuilder();
                options.append("<options xmlns='xdmp:eval'>");
                if (StringUtils.isNotBlank(database)) {
                    options.append("<database>{xdmp:database('" + database + "')}</database>");
                }
                options.append("<isolation>different-transaction</isolation>");
                options.append("</options>");

                builder.append(XQueryModuleXDMP.eval(getFileAsString(xquery),
                        "(" + values + ")", options.toString()));

                getLog().debug(builder.toString());
                return builder.toString();
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected HttpResponse executeBootstrapQuery(String query) throws MojoExecutionException {
        getLog().debug(query);
        return super.executeBootstrapQuery(query);
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (executeXQuery != null) {
            executeBootstrapQuery(getExecutionXQuery(executeXQuery, executeDatabase, collectExecutionProps()));
        } else {
            for (Execution execution : xqueryExecutions) {
                executeBootstrapQuery(
                        getExecutionXQuery(execution.xquery, execution.database, execution.properties));
            }
        }
    }

    private Map collectExecutionProps() {
        Map collected = new HashMap(executeProperties);
        Set<String> names = System.getProperties().stringPropertyNames();
        for (String name : names) {
            if(name.startsWith("marklogic.executionProperties.")) {
                collected.put(name.substring(30), System.getProperty(name));
            }
        }
        return collected;
    }
}
