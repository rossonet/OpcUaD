package org.rossonet.opcuad.neo4j;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.summary.ResultSummary;

class Neo4jSystemWrapperTest {

	@Test
	void testClient() throws IOException, InterruptedException {
		Neo4jSystemWrapper neo4jSystemWrapper = null;
		try {
			neo4jSystemWrapper = new LocalNeo4jSystemWrapper();
			neo4jSystemWrapper.startup();
			Thread.sleep(5000);
			final Driver neo4jClient = neo4jSystemWrapper.getNeo4jClient();
			final Session session = neo4jClient.session();
			final Result result = session.run("CREATE (n)");
			final ResultSummary summary = result.consume();
			System.out.println(summary.counters().nodesCreated());
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		if (neo4jSystemWrapper != null) {
			neo4jSystemWrapper.shutdown();
		}
	}

	@Test
	void testInstallIfNeeded() throws IOException, InterruptedException {
		final LocalNeo4jSystemWrapper neo4jSystemWrapper = new LocalNeo4jSystemWrapper();
		neo4jSystemWrapper.installIfNeeded();
	}

	@Test
	void testStartStop() throws IOException, InterruptedException {
		final Neo4jSystemWrapper neo4jSystemWrapper = new LocalNeo4jSystemWrapper();
		neo4jSystemWrapper.startup();
		Thread.sleep(60000);
		neo4jSystemWrapper.shutdown();
	}

}
