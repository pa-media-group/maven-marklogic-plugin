package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * @author Gavin Haydon <gavin.haydon@pressassociation.com>
 */

@MojoGoal("install-tasks")
public class InstallTasksMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installTasks();
    }
}
