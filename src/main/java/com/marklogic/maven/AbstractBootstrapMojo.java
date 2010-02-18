package com.marklogic.maven;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class AbstractBootstrapMojo extends AbstractMarkLogicMojo {
	
    /**
     * The port used to bootstrap MarkLogic Server.
     * 
     * @parameter default-value="8000" expression="${marklogic.bootstrap.port}"
     */
    protected int bootstrapPort;
    
    /**
     * The MarkLogic Installer XDBC server name
     * 
     * @parameter default-value="MarkLogic-Installer-XDBC" expression="${marklogic.xdbc.name}"
     */    
    protected String xdbcName;
    
    /**
     * The MarkLogic Installer XDBC module root setting
     * 
     * @parameter default-value="/" expression="${marklogic.xdbc.module-root}"
     */    
    protected String xdbcModuleRoot = "/";
    
    protected abstract String getBootstrapExecuteQuery();
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		HttpResponse response = executeBootstrapQuery(getBootstrapExecuteQuery());
	}
	
	protected HttpResponse executeBootstrapQuery(String query) throws MojoExecutionException {
		HttpClient httpClient = this.getHttpClient();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("queryInput", getBootstrapExecuteQuery()));
		URI uri = null;
		try {
			uri = URIUtils.createURI("http", this.hostName, bootstrapPort, "/use-cases/eval2.xqy", 
			URLEncodedUtils.format(qparams, "UTF-8"), null);
		} catch (URISyntaxException e1) {
			throw new MojoExecutionException("Unvalid uri", e1);
		}
		
		HttpPost httpPost = new HttpPost(uri);
		
		HttpResponse response = null;
        try {
        	response = httpClient.execute(httpPost);
		} catch (Exception e) {
			throw new MojoExecutionException("Error executing post", e);
		}
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new MojoExecutionException("Post response failed: " + response.getStatusLine());
    	}
		return response;
	}

}
