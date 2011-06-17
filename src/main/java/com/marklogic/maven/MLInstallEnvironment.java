package com.marklogic.maven;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * Created by IntelliJ IDEA.
 * User: bobb
 * Date: 15/06/2011
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class MLInstallEnvironment {

    /**
     * @parameter
     * @required
     */
    protected String name;

    /**
     * @parameter
     * @required
     */
    protected String applicationName;

    /**
     * @parameter
     * @required
     */
    protected String title;

    /**
     * @parameter default-value="/"
     */
    protected String filesystemRoot;

    /**
     * @parameter
     */
    protected ResourceFileSet[] resources;


    /**
     * @parameter
     */
    protected PlexusConfiguration[] databases;

    /**
     * @parameter
     */
    protected PlexusConfiguration servers;

    public String getName() {
        return name;
    }

    public ResourceFileSet[] getResources() {
        return resources;
    }

    public PlexusConfiguration[] getDatabases() {
        return databases;
    }

    public PlexusConfiguration getServers() {
        return servers;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getTitle() {
        return title;
    }

    public String getFilesystemRoot() {
        return filesystemRoot;
    }
}
