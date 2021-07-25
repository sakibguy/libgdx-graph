package com.gempukku.libgdx.graph.plugin.particles.design.producer;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.graph.pipeline.field.PipelineFieldType;
import com.gempukku.libgdx.graph.plugin.particles.ParticlesShaderRendererPipelineNodeConfiguration;
import com.gempukku.libgdx.graph.ui.graph.GraphBox;
import com.gempukku.libgdx.graph.ui.graph.GraphBoxImpl;
import com.gempukku.libgdx.graph.ui.producer.GraphBoxProducerImpl;

public class ParticlesShaderRendererBoxProducer extends GraphBoxProducerImpl<PipelineFieldType> {
    public ParticlesShaderRendererBoxProducer() {
        super(new ParticlesShaderRendererPipelineNodeConfiguration());
    }

    @Override
    public GraphBox<PipelineFieldType> createPipelineGraphBox(Skin skin, String id, JsonValue data) {
        GraphBoxImpl<PipelineFieldType> result = createGraphBox(id);
        addConfigurationInputsAndOutputs(result);
        ParticlesShadersBoxPart graphBoxPart = new ParticlesShadersBoxPart();
        graphBoxPart.initialize(data);
        result.addGraphBoxPart(graphBoxPart);
        return result;
    }
}
