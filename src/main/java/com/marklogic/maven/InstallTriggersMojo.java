package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Install triggers
 *
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
@MojoGoal("install-triggers")
public class InstallTriggersMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installTriggers();
    }
}
