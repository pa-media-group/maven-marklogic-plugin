package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal uninstall-servers
 */
public class UninstallServersMojo extends AbstractInstallMojo {

    private static final String ACTION_UNINSTALL_SERVERS = "uninstall-servers";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_UNINSTALL_SERVERS);
    }
}
