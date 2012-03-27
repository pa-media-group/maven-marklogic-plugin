package com.marklogic.maven;

import org.jfrog.maven.annomojo.annotations.MojoParameter;

/**
 * @author Gavin Haydon <gavin.haydon@pressassociation.com>
 */
public class Permission {
	/**
	 * The role name for this permission.
	 */
    @MojoParameter(required = true)
	private String role;

	/**
	 * The capability for this permission.
	 */
    @MojoParameter(required = true)
	private String capability;

	public String getCapability() {
		return capability;
	}

    public String getRole() {
   		return role;
   	}
}
