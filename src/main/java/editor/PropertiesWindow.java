package editor;

import components.NonPickable;
import imgui.ImGui;
import physics2d.componenets.Box2DCollider;
import physics2d.componenets.CircleCollider;
import physics2d.componenets.Collider;
import physics2d.componenets.Rigidbody2D;
import renderer.PickingTexture;
import scenes.Scene;
import spydr.GameObject;
import spydr.MouseListener;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        debounce -= dt;

        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);

            GameObject pickedObject = currentScene.getGameObject(gameObjectId);
            if(pickedObject != null && pickedObject.getComponent(NonPickable.class) == null) {
                activeGameObject = currentScene.getGameObject(gameObjectId);
            } else if (pickedObject == null && !MouseListener.isDragging()) {
                activeGameObject = null;
            }

            this.debounce = 0.2f;
        }

    }

    public void imgui() {
        if (activeGameObject != null) {
            ImGui.begin("Inspector");
            ImGui.text(activeGameObject.getClass().getSimpleName() + " | UID: " + activeGameObject.getUid());

            if(ImGui.beginPopupContextWindow("ComponentAdder")) {
                if(ImGui.menuItem("Add Rigidbody")) {
                    if(activeGameObject.getComponent(Rigidbody2D.class) == null) {
                        activeGameObject.addComponent(new Rigidbody2D());
                    }
                }
                if(ImGui.menuItem("Add Box Collider")) {
                    if(activeGameObject.getComponent(Collider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }
                if(ImGui.menuItem("Add Circle Collider")) {
                    if(activeGameObject.getComponent(Collider.class) == null) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }
                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return this.activeGameObject;
    }
}
