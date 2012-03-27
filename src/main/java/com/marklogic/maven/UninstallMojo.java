package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoExecute;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Remove all database(s), application server(s), etc.
 *
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 */
@MojoGoal("uninstall")
@MojoExecute(goal = "bootstrap")
public class UninstallMojo extends AbstractDeploymentMojo {

    private static final String ACTION_UNINSTALL_ALL = "uninstall-all";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_UNINSTALL_ALL);
    }

}