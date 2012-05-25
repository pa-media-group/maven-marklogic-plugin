package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Uninstall fields from Marklogic
 * 
 * @author Gavin Haydon <gavin.haydon@pressassociation.com>
 */
@MojoGoal("uninstall-fields")
public class UninstallFields extends AbstractUninstallMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		uninstallFields();
	}
}
