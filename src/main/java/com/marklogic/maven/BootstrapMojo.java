package com.marklogic.maven;

import com.marklogic.xcc.*;
import com.marklogic.xcc.exceptions.XccConfigException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import javax.xml.transform.dom.DOMResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Enumeration;


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
   
	protected String getBootstrapExecuteQuery() {
	  StringBuilder sb = new StringBuilder();
	  
	  // TODO: Handle errors caused by database or forest already existing
    sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
    sb.append("let $config := admin:database-create( admin:get-configuration()                 \n");
    sb.append("                                    , '" + xdbcModulesDatabase + "'             \n");
    sb.append("                                    , xdmp:database('Security')                 \n");
    sb.append("                                    , xdmp:database('Schemas') )                \n");
    sb.append("return admin:save-configuration($config)                                        \n");
    sb.append(";\n");
    
    sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
    sb.append("let $config := admin:forest-create( admin:get-configuration()                   \n");
    sb.append("                                  , '" + xdbcModulesDatabase + "'               \n");
    sb.append("                                  , xdmp:host(), () )                           \n");
    sb.append("return admin:save-configuration($config)                                        \n");
    sb.append(";\n");
    
    sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
    sb.append("let $config := admin:database-attach-forest( admin:get-configuration()                     \n");
    sb.append("                                           , xdmp:database('" + xdbcModulesDatabase + "')  \n");
    sb.append("                                           , xdmp:forest('" + xdbcModulesDatabase + "') )  \n");
    sb.append("return admin:save-configuration($config)                                                   \n");
    sb.append(";\n");    

    // TODO: Handle database existence error
		sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
		sb.append("let $config := admin:get-configuration()                                              \n");
		sb.append("let $database := xdmp:database('Security')                                            \n");
		sb.append("let $config := admin:xdbc-server-create( $config                                      \n");
		sb.append("                                       , admin:group-get-id($config, 'Default')       \n");
		sb.append("                                       , '" + xdbcName + "'                           \n");
		sb.append("                                       , '" + xdbcModuleRoot + "'                     \n");
		sb.append("                                       ,  " + xdbcPort + "                            \n");
		sb.append("                                       , xdmp:database('" + xdbcModulesDatabase + "') \n");
		sb.append("                                       , $database )                                  \n");
		sb.append("let $config := admin:save-configuration($config)                                      \n");
		sb.append("return	'Bootstrap Install - OK'                                                       ");
		
		/* Log xquery invocation */
		getLog().debug(sb.toString());
		
		return sb.toString();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("bootstrap execute");
		super.execute();

        this.database = xdbcModulesDatabase;

        Session session = getXccSession();

        String[] paths = { "install/install.xqy"
                         , "lib/lib-app-server.xqy"
                         , "lib/lib-cpf.xqy"
                         , "lib/lib-database-add.xqy"
                         , "lib/lib-database-set.xqy"
                         , "lib/lib-database.xqy"
                         , "lib/lib-field.xqy"
                         , "lib/lib-index.xqy"
                         , "lib/lib-install.xqy"
                         , "lib/lib-load.xqy" };
        ClassLoader loader =  Thread.currentThread().getContextClassLoader();
        for (String path : paths) {
                getLog().info("Uploading " + path);
            try {
                Content cs = ContentFactory.newContent(path, loader.getResource("xquery/" + path), null);
                session.insertContent(cs);
            } catch (Exception e) {
                getLog().error("Failed to insert required library.", e);
            }
            session.commit();
        }
    }
}
