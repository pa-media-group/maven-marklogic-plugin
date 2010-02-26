package com.marklogic.maven;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.dir.DirectoryArchiver;
import org.codehaus.plexus.archiver.ResourceIterator;

/**
 * This a special Archiver that behaves very much like the DirectoryArchiver.
 * This Archiver is used by the LoaderMojo, as a convenient (hacky) way
 * to get basic includes/excludes behavior for handling resources without
 * reinventing the wheel.
 * 
 * @author mhelmstetter
 *
 */
public class Archiver extends DirectoryArchiver {

	public void execute() throws ArchiverException, IOException {
		// Ripped this from DirectoryArchiver
		ResourceIterator iter = getResources();
		if (!iter.hasNext()) {
			throw new ArchiverException("You must set at least one file.");
		}

		File destDirectory = getDestFile();
		if (destDirectory == null) {
			throw new ArchiverException(
					"You must set the destination directory.");
		}
		if (destDirectory.exists() && !destDirectory.isDirectory()) {
			throw new ArchiverException(destDirectory + " is not a directory.");
		}
		if (destDirectory.exists() && !destDirectory.canWrite()) {
			throw new ArchiverException(destDirectory + " is not writable.");
		}

		getLogger().info("Copying files to " + destDirectory.getAbsolutePath());

		try {
			while (iter.hasNext()) {
				ArchiveEntry f = iter.next();
//				// Check if we don't add directory file in itself
//				if (ResourceUtils.isSame(f.getResource(), destDirectory)) {
//					throw new ArchiverException(
//							"The destination directory cannot include itself.");
//				}
				String fileName = f.getName();
				String destDir = destDirectory.getCanonicalPath();
				fileName = destDir + File.separator + fileName;
				getLogger().info("    " + fileName);
//				copyFile(f, fileName);
			}
		} catch (IOException ioe) {
			String message = "Problem copying files : " + ioe.getMessage();
			throw new ArchiverException(message, ioe);
		}
	}
}
