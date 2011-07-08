package com.marklogic.maven;

import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Install and configure the database(s), application server(s), etc. in the specified 
 * configuration.
 *
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal install
 * @execute goal="bootstrap"
 */
public final class InstallMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installDatabases();
        installServers();
        installCPF();
        installContent();
        restartServers();
    }
}
