package physics2d;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;
import physics2d.componenets.Box2DCollider;
import physics2d.componenets.CircleCollider;
import physics2d.componenets.Rigidbody2D;
import spydr.GameObject;
import spydr.Transform;

public class Physics2D {
    private Vec2 gravity = new  Vec2(0, -10.0f);
    private World world = new World(gravity);

    public float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;

    private int velocityIterations = 8;
    private int positionIterations = 3;

    public void add(GameObject object) {
        Rigidbody2D rb = object.getComponent(Rigidbody2D.class);
        if(rb != null && rb.getRawBody() == null) {
            Transform transform = object.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float) Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinousCollision();

            switch (rb.getBodyType()) {
                case Kinematic:
                    bodyDef.type = BodyType.KINEMATIC;
                    break;
                case Static:
                    bodyDef.type = BodyType.STATIC;
                    break;
                case Dynamic:
                    bodyDef.type = BodyType.DYNAMIC;
                    break;
            }

            PolygonShape shape = new PolygonShape();
            CircleCollider circleCollider;
            Box2DCollider boxCollider;

            if((circleCollider = object.getComponent(CircleCollider.class)) != null) {
                shape.setRadius(circleCollider.getRadius());
            } else if((boxCollider = object.getComponent(Box2DCollider.class)) != null) {
                Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
                Vector2f offset = boxCollider.getOffset();
                Vector2f origin = boxCollider.getOrigin();

                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);
                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            }

            Body body = this.world.createBody(bodyDef);
            rb.setRawBody(body);
            body.createFixture(shape, rb.getMass());
        }
    }

    public void update(float dt) {
        physicsTime += dt;
        if(physicsTime >= 0.0f) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTime, velocityIterations, positionIterations);
        }
    }
}
