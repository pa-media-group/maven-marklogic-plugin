package com.marklogic.maven;

import org.apache.maven.shared.model.fileset.FileSet;

/**
 * Created by IntelliJ IDEA.
 * User: bobb
 * Date: 16/06/2011
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class ResourceFileSet extends FileSet {

    /**
     * @parameter
     * @required
     */
    private String database;

    public String getDatabase() {
        return database;
    }
}
