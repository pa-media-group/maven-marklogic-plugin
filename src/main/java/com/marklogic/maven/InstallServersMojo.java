package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Install HTTP, XDBC Servers
 *
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
@MojoGoal("install-servers")
public class InstallServersMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installServers();
    }
}
