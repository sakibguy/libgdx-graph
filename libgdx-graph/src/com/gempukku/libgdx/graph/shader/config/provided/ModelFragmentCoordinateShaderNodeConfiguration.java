package com.gempukku.libgdx.graph.shader.config.provided;

import com.gempukku.libgdx.graph.NodeConfigurationImpl;
import com.gempukku.libgdx.graph.pipeline.loader.node.GraphNodeOutputImpl;
import com.gempukku.libgdx.graph.shader.ShaderFieldType;

public class ModelFragmentCoordinateShaderNodeConfiguration extends NodeConfigurationImpl<ShaderFieldType> {
    public ModelFragmentCoordinateShaderNodeConfiguration() {
        super("ModelFragmentCoordinate", "Model fragment coordinate", "Provided");
        addNodeOutput(
                new GraphNodeOutputImpl<ShaderFieldType>("output", "Output", ShaderFieldType.Vector2));
    }
}