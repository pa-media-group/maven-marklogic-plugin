package com.marklogic.maven;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.jfrog.maven.annomojo.annotations.MojoParameter;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
public class MLInstallEnvironment {

    /**
     * The name of the environment.
     */
    @MojoParameter(required = true)
    protected String name;

    /**
     * The name of the application being installed.
     */
    @MojoParameter(required = true)
    protected String applicationName;

    /**
     * The title of the application.
     */
    @MojoParameter(required = true)
    protected String title;

    /**
     * The Filesystem Root for the application.
     */
    @MojoParameter(defaultValue = "/")
    protected String filesystemRoot;

    /**
     * Set of pipeline configuration XML to be deployed.
     */
    @MojoParameter(alias = "pipeline-resources")
    protected ResourceFileSet[] pipelineResources;

    /**
     * Set of resources to be deployed.
     */
    @MojoParameter
    protected ResourceFileSet[] resources;


    /**
     * Set of database configurations.
     */
    @MojoParameter
    protected PlexusConfiguration[] databases;

    /**
     * Server configuration block.
     */
    @MojoParameter
    protected PlexusConfiguration servers;

    /**
     * @return the name of the environment
     */
    public String getName() {
        return name;
    }

    /**
     * @return the set of resources to be deployed
     */
    public ResourceFileSet[] getResources() {
        return resources;
    }

    /**
     * @return the set of databases to be created
     */
    public PlexusConfiguration[] getDatabases() {
        return databases;
    }

    /**
     * @return the set of servers to be created
     */
    public PlexusConfiguration getServers() {
        return servers;
    }

    /**
     * @return the application name
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @return the application title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the filesystem root property
     */
    public String getFilesystemRoot() {
        return filesystemRoot;
    }

    /**
     * @return the set of pipeline configuration xml to be deployed
     */
    public ResourceFileSet[] getPipelineResources() {
        return pipelineResources;
    }
}
