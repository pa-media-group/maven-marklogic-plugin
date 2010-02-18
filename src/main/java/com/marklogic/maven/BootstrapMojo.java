package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Create the necessary bootstrap configuration that the MarkLogic Plugin 
 * requires for executing its goals.
 * 
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @goal bootstrap
 * 
 */
public class BootstrapMojo extends AbstractBootstrapMojo {	
 
	protected String getBootstrapExecuteQuery() {
		return "xquery version '1.0-ml';"
		+"\n	import module namespace admin = 'http://marklogic.com/xdmp/admin' at '/MarkLogic/admin.xqy';"
		+"\n	let $config := admin:get-configuration()"
		+"\n	let $database := xdmp:database('Security')"
		//+"\n	let $config := admin:http-server-create($config, admin:group-get-id($config, 'Default'), '"+httpName+"', '"+httpPath+"', "+httpPort+", $database, $database )"
		+"\n	let $config := admin:xdbc-server-create($config, admin:group-get-id($config, 'Default'), '"+xdbcName+"', '"+xdbcModuleRoot+"', "+xdbcPort+", 'file-system', $database )"
		+"\n	let $config := admin:save-configuration($config)"
		+"\n	return	'Bootstrap Install - OK'"
		;
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("bootstrap execute");
		super.execute();
	}
	
	
}
