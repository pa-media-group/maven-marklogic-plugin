package com.marklogic.maven;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.json.JSONObject;

public abstract class AbstractBootstrapMojo extends AbstractMarkLogicMojo {

    protected static final String XQUERY_PROLOG = "xquery version '1.0-ml';\n";

    protected static final String ML_ADMIN_MODULE_IMPORT = "import module namespace admin = 'http://marklogic.com/xdmp/admin' at '/MarkLogic/admin.xqy';\n";

    /**
     * The MarkLogic version.
     */
    @MojoParameter(defaultValue = "5", expression = "${marklogic.version}")
    protected int marklogicVersion;
    
    /**
     * The port used to bootstrap MarkLogic Server.
     */
    @MojoParameter(defaultValue = "8000", expression = "${marklogic.bootstrap.port}")
    protected int bootstrapPort;
    
    /**
     * The config manager port used to query MarkLogic Server.
     */
    @MojoParameter(defaultValue = "8002", expression = "${marklogic.config.port}")
    protected int configPort;
    
    /**
     * The server used to gain an sid for bootstrap creation.
     */
    @MojoParameter(defaultValue = "Admin", expression = "${marklogic.config.server}")
    protected String configServer;

    /**
     * The MarkLogic Installer XDBC server name.
     */
    @MojoParameter(defaultValue = "MarkLogic-Installer-XDBC", expression = "${marklogic.xdbc.name}")
    protected String xdbcName;
    
    /**
     * The MarkLogic group name.
     */
    @MojoParameter(defaultValue = "Default", expression = "${marklogic.group}")
    protected String group;

    /**
     * The MarkLogic Installer XDBC module root setting.
     */
    @MojoParameter(defaultValue = "/", expression = "${marklogic.xdbc.module-root}")
    protected String xdbcModuleRoot = "/";

    protected abstract String getBootstrapExecuteQuery() throws MojoExecutionException;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Install Bootstrap = " + installBootstrap);
        if (installBootstrap) {
            getLog().info("Bootstrapping MarkLogic " + marklogicVersion);
        	String bootstrapQuery = getBootstrapExecuteQuery();
        	if (marklogicVersion == 4) {
        		executeML4BootstrapQuery(bootstrapQuery);
        	} else if (marklogicVersion == 5) {
        		executeML5BootstrapQuery(bootstrapQuery);
        	} else {
        		throw new MojoExecutionException("Unsupported MarkLogic version: marklogic.version=" + marklogicVersion);
        	}
        }
    }
    
	protected HttpResponse executeBootstrapQuery(String query)
			throws MojoExecutionException {
		getLog().info("Bootstrapping MarkLogic " + marklogicVersion);
		String bootstrapQuery = getBootstrapExecuteQuery();
		if (marklogicVersion == 4) {
			return executeML4BootstrapQuery(bootstrapQuery);
		} else if (marklogicVersion == 5) {
			return executeML5BootstrapQuery(bootstrapQuery);
		} else {
			throw new MojoExecutionException(
					"Unsupported MarkLogic version: marklogic.version="
							+ marklogicVersion);
		}
	}
    
    protected HttpResponse executeML4BootstrapQuery(String query) throws MojoExecutionException {
        HttpClient httpClient = this.getHttpClient();
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();        
        qparams.add(new BasicNameValuePair("queryInput", query));
        
        URI uri;
        try {
            uri = URIUtils.createURI("http", this.host, bootstrapPort, "/use-cases/eval2.xqy",
                    URLEncodedUtils.format(qparams, "UTF-8"), null);
        } catch (URISyntaxException e1) {
            throw new MojoExecutionException("Invalid uri for bootstrap query", e1);
        }

        HttpPost httpPost = new HttpPost(uri);
        
        HttpResponse response;
        
        try {
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing POST to create bootstrap server", e);
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new MojoExecutionException("POST response failed: " + response.getStatusLine());
        }

        return response;
    }
    
    protected HttpResponse executeML5BootstrapQuery(String query) throws MojoExecutionException {
        HttpClient httpClient = this.getHttpClient();
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("group-id", group));
        qparams.add(new BasicNameValuePair("format", "json"));

        URI uri;
        try {
            uri = URIUtils.createURI("http", this.host, configPort, "/manage/v1/servers/" + configServer,
                    URLEncodedUtils.format(qparams, "UTF-8"), null);
        } catch (URISyntaxException e1) {
            throw new MojoExecutionException("Invalid uri for querying " + configServer + " for it's id", e1);
        }

        HttpGet httpGet = new HttpGet(uri);

        HttpResponse response;
        String sid = null;
       

        try {
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing GET " + uri, e);
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new MojoExecutionException("GET response failed: " + response.getStatusLine());
        }
        
		try {
			if (response.getEntity() != null) {
				InputStream is = response.getEntity().getContent();
				JSONObject json = new JSONObject(IOUtil.toString(is));
				sid = json.getJSONObject("server-default").getString("id");
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Error parsing json response to get " + configServer + " server id", e);
		}
        
        
        if (sid == null) {
			throw new MojoExecutionException("Server id for " + configServer + " is null, aborting");
        }
        
        qparams.clear();
        qparams.add(new BasicNameValuePair("q", query));
        qparams.add(new BasicNameValuePair("resulttype", "xml"));
        qparams.add(new BasicNameValuePair("sid", sid));

        try {
            uri = URIUtils.createURI("http", this.host, bootstrapPort, "/qconsole/endpoints/eval.xqy",
                    URLEncodedUtils.format(qparams, "UTF-8"), null);
        } catch (URISyntaxException e1) {
            throw new MojoExecutionException("Invalid uri for bootstrap query", e1);
        }

        HttpPost httpPost = new HttpPost(uri);

        try {
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing POST to create bootstrap server", e);
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new MojoExecutionException("POST response failed: " + response.getStatusLine());
        }

        return response;
    }

}
