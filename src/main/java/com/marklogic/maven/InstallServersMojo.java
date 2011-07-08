package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal install-servers
 */
public class InstallServersMojo extends AbstractInstallMojo {

    protected static final String ACTION_INSTALL_SERVERS = "install-servers";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_INSTALL_SERVERS);
    }
}
