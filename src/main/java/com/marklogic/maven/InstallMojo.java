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
 */
public abstract class InstallMojo extends AbstractInstallMojo {

    protected static final String ACTION_INSTALL_DATABASES = "install-databases";
    protected static final String ACTION_INSTALL_SERVERS = "install-servers";
    protected static final String ACTION_INSTALL_CONTENT = "reinstall-content";
    protected static final String ACTION_INSTALL_CPF = "install-cpf";
    protected static final String ACTION_RESTART = "restart";

    protected Map<String, Session> sessions = new HashMap<String, Session>();

    protected Session getSession(final String database) {
        Session s = sessions.get(database);
        if(s == null) {
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

    public abstract void execute() throws MojoExecutionException, MojoFailureException;
}
