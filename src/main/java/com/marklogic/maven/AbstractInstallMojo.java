package com.marklogic.maven;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.types.ValueType;


public abstract class AbstractInstallMojo extends AbstractMarkLogicMojo {
	
    protected ResultSequence executeInstallAction(String action) throws RequestException {
    	Session session = this.getXccSession();
		Request request = session.newModuleInvoke("install/install.xqy");
		request.setNewStringVariable("action", action);
		request.setNewStringVariable("environ", "development");
		request.setNewVariable("delete-data", ValueType.XS_BOOLEAN, false);
		ResultSequence rs = session.submitRequest(request);
		return rs;
    }

}
