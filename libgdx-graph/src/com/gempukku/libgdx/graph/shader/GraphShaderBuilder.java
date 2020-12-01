package com.gempukku.libgdx.graph.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.gempukku.libgdx.graph.LibGDXCollections;
import com.gempukku.libgdx.graph.data.Graph;
import com.gempukku.libgdx.graph.data.GraphConnection;
import com.gempukku.libgdx.graph.data.GraphNode;
import com.gempukku.libgdx.graph.data.GraphNodeInput;
import com.gempukku.libgdx.graph.data.GraphProperty;
import com.gempukku.libgdx.graph.shader.builder.FragmentShaderBuilder;
import com.gempukku.libgdx.graph.shader.builder.GLSLFragmentReader;
import com.gempukku.libgdx.graph.shader.builder.VertexShaderBuilder;
import com.gempukku.libgdx.graph.shader.node.GraphShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.node.attribute.AttributePositionShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.node.provided.CameraPositionShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.property.GraphShaderPropertyProducer;

public class GraphShaderBuilder {
    private static GraphConfiguration[] configurations = new GraphConfiguration[]{new GraphShaderConfiguration(), new ModelShaderConfiguration()};

    public static GraphShader buildScreenShader(Texture defaultTexture,
                                                Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph,
                                                boolean designTime) {
        GraphShader graphShader = new GraphShader(defaultTexture);

        VertexShaderBuilder vertexShaderBuilder = new VertexShaderBuilder(graphShader);
        FragmentShaderBuilder fragmentShaderBuilder = new FragmentShaderBuilder(graphShader);

        initialize(graph, designTime, graphShader, vertexShaderBuilder, fragmentShaderBuilder);

        buildScreenVertexShader(graph, designTime, graphShader, vertexShaderBuilder, fragmentShaderBuilder);
        buildFragmentShader(graph, designTime, graphShader, vertexShaderBuilder, fragmentShaderBuilder);

        String vertexShader = vertexShaderBuilder.buildProgram();
        String fragmentShader = fragmentShaderBuilder.buildProgram();

        finalizeShader(graphShader, vertexShader, fragmentShader);

        debugShaders("screen", vertexShader, fragmentShader);

        return graphShader;
    }

    public static GraphShader buildModelShader(Texture defaultTexture,
                                               Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph,
                                               boolean designTime) {
        GraphShader graphShader = new GraphShader(defaultTexture);

        VertexShaderBuilder vertexShaderBuilder = new VertexShaderBuilder(graphShader);
        FragmentShaderBuilder fragmentShaderBuilder = new FragmentShaderBuilder(graphShader);

        initialize(graph, designTime, graphShader, vertexShaderBuilder, fragmentShaderBuilder);

        buildVertexShader(graph, designTime, graphShader, vertexShaderBuilder, fragmentShaderBuilder);
        buildFragmentShader(graph, designTime, graphShader, vertexShaderBuilder, fragmentShaderBuilder);

        String vertexShader = vertexShaderBuilder.buildProgram();
        String fragmentShader = fragmentShaderBuilder.buildProgram();

        finalizeShader(graphShader, vertexShader, fragmentShader);

        debugShaders("color", vertexShader, fragmentShader);

        return graphShader;
    }

    public static GraphShader buildModelDepthShader(Texture defaultTexture,
                                                    Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph,
                                                    boolean designTime) {
        GraphShader graphShader = new GraphShader(defaultTexture);

        VertexShaderBuilder vertexShaderBuilder = new VertexShaderBuilder(graphShader);
        FragmentShaderBuilder fragmentShaderBuilder = new FragmentShaderBuilder(graphShader);

        initialize(graph, designTime, graphShader, vertexShaderBuilder, fragmentShaderBuilder);

        buildVertexShader(graph, designTime, graphShader, vertexShaderBuilder, fragmentShaderBuilder);
        buildDepthFragmentShader(graph, designTime, graphShader, vertexShaderBuilder, fragmentShaderBuilder);

        String vertexShader = vertexShaderBuilder.buildProgram();
        String fragmentShader = fragmentShaderBuilder.buildProgram();

        finalizeShader(graphShader, vertexShader, fragmentShader);

        debugShaders("depth", vertexShader, fragmentShader);

        return graphShader;
    }

