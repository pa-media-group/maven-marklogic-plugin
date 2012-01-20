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

/**
     * The collection to load the specified resources into
     *
     * @parameter
     * @optional
     */
    private String[] collections;

    public String[] getCollections() {
        return collections;
    }
    
    /**
     * The permissions the specified resources should have
     *
     * @parameter
     * @optional
     */
    private Permission[] permissions;

    public Permission[] getPermissions() {
        return permissions;
    }

    /**
     * The format of the specified resources
     *
     * @parameter
     * @optional
     */
    private String format;
    
	public String getFormat() {
		return format;
	}


}
