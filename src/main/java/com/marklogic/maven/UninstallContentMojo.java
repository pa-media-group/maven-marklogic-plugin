package com.marklogic.maven;

import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.util.*;

import static com.marklogic.xcc.ContentFactory.newContent;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal uninstall-content
 */
public class UninstallContentMojo extends AbstractDeploymentMojo {

    private static final String ACTION_UNINSTALL_CONTENT = "uninstall-content";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_UNINSTALL_CONTENT);
        if(getCurrentEnvironment().getResources() != null) {
            uninstallResources(getCurrentEnvironment().getResources());
        }
    }

    private void uninstallResources(ResourceFileSet[] resources) {
        List uris = new ArrayList();

        try {
            FileSetManager manager = new FileSetManager();

            for (ResourceFileSet resource : resources) {
                final String targetDatabase = getCurrentEnvironment().getApplicationName() + "-" + resource.getDatabase();
                getLog().info(" -- ".concat(targetDatabase).concat(" -- "));

                /*
                 * Get connection to database for uploading content
                 */
                Session session = getSession(targetDatabase);

                for (String f : manager.getIncludedFiles(resource)) {
                    File destinationFile = new File(resource.getOutputDirectory(), f);
                    String destinationPath = destinationFile.getPath().replace(
                            File.separatorChar, '/');
                    getLog().info(String.format("Submitting %s for removal.", destinationPath));
                    uris.add(destinationPath);
                }

                StringBuilder b = new StringBuilder("for $uri in (");
                Iterator iter = uris.iterator();
                while(iter.hasNext()) {
                    b.append("'").append((String) iter.next()).append("'");
                    if(iter.hasNext()) {
                        b.append(",");
                    }
                }
                b.append(") return try { xdmp:document-delete($uri) } catch ($e) { () }");

                AdhocQuery q = session.newAdhocQuery(b.toString());
                try {
                    session.submitRequest(q);
                } catch (RequestException e) {
                    getLog().error("Failed to remove content file.", e);
                }
            }

        } finally {
            for ( Map.Entry<String, Session> e : sessions.entrySet() ) {
                e.getValue().close();
            }
            sessions = new HashMap<String, Session>();
        }
    }
}
