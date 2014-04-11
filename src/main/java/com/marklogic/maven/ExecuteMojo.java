package com.marklogic.maven;

import com.google.common.collect.ImmutableList;
import com.marklogic.install.xquery.XQueryDocumentBuilder;
import com.marklogic.install.xquery.XQueryModule;
import com.marklogic.install.xquery.XQueryModuleXDMP;
import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.ResultItem;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Remove the bootstrap configuration created by the marklogic:bootstrap goal.
 * 
 * @author <a href="mailto:bob.browning@pressassociation.com">Bob Browning</a>
 */
@MojoGoal("execute")
public class ExecuteMojo extends AbstractInstallMojo {

	/**
	 * XQuery script to be executed
	 */
	@MojoParameter(expression = "${marklogic.xquery}")
	protected File executeXQuery;

	/**
	 * Database to execute XQuery against
	 */
	@MojoParameter(expression = "${marklogic.executeDatabase}")
	protected String executeDatabase;

	/**
	 * XQuery execution properties
	 */
	@MojoParameter(expression = "${marklogic.executeProperties}")
	protected Map executeProperties;

	/**
	 * Sequence of XQuery scripts to be executed
	 */
	@MojoParameter
	protected Execution xqueryExecutions[];

	/**
	 * Returns the string representation of the specified file
	 * 
	 * @param file
	 *            The file to be loaded
	 * @return
	 * @throws java.io.IOException
	 */
	protected String getFileAsString(final File file) throws IOException {
		StringBuilder builder = new StringBuilder((int) file.length());
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead;
		while ((numRead = reader.read(buf)) != -1) {
			builder.append(buf, 0, numRead);
		}
		reader.close();
		return builder.toString();
	}

	protected String getBootstrapExecuteQuery() throws MojoExecutionException {
		throw new UnsupportedOperationException("Not Implemented.");
	}

	public String getExecutionXQuery(File xquery) throws MojoExecutionException {
		return this.getExecutionXQuery(xquery, null, null, false);
	}

	public String getExecutionXQuery(File xquery, String database,
			@Nullable Map properties) throws MojoExecutionException {
		return this.getExecutionXQuery(xquery, null, null, true);
	}

	private String getExecutionXQuery(File xquery, String database,
			@Nullable Map properties, boolean useEval)
			throws MojoExecutionException {
		checkNotNull(xquery);
		try {
			if (useEval == false) {
				return getFileAsString(xquery);
			} else if (StringUtils.isBlank(database)
					&& (properties == null || properties.isEmpty())) {
				return getFileAsString(xquery);
			} else {
				checkNotNull(database);
				XQueryDocumentBuilder builder = new XQueryDocumentBuilder();
				String values = "";
				if (properties != null && !properties.isEmpty()) {
					List<String> vars = new ArrayList<String>(properties.size());
					for (Object key : properties.keySet()) {
						vars.add(XQueryModule.createQName(((String) key)
								.replace('.', '_')));
						vars.add(XQueryModule.quote((String) properties
								.get(key)));
					}
					values = StringUtils.join(vars.toArray(), ",");
				}
				StringBuilder options = new StringBuilder();
				options.append("<options xmlns='xdmp:eval'>");
				if (StringUtils.isNotBlank(database)) {
					options.append("<database>{xdmp:database('" + database
							+ "')}</database>");
				}
				options.append("<isolation>different-transaction</isolation>");
				options.append("</options>");

				builder.append(XQueryModuleXDMP.eval(getFileAsString(xquery),
						"(" + values + ")", options.toString()));

				getLog().debug("Eval statement:\n" + builder.toString());

				return builder.toString();
			}
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	protected void executeQuery(String query, String database)
			throws MojoExecutionException, RequestException {
		/* Do query here */

		Session session = getSession(database);
		if (session == null || session.isClosed()) {
			throw new MojoExecutionException(
					"Unable to get XCC session to bootstrap server");
		}

		AdhocQuery adhocQuery = session.newAdhocQuery(query);

        ResultSequence results = session.submitRequest(adhocQuery);
        for (ResultItem i : ImmutableList.copyOf(results.iterator())) {
            getLog().info(i.asString());
        }
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Executing queries via connection to bootstrap server");

		if (executeXQuery != null) {
			if (executeDatabase == null || StringUtils.isBlank(executeDatabase)) {
				throw new MojoExecutionException("No database is set");
			}

			try {
				executeQuery(getExecutionXQuery(executeXQuery), executeDatabase);
			} catch (RequestException e) {
				throw new MojoExecutionException("Failed to execute "
						+ executeXQuery.getPath(), e);
			}

		} else {
			for (Execution execution : xqueryExecutions) {
				String database = executeDatabase;
				if (execution.database != null
						&& !StringUtils.isBlank(execution.database)) {
					database = execution.database;
				}
				getLog().info(
						"Executing following XQuery against " + database
								+ ":\n" + execution.xquery.getPath());
				if (database == null || StringUtils.isBlank(database)) {
					throw new MojoExecutionException(
							"No database is set at the task or execute level!");
				}
				try {
					executeQuery(getExecutionXQuery(execution.xquery),
							execution.database);
				} catch (RequestException e) {
					throw new MojoExecutionException("Failed to execute "
							+ execution.xquery.getPath(), e);
				}
			}
		}
	}

	private Map collectExecutionProps() {
		Map collected = new HashMap(executeProperties);
		Set<String> names = System.getProperties().stringPropertyNames();
		for (String name : names) {
			if (name.startsWith("marklogic.executionProperties.")) {
				collected.put(name.substring(30), System.getProperty(name));
			}
		}
		return collected;
	}
}
