package components.gizmos;

import components.Sprite;
import editor.PropertiesWindow;
import spydr.MouseListener;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow inspector) {
        super(arrowSprite, inspector);
    }

    @Override
    public void update(float dt) {
        if(super.activeGameObject != null) {
            if(xAxisActive && !yAxisActive) {
                activeGameObject.transform.position.x -= MouseListener.getWorldDx();
            } else if(yAxisActive && !xAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.getWorldDy();
            }
        }

        super.update(dt);
    }
}
