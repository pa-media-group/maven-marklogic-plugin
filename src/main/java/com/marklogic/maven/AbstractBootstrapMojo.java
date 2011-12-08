package com.marklogic.maven;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBootstrapMojo extends AbstractMarkLogicMojo {

    protected static final String XQUERY_PROLOG = "xquery version '1.0-ml';\n";
  
    protected static final String ML_ADMIN_MODULE_IMPORT = "import module namespace admin = 'http://marklogic.com/xdmp/admin' at '/MarkLogic/admin.xqy';\n";
    
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

    protected abstract String getBootstrapExecuteQuery() throws MojoExecutionException;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		executeBootstrapQuery(getBootstrapExecuteQuery());
	}
	
	protected HttpResponse executeBootstrapQuery(String query) throws MojoExecutionException {
		HttpClient httpClient = this.getHttpClient();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("queryInput", query));
		URI uri;
		try {
			uri = URIUtils.createURI("http", this.host, bootstrapPort, "/use-cases/eval2.xqy", 
			URLEncodedUtils.format(qparams, "UTF-8"), null);
		} catch (URISyntaxException e1) {
			throw new MojoExecutionException("Invalid uri", e1);
		}
		
		HttpPost httpPost = new HttpPost(uri);
		
		HttpResponse response;
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
