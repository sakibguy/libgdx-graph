package com.gempukku.libgdx.graph.ui.graph.property;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.graph.config.PropertyNodeConfiguration;
import com.gempukku.libgdx.graph.shader.field.ShaderFieldType;
import com.gempukku.libgdx.graph.shader.field.ShaderFieldTypeRegistry;
import com.gempukku.libgdx.graph.shader.property.PropertyLocation;
import com.gempukku.libgdx.graph.ui.graph.GraphBox;
import com.gempukku.libgdx.graph.ui.graph.GraphBoxImpl;
import com.gempukku.libgdx.graph.ui.graph.GraphChangedEvent;
import com.gempukku.libgdx.graph.ui.part.SelectBoxPart;
import com.gempukku.libgdx.graph.ui.part.StringifyEnum;
import com.gempukku.libgdx.graph.ui.producer.ValueGraphNodeOutput;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.LinkedList;
import java.util.List;


public class PropertyBoxImpl extends VisTable implements PropertyBox {
    private String propertyType;
    private List<PropertyBoxPart> propertyBoxParts = new LinkedList<>();
    private VisTextField nameField;
    private PropertyLocation[] propertyLocations;
    private SelectBoxPart locationPart;

    public PropertyBoxImpl(String name, String propertyType,
                           PropertyLocation selectedLocation,
                           PropertyLocation... propertyLocations) {
        this.propertyType = propertyType;

        locationPart = new SelectBoxPart("Location", "location", new StringifyEnum<PropertyLocation>(), propertyLocations);
        if (selectedLocation != null)
            locationPart.setSelected(selectedLocation);

        nameField = new VisTextField(name);
        this.propertyLocations = propertyLocations;
        VisTable headerTable = new VisTable();
        headerTable.add(new VisLabel("Name: "));
        headerTable.add(nameField).growX();
        nameField.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        fire(new GraphChangedEvent(true, true));
                    }
                });
        headerTable.row();
        add(headerTable).growX().row();

        if (propertyLocations.length > 1)
            add(locationPart).growX().row();
    }

    @Override
    public String getType() {
        return propertyType;
    }

    @Override
    public String getName() {
        return nameField.getText();
    }

    @Override
    public JsonValue getData() {
        JsonValue result = new JsonValue(JsonValue.ValueType.object);

        for (PropertyBoxPart graphBoxPart : propertyBoxParts)
            graphBoxPart.serializePart(result);

        if (result.isEmpty())
            return null;
        return result;
    }

    public void addPropertyBoxPart(PropertyBoxPart propertyBoxPart) {
        propertyBoxParts.add(propertyBoxPart);
        final Actor actor = propertyBoxPart.getActor();
        add(actor).growX().row();
    }

    public void initialize(JsonValue value) {
        for (PropertyBoxPart propertyBoxPart : propertyBoxParts) {
            propertyBoxPart.initialize(value);
        }
    }

    @Override
    public PropertyLocation getLocation() {
        if (propertyLocations.length > 0)
            return PropertyLocation.valueOf(locationPart.getSelected());
        return null;
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public GraphBox createPropertyBox(Skin skin, String id, float x, float y) {
        final String name = getName();
        GraphBoxImpl result = new GraphBoxImpl(id, new PropertyNodeConfiguration(name, propertyType)) {
            @Override
            public JsonValue getData() {
                JsonValue result = new JsonValue(JsonValue.ValueType.object);
                result.addChild("name", new JsonValue(name));
                result.addChild("type", new JsonValue(propertyType));
                return result;
            }
        };
        result.addOutputGraphPart(new ValueGraphNodeOutput(name, propertyType));
        ShaderFieldType shaderFieldType = ShaderFieldTypeRegistry.findShaderFieldType(propertyType);
        if (shaderFieldType != null && shaderFieldType.isTexture()) {
            result.addGraphBoxPart(new TextureSettingsGraphBoxPart());
        }

        return result;
    }

    @Override
    public void dispose() {
        for (PropertyBoxPart propertyBoxPart : propertyBoxParts) {
            propertyBoxPart.dispose();
        }
    }
}
