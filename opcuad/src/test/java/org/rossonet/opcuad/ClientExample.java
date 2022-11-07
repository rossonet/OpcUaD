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

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.rossonet.opcua.milo.server.conf.OpcUaServerConfiguration;

public interface ClientExample {

	default Predicate<EndpointDescription> endpointFilter() {
		return e -> getSecurityPolicy().getUri().equals(e.getSecurityPolicyUri());
	}

	default String getEndpointUrl() {
		return "opc.tcp://127.0.0.1:" + OpcUaServerConfiguration.DEFAULT_TCP_BIND_PORT
				+ OpcUaServerConfiguration.DEFAULT_DISCOVERY_PATH;
	}

	default IdentityProvider getIdentityProvider() {
		return new AnonymousProvider();
	}

	default SecurityPolicy getSecurityPolicy() {
		return SecurityPolicy.None;
	}

	boolean getTestResult();

	void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception;

}
