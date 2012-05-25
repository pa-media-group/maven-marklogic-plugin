package com.marklogic.maven;


import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Session;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoRequiresProject;

@MojoRequiresProject
public abstract class AbstractMarkLogicMojo extends AbstractMojo {

    /**
     * The Maven project object.
     */
    @MojoParameter(expression = "${project}", readonly = true)
    protected MavenProject project;

    /**
     * The host MarkLogic Server is running on.
     */
    @MojoParameter(defaultValue = "localhost", expression = "${marklogic.host}")
    protected String host;

    /**
     * The host MarkLogic Server is running on.
     */
    @MojoParameter(defaultValue = "admin", expression = "${marklogic.username}")
    protected String username;

    /**
     * The host MarkLogic Server is running on.
     */
    @MojoParameter(defaultValue = "admin", expression = "${marklogic.password}")
    protected String password;

    /**
     * The XDBC port used for install purposes.
     */
    @MojoParameter(defaultValue = "8997", expression = "${marklogic.xdbc.port}")
    protected int xdbcPort;

    /**
     * The database to be used for XDBC connections.
     */
    @MojoParameter(expression = "${marklogic.xdbc.database}")
    protected String database;

    /**
     * The environment name, which specifies with configuration profile is being applied.
     */
    @MojoParameter(defaultValue = "development", expression = "${marklogic.environment}")
    protected String environment;

    /**
     * The modules database used to bootstrap MarkLogic Server.
     */
    @MojoParameter(defaultValue = "InstallModules", expression = "${marklogic.xdbc.modules-db}")
    protected String xdbcModulesDatabase;

    /**
     * The control to install the bootstrap server.
     */
    @MojoParameter(defaultValue = "true", expression = "${marklogic.bootstrap}")
    protected boolean installBootstrap;
    
    protected Session getXccSession() {
        return getXccSession(database);
    }

    protected Session getXccSession(final String database) {
        ContentSource cs;
        if (database == null) {
            cs = ContentSourceFactory.newContentSource(host, xdbcPort, username, password);
        } else {
            cs = ContentSourceFactory.newContentSource(host, xdbcPort, username, password, database);
        }
        return cs.newSession();
    }

	protected Session getXccSession(final String database, final int port) {
		return ContentSourceFactory.newContentSource(host, port, username, password, database).newSession();
	}
	
    protected String getXdbcConnectionString() {
        StringBuilder sb = new StringBuilder();
        sb.append("xcc://");
        if (username != null) {
            sb.append(username);
            sb.append(":");
            sb.append(password);
            sb.append("@");
        }
        sb.append(host);
        sb.append(":");
        sb.append(xdbcPort);
        if (database != null) {
            sb.append("/");
            sb.append(database);
        }
        return sb.toString();
    }

    protected HttpClient getHttpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getCredentialsProvider().setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");

        //httpClient.getHostConfiguration().setProxy("my.proxyhost.com", 80);
        //Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
        //httpClient.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM), defaultcreds);
        return httpClient;
    }

}
