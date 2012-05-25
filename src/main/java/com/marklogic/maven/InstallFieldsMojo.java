package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Install Fields defined in POM
 *
 * @author Gavin Haydon <gavin.haydon@pressassociation.com>
 */
@MojoGoal("install-fields")
public class InstallFieldsMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installFields();
    }
}
