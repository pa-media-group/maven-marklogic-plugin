package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal install-databases
 */
public class InstallDatabasesMojo extends AbstractInstallMojo {

    protected static final String ACTION_INSTALL_DATABASES = "install-databases";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_INSTALL_DATABASES);
    }
}
