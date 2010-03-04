package com.marklogic.maven;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.types.ValueType;


public abstract class AbstractInstallMojo extends AbstractMarkLogicMojo {
	
    /**
     * The installation module path.  This path is relative to the 
     * xdbcModuleRoot.
     * 
     * For example if the xdbcModuleRoot is set to /modules/,
     * and the installation module is deployed at /modules/install/install.xqy,
     * set the installModule to install/install.xqy.
     * 
     * The default value is just "install.xqy" since we assume that the xdbc server 
     * will point directly to the installation xquery.
     * 
     * @parameter default-value="install.xqy" expression="${marklogic.install.module}"
     */    
    protected String installModule;
    
    protected ResultSequence executeInstallAction(String action, String module) throws RequestException {
    	Session session = this.getXccSession();
		Request request = session.newModuleInvoke(module);
		request.setNewStringVariable("action", action);
		request.setNewStringVariable("environ", environment);
		request.setNewVariable("delete-data", ValueType.XS_BOOLEAN, false);
		ResultSequence rs = session.submitRequest(request);
		return rs;
    }

}
