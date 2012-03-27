package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Install Content into Database
 *
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
@MojoGoal("install-content")
public class InstallContentMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installContent();
    }

}
