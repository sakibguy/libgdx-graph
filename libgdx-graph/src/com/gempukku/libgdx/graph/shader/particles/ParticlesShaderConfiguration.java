package com.gempukku.libgdx.graph.shader.particles;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.graph.shader.config.GraphConfiguration;
import com.gempukku.libgdx.graph.shader.node.GraphShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.particles.particle.ParticleLifePercentageShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.particles.particle.ParticleLifetimeShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.particles.particle.ParticleLocationShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.particles.particle.ParticleSeedShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.particles.particle.ParticleUVShaderNodeBuilder;
import com.gempukku.libgdx.graph.shader.property.GraphShaderPropertyProducer;

public class ParticlesShaderConfiguration implements GraphConfiguration {
    public static ObjectMap<String, GraphShaderNodeBuilder> graphShaderNodeBuilders = new ObjectMap<>();
    public static Array<GraphShaderPropertyProducer> graphShaderPropertyProducers = new Array<>();

    static {
        // End
        addGraphShaderNodeBuilder(new EndBillboardParticlesShaderNodeBuilder());

        // Particle
        addGraphShaderNodeBuilder(new ParticleLocationShaderNodeBuilder());
        addGraphShaderNodeBuilder(new ParticleUVShaderNodeBuilder());
        addGraphShaderNodeBuilder(new ParticleSeedShaderNodeBuilder());
        addGraphShaderNodeBuilder(new ParticleLifetimeShaderNodeBuilder());
        addGraphShaderNodeBuilder(new ParticleLifePercentageShaderNodeBuilder());

        // Property
        addGraphShaderNodeBuilder(new ParticlesPropertyShaderNodeBuilder());
    }

    private static void addGraphShaderNodeBuilder(GraphShaderNodeBuilder builder) {
        graphShaderNodeBuilders.put(builder.getType(), builder);
    }

    public ParticlesShaderConfiguration() {

    }

    @Override
    public Array<GraphShaderPropertyProducer> getPropertyProducers() {
        return graphShaderPropertyProducers;
    }

    @Override
    public ObjectMap<String, GraphShaderNodeBuilder> getGraphShaderNodeBuilders() {
        return graphShaderNodeBuilders;
    }
}
