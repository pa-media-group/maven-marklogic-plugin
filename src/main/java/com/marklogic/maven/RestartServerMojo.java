package com.marklogic.maven;

import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Bob Browning <bob.browning@pressassociation.com>
 * @goal restart-server
 */
public class RestartServerMojo extends AbstractInstallMojo {

    protected static final String ACTION_RESTART = "restart";

    public void execute() throws MojoExecutionException, MojoFailureException {
        executeAction(ACTION_RESTART);

        /**
         * Ensure server is ready
         */
        int count = 10;
        boolean success = false;
        RequestException lastException = null;
        while(count-- > 0) {
            /* Try and get session */
            Session session = getXccSession();
            if(session != null) {
                /* Attempt simple xquery */
                AdhocQuery q = session.newAdhocQuery("xquery version \"1.0-ml\";\n1");
                try {
                    session.submitRequest(q);
                    success = true;
                } catch (RequestException e) {
                    lastException = e;
                } finally {
                    session.close();
                }
            }

            if(success) {
                break;
            } else {
                try {
                    getLog().info("Waiting for server to be ready.");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Interrupted
                }
            }
        }

        if(!success) {
            throw new MojoExecutionException("Job timed out waiting for servers on host to restart.", lastException);
        }
    }
}
