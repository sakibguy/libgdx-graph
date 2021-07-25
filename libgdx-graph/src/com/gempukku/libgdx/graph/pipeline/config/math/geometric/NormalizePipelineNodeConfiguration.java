package com.gempukku.libgdx.graph.pipeline.config.math.geometric;

import com.gempukku.libgdx.graph.config.SameTypeOutputTypeFunction;
import com.gempukku.libgdx.graph.data.NodeConfigurationImpl;
import com.gempukku.libgdx.graph.pipeline.field.PipelineFieldType;
import com.gempukku.libgdx.graph.pipeline.producer.node.GraphNodeInputImpl;
import com.gempukku.libgdx.graph.pipeline.producer.node.GraphNodeOutputImpl;

public class NormalizePipelineNodeConfiguration extends NodeConfigurationImpl<PipelineFieldType> {
    public NormalizePipelineNodeConfiguration() {
        super("Normalize", "Normalize", "Math/Geometric");
        addNodeInput(
                new GraphNodeInputImpl<PipelineFieldType>("input", "Input", true, PipelineFieldType.Color, PipelineFieldType.Vector3, PipelineFieldType.Vector2, PipelineFieldType.Float));
        addNodeOutput(
                new GraphNodeOutputImpl<PipelineFieldType>("output", "Result",
                        new SameTypeOutputTypeFunction<PipelineFieldType>("input"),
                        PipelineFieldType.Float, PipelineFieldType.Vector2, PipelineFieldType.Vector3, PipelineFieldType.Color));
    }
}
