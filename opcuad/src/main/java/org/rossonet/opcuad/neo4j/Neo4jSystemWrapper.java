package org.rossonet.opcuad.neo4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4jSystemWrapper {

	private static final Logger logger = LoggerFactory.getLogger(Neo4jSystemWrapper.class);

	public final String ORIGINAL_TGZ_URL = "https://www.rossonet.net/dati/neo4j/neo4j-community-5.1.0-unix.tar.gz";

	private final Path neo4jDirectory = Paths.get("neo4j-bin");

	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public void startup() {
		try {
			installIfNeeded();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean checkDecompressVersionExists() {
		// TODO Auto-generated method stub
		return false;
	}

	private void createDirectory() throws IOException {
		Files.createDirectory(neo4jDirectory);

	}

	private void deleteDirectoryIfExists() {
		// TODO Auto-generated method stub

	}

	private void downloadArchive() {
		// TODO Auto-generated method stub

	}

	void installIfNeeded() throws IOException {
		if (!checkDecompressVersionExists()) {
			deleteDirectoryIfExists();
			createDirectory();
			downloadArchive();
		}

	}

}
