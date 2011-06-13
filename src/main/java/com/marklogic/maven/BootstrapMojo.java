package com.marklogic.maven;

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
   
	protected String getBootstrapExecuteQuery() {
	  StringBuilder sb = new StringBuilder();
	  
	  // TODO: Handle errors caused by database or forest already existing
    sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
    sb.append("let $config := admin:database-create( admin:get-configuration()                 \n");
    sb.append("                                    , 'InstallModules'                          \n");
    sb.append("                                    , xdmp:database('Security')                 \n");
    sb.append("                                    , xdmp:database('Schemas') )                \n");
    sb.append("return admin:save-configuration($config)                                        \n");
    sb.append(";\n");
    
    sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
    sb.append("let $config := admin:forest-create( admin:get-configuration()                   \n");
    sb.append("                                  , 'InstallModules'                            \n");
    sb.append("                                  , xdmp:host(), () )                           \n");
    sb.append("return admin:save-configuration($config)                                        \n");
    sb.append(";\n");
    
    sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
    sb.append("let $config := admin:database-attach-forest( admin:get-configuration()          \n");
    sb.append("                                           , xdmp:database('InstallModules')    \n");
    sb.append("                                           , xdmp:forest('InstallModules') )    \n");
    sb.append("return admin:save-configuration($config)                                        \n");
    sb.append(";\n");    

    // TODO: Handle database existence error
		sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
		sb.append("let $config := admin:get-configuration()                                        \n");
		sb.append("let $database := xdmp:database('Security')                                      \n");
		sb.append("let $config := admin:xdbc-server-create( $config                                \n");
		sb.append("                                       , admin:group-get-id($config, 'Default') \n");
		sb.append("                                       , '" + xdbcName + "'                     \n");
		sb.append("                                       , '" + xdbcModuleRoot + "'               \n");
		sb.append("                                       ,  " + xdbcPort + "                      \n");
		sb.append("                                       , xdmp:database('InstallModules')        \n");
		sb.append("                                       , $database )                            \n");
		sb.append("let $config := admin:save-configuration($config)                                \n");
		sb.append("return	'Bootstrap Install - OK'                                                   ");
		
		/* Log xquery invocation */
		getLog().debug(sb.toString());
		
		return sb.toString();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("bootstrap execute");
		super.execute();
	}
	
	
}