    private static void initialize(Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph, boolean designTime, GraphShader graphShader, VertexShaderBuilder vertexShaderBuilder, FragmentShaderBuilder fragmentShaderBuilder) {
        initializePropertyMap(graphShader, graph, designTime);
        initializeShaders(vertexShaderBuilder, fragmentShaderBuilder);
        setupOpenGLSettings(graphShader, graph);
    }

    private static void finalizeShader(GraphShader graphShader, String vertexShader, String fragmentShader) {
        ShaderProgram shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        graphShader.setProgram(shaderProgram);
        graphShader.init();
    }

    private static void initializePropertyMap(GraphShader graphShader, Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph, boolean designTime) {
        ObjectMap<String, PropertySource> propertyMap = new ObjectMap<>();
        for (GraphProperty<ShaderFieldType> property : graph.getProperties()) {
            String name = property.getName();
            propertyMap.put(name, findPropertyProducerByType(property.getType()).createProperty(name, property.getData(), designTime));
        }
        graphShader.setPropertySourceMap(propertyMap);
    }

    private static void setupOpenGLSettings(GraphShader graphShader, Graph graph) {
        GraphNode<ShaderFieldType> endNode = graph.getNodeById("end");
        JsonValue data = endNode.getData();

        String cullingValue = data.getString("culling", null);
        if (cullingValue != null)
            graphShader.setCulling(BasicShader.Culling.valueOf(cullingValue));

        String transparency = data.getString("transparency", null);
        if (transparency != null)
            graphShader.setTransparency(BasicShader.Transparency.valueOf(transparency));

        String blending = data.getString("blending", null);
        if (blending != null)
            graphShader.setBlending(BasicShader.Blending.valueOf(blending));

        String depthTest = data.getString("depthTest", null);
        if (depthTest != null)
            graphShader.setDepthTesting(BasicShader.DepthTesting.valueOf(depthTest.replace(' ', '_')));
    }

    private static void debugShaders(String type, String vertexShader, String fragmentShader) {
        Gdx.app.debug("Shader", "--------------");
        Gdx.app.debug("Shader", "Vertex " + type + " shader:");
        Gdx.app.debug("Shader", "--------------");
        Gdx.app.debug("Shader", "\n" + vertexShader);
        Gdx.app.debug("Shader", "----------------");
        Gdx.app.debug("Shader", "Fragment " + type + " shader:");
        Gdx.app.debug("Shader", "----------------");
        Gdx.app.debug("Shader", "\n" + fragmentShader);
    }

