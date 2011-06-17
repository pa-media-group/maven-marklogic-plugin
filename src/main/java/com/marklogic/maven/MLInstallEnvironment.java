package com.marklogic.maven;

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
    private ResourceFileSet[] resources;

    public String getName() {
        return name;
    }

    public ResourceFileSet[] getResources() {
        return resources;
    }

}
