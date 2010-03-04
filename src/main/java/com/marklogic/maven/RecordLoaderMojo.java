package com.marklogic.maven;

import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

import com.marklogic.ps.RecordLoader;
import com.marklogic.recordloader.Configuration;

/**
 * Load data into a MarkLogic database.
 * 
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @goal record-load
 */
public class RecordLoaderMojo extends AbstractMarkLogicMojo {
    
	/**
     * 
     * @parameter expression="${marklogic.recordLoader.inputPattern}"
     */
    protected String inputPattern;
    
	/**
     * 
     * @parameter expression="${marklogic.recordLoader.inputPath}"
     */
    protected String inputPath;
    
	/**
     * 
     * @parameter expression="${marklogic.recordLoader.inputPattern}"
     */
    protected String inputStripPrefix;
    
    /**
     * 
     * @parameter expression="${marklogic.recordLoader.inputNormalizePaths}"
     */
    protected Boolean inputNormalizePaths;    
    
	/**
     * 
     * @parameter expression="${marklogic.recordLoader.documentFormat}"
     */
    protected String documentFormat;
    
	/**
     * 
     * @parameter expression="${marklogic.recordLoader.threads}"
     */
    protected Integer threads;        
    
	/**
     * 
     * @parameter expression="${marklogic.recordLoader.uriPrefix}"
     */
    protected String uriPrefix;    
    
    
    private Properties getProperties() {
    	Properties p = new Properties();
    	p.put("CONNECTION_STRING", getXdbcConnectionString());
    	if (inputPath != null) {
    		p.put("INPUT_PATH", inputPath);
    	}
    	if (inputPattern != null) {
    		p.put("INPUT_PATTERN", inputPattern);
    	}
    	if (inputStripPrefix != null) {
    		// if we're on windows, we need to convert the \ in the path to /
    		inputStripPrefix = StringUtils.replace(inputStripPrefix, '\\', '/');
    		p.put("INPUT_STRIP_PREFIX", inputStripPrefix);
    	}
    	
    	if (inputNormalizePaths != null && inputNormalizePaths) {
    		p.put("INPUT_NORMALIZE_PATHS", "true");
    	}
    	if (documentFormat != null) {
    		p.put("DOCUMENT_FORMAT", documentFormat);
    	}
    	return p;
    }
    

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			//RecordLoader.main(getParams());
			
			Configuration config = new Configuration();
			Properties properties = getProperties();
			config.load(properties);
			RecordLoader rl = new RecordLoader(config);
			rl.run();
			
//		} catch (IOException e) {
//			throw new MojoExecutionException("Error invoking RecordLoader", e);
//		} catch (URISyntaxException e) {
//			throw new MojoExecutionException("Bad connection uri", e);
		} catch (Exception e) {
			throw new MojoExecutionException("Error invoking RecordLoader", e);
		}
	}



}
