package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Uninstall scheduled tasks from server.
 *
 * @author Gavin Haydon <gavin.haydon@pressassociation.com>
 */
@MojoGoal("uninstall-tasks")
public class UninstallTasks extends AbstractDeploymentMojo {

    public static final String ACTION_UNINSTALL_TASKS = "uninstall-tasks";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_UNINSTALL_TASKS);
    }
}
