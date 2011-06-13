package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Remove the bootstrap configuration created by the marklogic:bootstrap goal.
 * 
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @author <a href="mailto:bob.browning@pressassociation.com">Bob Browning</a>
 * @goal bootstrap-uninstall
 */
public class BootstrapUninstallMojo extends AbstractBootstrapMojo {	

	protected String getBootstrapExecuteQuery() {
	  StringBuilder sb = new StringBuilder();
    
    sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
    sb.append("try { admin:save-configuration(                                         \n");
    sb.append("        admin:appserver-delete( admin:get-configuration()               \n");
    sb.append("                              , xdmp:server('" + xdbcName + "') )       \n");
    sb.append("      ) } catch ($e) { () }                                             \n");
    sb.append(";\n");

    sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
    sb.append("try { admin:save-configuration(                                               \n");
    sb.append("        admin:database-delete( admin:get-configuration()                      \n");
    sb.append("                             , xdmp:database('" + xdbcModulesDatabase + "') ) \n");
    sb.append("      ) } catch ($e) { () }                                                   \n");
    sb.append(";\n");
    
    sb.append(XQUERY_PROLOG);
    sb.append(ML_ADMIN_MODULE_IMPORT);
    sb.append("try { admin:save-configuration(                                                      \n");
    sb.append("        admin:forest-delete( admin:get-configuration()                               \n");
    sb.append("                           , xdmp:forest('" + xdbcModulesDatabase + "'), fn:true() ) \n");
    sb.append("      ) } catch ($e) { () }                                                          \n");
    sb.append(";\n");

		sb.append("'Bootstrap Uninstall - OK'");
	  
	  getLog().debug(sb.toString());
	  
	  return sb.toString();
	}
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("bootstrap-uninstall execute");
		super.execute();
	}	
}
