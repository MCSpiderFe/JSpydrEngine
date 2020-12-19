package components.gizmos;

import components.Sprite;
import editor.PropertiesWindow;
import spydr.MouseListener;

public class ScaleGizmo extends Gizmo {
    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow inspector) {
        super(scaleSprite, inspector);
    }

    @Override
    public void update(float dt) {
        if(super.activeGameObject != null) {
            if(xAxisActive && !yAxisActive) {
                activeGameObject.transform.scale.x -= MouseListener.getWorldDx();
            } else if(yAxisActive && !xAxisActive) {
                activeGameObject.transform.scale.y -= MouseListener.getWorldDy();
            }
        }

        super.update(dt);
    }
}
