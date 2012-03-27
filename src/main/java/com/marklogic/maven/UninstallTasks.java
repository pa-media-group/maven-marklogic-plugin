package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Gavin Haydon <gavin.haydon@pressassociation.com>
 * @goal uninstall-tasks
 */
public class UninstallTasks extends AbstractDeploymentMojo {

    public static final String ACTION_UNINSTALL_TASKS = "uninstall-tasks";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_UNINSTALL_TASKS);
    }
}
