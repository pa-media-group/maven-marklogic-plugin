package com.marklogic.maven;

import com.marklogic.xcc.*;
import com.marklogic.xcc.exceptions.RequestException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.io.IOException;

import static com.marklogic.xcc.ContentFactory.newContent;


/**
 * Install and configure the database(s), application server(s), etc. in the specified 
 * configuration.
 *
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @goal install
 * @execute goal="bootstrap"
 */
public class InstallMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Deploying site code to bootstrap modules location");

        getLog().info("Executing install-all");
        try {
            ResultSequence rs = executeInstallAction("install-all", installModule);
            getLog().info(rs.asString());
        } catch (RequestException e) {
            throw new MojoExecutionException("xcc request error", e);
        }


        MLInstallEnvironment env = null;
        for(MLInstallEnvironment e : environments) {
            if(e.getName().equalsIgnoreCase(environment)) {
                env = e;
                break;
            }
        }

        if(env != null) {
            Session session = getXccSession(env.getDatabase());

            try {
                FileSetManager manager = new FileSetManager();

                for (FileSet resource : env.getResources()) {
                    File directory = new File(resource.getDirectory());

                    for (String f : manager.getIncludedFiles(resource)) {
                        getLog().info(String.format("Deploying %s", f, f));
                        try {
                            Content c = newContent("/".concat(f), getFileAsString(new File(directory, f)), null);
                            session.insertContent(c);
                        } catch (IOException e) {
                            getLog().error("Failed to read content file ".concat(f), e);
                        } catch (RequestException e) {
                            getLog().error("Failed to insert content file ".concat(f), e);
                        }
                    }
                }
            } finally {
                if(session != null) {
                    session.close();
                }
            }
        }
    }

}
