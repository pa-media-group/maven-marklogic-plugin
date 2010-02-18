package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Create the necessary bootstrap configuration that the MarkLogic Plugin 
 * requires for executing its goals.
 * 
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @goal install
 * @execute goal="bootstrap"
 */
public class InstallMojo extends AbstractInstallMojo {
	
	// TODO should we just manually check if bootstrap is required and provide an error,
	// rather than just executing bootstrap every time?

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("install execute");
	}	
 

}
