package physics2d.componenets;

import org.joml.Vector2f;

public class Box2DCollider extends Collider {
    private Vector2f halfSize = new Vector2f();
    private Vector2f origin = new Vector2f();

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    public Vector2f getOrigin() {
        return this.origin;
    }
}