    private static void buildDepthFragmentShader(Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph, boolean designTime, GraphShader graphShader, VertexShaderBuilder vertexShaderBuilder, FragmentShaderBuilder fragmentShaderBuilder) {
        fragmentShaderBuilder.addUniformVariable("u_cameraClipping", "vec2", true, UniformSetters.cameraClipping);

        AttributePositionShaderNodeBuilder position = new AttributePositionShaderNodeBuilder();
        JsonValue positionData = new JsonValue(JsonValue.ValueType.object);
        positionData.addChild("coordinates", new JsonValue("world"));
        GraphShaderNodeBuilder.FieldOutput positionField = position.buildFragmentNodeSingleInputs(false, "defaultPositionAttribute", positionData, LibGDXCollections.<String, GraphShaderNodeBuilder.FieldOutput>emptyMap(),
                LibGDXCollections.singleton("position"), vertexShaderBuilder, fragmentShaderBuilder, graphShader, graphShader).get("position");

        CameraPositionShaderNodeBuilder cameraPosition = new CameraPositionShaderNodeBuilder();
        GraphShaderNodeBuilder.FieldOutput cameraPositionField = cameraPosition.buildFragmentNodeSingleInputs(false, "cameraPosition", null, LibGDXCollections.<String, GraphShaderNodeBuilder.FieldOutput>emptyMap(),
                LibGDXCollections.singleton("position"), vertexShaderBuilder, fragmentShaderBuilder, graphShader, graphShader).get("position");

        fragmentShaderBuilder.addFunction("packFloatToVec3", GLSLFragmentReader.getFragment("packFloatToVec3"));

        ObjectMap<String, ObjectMap<String, GraphShaderNodeBuilder.FieldOutput>> fragmentNodeOutputs = new ObjectMap<>();
        GraphShaderNodeBuilder.FieldOutput alphaField = getOutput(findInputVertices(graph, "end", "alpha"),
                designTime, true, graph, graphShader, graphShader, fragmentNodeOutputs, vertexShaderBuilder, fragmentShaderBuilder);
        String alpha = (alphaField != null) ? alphaField.getRepresentation() : "1.0";
        GraphShaderNodeBuilder.FieldOutput alphaClipField = getOutput(findInputVertices(graph, "end", "alphaClip"),
                designTime, true, graph, graphShader, graphShader, fragmentNodeOutputs, vertexShaderBuilder, fragmentShaderBuilder);
        String alphaClip = (alphaClipField != null) ? alphaClipField.getRepresentation() : "0.0";
        if (alphaField != null || alphaClipField != null) {
            fragmentShaderBuilder.addMainLine("// End Graph Node");
            fragmentShaderBuilder.addMainLine("if (" + alpha + " <= " + alphaClip + ")");
            fragmentShaderBuilder.addMainLine("  discard;");
        }
        fragmentShaderBuilder.addMainLine("gl_FragColor = vec4(packFloatToVec3(distance(" + positionField.getRepresentation() + ", " + cameraPositionField.getRepresentation() + "), u_cameraClipping.x, u_cameraClipping.y), 1.0);");
    }

    private static void buildFragmentShader(Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph, boolean designTime, GraphShader graphShader, VertexShaderBuilder vertexShaderBuilder, FragmentShaderBuilder fragmentShaderBuilder) {
        // Fragment part
        ObjectMap<String, ObjectMap<String, GraphShaderNodeBuilder.FieldOutput>> fragmentNodeOutputs = new ObjectMap<>();
        GraphShaderNodeBuilder.FieldOutput alphaField = getOutput(findInputVertices(graph, "end", "alpha"),
                designTime, true, graph, graphShader, graphShader, fragmentNodeOutputs, vertexShaderBuilder, fragmentShaderBuilder);
        String alpha = (alphaField != null) ? alphaField.getRepresentation() : "1.0";
        GraphShaderNodeBuilder.FieldOutput alphaClipField = getOutput(findInputVertices(graph, "end", "alphaClip"),
                designTime, true, graph, graphShader, graphShader, fragmentNodeOutputs, vertexShaderBuilder, fragmentShaderBuilder);
        String alphaClip = (alphaClipField != null) ? alphaClipField.getRepresentation() : "0.0";
        if (alphaField != null || alphaClipField != null) {
            fragmentShaderBuilder.addMainLine("// End Graph Node");
            fragmentShaderBuilder.addMainLine("if (" + alpha + " <= " + alphaClip + ")");
            fragmentShaderBuilder.addMainLine("  discard;");
        }

        GraphShaderNodeBuilder.FieldOutput colorField = getOutput(findInputVertices(graph, "end", "color"),
                designTime, true, graph, graphShader, graphShader, fragmentNodeOutputs, vertexShaderBuilder, fragmentShaderBuilder);
        String color;
        if (colorField == null) {
            color = "vec4(1.0, 1.0, 1.0, " + alpha + ")";
        } else if (colorField.getFieldType() == ShaderFieldType.Vector4) {
            color = "vec4(" + colorField.getRepresentation() + ".rgb, " + alpha + ")";
        } else if (colorField.getFieldType() == ShaderFieldType.Vector3) {
            color = "vec4(" + colorField.getRepresentation() + ", " + alpha + ")";
        } else if (colorField.getFieldType() == ShaderFieldType.Vector2) {
            color = "vec4(" + colorField.getRepresentation() + ", 0.0, " + alpha + ")";
        } else {
            color = "vec4(vec3(" + colorField.getRepresentation() + "), " + alpha + ")";
        }
        fragmentShaderBuilder.addMainLine("// End Graph Node");
        fragmentShaderBuilder.addMainLine("gl_FragColor = " + color + ";");
    }

