package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Install Databases defined in POM
 *
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
@MojoGoal("install-databases")
public class InstallDatabasesMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installDatabases();
    }
}
