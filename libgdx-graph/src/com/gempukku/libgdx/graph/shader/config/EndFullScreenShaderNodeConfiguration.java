package com.gempukku.libgdx.graph.shader.config;

import com.gempukku.libgdx.graph.NodeConfigurationImpl;
import com.gempukku.libgdx.graph.pipeline.loader.node.GraphNodeInputImpl;
import com.gempukku.libgdx.graph.shader.ShaderFieldType;

public class EndFullScreenShaderNodeConfiguration extends NodeConfigurationImpl<ShaderFieldType> {
    public EndFullScreenShaderNodeConfiguration() {
        super("FullScreenShaderEnd", "Shader output", null);
        addNodeInput(
                new GraphNodeInputImpl<ShaderFieldType>("color", "Color", false, false,
                        ShaderFieldType.Vector4, ShaderFieldType.Vector3, ShaderFieldType.Vector2, ShaderFieldType.Float));
        addNodeInput(
                new GraphNodeInputImpl<ShaderFieldType>("alpha", "Alpha", false, false, ShaderFieldType.Float));
        addNodeInput(
                new GraphNodeInputImpl<ShaderFieldType>("alphaClip", "Alpha clip", false, false, ShaderFieldType.Float));
    }
}