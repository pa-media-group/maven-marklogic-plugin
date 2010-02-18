package com.marklogic.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Remove the bootstrap configuration created by the marklogic:bootstrap goal.
 * 
 * @author <a href="mailto:mark.helmstetter@marklogic.com">Mark Helmstetter</a>
 * @goal bootstrap-uninstall
 */
public class BootstrapUninstallMojo extends AbstractBootstrapMojo {	
 
	@Override
	protected String getBootstrapExecuteQuery() {
		return 	"xquery version '1.0-ml';"
		+"\n	import module namespace admin = 'http://marklogic.com/xdmp/admin' at '/MarkLogic/admin.xqy';"
		+"\n	let $config := admin:get-configuration()"
		//+"\n	let $config := try{admin:appserver-delete($config, xdmp:server('"+httpName+"'))}catch($e){$config}"
		+"\n	let $config := try{admin:appserver-delete($config, xdmp:server('"+xdbcName+"'))}catch($e){$config}"
		+"\n	let $config := try{admin:database-detach-forest($config, xdmp:database('"+databaseName+"'), xdmp:forest('"+forestName+"'))}catch($e){$config}"
		+"\n	let $config := admin:save-configuration-without-restart($config)"
		+"\n	let $config := admin:get-configuration()"
		+"\n	let $config := try{admin:database-delete($config, xdmp:database('"+databaseName+"'))}catch($e){$config}"
		+"\n	let $config := try{admin:forest-delete($config, xdmp:forest('"+forestName+"'), fn:true())}catch($e){$config}"
		+"\n	let $config := admin:save-configuration($config)"
		+"\n	return	'Bootstrap Uninstall - OK'"
		;
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("bootstrap-uninstall execute");
		super.execute();
	}	
}
