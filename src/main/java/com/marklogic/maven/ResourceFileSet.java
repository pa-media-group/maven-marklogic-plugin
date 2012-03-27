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

    /**
     * The collection to load the specified resources into.
     */
    @MojoParameter
    private String[] collections;

    /**
     * The permissions the specified resources should have.
     */
    @MojoParameter
    private Permission[] permissions;

    /**
     * The format of the specified resources.
     */
    @MojoParameter
    private String format;

    /**
     * @return Collections to load resources into.
     */
    public String[] getCollections() {
        return collections;
    }

    /**
     * @return Database to load resources into.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @return Format of the specified resources (binary, text, xml).
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return Permissions that should be applied to the resources.
     */
    public Permission[] getPermissions() {
        return permissions;
    }
}
