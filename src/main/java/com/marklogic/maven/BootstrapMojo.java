package com.marklogic.maven;

import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentFactory;
import com.marklogic.xcc.Session;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Create the necessary bootstrap configuration that the MarkLogic Plugin 
 * requires for executing its goals.
 * 
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @author <a href="mailto:bob.browning@pressassociation.com">Bob Browning</a>
 * @goal bootstrap
 * 
 */
public class BootstrapMojo extends AbstractBootstrapMojo {

    private String createDatabase() {
        StringBuilder xquery = new StringBuilder();

        // TODO: Handle errors caused by database or forest already existing
        xquery.append(XQUERY_PROLOG);
        xquery.append(ML_ADMIN_MODULE_IMPORT);
        xquery.append(XQueryFactory.getAdminConfiguration());
        xquery.append(XQueryFactory.createDatabase(xdbcModulesDatabase));
        xquery.append(XQueryFactory.saveAdminConfiguration());

        return XQueryFactory.eval(xquery.toString());
    }

    private String createForest() {
        StringBuilder xquery = new StringBuilder();

        xquery.append(XQUERY_PROLOG);
        xquery.append(ML_ADMIN_MODULE_IMPORT);
        xquery.append(XQueryFactory.getAdminConfiguration());
        xquery.append(XQueryFactory.createForest(xdbcModulesDatabase));
        xquery.append(XQueryFactory.saveAdminConfiguration());

        return XQueryFactory.eval(xquery.toString());
    }

    private String attachForestToDatabase() {
        StringBuilder xquery = new StringBuilder();

        xquery.append(XQUERY_PROLOG);
        xquery.append(ML_ADMIN_MODULE_IMPORT);
        xquery.append(XQueryFactory.getAdminConfiguration());
        xquery.append(XQueryFactory.attachForest(xdbcModulesDatabase, xdbcModulesDatabase));
        xquery.append(XQueryFactory.saveAdminConfiguration());

        return XQueryFactory.eval(xquery.toString());
    }

    private String createWebDAVServer() {
        StringBuilder xquery = new StringBuilder();

        xquery.append(XQUERY_PROLOG);
        xquery.append(ML_ADMIN_MODULE_IMPORT);
        xquery.append(XQueryFactory.getAdminConfiguration());
        xquery.append(XQueryFactory.createWebDavServer(xdbcName + "-WebDAV", xdbcModuleRoot, xdbcPort, xdbcModulesDatabase));
        xquery.append(XQueryFactory.saveAdminConfiguration());
        return XQueryFactory.eval(xquery.toString());

    }

    private String createXDBCServer() {
        StringBuilder xquery = new StringBuilder();

        // TODO: Handle database existence error
        xquery.append(XQUERY_PROLOG);
        xquery.append(ML_ADMIN_MODULE_IMPORT);
        xquery.append(XQueryFactory.getAdminConfiguration());
        xquery.append(XQueryFactory.createXDBCServer(xdbcName, xdbcModuleRoot, xdbcPort, xdbcModulesDatabase, "Security"));
        xquery.append(XQueryFactory.saveAdminConfiguration(false));

        return XQueryFactory.eval(xquery.toString());
    }

    protected String getBootstrapExecuteQuery() {
        StringBuilder sb = new StringBuilder();

        sb.append(XQUERY_PROLOG);
        sb.append(createDatabase());
        sb.append(createForest());
        sb.append(attachForestToDatabase());
        sb.append(createWebDAVServer());
        sb.append(createXDBCServer());

        sb.append("return 'Bootstrap Install - OK'");

        /* Log xquery invocation */
        getLog().debug(sb.toString());

        return sb.toString();
    }

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("bootstrap execute");
		super.execute();

        this.database = xdbcModulesDatabase;

        Session session = getXccSession();

        String[] paths = { "/install.xqy"
                         , "/lib/lib-app-server.xqy"
                         , "/lib/lib-cpf.xqy"
                         , "/lib/lib-database-add.xqy"
                         , "/lib/lib-database-set.xqy"
                         , "/lib/lib-database.xqy"
                         , "/lib/lib-field.xqy"
                         , "/lib/lib-index.xqy"
                         , "/lib/lib-install.xqy"
                         , "/lib/lib-load.xqy" };

        ClassLoader loader =  Thread.currentThread().getContextClassLoader();
        for (String path : paths) {
                getLog().info("Uploading " + path);
            try {
                Content cs = ContentFactory.newContent(path, loader.getResource("xquery" + path), null);
                session.insertContent(cs);
            } catch (Exception e) {
                getLog().error("Failed to insert required library.");
            }
            session.commit();
        }
    }
}
