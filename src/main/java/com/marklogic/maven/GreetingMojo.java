package com.marklogic.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Says "Hi" to the user.
 * @goal sayhi
 */
public class GreetingMojo extends AbstractMojo {
	
	public void execute() throws MojoExecutionException {
		getLog().info("Hello, world.");
	}
	
}
