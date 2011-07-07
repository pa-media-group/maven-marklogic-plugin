package com.marklogic.maven;

import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal install-cpf
 */
public class InstallCPFMojo extends InstallMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(getCurrentEnvironment().getPipelineResources() != null) {
            /*
             * Install pipeline resources from maven project
             */
            installPipeline(getCurrentEnvironment().getPipelineResources());
        }

        executeAction(ACTION_INSTALL_CPF);
    }

    private void installPipeline(ResourceFileSet[] resources) {
         try {
            FileSetManager manager = new FileSetManager();

            for (ResourceFileSet resource : resources) {
                final String targetDatabase = getCurrentEnvironment().getApplicationName() + "-" + resource.getDatabase();
                getLog().info(" -- ".concat(targetDatabase).concat(" -- "));

                /*
                 * Get connection to database for uploading content
                 */
                Session session = getSession(targetDatabase);

                AdhocQuery query = session.newAdhocQuery("(::)\n" +
                        "xquery version \"1.0-ml\";\n" +
                        "import module namespace p = \"http://marklogic.com/cpf/pipelines\" at \"/MarkLogic/cpf/pipelines.xqy\";\n" +
                        "declare variable $file as xs:string external; \n" +
                        "p:insert( xdmp:unquote($file)/* )\n" +
                        "(::)");

                for (String f : manager.getIncludedFiles(resource)) {
                    File sourceFile = new File(resource.getDirectory(), f);
                    getLog().info(String.format("Loading pipeline configuration %s", sourceFile.getPath()));
                    try {
                        query.setNewStringVariable("file", getFileAsString(sourceFile));
                        session.submitRequest(query);
                    } catch (IOException e) {
                        getLog().error("Failed to read pipeline file ".concat(f), e);
                    } catch (RequestException e) {
                        getLog().error("Failed to insert pipeline file ".concat(f), e);
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
