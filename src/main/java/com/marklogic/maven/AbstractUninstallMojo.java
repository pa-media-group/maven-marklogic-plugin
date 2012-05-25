package com.marklogic.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

/**
 * @author Gavin Haydon <gavin.haydon@pressassociation.com>
 */
public abstract class AbstractUninstallMojo extends AbstractDeploymentMojo {

	protected static final String ACTION_UNINSTALL_ALL = "uninstall-all";
	protected static final String ACTION_UNINSTALL_CONTENT = "uninstall-content";
	protected static final String ACTION_UNINSTALL_DATABASES = "uninstall-databases";
	protected static final String ACTION_UNINSTALL_INDICES = "uninstall-indices";
	protected static final String ACTION_UNINSTALL_FIELDS = "uninstall-fields";
	protected static final String ACTION_UNINSTALL_TRIGGERS = "uninstall-triggers";
	protected static final String ACTION_UNINSTALL_SERVERS = "uninstall-servers";
	protected static final String ACTION_UNINSTALL_TASKS = "uninstall-tasks";

	protected void uninstallAll() throws MojoExecutionException {
		executeAction(ACTION_UNINSTALL_ALL);
	}

	protected void uninstallContent() throws MojoExecutionException {
		if (getCurrentEnvironment().getResources() != null) {
			/*
			 * Install content resources from maven project
			 */
			uninstallResources(getCurrentEnvironment().getResources());
		}
	}

	protected void uninstallDatabases() throws MojoExecutionException {
		executeAction(ACTION_UNINSTALL_DATABASES);
	}

	protected void uninstallIndices() throws MojoExecutionException {
		executeAction(ACTION_UNINSTALL_INDICES);
	}

	protected void uninstallFields() throws MojoExecutionException {
		executeAction(ACTION_UNINSTALL_FIELDS);
	}

	protected void uninstallTriggers() throws MojoExecutionException {
		executeAction(ACTION_UNINSTALL_TRIGGERS);
	}

	protected void uninstallTasks() throws MojoExecutionException {
		executeAction(ACTION_UNINSTALL_TASKS);
	}

	protected void uninstallServers() throws MojoExecutionException {
		executeAction(ACTION_UNINSTALL_SERVERS);
	}

	private void uninstallResources(ResourceFileSet[] resources) {
		List<String> uris = new ArrayList<String>();

		try {
			FileSetManager manager = new FileSetManager();

			for (ResourceFileSet resource : resources) {
				final String targetDatabase = getCurrentEnvironment()
						.getApplicationName() + "-" + resource.getDatabase();
				getLog().info(" -- ".concat(targetDatabase).concat(" -- "));

				/*
				 * Get connection to database for uploading content
				 */
				Session session = getSession(targetDatabase);

				for (String f : manager.getIncludedFiles(resource)) {
					File destinationFile = new File(
							resource.getOutputDirectory(), f);
					String destinationPath = destinationFile.getPath().replace(
							File.separatorChar, '/');
					getLog().info(
							String.format("Submitting %s for removal.",
									destinationPath));
					uris.add(destinationPath);
				}

				StringBuilder b = new StringBuilder("for $uri in (");
				Iterator<String> iter = uris.iterator();
				while (iter.hasNext()) {
					b.append("'").append(iter.next()).append("'");
					if (iter.hasNext()) {
						b.append(",");
					}
				}
				b.append(") return try { xdmp:document-delete($uri) } catch ($e) { () }");

				AdhocQuery q = session.newAdhocQuery(b.toString());
				try {
					session.submitRequest(q);
				} catch (RequestException e) {
					getLog().error("Failed to remove content file.", e);
				}
			}

		} finally {
			for (Map.Entry<String, Session> e : sessions.entrySet()) {
				e.getValue().close();
			}
			sessions = new HashMap<String, Session>();
		}
	}

}
