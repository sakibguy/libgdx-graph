package com.gempukku.libgdx.graph.plugin.screen;

import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.graph.pipeline.producer.rendering.producer.WritablePropertyContainer;
import com.gempukku.libgdx.graph.plugin.RuntimePipelinePlugin;
import com.gempukku.libgdx.graph.time.TimeProvider;

public class GraphScreenShadersImpl implements GraphScreenShaders, RuntimePipelinePlugin {
    private ObjectMap<String, WritablePropertyContainer> propertyContainers = new ObjectMap<>();

    public void registerTag(String tag, ScreenGraphShader shader) {
        if (propertyContainers.containsKey(tag))
            throw new IllegalStateException("Duplicate screen shader with tag - " + tag);
        propertyContainers.put(tag, shader.getPropertyContainer());
    }

    @Override
    public void setProperty(String tag, String name, Object value) {
        WritablePropertyContainer propertyContainer = propertyContainers.get(tag);
        if (propertyContainer == null)
            throw new IllegalArgumentException("Screen shader tag not found - " + tag);
        propertyContainer.setValue(name, value);
    }

    @Override
    public void unsetProperty(String tag, String name) {
        WritablePropertyContainer propertyContainer = propertyContainers.get(tag);
        if (propertyContainer == null)
            throw new IllegalArgumentException("Screen shader tag not found - " + tag);
        propertyContainer.remove(name);
    }

    @Override
    public Object getProperty(String tag, String name) {
        WritablePropertyContainer propertyContainer = propertyContainers.get(tag);
        if (propertyContainer == null)
            throw new IllegalArgumentException("Screen shader tag not found - " + tag);
        return propertyContainer.getValue(name);
    }

    @Override
    public void update(TimeProvider timeProvider) {

    }
}
