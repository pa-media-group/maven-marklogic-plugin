package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Invoke an XQuery module in a MarkLogic database.
 *
 * @author <a href="mailto:ralph.hodgson@pressassociation.com">Ralph Hodgson</a>
 */
@MojoGoal("module-invoke")
public class InvokeModuleMojo extends AbstractInstallMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        invokeModules();
    }
}