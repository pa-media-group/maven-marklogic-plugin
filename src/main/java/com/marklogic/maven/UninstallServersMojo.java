package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Uninstall servers HTTP, XDBC from Marklogic
 * 
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
@MojoGoal("uninstall-servers")
public class UninstallServersMojo extends AbstractUninstallMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		uninstallServers();
	}
}
