package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal uninstall-databases
 * @execute goal="uninstall-servers"
 */
public class UninstallDatabases extends AbstractDeploymentMojo {

    public static final String ACTION_UNINSTALL_DATABASES = "uninstall-databases";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(UninstallTriggers.ACTION_UNINSTALL_TRIGGERS);
        executeAction(ACTION_UNINSTALL_DATABASES);
        restartServers();
    }
}
