package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Invoke an XQuery module in a MarkLogic database.
 * 
 * @author <a href="mailto:ralph.hodgson@pressassociation.com">Ralph Hodgson</a>
 * @goal module-invoke
 */
public class ModuleInvokerMojo extends AbstractInstallMojo
{
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		invokeModules();
	}
}