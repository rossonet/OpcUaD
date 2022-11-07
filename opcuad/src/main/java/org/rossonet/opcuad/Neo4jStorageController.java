package org.rossonet.opcuad;

import org.rossonet.opcua.milo.server.listener.AuditListener;
import org.rossonet.opcua.milo.server.storage.OnMemoryStorageController;
import org.rossonet.opcuad.neo4j.Neo4jSystemWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4jStorageController extends OnMemoryStorageController implements AuditListener {

	private static final Logger logger = LoggerFactory.getLogger(Neo4jStorageController.class);

	private final Neo4jSystemWrapper neo4jServer = new Neo4jSystemWrapper();

	@Override
	public void shutdown() {
		neo4jServer.shutdown();
		super.shutdown();
	}

	@Override
	public void startup() {
		logger.info("** startup storage controller " + this.getClass().getName());
		neo4jServer.startup();
		super.startup();
	}

}
