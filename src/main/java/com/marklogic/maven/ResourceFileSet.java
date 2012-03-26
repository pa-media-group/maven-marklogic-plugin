package com.marklogic.maven;

import org.apache.maven.shared.model.fileset.FileSet;
import org.jfrog.maven.annomojo.annotations.MojoParameter;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
public class ResourceFileSet extends FileSet {

    /**
     * The database to load the specified resources into.
     */
    @MojoParameter(required = true)
    private String database;

    public String getDatabase() {
        return database;
    }

    /**
     * The collection to load the specified resources into.
     */
    @MojoParameter
    private String[] collections;

    public String[] getCollections() {
        return collections;
    }

}
