package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoExecute;
import org.jfrog.maven.annomojo.annotations.MojoGoal;


/**
 * Install and configure the database(s), application server(s) etc.
 *
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
@MojoGoal("install")
@MojoExecute(goal = "bootstrap")
public final class InstallMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installDatabases();
        installTriggers();
        installServers();
        installCPF();
        installContent();
        restartServers();
    }
}
