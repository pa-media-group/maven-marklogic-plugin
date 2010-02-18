package com.marklogic.maven;


import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.AbstractMojo;

/**
 * 
 */
public abstract class AbstractMarkLogicMojo extends AbstractMojo {

    /**
     * The host MarkLogic Server is running on.
     * 
     * @parameter default-value="localhost" expression="${marklogic.hostName}"
     */
    protected String hostName;
    
    /**
     * The host MarkLogic Server is running on.
     * 
     * @parameter default-value="admin" expression="${marklogic.username}"
     */
    protected String username;
    
    /**
     * The host MarkLogic Server is running on.
     * 
     * @parameter default-value="admin" expression="${marklogic.password}"
     */
    protected String password;
    
    /**
     * The XDBC port used for install purposes
     * 
     * @parameter default-value="8998" expression="${marklogic.xdbc.port}"
     */
    protected int xdbcPort = 8998;
    
  
    
	protected HttpClient getHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getCredentialsProvider().setCredentials(
                AuthScope.ANY, 
                new UsernamePasswordCredentials(username, password));

        //httpClient.getHostConfiguration().setProxy("my.proxyhost.com", 80);
        //Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
        //httpClient.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM), defaultcreds);
        return httpClient;
	}
	
}
