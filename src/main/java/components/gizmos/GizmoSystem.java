package components.gizmos;

import components.Component;
import components.Spritesheet;
import components.TranslateGizmo;
import org.lwjgl.glfw.GLFW;
import spydr.KeyListener;
import spydr.Window;

public class GizmoSystem extends Component {
    private Spritesheet gizmos;
    private int usingGizmo = 0;

    public GizmoSystem(Spritesheet gizmoSprites) {
        this.gizmos = gizmoSprites;
    }

    @Override
    public void start() {
        super.start();
        gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1), Window.getImGuiLayer().getPropertiesWindow()));
        gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2), Window.getImGuiLayer().getPropertiesWindow()));
    }

    @Override
    public void update(float dt) {
        if(usingGizmo == 0) {
            gameObject.getComponent(ScaleGizmo.class).setNotUsing();
            gameObject.getComponent(TranslateGizmo.class).setUsing();
        } else if(usingGizmo == 1) {
            gameObject.getComponent(TranslateGizmo.class).setNotUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
        }

        if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_S)) {
            usingGizmo = 1;
        } else if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)) {
            usingGizmo = 0;
        }
    }
}
