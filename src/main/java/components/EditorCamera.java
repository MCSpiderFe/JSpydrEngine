package components;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import spydr.Camera;
import spydr.KeyListener;
import spydr.MouseListener;

public class EditorCamera extends Component {

    private float dragDebounce = 0.064f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;
    private boolean reset = true;

    private float lerpTime = 0;

    private final Camera levelEditorCamera;
    private Vector2f clickOrigin;

    public EditorCamera(Camera levelEditorCamera) {

        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {
        if(MouseListener.mouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce > 0) {
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }

        if(dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            dragDebounce = 0.064f;
        }

        if(MouseListener.getScrollY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity), 1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_F1)) {
            reset();
        }

        if(reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(levelEditorCamera.getZoom() + ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            lerpTime += 0.1f * dt;
            if(Math.abs(levelEditorCamera.position.x) <= 0.5f && Math.abs(levelEditorCamera.position.y) <= 0.5f ) {
                levelEditorCamera.position.set(0f, 0f);
                levelEditorCamera.setZoom(1);
                reset = false;
                lerpTime = 0.0f;
            }
        }
    }

    public void reset() {
        this.reset = true;
    }
}
