package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal uninstall-databases
 * @execute goal="uninstall-servers"
 */
public class UninstallDatabases extends AbstractInstallMojo {

    private static final String ACTION_UNINSTALL_DATABASES = "uninstall-databases";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_UNINSTALL_DATABASES);
    }
}
