package org.rossonet.opcuad.neo4j;

import java.nio.file.Path;

import org.neo4j.driver.Driver;

public interface Neo4jSystemWrapper {

	void dumpDatabase(String database, Path targetPath);

	String getActualPassword();

	Driver getNeo4jClient();

	void loadDatabase(String database, Path sourcePath);

	void shutdown();

	void startup();

	boolean verifyConnection();

}