    private static void buildVertexShader(Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph, boolean designTime, GraphShader graphShader, VertexShaderBuilder vertexShaderBuilder, FragmentShaderBuilder fragmentShaderBuilder) {
        // Vertex part
        int boneCount = GraphShaderConfig.getMaxNumberOfBonesPerMesh();
        int boneWeightCount = GraphShaderConfig.getMaxNumberOfBoneWeights();
        vertexShaderBuilder.addArrayUniformVariable("u_bones", boneCount, "mat4", false, new UniformSetters.Bones(boneCount));
        for (int i = 0; i < boneWeightCount; i++) {
            vertexShaderBuilder.addAttributeVariable("a_boneWeight" + i, "vec2");
        }
        StringBuilder getSkinning = new StringBuilder();
        getSkinning.append("mat4 getSkinning() {\n");
        getSkinning.append("  mat4 skinning = mat4(0.0);\n");
        for (int i = 0; i < boneWeightCount; i++) {
            getSkinning.append("  skinning += (a_boneWeight").append(i).append(".y) * u_bones[int(a_boneWeight").append(i).append(".x)];\n");
        }
        getSkinning.append("  return skinning;\n");
        getSkinning.append("}\n");

        vertexShaderBuilder.addFunction("getSkinning", getSkinning.toString());

        vertexShaderBuilder.addMainLine("mat4 skinning = getSkinning();");

        ObjectMap<String, ObjectMap<String, GraphShaderNodeBuilder.FieldOutput>> vertexNodeOutputs = new ObjectMap<>();
        GraphShaderNodeBuilder.FieldOutput positionField = getOutput(findInputVertices(graph, "end", "position"),
                designTime, false, graph, graphShader, graphShader, vertexNodeOutputs, vertexShaderBuilder, fragmentShaderBuilder);
        if (positionField == null) {
            AttributePositionShaderNodeBuilder position = new AttributePositionShaderNodeBuilder();
            JsonValue positionData = new JsonValue(JsonValue.ValueType.object);
            positionData.addChild("coordinates", new JsonValue("world"));
            positionField = position.buildVertexNodeSingleInputs(false, "defaultPositionAttribute", positionData, LibGDXCollections.<String, GraphShaderNodeBuilder.FieldOutput>emptyMap(),
                    LibGDXCollections.singleton("position"), vertexShaderBuilder, graphShader, graphShader).get("position");
        }
        vertexShaderBuilder.addUniformVariable("u_projViewTrans", "mat4", true, UniformSetters.projViewTrans);
        String worldPosition = "vec4(" + positionField.getRepresentation() + ", 1.0)";
        vertexShaderBuilder.addMainLine("// End Graph Node");
        vertexShaderBuilder.addMainLine("gl_Position = u_projViewTrans * " + worldPosition + ";");
    }

    private static void buildScreenVertexShader(Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph, boolean designTime, GraphShader graphShader, VertexShaderBuilder vertexShaderBuilder, FragmentShaderBuilder fragmentShaderBuilder) {
        // Vertex part
        vertexShaderBuilder.addAttributeVariable(ShaderProgram.POSITION_ATTRIBUTE, "vec3");
        vertexShaderBuilder.addMainLine("// End Graph Node");
        vertexShaderBuilder.addMainLine("gl_Position = vec4((a_position.xy * 2.0 - 1.0), 1.0, 1.0);");
    }

