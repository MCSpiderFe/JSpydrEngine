package components;

import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import spydr.Camera;
import spydr.Window;

public class GridLines extends Component {

    public static int GRID_WIDTH = 32;
    public static int GRID_HEIGHT = 32;

    @Override
    public void update(float dt) {
        Camera camera = Window.getScene().camera();

        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = new Vector2f(camera.getProjectionSize()).mul(camera.getZoom());

        int firstX = ((int)(cameraPos.x / GRID_WIDTH) - 1) * GRID_HEIGHT;
        int firstY = ((int)(cameraPos.y / GRID_HEIGHT) - 1) * GRID_HEIGHT;

        int numVtLines = (int)(projectionSize.x / GRID_WIDTH) + 2;
        int numHzLines = (int)(projectionSize.y / GRID_HEIGHT) + 2;

        int height = (int)projectionSize.y + GRID_HEIGHT * 2;
        int width = (int)projectionSize.x + GRID_WIDTH * 2;

        int maxLines = Math.max(numVtLines, numHzLines);
        Vector3f color = new Vector3f(0.38f, 0.38f, 0.38f);
        for (int i=0; i < maxLines; i++) {
            int x = firstX + (GRID_WIDTH * i);
            int y = firstY + (GRID_HEIGHT * i);

            if (i < numVtLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHzLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}
