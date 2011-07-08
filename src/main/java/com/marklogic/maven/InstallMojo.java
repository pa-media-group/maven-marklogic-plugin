package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Install and configure the database(s), application server(s), etc. in the specified 
 * configuration.
 *
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal install
 * @execute lifecycle="mlcycle-install" phase="install"
 */
public final class InstallMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        // Does nothing, just forks the mlcyle lifecycle
    }

}
