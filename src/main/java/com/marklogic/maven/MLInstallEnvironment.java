package com.marklogic.maven;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
public class MLInstallEnvironment {

    /**
     * The name of the environment
     *
     * @parameter
     * @required
     */
    protected String name;

    /**
     * The name of the application being installed
     *
     * @parameter
     * @required
     */
    protected String applicationName;

    /**
     * The title of the application
     * @parameter
     * @required
     */
    protected String title;

    /**
     * The Filesystem Root for the application
     *
     * @parameter default-value="/"
     */
    protected String filesystemRoot;

    /**
     * Set of pipeline configuration XML to be deployed
     *
     * @parameter alias="pipeline-resources"
     */
    protected ResourceFileSet[] pipelineResources;

    /**
     * Set of resources to be deployed
     *
     * @parameter
     */
    protected ResourceFileSet[] resources;


    /**
     * Set of database configurations
     *
     * @parameter
     */
    protected PlexusConfiguration[] databases;

    /**
     * Server configuration block
     *
     * @parameter
     */
    protected PlexusConfiguration servers;

    /**
     * Return the name of the environment
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Return the set of resources to be deployed
     *
     * @return
     */
    public ResourceFileSet[] getResources() {
        return resources;
    }

    /**
     * Return the set of databases to be created
     *
     * @return
     */
    public PlexusConfiguration[] getDatabases() {
        return databases;
    }

    /**
     * Return the set of servers to be created
     *
     * @return
     */
    public PlexusConfiguration getServers() {
        return servers;
    }

    /**
     * Return the application name
     *
     * @return
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Return the application title
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Return the filesystem root property
     *
     * @return
     */
    public String getFilesystemRoot() {
        return filesystemRoot;
    }

    /**
     * Return the set of pipeline configuration xml to be deployed
     *
     * @return
     */
    public ResourceFileSet[] getPipelineResources() {
        return pipelineResources;
    }
}
