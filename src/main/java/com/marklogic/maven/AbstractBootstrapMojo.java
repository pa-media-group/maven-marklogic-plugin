package com.marklogic.maven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONML;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

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
        	String bootstrapQuery = getBootstrapExecuteQuery();
        	executeBootstrapQuery(bootstrapQuery);
        }
    }
    
	protected HttpResponse executeBootstrapQuery(String query)
			throws MojoExecutionException {
		HttpResponse response;
		getLog().info("MarkLogic Version " + this.marklogicVersion);
		getLog().info("query=" + query);
		getLog().info("host=" + this.host);
		int actualVersion = getMarkLogicVersion();
		getLog().info("Actual MarkLogic Version " + actualVersion);
		this.marklogicVersion = actualVersion;
		if (isMarkLogic7()) {
			getLog().info("Bootstrapping MarkLogic 7");
			response = executeML7BootstrapQuery(query);
			
		} else if (isMarkLogic5()) {
			getLog().info("Bootstrapping MarkLogic 5");
			response = executeML5BootstrapQuery(query);
		} else {
			getLog().info("Bootstrapping MarkLogic 4");
			response =  executeML4BootstrapQuery(query);
		}
		
		if (response.getEntity() != null) {
			try {
				InputStream is = response.getEntity().getContent();
				getLog().info(IOUtil.toString(is));
			} catch (IOException e) {
				throw new MojoExecutionException("IO Error reading response", e);
			}
		}
		
		return response;
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

    protected int getMarkLogicVersion() throws MojoExecutionException {
	    HttpClient httpClient = this.getHttpClient();
	    List<NameValuePair> qparams = new ArrayList<NameValuePair>();
	    int version = 4;
	    URI uri;
		try {
	        uri = URIUtils.createURI("http", this.host, bootstrapPort, "/qconsole",
	                URLEncodedUtils.format(qparams, "UTF-8"), null);
	    } catch (URISyntaxException e1) {
	        throw new MojoExecutionException("Invalid uri for qconsole probe", e1);
	    }
	    HttpGet httpGet = new HttpGet(uri);
	
	    HttpResponse response;
	    HttpEntity entity;
		try {
	        response = httpClient.execute(httpGet);
	        if (response.getEntity() != null) {
	        	entity = response.getEntity();
	        	EntityUtils.consume(entity);
	        }
	    } catch (Exception e) {
	        throw new MojoExecutionException("Error executing GET to probe qconsole", e);
	    }
	    
	    getLog().info("Probe got " + response.getStatusLine());
	    if (response.getStatusLine().getStatusCode() != 200)
	    	version = 4;
	    else {
	    	HttpPost httpPost;
	    	String sid = getConfigServerID();
		    getLog().info("ConfigServerID = " + sid);
	    	qparams.clear();
	        qparams.add(new BasicNameValuePair("q", "xdmp:version()"));
	        qparams.add(new BasicNameValuePair("resulttype", "text"));
	        qparams.add(new BasicNameValuePair("sid", sid));

	        try {
	            uri = URIUtils.createURI("http", this.host, bootstrapPort, "/qconsole/endpoints/eval.xqy",
	                    URLEncodedUtils.format(qparams, "UTF-8"), null);
	        } catch (URISyntaxException e1) {
	            throw new MojoExecutionException("Invalid uri for qconsole eval query", e1);
	        }

	        httpPost = new HttpPost(uri);
	        try {
	            response = httpClient.execute(httpPost);
	        } catch (Exception e) {
	            throw new MojoExecutionException("Error executing POST to get ML version", e);
	        }
	        if (response.getStatusLine().getStatusCode() == 200) {
			    getLog().info("/qconsole/endpoints/eval.xqy SUCCEEDED");
		        try {
			        BufferedReader rd = new BufferedReader(
			    	        new InputStreamReader(response.getEntity().getContent()));
			     
			        StringBuffer result = new StringBuffer();
			    	String line = "";
			    	while ((line = rd.readLine()) != null) {
			    		result.append(line);
			    	}
				    getLog().info("Version got " + result);
				    version = Integer.parseInt(result.substring(0, result.indexOf(".", 1)));
				    getLog().info("MarkLogic Main Version got " + version);
		        } catch (IOException e) {
		            throw new MojoExecutionException("Cannot read the response", e);
				}
	        } else {
	        	//clear previous response
	        	try {
			        if (response.getEntity() != null) {
			        	EntityUtils.consume(response.getEntity());
			        }
	        	} catch (IOException e) {
				    getLog().info("clear previous request" + e.getMessage());
				}
			    getLog().info("/qconsole/endpoints/eval.xqy FAILED, trying /qconsole/endpoints/evaler.xqy");
	            StringEntity payload;
	            try {
					payload = new StringEntity("xdmp:version()");
				} catch (UnsupportedEncodingException e2) {
		            throw new MojoExecutionException("Cannot create payload", e2);
				}
		    	qparams.clear();
		        qparams.add(new BasicNameValuePair("sid", sid));
		        qparams.add(new BasicNameValuePair("action", "eval"));
		        qparams.add(new BasicNameValuePair("querytype", "xquery"));

		        try {
		            uri = URIUtils.createURI("http", this.host, bootstrapPort, "/qconsole/endpoints/evaler.xqy",
		                    URLEncodedUtils.format(qparams, "UTF-8"), null);
		        } catch (URISyntaxException e1) {
		            throw new MojoExecutionException("Invalid uri for qconsole eval query", e1);
		        }

		        httpPost = new HttpPost(uri);
		        try {
					payload = new StringEntity("xdmp:version()");
				} catch (UnsupportedEncodingException e) {
					throw new MojoExecutionException("Error creating payload to get ML version",e);
				}
		        getLog().info("POST payload Content-Type=" + payload.getContentType());
		        
		        httpPost.setEntity(payload);
		        httpPost.setHeader("content-type","text/plain");
		        try {
		            response = httpClient.execute(httpPost);
		        } catch (Exception e) {
		            throw new MojoExecutionException("Error executing POST to get ML version", e);
		        }
		        if (response.getStatusLine().getStatusCode() != 200) {
		            throw new MojoExecutionException("POST response failed: " + response.getStatusLine());
		        }

				Header[] headers = response.getHeaders("qconsole");
				if (headers != null && headers.length > 0) {
					try {
						JSONObject json = new JSONObject(headers[0].getValue());
						if (json.getString("type").equals("error")) {
							StringBuilder b = new StringBuilder(
									"Failed to execute query ...\n");
							if (response.getEntity() != null) {
								InputStream is = response.getEntity().getContent();
								JSONObject jsonError = new JSONObject(
										IOUtil.toString(is));
								b.append(XML.toString(jsonError));
							}
							throw new MojoExecutionException(b.toString());
						} 
					} catch (JSONException e) {
						throw new MojoExecutionException(
								"Unable to parse json error header", e);
					} catch (IOException ioe) {
						throw new MojoExecutionException(
								"IOException parsing json error", ioe);
					}
				}
				try {
					JSONObject jsonObj = null;
					JSONArray jsonArr = null;
					String result = null;
					entity = response.getEntity();
					String jsonString = EntityUtils.toString(entity);
					getLog().info("Response = " + jsonString);
					Object json = new JSONTokener(jsonString).nextValue();
					if (json instanceof JSONObject) {
						jsonObj = new JSONObject(jsonString);
						result = jsonObj.getString("result");
					} else if (json instanceof JSONArray) {
				    	jsonArr = new JSONArray(jsonString);
				    	result = jsonArr.getJSONObject(0).getString("result");
					} else {
						throw new MojoExecutionException(
								"Cannot parse the resulting JSON in " + jsonString);
					}
				    getLog().info("Version got " + result);
				    version = Integer.parseInt(result.substring(0, result.indexOf(".", 1)));
				    getLog().info("MarkLogic Main Version got " + version);
				} catch (JSONException e) {
					throw new MojoExecutionException(
							"Unable to parse json error header", e);
				} catch (IOException e) {
					throw new MojoExecutionException(
							"IOException parsing json error", e);
				}
	        }
	    }
	    return version; 
    }
    
    protected String getConfigServerID() throws MojoExecutionException {
        HttpClient httpClient = this.getHttpClient();
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("group-id", group));
        qparams.add(new BasicNameValuePair("format", "json"));

        URI uri;
        try {
            uri = URIUtils.createURI("http", this.host, configPort, "/manage/LATEST/servers/" + configServer,
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
        
    	return sid;
    }
    
    protected HttpResponse executeML5BootstrapQuery(String query) throws MojoExecutionException {
        HttpClient httpClient = this.getHttpClient();
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        URI uri;
        HttpResponse response;
    	String sid = getConfigServerID();
    	
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

		Header[] headers = response.getHeaders("qconsole");
		if (headers != null && headers.length > 0) {
			try {
				JSONObject json = new JSONObject(headers[0].getValue());
				if (json.getString("type").equals("error")) {
					StringBuilder b = new StringBuilder(
							"Failed to execute query ...\n");
					if (response.getEntity() != null) {
						InputStream is = response.getEntity().getContent();
						JSONObject jsonError = new JSONObject(
								IOUtil.toString(is));
						b.append(XML.toString(jsonError));
					}
					throw new MojoExecutionException(b.toString());
				}
			} catch (JSONException e) {
				throw new MojoExecutionException(
						"Unable to parse json error header", e);
			} catch (IOException ioe) {
				throw new MojoExecutionException(
						"IOException parsing json error", ioe);
			}
		}
        return response;
    }


    protected HttpResponse executeML7BootstrapQuery(String query) throws MojoExecutionException {
        HttpClient httpClient = this.getHttpClient();
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        URI uri;
        StringEntity payload ;
        HttpResponse response;
        String sid = getConfigServerID();

        qparams.add(new BasicNameValuePair("sid", sid));
        qparams.add(new BasicNameValuePair("action", "eval"));
        qparams.add(new BasicNameValuePair("querytype", "xquery"));
        getLog().info("POST request::param sid="+sid);
        
        try {
            uri = URIUtils.createURI("http", this.host, bootstrapPort, "/qconsole/endpoints/evaler.xqy",
                    URLEncodedUtils.format(qparams, "UTF-8"), null);
        } catch (URISyntaxException e1) {
            throw new MojoExecutionException("Invalid uri for bootstrap query", e1);
        }
        getLog().info("POST request::uri="+uri.toASCIIString());

        HttpPost httpPost = new HttpPost(uri);

        try {
			payload = new StringEntity(query);
		} catch (UnsupportedEncodingException e) {
			throw new MojoExecutionException("Error creating payload for POST bootstrap server",e);
		}
        getLog().info("POST payload Content-Type=" + payload.getContentType());
        
        httpPost.setEntity(payload);
        httpPost.setHeader("content-type","text/plain");
        try {
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing POST to create bootstrap server", e);
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new MojoExecutionException("POST response failed: " + response.getStatusLine());
        }

		Header[] headers = response.getHeaders("qconsole");
		if (headers != null && headers.length > 0) {
			try {
				JSONObject json = new JSONObject(headers[0].getValue());
				if (json.getString("type").equals("error")) {
					StringBuilder b = new StringBuilder(
							"Failed to execute query ...\n");
					if (response.getEntity() != null) {
						InputStream is = response.getEntity().getContent();
						JSONObject jsonError = new JSONObject(
								IOUtil.toString(is));
						b.append(XML.toString(jsonError));
					}
					throw new MojoExecutionException(b.toString());
				}
			} catch (JSONException e) {
				throw new MojoExecutionException(
						"Unable to parse json error header", e);
			} catch (IOException ioe) {
				throw new MojoExecutionException(
						"IOException parsing json error", ioe);
			}
		}
        return response;
    }

    protected boolean isMarkLogic8() {
    	if (this.marklogicVersion == 8)
    		return true;
    	return false;
    }
    

    protected boolean isMarkLogic7() {
    	if (this.marklogicVersion == 7)
    		return true;
    	return false;
    }
    
    protected boolean isMarkLogic6() {
    	if (this.marklogicVersion == 6)
    		return true;
    	return false;
    }
    
	protected boolean isMarkLogic5() throws MojoExecutionException {
	    HttpClient httpClient = this.getHttpClient();
	    List<NameValuePair> qparams = new ArrayList<NameValuePair>();
	    URI uri;
	
		try {
	        uri = URIUtils.createURI("http", this.host, bootstrapPort, "/qconsole",
	                URLEncodedUtils.format(qparams, "UTF-8"), null);
	    } catch (URISyntaxException e1) {
	        throw new MojoExecutionException("Invalid uri for qconsole probe", e1);
	    }
	
	    HttpGet httpGet = new HttpGet(uri);
	
	    HttpResponse response;
		try {
	        response = httpClient.execute(httpGet);
	        if (response.getEntity() != null) {
	        	response.getEntity().getContent();
	        }
	    } catch (Exception e) {
	        throw new MojoExecutionException("Error executing GET to proble qconsole", e);
	    }
	    
	    getLog().debug("Probe got " + response.getStatusLine());
	    
	    return (response.getStatusLine().getStatusCode() == 200);
	}

}
