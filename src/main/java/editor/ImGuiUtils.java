package editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ImGuiUtils {
    private static final float defaultResetValue = 0.0f;
    private static final float defaultColumnWidth = 128.0f;
    private static final float defaultDragSpeed = 1.0f;

    public static void drawVec2Control(String label, Vector2f values) {
        drawVec2Control(label, values, defaultResetValue, defaultColumnWidth);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue) {
        drawVec2Control(label, values, resetValue, defaultColumnWidth);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidth) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.setColumnWidth(1, defaultColumnWidth * 3);
        ImGui.text(label);
        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY();
        Vector2f buttonSize = new Vector2f(lineHeight + 3, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.7f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.8f, 0.3f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.7f, 0.2f, 0.2f, 1.0f);
        if(ImGui.button("X", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }

        ImGui.sameLine();
        float[] vecValuesX = {values.x};
        ImGui.dragFloat("##x", vecValuesX, defaultDragSpeed);
        ImGui.sameLine();

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        if(ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }

        ImGui.sameLine();
        float[] vecValuesY = {values.y};
        ImGui.dragFloat("##y", vecValuesY, defaultDragSpeed);
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.nextColumn();

        values.x = vecValuesX[0];
        values.y = vecValuesY[0];

        ImGui.columns(1);
        ImGui.popStyleVar();
        ImGui.popStyleColor(6);
        ImGui.popID();
    }

    public static float drawFloatControl(String label, float value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.setColumnWidth(1, defaultColumnWidth * 3);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] valArr = {value};
        ImGui.dragFloat("##float", valArr, defaultDragSpeed);

        ImGui.columns(1);
        ImGui.popID();
        return valArr[0];
    }

    public static int drawIntegerControl(String label, int value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.setColumnWidth(1, defaultColumnWidth * 3);
        ImGui.text(label);
        ImGui.nextColumn();

        int[] valArr = {value};
        ImGui.dragInt("##integer", valArr, defaultDragSpeed);

        ImGui.columns(1);
        ImGui.popID();
        return valArr[0];
    }

    public static boolean drawBooleanControl(String label, boolean value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.setColumnWidth(1, defaultColumnWidth * 3);
        ImGui.text(label);
        ImGui.nextColumn();

        if(ImGui.checkbox("##bool", value)) {
            value = !value;
        }

        ImGui.columns(1);
        ImGui.popID();
        return value;
    }


    public static boolean drawColorPicker4Control(String label, Vector4f color) {
        boolean result = false;
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] imColor = {color.x, color.y, color.z, color.w};
        if(ImGui.colorEdit4(label, imColor)) {
            color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            result = true;
        }

        ImGui.columns(1);
        ImGui.popID();
        return result;
    }
}
