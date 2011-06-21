package com.marklogic.maven;

import org.apache.maven.shared.model.fileset.FileSet;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
public class ResourceFileSet extends FileSet {

    /**
     * The database to load the specified resources into
     *
     * @parameter
     * @required
     */
    private String database;

    public String getDatabase() {
        return database;
    }
}