    private static GraphShaderNodeBuilder.FieldOutput getOutput(Array<GraphConnection> connections,
                                                                boolean designTime, boolean fragmentShader,
                                                                Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph,
                                                                GraphShaderContext context, GraphShader graphShader, ObjectMap<String, ObjectMap<String, GraphShaderNodeBuilder.FieldOutput>> nodeOutputs,
                                                                VertexShaderBuilder vertexShaderBuilder, FragmentShaderBuilder fragmentShaderBuilder) {
        if (connections == null || connections.size == 0)
            return null;
        GraphConnection connection = connections.get(0);
        ObjectMap<String, ? extends GraphShaderNodeBuilder.FieldOutput> output = buildNode(designTime, fragmentShader, graph, context, graphShader, connection.getNodeFrom(), nodeOutputs, vertexShaderBuilder, fragmentShaderBuilder);
        return output.get(connection.getFieldFrom());
    }


    private static void initializeShaders(VertexShaderBuilder vertexShaderBuilder, FragmentShaderBuilder fragmentShaderBuilder) {
        vertexShaderBuilder.addInitialLine("#ifdef GL_ES");
        vertexShaderBuilder.addInitialLine("#define LOWP lowp");
        vertexShaderBuilder.addInitialLine("#define MED mediump");
        vertexShaderBuilder.addInitialLine("#define HIGH highp");
        vertexShaderBuilder.addInitialLine("precision mediump float;");
        vertexShaderBuilder.addInitialLine("#else");
        vertexShaderBuilder.addInitialLine("#define MED");
        vertexShaderBuilder.addInitialLine("#define LOWP");
        vertexShaderBuilder.addInitialLine("#define HIGH");
        vertexShaderBuilder.addInitialLine("#endif");

        fragmentShaderBuilder.addInitialLine("#ifdef GL_ES");
        fragmentShaderBuilder.addInitialLine("#define LOWP lowp");
        fragmentShaderBuilder.addInitialLine("#define MED mediump");
        fragmentShaderBuilder.addInitialLine("#define HIGH highp");
        fragmentShaderBuilder.addInitialLine("precision mediump float;");
        fragmentShaderBuilder.addInitialLine("#else");
        fragmentShaderBuilder.addInitialLine("#define MED");
        fragmentShaderBuilder.addInitialLine("#define LOWP");
        fragmentShaderBuilder.addInitialLine("#define HIGH");
        fragmentShaderBuilder.addInitialLine("#endif");
    }

