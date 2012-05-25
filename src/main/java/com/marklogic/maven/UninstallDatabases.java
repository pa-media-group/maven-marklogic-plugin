package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoExecute;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Uninstall databases.
 * 
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
@MojoGoal("uninstall-databases")
@MojoExecute(goal = "uninstall-servers")
public class UninstallDatabases extends AbstractUninstallMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		uninstallTasks();
		uninstallTriggers();
		uninstallDatabases();
		restartServers();
	}
}
