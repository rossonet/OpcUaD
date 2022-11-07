package org.rossonet.opcuad.neo4j;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class Neo4jSystemWrapperTest {

	@Test
	void testInstallIfNeeded() throws IOException {
		final Neo4jSystemWrapper neo4jSystemWrapper = new Neo4jSystemWrapper();
		neo4jSystemWrapper.installIfNeeded();
	}

}
