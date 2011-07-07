package com.marklogic.maven;

import com.marklogic.xcc.Content;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.marklogic.xcc.ContentFactory.newContent;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal install-content
 */
public class InstallContentMojo extends InstallMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_INSTALL_CONTENT);

        if(getCurrentEnvironment().getResources() != null) {
            /*
             * Install content resources from maven project
             */
            installResources(getCurrentEnvironment().getResources());
        }
    }

    private void installResources(ResourceFileSet[] resources) {
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
                    File sourceFile = new File(resource.getDirectory(), f);
                    File destinationFile = new File(resource.getOutputDirectory(), f);
                    getLog().info(String.format("Deploying %s to %s", sourceFile.getPath(),
                            destinationFile.getPath()));
                    try {
                        Content c = newContent(destinationFile.getPath(),
                                getFileAsString(sourceFile), null);
                        session.insertContent(c);
                    } catch (IOException e) {
                        getLog().error("Failed to read content file ".concat(f), e);
                    } catch (RequestException e) {
                        getLog().error("Failed to insert content file ".concat(f), e);
                    }
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
