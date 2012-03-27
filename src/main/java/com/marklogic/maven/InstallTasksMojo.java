package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Gavin Haydon <gavin.haydon@pressassociation.com>
 * @goal install-tasks
 */
public class InstallTasksMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installTasks();
    }
}
