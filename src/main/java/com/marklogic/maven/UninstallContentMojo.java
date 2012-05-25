package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Uninstall content from database.
 * 
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
@MojoGoal("uninstall-content")
public class UninstallContentMojo extends AbstractUninstallMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		uninstallContent();
	}
}
