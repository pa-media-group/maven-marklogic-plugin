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
public class UninstallDatabases extends AbstractDeploymentMojo {

    public static final String ACTION_UNINSTALL_DATABASES = "uninstall-databases";

    public void execute() throws MojoExecutionException, MojoFailureException {
    	executeAction(UninstallTasks.ACTION_UNINSTALL_TASKS);
        executeAction(UninstallTriggers.ACTION_UNINSTALL_TRIGGERS);
        executeAction(ACTION_UNINSTALL_DATABASES);
        restartServers();
    }
}
