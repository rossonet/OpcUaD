package org.rossonet.opcuad;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.NamespaceTable;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.neo4j.driver.Driver;
import org.rossonet.opcua.milo.server.listener.AuditListener;
import org.rossonet.opcua.milo.server.storage.OnMemoryStorageController;
import org.rossonet.opcuad.neo4j.LocalNeo4jSystemWrapper;
import org.rossonet.opcuad.neo4j.Neo4jSystemWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedHashMultiset;

public class Neo4jStorageController extends OnMemoryStorageController implements AuditListener {

	private static final Logger logger = LoggerFactory.getLogger(Neo4jStorageController.class);

	private final Neo4jSystemWrapper neo4jServer = new LocalNeo4jSystemWrapper();

	@Override
	public Optional<UaNode> addNode(final UaNode node) {
		// TODO Auto-generated method stub
		return super.addNode(node);
	}

	@Override
	public synchronized void addReference(final Reference reference) {
		// TODO Auto-generated method stub
		super.addReference(reference);
	}

	@Override
	public synchronized void addReferences(final Reference reference, final NamespaceTable namespaceTable) {
		// TODO Auto-generated method stub
		super.addReferences(reference, namespaceTable);
	}

	@Override
	public void attributeChanged(final UaNode node, final AttributeId attributeId, final Object value) {
		// TODO Auto-generated method stub
		super.attributeChanged(node, attributeId, value);
	}

	@Override
	public boolean containsNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		// TODO Auto-generated method stub
		return super.containsNode(nodeId, namespaceTable);
	}

	@Override
	public boolean containsNode(final NodeId nodeId) {
		// TODO Auto-generated method stub
		return super.containsNode(nodeId);
	}

	@Override
	public void createClass() {
		// TODO Auto-generated method stub
		super.createClass();
	}

	public Driver getNeo4jClient() {
		return neo4jServer.getNeo4jClient();
	}

	@Override
	public Optional<UaNode> getNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		// TODO Auto-generated method stub
		return super.getNode(nodeId, namespaceTable);
	}

	@Override
	public Optional<UaNode> getNode(final NodeId nodeId) {
		// TODO Auto-generated method stub
		return super.getNode(nodeId);
	}

	@Override
	public List<NodeId> getNodeIds() {
		// TODO Auto-generated method stub
		return super.getNodeIds();
	}

	@Override
	public ConcurrentMap<NodeId, UaNode> getNodeMap() {
		// TODO Auto-generated method stub
		return super.getNodeMap();
	}

	@Override
	public List<UaNode> getNodes() {
		// TODO Auto-generated method stub
		return super.getNodes();
	}

	@Override
	public ConcurrentMap<NodeId, LinkedHashMultiset<Reference>> getReferenceMap() {
		// TODO Auto-generated method stub
		return super.getReferenceMap();
	}

	@Override
	public synchronized List<Reference> getReferences(final NodeId nodeId) {
		// TODO Auto-generated method stub
		return super.getReferences(nodeId);
	}

	@Override
	public List<Reference> getReferences(final NodeId nodeId, final Predicate<Reference> filter) {
		// TODO Auto-generated method stub
		return super.getReferences(nodeId, filter);
	}

	@Override
	public Optional<UaNode> removeNode(final ExpandedNodeId nodeId, final NamespaceTable namespaceTable) {
		// TODO Auto-generated method stub
		return super.removeNode(nodeId, namespaceTable);
	}

	@Override
	public Optional<UaNode> removeNode(final NodeId nodeId) {
		// TODO Auto-generated method stub
		return super.removeNode(nodeId);
	}

	@Override
	public synchronized void removeReference(final Reference reference) {
		// TODO Auto-generated method stub
		super.removeReference(reference);
	}

	@Override
	public synchronized void removeReferences(final Reference reference, final NamespaceTable namespaceTable) {
		// TODO Auto-generated method stub
		super.removeReferences(reference, namespaceTable);
	}

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
