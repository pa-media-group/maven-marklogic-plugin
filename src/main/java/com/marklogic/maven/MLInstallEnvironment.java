package com.marklogic.maven;

import org.apache.maven.shared.model.fileset.FileSet;

import java.util.List;

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
    private String name;

    /**
     * @parameter
     */
    private String database;

    /**
     * @parameter
     */
    private FileSet[] resources;

    public String getDatabase() {
        return database;
    }

    public FileSet[] getResources() {
        return resources;
    }

    public String getName() {
        return name;
    }

}
