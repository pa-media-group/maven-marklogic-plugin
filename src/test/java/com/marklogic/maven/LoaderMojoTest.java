package com.marklogic.maven;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * Test for {@link LoaderMojo}
 * 
 * @version $Id: $
 */
public class LoaderMojoTest extends AbstractMojoTestCase {

	private File testPom = new File(getBasedir(),
			"src/test/resources/unit/loader-basic-test/pom.xml");

	protected void setUp() throws Exception {
		// required for mojo lookups to work
		super.setUp();
	}
	
	public void testEnvironment() throws Exception {
		LoaderMojo mojo = (LoaderMojo) lookupMojo( "load", testPom );
        assertNotNull( mojo );
	}

}
