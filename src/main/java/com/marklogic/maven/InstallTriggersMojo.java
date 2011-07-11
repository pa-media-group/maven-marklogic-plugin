package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal install-triggers
 */
public class InstallTriggersMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installTriggers();
    }
}
