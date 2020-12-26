package com.gempukku.libgdx.graph.pipeline.loader;

import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.gempukku.libgdx.graph.pipeline.PipelinePropertySource;
import com.gempukku.libgdx.graph.shader.model.impl.GraphModelsImpl;
import com.gempukku.libgdx.graph.shader.particles.GraphParticleEffectsImpl;
import com.gempukku.libgdx.graph.shader.screen.GraphScreenShadersImpl;
import com.gempukku.libgdx.graph.shader.sprite.impl.GraphSpritesImpl;
import com.gempukku.libgdx.graph.time.TimeProvider;

public interface PipelineRenderingContext {
    int getRenderWidth();

    int getRenderHeight();

    GraphModelsImpl getGraphShaderModels();

    GraphSpritesImpl getGraphSprites();

    GraphScreenShadersImpl getScreenShaders();

    GraphParticleEffectsImpl getGraphParticleEffects();

    PipelinePropertySource getPipelinePropertySource();

    TimeProvider getTimeProvider();

    RenderContext getRenderContext();

    FullScreenRender getFullScreenRender();
}
