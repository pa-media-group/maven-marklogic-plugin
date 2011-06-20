package com.marklogic.maven;

import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.Content;
import com.marklogic.xcc.ResultSequence;
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
 * Install and configure the database(s), application server(s), etc. in the specified 
 * configuration.
 *
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @goal install
 * @execute goal="bootstrap"
 */
public class InstallMojo extends AbstractInstallMojo {

    private static final String ACTION_INSTALL_ALL = "install-all";
    private static final String ACTION_INSTALL_DATABASES = "install-databases";
    private static final String ACTION_INSTALL_SERVERS = "install-servers";
    private static final String ACTION_INSTALL_CONTENT = "reinstall-content";
    private static final String ACTION_INSTALL_CPF = "install-cpf";
    private static final String ACTION_RESTART = "restart";

    private Map<String, Session> sessions = new HashMap<String, Session>();

    public Session getSession(final String database) {
        Session s = sessions.get(database);
        if(s == null) {
            s = sessions.put(database, getXccSession(database));
        }
        return s;
    }

    private void executeAction(String action) throws MojoExecutionException {
        getLog().info("Executing ".concat(action));
        try {
            ResultSequence rs = executeInstallAction(action, installModule);
            getLog().debug(rs.asString());
        } catch (RequestException e) {
            throw new MojoExecutionException("xcc request error", e);
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_INSTALL_DATABASES);
        executeAction(ACTION_INSTALL_SERVERS);

        if(getCurrentEnvironment().getPipelineResources() != null) {
            /*
             * Install pipeline resources from maven project
             */
            installPipeline(getCurrentEnvironment().getPipelineResources());
        }

        executeAction(ACTION_INSTALL_CPF);
        executeAction(ACTION_INSTALL_CONTENT);

        if(getCurrentEnvironment().getResources() != null) {
            /*
             * Install content resources from maven project
             */
            installResources(getCurrentEnvironment().getResources());
        }

        executeAction(ACTION_RESTART);
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
                Session session = getXccSession(targetDatabase);

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

    private void installResources(ResourceFileSet[] resources) {
        try {
            FileSetManager manager = new FileSetManager();

            for (ResourceFileSet resource : resources) {
                final String targetDatabase = getCurrentEnvironment().getApplicationName() + "-" + resource.getDatabase();
                getLog().info(" -- ".concat(targetDatabase).concat(" -- "));

                /*
                 * Get connection to database for uploading content
                 */
                Session session = getXccSession(targetDatabase);

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
