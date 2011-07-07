package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal install-servers
 */
public class InstallServersMojo extends InstallMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_INSTALL_SERVERS);
    }
}
