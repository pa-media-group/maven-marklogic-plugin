package com.marklogic.maven;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.ValueFactory;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.types.ValueType;
import com.marklogic.xcc.types.XdmNode;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import java.io.*;


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

    /**
     * The path to the file controlling installation.
     *
     * @parameter default-value="${basedir}/src/main/marklogic/configuration.xml"
     * @required
     */
    protected File installConfigurationFile;

    protected ResultSequence executeInstallAction(String action, String module) throws RequestException {
    	Session session = this.getXccSession();
		Request request = session.newModuleInvoke(module);
		request.setNewStringVariable("action", action);
		request.setNewStringVariable("environ", environment);
		request.setNewVariable("delete-data", ValueType.XS_BOOLEAN, false);
        try {
            request.setNewStringVariable("configuration-string", getInstallConfiguration());
        } catch (IOException e) {
            throw new RequestException("Cannot load configuration file", request, e);
        }
        ResultSequence rs = session.submitRequest(request);
		return rs;
    }

    private String getInstallConfiguration() throws IOException {
        StringBuffer buffer = new StringBuffer((int)installConfigurationFile.length());
        BufferedReader reader = new BufferedReader(new FileReader(installConfigurationFile));
        char[] buf = new char[1024];
        int numRead = 0;
        while((numRead = reader.read(buf))!= -1) {
            buffer.append(buf, 0, numRead);
        }
        reader.close();
        return buffer.toString();
    }
}
