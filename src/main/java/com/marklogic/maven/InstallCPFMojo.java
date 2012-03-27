package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jfrog.maven.annomojo.annotations.MojoGoal;

/**
 * Install Content Processing Framework
 *
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
@MojoGoal("install-cpf")
public class InstallCPFMojo extends AbstractInstallMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        installCPF();
    }
}
