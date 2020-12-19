package components.gizmos;

import components.Component;
import components.NonPickable;
import components.Sprite;
import components.SpriteRenderer;
import editor.PropertiesWindow;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import spydr.GameObject;
import spydr.MouseListener;
import spydr.Prefabs;
import spydr.Window;

public class Gizmo extends Component {
    private final Vector4f xAxisColor = new Vector4f(1f, 0.3f, 0.3f, 1f);
    private final Vector4f yAxisColor = new Vector4f(0.3f, 1f, 0.3f, 1f);
    private final Vector4f xAxisColorHover = new Vector4f(1f, 0f, 0f, 1f);
    private final Vector4f yAxisColorHover = new Vector4f(0f, 1, 0f, 1f);

    private final Vector2f xAxisOffset = new Vector2f(60f, -5f);
    private final Vector2f yAxisOffset = new Vector2f(11f, 60f);

    private final GameObject xAxisObject;
    private final GameObject yAxisObject;
    private final GameObject freeMoveObject;
    private final SpriteRenderer xAxisSprite;
    private final SpriteRenderer yAxisSprite;

    private final PropertiesWindow inspector;

    private final int gizmoWidth = 16;
    private final int gizmoHeight = 48;

    protected GameObject activeGameObject = null;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private boolean using = false;

    public Gizmo(Sprite arrowSprite, PropertiesWindow inspector) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.freeMoveObject = Prefabs.generateSpriteObject(arrowSprite, 16, 16);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);

        this.inspector = inspector;
    }

    @Override
    public void start() {
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
    }

    @Override
    public void update(float dt) {
        if(!using) return;

        this.activeGameObject = this.inspector.getActiveGameObject();
        if(this.activeGameObject != null) {
            this.setActive();
        } else {
            this.setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            yAxisActive = true;
            xAxisActive = false;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        float scale = Window.getScene().camera().getZoom();

        if(this.activeGameObject != null) {
            Vector2f xScaledOffset = new Vector2f(xAxisOffset).mul(scale);
            Vector2f yScaledOffset = new Vector2f(yAxisOffset).mul(scale);

            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(xScaledOffset);
            this.yAxisObject.transform.position.add(yScaledOffset);
        }

        this.xAxisObject.transform.scale.set(gizmoWidth * scale, gizmoHeight * scale);
        this.yAxisObject.transform.scale.set(gizmoWidth * scale, gizmoHeight * scale);
    }

    private void setActive() {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    private void setInactive() {
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0f, 0f, 0f, 0f));
        this.yAxisSprite.setColor(new Vector4f(0f, 0f, 0f, 0f));
    }

    private boolean checkXHoverState() {
        float scale = Window.getScene().camera().getZoom();

        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= xAxisObject.transform.position.x && mousePos.x >= xAxisObject.transform.position.x - gizmoHeight * scale && mousePos.y >= xAxisObject.transform.position.y && mousePos.y <= xAxisObject.transform.position.y + gizmoWidth * scale) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        float scale = Window.getScene().camera().getZoom();

        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= yAxisObject.transform.position.x && mousePos.x >= yAxisObject.transform.position.x - gizmoWidth * scale && mousePos.y <= yAxisObject.transform.position.y && mousePos.y >= yAxisObject.transform.position.y - gizmoHeight * scale) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    public void setUsing() {
        this.using = true;
    }

    public void setNotUsing() {
        this.using = false;
        this.setInactive();
    }
}
