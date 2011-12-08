package com.marklogic.maven;

import com.marklogic.maven.xquery.XQueryDocumentBuilder;
import com.marklogic.maven.xquery.XQueryModule;
import com.marklogic.maven.xquery.XQueryModuleAdmin;
import com.marklogic.maven.xquery.XQueryModuleXDMP;
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
     * @required
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
        try {
            if (StringUtils.isBlank(executeDatabase) && (executeProperties == null || executeProperties.isEmpty())) {
                return getFileAsString(executeXQuery);
            } else {                
                XQueryDocumentBuilder builder = new XQueryDocumentBuilder();
                String values = "";
                if(executeProperties != null && !executeProperties.isEmpty()) {
                    List vars = new ArrayList(executeProperties.size());
                    for (Object key : executeProperties.keySet()) {
                        vars.add(XQueryModule.createQName(((String) key).replace('.', '_')));
                        vars.add(XQueryModule.quote((String) executeProperties.get(key)));
                    }
                    values = StringUtils.join(vars.toArray(), ",");
                }
                StringBuilder options = new StringBuilder();
                options.append("<options xmlns='xdmp:eval'>");
                if(StringUtils.isNotBlank(executeDatabase)) {
                    options.append("<database>{xdmp:database('" + executeDatabase + "')}</database>");
                }
                options.append("<isolation>different-transaction</isolation>");
                options.append("</options>");

                builder.append(XQueryModuleXDMP.eval(getFileAsString(executeXQuery),
                        "(" + values + ")", options.toString()));

                getLog().info(builder.toString());
                return builder.toString();
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
    }
}
