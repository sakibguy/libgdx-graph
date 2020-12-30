package com.gempukku.libgdx.graph.system.camera.constraint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public class LockedToCameraConstraint implements CameraConstraint {
    private Vector2 anchor = new Vector2();

    public LockedToCameraConstraint(Vector2 anchor) {
        this.anchor.set(anchor);
    }

    @Override
    public void applyConstraint(Camera camera, Vector2 focus, float delta) {
        float currentAnchorX = 0.5f + (focus.x - camera.position.x) / camera.viewportWidth;
        float currentAnchorY = 0.5f + (focus.y - camera.position.y) / camera.viewportHeight;
        camera.position.x += camera.viewportWidth * (currentAnchorX - anchor.x);
        camera.position.y += camera.viewportHeight * (currentAnchorY - anchor.y);
        camera.update();
    }
}
