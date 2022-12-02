package com.tac.guns.graph;

import com.tac.guns.graph.math.LocalVector3f;

public class RenderCamera {

    private final LocalVector3f position;

    private final LocalVector3f rotation;

    public RenderCamera() {
        position = new LocalVector3f(0, 0, 0);
        rotation = new LocalVector3f(0, 0, 0);
    }

    public RenderCamera(LocalVector3f position, LocalVector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public LocalVector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.setX(x);
        position.setY(y);
        position.setZ(z);
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.setX(position.x() + (float)Math.sin(Math.toRadians(rotation.y())) * -1.0f * offsetZ);
            position.setZ(position.z() + (float)Math.cos(Math.toRadians(rotation.y())) * offsetZ);
        }
        if ( offsetX != 0) {
            position.setX(position.x() + (float)Math.sin(Math.toRadians(rotation.y() - 90)) * -1.0f * offsetX);
            position.setZ(position.z() + (float)Math.cos(Math.toRadians(rotation.y() - 90)) * offsetX);
        }
        position.setY(position.y() + offsetY);
    }

    public LocalVector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.setX(x);
        rotation.setY(y);
        rotation.setZ(z);
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.setX(rotation.x() + offsetX);
        rotation.setY(rotation.y() + offsetY);
        rotation.setZ(rotation.z() + offsetZ);
    }
}
