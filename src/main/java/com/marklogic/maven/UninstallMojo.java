package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.exceptions.RequestException;


/**
 * Remove all database(s), application server(s), etc. in the specified 
 * configuration.
 * 
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @goal uninstall
 */
public class UninstallMojo extends AbstractInstallMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("uninstall execute");
		try {
			ResultSequence rs = executeInstallAction("uninstall-all", installModule);
			System.out.println(rs.asString());
		} catch (RequestException e) {
			throw new MojoExecutionException("xcc request error", e);
		}
	}
	
}
