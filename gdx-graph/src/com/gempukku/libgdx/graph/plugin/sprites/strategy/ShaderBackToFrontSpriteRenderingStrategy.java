package com.gempukku.libgdx.graph.plugin.sprites.strategy;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.graph.plugin.sprites.impl.DistanceSpriteSorter;
import com.gempukku.libgdx.graph.plugin.sprites.impl.GraphSpriteImpl;
import com.gempukku.libgdx.graph.plugin.sprites.impl.GraphSpritesImpl;
import com.gempukku.libgdx.graph.plugin.sprites.impl.NonBatchedTagSpriteData;

public class ShaderBackToFrontSpriteRenderingStrategy implements SpriteRenderingStrategy {
    private Array<GraphSpriteImpl> orderingArray = new Array<>();
    private DistanceSpriteSorter spriteSorter = new DistanceSpriteSorter(DistanceSpriteSorter.Order.Back_To_Front);

    @Override
    public boolean isBatched() {
        return false;
    }

    @Override
    public void processSprites(GraphSpritesImpl sprites, Array<String> tags, Camera camera, StrategyCallback callback) {
        callback.begin();
        for (String tag : tags) {
            orderingArray.clear();
            for (GraphSpriteImpl nonBatchedSprite : sprites.getNonBatchedSprites(tag)) {
                if (nonBatchedSprite.getRenderableSprite().isRendered(camera))
                    orderingArray.add(nonBatchedSprite);
            }
            spriteSorter.sort(camera.position, orderingArray);

            NonBatchedTagSpriteData nonBatchedSpriteData = sprites.getNonBatchedSpriteData(tag);
            for (GraphSpriteImpl graphSprite : orderingArray) {
                nonBatchedSpriteData.setSprite(graphSprite);
                callback.process(nonBatchedSpriteData, tag);
            }
        }
        callback.end();
    }
}
