package com.gempukku.libgdx.graph;

import com.gempukku.libgdx.graph.data.FieldType;
import com.gempukku.libgdx.graph.data.Graph;
import com.gempukku.libgdx.graph.data.GraphConnection;
import com.gempukku.libgdx.graph.data.GraphNode;
import com.gempukku.libgdx.graph.data.GraphNodeInput;
import com.gempukku.libgdx.graph.data.GraphNodeOutput;
import com.gempukku.libgdx.graph.data.GraphProperty;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class GraphDataLoaderCallback<T, U extends FieldType> implements GraphLoaderCallback<T>, Graph<GraphDataLoaderCallback.GraphNodeData<U>, GraphDataLoaderCallback.GraphConnectionData, GraphDataLoaderCallback.GraphPropertyData<U>, U> {
    private Map<String, GraphNodeData<U>> graphNodes = new HashMap<>();
    private List<GraphConnectionData> graphConnections = new LinkedList<>();
    private Map<String, GraphPropertyData<U>> graphProperties = new HashMap<>();

    @Override
    public void addPipelineNode(String id, String type, float x, float y, JSONObject data) {
        graphNodes.put(id, new GraphNodeData<U>(id, getNodeConfiguration(type, data), data));
    }

    @Override
    public void addPipelineVertex(String fromNode, String fromField, String toNode, String toField) {
        graphConnections.add(new GraphConnectionData(fromNode, fromField, toNode, toField));
    }

    @Override
    public void addPipelineProperty(String type, String name, JSONObject data) {
        graphProperties.put(name, new GraphPropertyData<U>(name, getFieldType(type), data));
    }

    @Override
    public GraphNodeData<U> getNodeById(String id) {
        return graphNodes.get(id);
    }

    @Override
    public GraphPropertyData<U> getPropertyByName(String name) {
        return graphProperties.get(name);
    }

    @Override
    public Iterable<? extends GraphConnectionData> getConnections() {
        return graphConnections;
    }

    @Override
    public Iterable<? extends GraphNodeData<U>> getNodes() {
        return graphNodes.values();
    }

    @Override
    public Iterable<? extends GraphPropertyData<U>> getProperties() {
        return graphProperties.values();
    }

    protected abstract U getFieldType(String type);

    protected abstract NodeConfiguration<U> getNodeConfiguration(String type, JSONObject data);

    protected static class GraphNodeData<T extends FieldType> implements GraphNode<T> {
        private String id;
        private NodeConfiguration<T> configuration;
        private JSONObject data;

        public GraphNodeData(String id, NodeConfiguration<T> configuration, JSONObject data) {
            this.id = id;
            this.configuration = configuration;
            this.data = data;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean isInputField(String fieldId) {
            return configuration.getNodeInputs().containsKey(fieldId);
        }

        @Override
        public Map<String, ? extends GraphNodeInput<T>> getInputs() {
            return configuration.getNodeInputs();
        }

        @Override
        public Map<String, ? extends GraphNodeOutput<T>> getOutputs() {
            return configuration.getNodeOutputs();
        }

        public NodeConfiguration<T> getConfiguration() {
            return configuration;
        }

        public JSONObject getData() {
            return data;
        }
    }

    protected static class GraphPropertyData<T extends FieldType> implements GraphProperty<T> {
        private String name;
        private T type;
        private JSONObject data;

        public GraphPropertyData(String name, T type, JSONObject data) {
            this.name = name;
            this.type = type;
            this.data = data;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public T getType() {
            return type;
        }

        public JSONObject getData() {
            return data;
        }
    }

    protected static class GraphConnectionData implements GraphConnection {
        private String nodeFrom;
        private String fieldFrom;
        private String nodeTo;
        private String fieldTo;

        public GraphConnectionData(String nodeFrom, String fieldFrom, String nodeTo, String fieldTo) {
            this.nodeFrom = nodeFrom;
            this.fieldFrom = fieldFrom;
            this.nodeTo = nodeTo;
            this.fieldTo = fieldTo;
        }

        @Override
        public String getNodeFrom() {
            return nodeFrom;
        }

        @Override
        public String getFieldFrom() {
            return fieldFrom;
        }

        @Override
        public String getNodeTo() {
            return nodeTo;
        }

        @Override
        public String getFieldTo() {
            return fieldTo;
        }
    }
}