    private static ObjectMap<String, GraphShaderNodeBuilder.FieldOutput> buildNode(
            boolean designTime, boolean fragmentShader,
            Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph,
            GraphShaderContext context, GraphShader graphShader, String nodeId, ObjectMap<String, ObjectMap<String, GraphShaderNodeBuilder.FieldOutput>> nodeOutputs,
            VertexShaderBuilder vertexShaderBuilder, FragmentShaderBuilder fragmentShaderBuilder) {
        ObjectMap<String, GraphShaderNodeBuilder.FieldOutput> nodeOutput = nodeOutputs.get(nodeId);
        if (nodeOutput == null) {
            GraphNode<ShaderFieldType> nodeInfo = graph.getNodeById(nodeId);
            String nodeInfoType = nodeInfo.getType();
            GraphShaderNodeBuilder nodeBuilder = getNodeBuilder(nodeInfoType);
            if (nodeBuilder == null)
                throw new IllegalStateException("Unable to find graph shader node builder for type: " + nodeInfoType);
            ObjectMap<String, Array<GraphShaderNodeBuilder.FieldOutput>> inputFields = new ObjectMap<>();
            for (GraphNodeInput<ShaderFieldType> nodeInput : new ObjectMap.Values<>(nodeBuilder.getConfiguration(nodeInfo.getData()).getNodeInputs())) {
                String fieldId = nodeInput.getFieldId();
                Array<GraphConnection> vertexInfos = findInputVertices(graph, nodeId, fieldId);
                if (vertexInfos.size == 0 && nodeInput.isRequired())
                    throw new IllegalStateException("Required input not provided");
                Array<ShaderFieldType> fieldTypes = new Array<>();
                Array<GraphShaderNodeBuilder.FieldOutput> fieldOutputs = new Array<>();
                for (GraphConnection vertexInfo : vertexInfos) {
                    ObjectMap<String, GraphShaderNodeBuilder.FieldOutput> output = buildNode(designTime, fragmentShader, graph, context, graphShader, vertexInfo.getNodeFrom(), nodeOutputs, vertexShaderBuilder, fragmentShaderBuilder);
                    GraphShaderNodeBuilder.FieldOutput fieldOutput = output.get(vertexInfo.getFieldFrom());
                    ShaderFieldType fieldType = fieldOutput.getFieldType();
                    fieldTypes.add(fieldType);
                    fieldOutputs.add(fieldOutput);
                }
                if (!nodeInput.acceptsInputTypes(fieldTypes))
                    throw new IllegalStateException("Producer produces a field of value not compatible with consumer");
                inputFields.put(fieldId, fieldOutputs);
            }
            ObjectSet<String> requiredOutputs = findRequiredOutputs(graph, nodeId);
            if (fragmentShader) {
                nodeOutput = (ObjectMap<String, GraphShaderNodeBuilder.FieldOutput>) nodeBuilder.buildFragmentNode(designTime, nodeId, nodeInfo.getData(), inputFields, requiredOutputs, vertexShaderBuilder, fragmentShaderBuilder, context, graphShader);
            } else {
                nodeOutput = (ObjectMap<String, GraphShaderNodeBuilder.FieldOutput>) nodeBuilder.buildVertexNode(designTime, nodeId, nodeInfo.getData(), inputFields, requiredOutputs, vertexShaderBuilder, context, graphShader);
            }
            nodeOutputs.put(nodeId, nodeOutput);
        }

        return nodeOutput;
    }

    private static GraphShaderNodeBuilder getNodeBuilder(String nodeInfoType) {
        for (GraphConfiguration configuration : configurations) {
            GraphShaderNodeBuilder graphShaderNodeBuilder = configuration.getGraphShaderNodeBuilders().get(nodeInfoType);
            if (graphShaderNodeBuilder != null)
                return graphShaderNodeBuilder;
        }

        return null;
    }

    private static ObjectSet<String> findRequiredOutputs(Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph,
                                                         String nodeId) {
        ObjectSet<String> result = new ObjectSet<>();
        for (GraphConnection vertex : graph.getConnections()) {
            if (vertex.getNodeFrom().equals(nodeId))
                result.add(vertex.getFieldFrom());
        }
        return result;
    }

    private static Array<GraphConnection> findInputVertices(Graph<? extends GraphNode<ShaderFieldType>, ? extends GraphConnection, ? extends GraphProperty<ShaderFieldType>, ShaderFieldType> graph,
                                                            String nodeId, String nodeField) {
        Array<GraphConnection> result = new Array<>();
        for (GraphConnection vertex : graph.getConnections()) {
            if (vertex.getNodeTo().equals(nodeId) && vertex.getFieldTo().equals(nodeField))
                result.add(vertex);
        }
        return result;
    }

    private static GraphShaderPropertyProducer findPropertyProducerByType(ShaderFieldType type) {
        for (GraphConfiguration configuration : configurations) {
            for (GraphShaderPropertyProducer graphShaderPropertyProducer : configuration.getPropertyProducers()) {
                if (graphShaderPropertyProducer.getType() == type)
                    return graphShaderPropertyProducer;
            }
        }

        return null;
    }
}
