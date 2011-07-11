package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal uninstall-triggers
 */
public class UninstallTriggers extends AbstractDeploymentMojo {

    public static final String ACTION_UNINSTALL_TRIGGERS = "uninstall-triggers";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_UNINSTALL_TRIGGERS);
    }
}
