package com.marklogic.maven;

/**
 * @author Gavin Haydon <gavin.haydon@pressassociation.com>
 */
public class Permission {
	/**
	 * The role name for this permission
	 * 
	 * @parameter
	 * @required
	 */
	private String role;

	public String getRole() {
		return role;
	}

	/**
	 * The capability for this permission
	 * 
	 * @parameter
	 * @required
	 */
	private String capability;

	public String getCapability() {
		return capability;
	}

}
