package com.marklogic.maven;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;

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

	private static final String[] DEFAULT_EXCLUDES = new String[] {};

	private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

	/**
	 * Directory containing the build files.
	 * 
	 * @parameter expression="${project.build.directory}/maven-marklogic-tmp"
	 */
	private File buildDirectory;
	
	/**
	* Base directory of the project.
	* @parameter expression="${basedir}"
	*/
	private File sourceDirectory;	
	
	

	/**
	 * The DirectoryArchiver.
	 * 
	 * @parameter
	 */
	private Archiver archiver;

	/**
	 * The MarkLogic database name where data should be loaded.
	 * 
	 * @parameter expression="${marklogic.database}"
	 * @required
	 */
	protected String database;

	/**
	 * List of files to include.
	 * 
	 * @parameter
	 */
	private String[] includes;

	/**
	 * List of files to exclude.
	 * 
	 * @parameter
	 */
	private String[] excludes;

	private String[] getIncludes() {
		if (includes != null && includes.length > 0) {
			return includes;
		}
		return DEFAULT_INCLUDES;
	}

	private String[] getExcludes() {
		if (excludes != null && excludes.length > 0) {
			return excludes;
		}
		return DEFAULT_EXCLUDES;
	}
	
	public void setExcludes( String[] excludes ) { this.excludes = excludes; }
	public void setIncludes( String[] includes ) { this.includes = includes; }

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("load execute, database=" + database);
		archiver = new Archiver();
		buildDirectory.mkdirs();
		try {
			archiver.addDirectory(sourceDirectory, includes, excludes);
			archiver.setDestFile(sourceDirectory);
			ResourceIterator iter = archiver.getResources();
			while (iter.hasNext()) {
				ArchiveEntry f = iter.next();
				String fileName = f.getName();
				getLog().info("    " + fileName);
			}
		} catch (ArchiverException e) {
			throw new MojoExecutionException("archive error", e);
		} 
//			catch (IOException e) {
//			throw new MojoExecutionException("archive error", e);
//		}
		
	}

	/**
	 * Override so Session is associated with our database.
	 */
	protected Session getXccSession() {
		ContentSource cs = ContentSourceFactory.newContentSource(host,
				xdbcPort, username, password, database);
		Session session = cs.newSession();
		return session;
	}

}
