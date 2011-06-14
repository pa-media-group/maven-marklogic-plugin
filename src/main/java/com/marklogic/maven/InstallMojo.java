package com.marklogic.maven;

import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.exceptions.RequestException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Install and configure the database(s), application server(s), etc. in the specified 
 * configuration.
 * 
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @goal install
 * @execute goal="bootstrap"
 */
public class InstallMojo extends AbstractInstallMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("install execute");
		try {
			ResultSequence rs = executeInstallAction("install-all", installModule);
			System.out.println(rs.asString());
		} catch (RequestException e) {
			throw new MojoExecutionException("xcc request error", e);
		}
	}
	
}
