package com.gempukku.libgdx.graph.pipeline.loader.node;

import com.gempukku.libgdx.graph.shader.model.ModelGraphShader;
import com.gempukku.libgdx.graph.shader.screen.ScreenGraphShader;
import com.gempukku.libgdx.graph.shader.sprite.SpriteGraphShader;

public interface PipelineInitializationFeedback {
    <T> T getPrivatePluginData(Class<T> clazz);

    void registerScreenShader(String tag, ScreenGraphShader shader);

    void registerModelShader(String tag, ModelGraphShader shader);

    void registerSpriteShader(String tag, SpriteGraphShader shader);
}
