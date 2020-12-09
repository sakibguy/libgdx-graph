package com.gempukku.libgdx.graph.shader.config.particles;

import com.gempukku.libgdx.graph.data.NodeConfigurationImpl;
import com.gempukku.libgdx.graph.pipeline.loader.node.GraphNodeOutputImpl;
import com.gempukku.libgdx.graph.shader.ShaderFieldType;

public class ParticleUVShaderNodeConfiguration extends NodeConfigurationImpl<ShaderFieldType> {
    public ParticleUVShaderNodeConfiguration() {
        super("ParticleUV", "Particle UV", "Particle");
        addNodeOutput(
                new GraphNodeOutputImpl<ShaderFieldType>("uv", "UV", ShaderFieldType.Vector2));
    }
}
