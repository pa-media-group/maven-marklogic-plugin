package com.marklogic.maven;

import com.marklogic.ps.RecordLoader;
import com.marklogic.recordloader.Configuration;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;

import java.util.Properties;

/**
 * Load data into a MarkLogic database.
 *
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 */
@MojoGoal("record-load")
public class RecordLoaderMojo extends AbstractMarkLogicMojo {

    /**
     * Pattern of documents to be loaded.
     */
    @MojoParameter(expression = "${marklogic.recordLoader.inputPattern}")
    protected String inputPattern;

    /**
     * Path of documents to be loaded.
     */
    @MojoParameter(expression = "${marklogic.recordLoader.inputPath}")
    protected String inputPath;

    /**
     * Prefix to be removed from input filename.
     */
    @MojoParameter(expression = "${marklogic.recordLoader.inputPattern}")
    protected String inputStripPrefix;

    /**
     * Should paths be normalized.
     */
    @MojoParameter(expression = "${marklogic.recordLoader.inputNormalizePaths}")
    protected Boolean inputNormalizePaths;

    /**
     * Document format ( binary, xml or text ).
     */
    @MojoParameter(expression = "${marklogic.recordLoader.documentFormat}")
    protected String documentFormat;

    /**
     * Number of threads to use.
     */
    @MojoParameter(expression = "${marklogic.recordLoader.threads}")
    protected Integer threads;

    /**
     * URI prefix to load documents in with.
     */
    @MojoParameter(expression = "${marklogic.recordLoader.uriPrefix}")
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
            Configuration config = new Configuration();
            Properties properties = getProperties();
            config.load(properties);
            RecordLoader rl = new RecordLoader(config);
            rl.run();
        } catch (Exception e) {
            throw new MojoExecutionException("Error invoking RecordLoader", e);
        }
    }
}
