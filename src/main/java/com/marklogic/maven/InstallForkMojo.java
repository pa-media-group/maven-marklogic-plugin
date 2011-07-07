package com.marklogic.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal install
 * @execute lifecycle="mlcycle" phase="install"
 */
public class InstallForkMojo extends AbstractMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        /* Does nothing */
    }
}
