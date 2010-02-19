package com.marklogic.maven;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Session;

/**
 * Load data into a MarkLogic database.
 * 
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @goal load
 */
public class LoaderMojo extends AbstractMarkLogicMojo {

	/**
	 * The MarkLogic database name where data should be loaded.
	 * 
	 * @parameter expression="${marklogic.database}"
	 * @required
	 */
	protected String database;

	/**
	 * The list of resources we want to transfer.
	 * 
	 * @parameter
	 * @required
	 */
	private List resources;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("load execute, database=" + database);

		for (Iterator i = resources.iterator(); i.hasNext();) {
			Resource resource = (Resource) i.next();
			getLog().info("resource:" + resource);
		}
	}

	/**
	 * Override so Session is associated with our database.
	 */
	protected Session getXccSession() {
		ContentSource cs = ContentSourceFactory.newContentSource(hostName,
				xdbcPort, username, password, database);
		Session session = cs.newSession();
		return session;
	}

}
