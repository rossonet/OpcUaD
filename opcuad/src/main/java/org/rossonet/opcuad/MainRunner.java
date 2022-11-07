package org.rossonet.opcuad;

import org.rossonet.opcua.milo.server.Ar4kOpcUaServer;
import org.rossonet.opcua.milo.server.conf.OpcUaServerConfiguration;
import org.rossonet.opcua.milo.server.listener.ShutdownListener;
import org.rossonet.opcua.milo.server.listener.ShutdownReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainRunner {

	public static boolean running = true;

	private static final Logger logger = LoggerFactory.getLogger(MainRunner.class);
	private static OpcUaServerConfiguration opcServerConfiguration;

	private static Ar4kOpcUaServer server = null;

	public static OpcUaServerConfiguration getOpcServerConfiguration() {
		return opcServerConfiguration;
	}

	public static Ar4kOpcUaServer getServer() {
		return server;
	}

	public static boolean isRunning() {
		return running;
	}

	public static void main(final String[] args) throws Exception {
		logger.info("starting OPCUA server");
		startOpcUaServer();
		while (running) {
			Thread.sleep(2000);
		}
		stopOpcUaServer();
		logger.info("OPCUA server shutdown completed!");
	}

	public static void startOpcUaServer() throws Exception {
		opcServerConfiguration = new OpcUaServerConfiguration();
		final Neo4jStorageController storageController = new Neo4jStorageController();
		server = Ar4kOpcUaServer.getNewServer(opcServerConfiguration, storageController);
		final ShutdownListener shutdownReason = new ShutdownListener() {
			@Override
			public void shutdown(final ShutdownReason reason) {
				logger.warn("shutdown reason " + reason);
				running = false;
			}
		};
		server.addShutdownHook(shutdownReason);
		server.addAuditHook(storageController);
		server.startup();
	}

	public static void stopOpcUaServer() throws Exception {
		if (server != null) {
			server.shutdown();
		}
	}

	public static void waitStarted() throws InterruptedException {
		if (server != null) {
			server.waitServerStarted();
		}

	}
}
