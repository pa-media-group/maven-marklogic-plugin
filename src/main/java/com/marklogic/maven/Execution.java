package com.marklogic.maven;

import java.io.File;
import java.util.Map;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
public class Execution {
    /**
     * @parameter
     * @required
     */
    protected File xquery;

    /**
     * @parameter
     */
    protected String database;

    /**
     * @parameter
     */
    protected Map properties;
}