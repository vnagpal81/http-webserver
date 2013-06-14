package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Interface for a file repository. Supported operations are to be implemented
 * by concrete classes which actually represent real-world repositories.
 * 
 * @see LocalRepository
 * @see S3Repository
 * @see RemoteRepository
 * @see FTPRepository
 * 
 * @author vnagpal
 * 
 */
public interface Repository {

	/**
	 * Gets the request file handle as an input stream. The caller method bears
	 * the responsibility of retrying the lookup in case of connection failure
	 * 
	 * @param uri
	 *            requested file path
	 * @return the file as input stream. returns null if not found.
	 * @throws IOException
	 *             if not able to connect to the repo
	 */
	FileInputStream lookup(String uri) throws IOException;

	/**
	 * Creates a new file in the repository
	 * 
	 * @param file
	 *            the file contents as an in-memory java.io.File object
	 * @throws IOException
	 *             if file already exists or if not able to connect to the repo
	 */
	void create(File file) throws IOException;

	/**
	 * Checks for the existence of the specified file path Consumers should
	 * check for existence before any operation to avoid exceptions.
	 * 
	 * @param uri
	 *            path to lookup
	 * @return true if a file exists at the specified location, false otherwise
	 * @throws IOException
	 *             if not able to connect to the repo
	 */
	boolean exists(String uri) throws IOException;

	/**
	 * Deletes the specified file from the repo permanently. This action is
	 * irreversible and this method should be used with care and awareness
	 * especially when working with remote repos. If some repo extends the
	 * functionality of a soft delete and rescue, it must be implemented
	 * separately in the child class. Such methods, since not standard across
	 * all types of file storage, are not part of the top most ancestor in the
	 * class heirarchy
	 * 
	 * @param uri
	 *            the path to delete
	 * @throws IOException
	 *             if file does not exist at desired location or if not able to
	 *             connect to the repo
	 */
	void delete(String uri) throws IOException;

	/**
	 * Queries the repository for file info metadata.
	 * 
	 * @param uri
	 *            the path to get info for
	 * @return a wrapper over file info metadata
	 * @throws IOException
	 *             if file does not exist at desired location or if not able to
	 *             connect to the repo
	 */
	Metadata getInfo(String uri) throws IOException;
}
