package com.marklogic.maven;

import org.jfrog.maven.annomojo.annotations.MojoParameter;

import java.io.File;
import java.util.Map;

/**
 * Defines the context of an XQuery Execution
 *
 * @author Bob Browning <bob.browning@pressassociation.com>
 */
public class Execution {
    /**
     * XQuery to be invoked
     */
    @MojoParameter(required = true)
    protected File xquery;

    /**
     * Database to invoke XQuery against
     */
    @MojoParameter
    protected String database;

    /**
     * Properties to be used for invocation.
     */
    @MojoParameter
    protected Map properties;
}