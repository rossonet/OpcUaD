/*
 * Copyright (c) 2021 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.rossonet.opcuad;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.client.security.DefaultClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.rossonet.opcua.milo.client.KeyStoreLoaderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRossonetTestRunner {
	static {
		// Required for SecurityPolicy.Aes256_Sha256_RsaPss
		Security.addProvider(new BouncyCastleProvider());
	}

	private final ClientExample clientExample;

	private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final boolean serverRequired;
	private DefaultTrustListManager trustListManager;

	public ClientRossonetTestRunner(final ClientExample clientExample) throws Exception {
		this(clientExample, true);
	}

	public ClientRossonetTestRunner(final ClientExample clientExample, final boolean serverRequired) throws Exception {
		this.clientExample = clientExample;
		this.serverRequired = serverRequired;

		if (serverRequired) {
			logger.info("starting server");
			MainRunner.startOpcUaServer();
			MainRunner.waitStarted();
		}
	}

	public boolean getTestResult() {
		return clientExample.getTestResult();
	}

	public void run() {
		try {
			final OpcUaClient client = createClient();

			// For the sake of the examples we will create mutual trust between the client
			// and
			// server so we can run them with security enabled by default.
			// If the client example is pointed at another server then the rejected
			// certificate
			// will need to be moved from the security "pki/rejected" directory to the
			// "pki/trusted/certs" directory.

			// Make the example server trust the example client certificate by default.
			/*
			 * client.getConfig().getCertificate().ifPresent(certificate ->
			 * exampleServer.getServer().getConfig()
			 * .getTrustListManager().addTrustedCertificate(certificate));
			 *
			 * // Make the example client trust the example server certificate by default.
			 * exampleServer.getServer().getConfig().getCertificateManager().getCertificates
			 * () .forEach(certificate ->
			 * trustListManager.addTrustedCertificate(certificate));
			 */
			future.whenCompleteAsync((c, ex) -> {
				if (ex != null) {
					logger.error("Error running example: {}", ex.getMessage(), ex);
				}

				try {
					client.disconnect().get();
					if (serverRequired) {
						MainRunner.stopOpcUaServer();
					}
					Stack.releaseSharedResources();
				} catch (final Exception e) {
					logger.error("Error disconnecting: {}", e.getMessage(), e);
				}

				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			});

			try {
				clientExample.run(client, future);
				future.get(120, TimeUnit.SECONDS);
			} catch (final Throwable t) {
				logger.error("Error running client example: {}", t.getMessage(), t);
				future.completeExceptionally(t);
			}
		} catch (final Throwable t) {
			logger.error("Error getting client: {}", t.getMessage(), t);

			future.completeExceptionally(t);

			try {
				Thread.sleep(1000);
				// System.exit(0);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("** before sleep...");
		try {
			if (serverRequired) {
				MainRunner.stopOpcUaServer();
				Thread.sleep(2000);
			}
			logger.info("** after sleep, client shutdown completed");
		} catch (final Exception e) {
			// not important
		}
	}

	private OpcUaClient createClient() throws Exception {
		final Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "client", "security");
		Files.createDirectories(securityTempDir);
		if (!Files.exists(securityTempDir)) {
			throw new Exception("unable to create security dir: " + securityTempDir);
		}

		final File pkiDir = securityTempDir.resolve("pki").toFile();

		LoggerFactory.getLogger(getClass()).info("security dir: {}", securityTempDir.toAbsolutePath());
		LoggerFactory.getLogger(getClass()).info("security pki dir: {}", pkiDir.getAbsolutePath());

		final KeyStoreLoaderClient loader = new KeyStoreLoaderClient().load(securityTempDir);

		trustListManager = new DefaultTrustListManager(pkiDir);

		final DefaultClientCertificateValidator certificateValidator = new DefaultClientCertificateValidator(
				trustListManager);

		return OpcUaClient.create(clientExample.getEndpointUrl(),
				endpoints -> endpoints.stream().filter(clientExample.endpointFilter()).findFirst(),
				configBuilder -> configBuilder.setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
						.setApplicationUri("urn:eclipse:milo:examples:client").setKeyPair(loader.getClientKeyPair())
						.setCertificate(loader.getClientCertificate())
						.setCertificateChain(loader.getClientCertificateChain())
						.setCertificateValidator(certificateValidator)
						.setIdentityProvider(clientExample.getIdentityProvider()).setRequestTimeout(uint(5000))
						.build());
	}

}
