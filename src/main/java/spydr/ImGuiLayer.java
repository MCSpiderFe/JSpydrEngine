package spydr;

import editor.GameViewWindow;
import editor.PropertiesWindow;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.type.ImBoolean;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

    private long glfwWindow;

    // Mouse cursors provided by GLFW
    private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

    // LWJGL3 renderer (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final GameViewWindow gameViewWindow;
    private PropertiesWindow inspector;

    public ImGuiLayer(long glfwWindow, PickingTexture pickingTexture) {
        this.glfwWindow = glfwWindow;
        this.gameViewWindow = new GameViewWindow();
        this.inspector = new PropertiesWindow(pickingTexture);
    }

    // Initialize Dear ImGui.
    public void initImGui() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename("imgui.ini"); // We don't want to save .ini file
        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Navigation with keyboard
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors); // Mouse cursors to display while resizing windows etc.
        io.setBackendPlatformName("imgui_java_impl_glfw");

        // ------------------------------------------------------------
        // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
        final int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
        keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
        keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
        keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
        keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
        keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
        keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
        keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
        keyMap[ImGuiKey.End] = GLFW_KEY_END;
        keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
        keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
        keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
        keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
        keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
        keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
        keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
        keyMap[ImGuiKey.A] = GLFW_KEY_A;
        keyMap[ImGuiKey.C] = GLFW_KEY_C;
        keyMap[ImGuiKey.V] = GLFW_KEY_V;
        keyMap[ImGuiKey.X] = GLFW_KEY_X;
        keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
        keyMap[ImGuiKey.Z] = GLFW_KEY_Z;
        io.setKeyMap(keyMap);

        // ------------------------------------------------------------
        // Mouse cursors mapping
        mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
        mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input

        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

            if (!io.getWantCaptureKeyboard()) {
                KeyListener.keyCallback(w, key, scancode, action, mods);
            }
        });

        glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });

        glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }

            if (!io.getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
                MouseListener.mouseButtonCallback(w, button, action, mods);
            }
        });

        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
            MouseListener.mouseScrollCallback(w, xOffset, yOffset);
        });

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(glfwWindow, s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = glfwGetClipboardString(glfwWindow);
                if (clipboardString != null) {
                    return clipboardString;
                } else {
                    return "";
                }
            }
        });

        // ------------------------------------------------------------
        // Fonts configuration
        // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        // Glyphs could be added per-font as well as per config used globally like here
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        // Fonts merge example
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontFromFileTTF("assets/fonts/calibri.ttf", 15, fontConfig);

        fontConfig.destroy(); // After all fonts were added we don't need this config more

        // ------------------------------------------------------------
        // Use freetype instead of stb_truetype to build a fonts texture
        ImGuiFreeType.buildFontAtlas(fontAtlas, ImGuiFreeType.RasterizerFlags.LightHinting);

        // Method initializes LWJGL3 renderer.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.

        setColors();
        imGuiGl3.init("#version 330 core");
    }

    public void update(float dt, Scene currentScene) {
        startFrame(dt);

        // Any Dear ImGui code SHOULD go between ImGui.newFrame()/ImGui.render() methods
        ImGui.newFrame();
        setupDockspace();
        currentScene.imgui();
        inspector.update(dt, currentScene);
        inspector.imgui();
        gameViewWindow.imgui();
        ImGui.end();
        ImGui.render();

        endFrame();
    }

    private void startFrame(final float deltaTime) {
        // Get window properties and mouse position
        float[] winWidth = {Window.getWidth()};
        float[] winHeight = {Window.getHeight()};
        double[] mousePosX = {0};
        double[] mousePosY = {0};
        glfwGetCursorPos(glfwWindow, mousePosX, mousePosY);

        // We SHOULD call those methods to update Dear ImGui state for the current frame
        final ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(winWidth[0], winHeight[0]);
        io.setDisplayFramebufferScale(1f, 1f);
        io.setMousePos((float) mousePosX[0], (float) mousePosY[0]);
        io.setDeltaTime(deltaTime);

        // Update the mouse cursor
        final int imguiCursor = ImGui.getMouseCursor();
        glfwSetCursor(glfwWindow, mouseCursors[imguiCursor]);
        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    private void endFrame() {
        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        imGuiGl3.render(ImGui.getDrawData());
    }

    // If you want to clean a room after yourself - do it by yourself
    private void destroyImGui() {
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }

    private void setColors() {
        ImGuiStyle theme = ImGui.getStyle();
        theme.setWindowRounding(5.3f);
        theme.setFrameRounding(0f);
        theme.setScrollbarRounding(0);
        theme.setTabRounding(0);
        theme.setWindowPadding(2, 2);

        theme.setColor(ImGuiCol.Text,1.00f, 1.00f, 1.00f, 1.00f);
        theme.setColor(ImGuiCol.TextDisabled,0.50f, 0.50f, 0.50f, 1.00f);
        theme.setColor(ImGuiCol.WindowBg,0.13f, 0.14f, 0.15f, 1.00f);
        theme.setColor(ImGuiCol.ChildBg,0.13f, 0.14f, 0.15f, 1.00f);
        theme.setColor(ImGuiCol.PopupBg,0.13f, 0.14f, 0.15f, 1.00f);
        theme.setColor(ImGuiCol.Border, 0.43f, 0.43f, 0.50f, 0.50f);
        theme.setColor(ImGuiCol.BorderShadow,0.00f, 0.00f, 0.00f, 0.00f);
        theme.setColor(ImGuiCol.FrameBg, 0.25f, 0.25f, 0.25f, 1.00f);
        theme.setColor(ImGuiCol.FrameBgHovered, 0.38f, 0.38f, 0.38f, 1.00f);
        theme.setColor(ImGuiCol.FrameBgActive, 0.67f, 0.67f, 0.67f, 0.39f);
        theme.setColor(ImGuiCol.TitleBg, 0.08f, 0.08f, 0.09f, 1.00f);
        theme.setColor(ImGuiCol.TitleBgActive, 0.08f, 0.08f, 0.09f, 1.00f);
        theme.setColor(ImGuiCol.TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 0.51f);
        theme.setColor(ImGuiCol.MenuBarBg, 0.14f, 0.14f, 0.14f, 1.00f);
        theme.setColor(ImGuiCol.ScrollbarBg, 0.02f, 0.02f, 0.02f, 0.53f);
        theme.setColor(ImGuiCol.ScrollbarGrab, 0.31f, 0.31f, 0.31f, 1.00f);
        theme.setColor(ImGuiCol.ScrollbarGrabHovered, 0.41f, 0.41f, 0.41f, 1.00f);
        theme.setColor(ImGuiCol.ScrollbarGrabActive, 0.51f, 0.51f, 0.51f, 1.00f);
        theme.setColor(ImGuiCol.CheckMark, 0.11f, 0.64f, 0.92f, 1.00f);
        theme.setColor(ImGuiCol.SliderGrab, 0.11f, 0.64f, 0.92f, 1.00f);
        theme.setColor(ImGuiCol.SliderGrabActive, 0.08f, 0.50f, 0.72f, 1.00f);
        theme.setColor(ImGuiCol.Button, 0.25f, 0.25f, 0.25f, 1.00f);
        theme.setColor(ImGuiCol.ButtonHovered, 0.38f, 0.38f, 0.38f, 1.00f);
        theme.setColor(ImGuiCol.ButtonActive, 0.67f, 0.67f, 0.67f, 0.39f);
        theme.setColor(ImGuiCol.Header, 0.22f, 0.22f, 0.22f, 1.00f);
        theme.setColor(ImGuiCol.HeaderHovered, 0.25f, 0.25f, 0.25f, 1.00f);
        theme.setColor(ImGuiCol.HeaderActive, 0.67f, 0.67f, 0.67f, 0.39f);
        theme.setColor(ImGuiCol.Separator, 0.41f, 0.42f, 0.44f, 1.00f);
        theme.setColor(ImGuiCol.SeparatorHovered, 0.26f, 0.59f, 0.98f, 0.95f);
        theme.setColor(ImGuiCol.SeparatorActive, 0.00f, 0.00f, 0.00f, 0.00f);
        theme.setColor(ImGuiCol.ResizeGrip, 0.00f, 0.00f, 0.00f, 0.00f);
        theme.setColor(ImGuiCol.ResizeGripHovered, 0.29f, 0.30f, 0.31f, 0.67f);
        theme.setColor(ImGuiCol.ResizeGripActive, 0.26f, 0.59f, 0.98f, 0.95f);
        theme.setColor(ImGuiCol.Tab,0.08f, 0.08f, 0.09f, 0.83f);
        theme.setColor(ImGuiCol.TabHovered,0.33f, 0.34f, 0.36f, 0.83f);
        theme.setColor(ImGuiCol.TabActive,0.23f, 0.23f, 0.24f, 1.00f);
        theme.setColor(ImGuiCol.TabUnfocused,0.08f, 0.08f, 0.09f, 1.00f);
        theme.setColor(ImGuiCol.TabUnfocusedActive,0.13f, 0.14f, 0.15f, 1.00f);
        theme.setColor(ImGuiCol.DockingPreview,0.26f, 0.59f, 0.98f, 0.70f);
        theme.setColor(ImGuiCol.DockingEmptyBg,0.20f, 0.20f, 0.20f, 1.00f);
        theme.setColor(ImGuiCol.PlotLines, 0.61f, 0.61f, 0.61f, 1.00f);
        theme.setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        theme.setColor(ImGuiCol.PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        theme.setColor(ImGuiCol.PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        theme.setColor(ImGuiCol.TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f);
        theme.setColor(ImGuiCol.ModalWindowDimBg, 0.80f, 0.80f, 0.80f, 0.20f);
        theme.setColor(ImGuiCol.DragDropTarget, 0.26f, 0.59f, 0.98f, 1.00f);
        theme.setColor(ImGuiCol.NavHighlight, 0.60f, 0.60f, 0.60f, 1.00f);
        theme.setColor(ImGuiCol.NavWindowingHighlight, 1.00f, 1.00f, 1.00f, 0.70f);

        /*theme.setColor(ImGuiCol.Text, 0.925f, 0.937f, 0.956f, 1f);
        theme.setColor(ImGuiCol.TextDisabled, 0.847f, 0.870f, 0.913f, 1f);
        theme.setColor(ImGuiCol.WindowBg, 0.180f, 0.203f, 0.250f, 1f);
        theme.setColor(ImGuiCol.ChildBg, 0.180f, 0.203f, 0.250f, 1f);
        theme.setColor(ImGuiCol.PopupBg, 0.231f, 0.258f, 0.321f, 1f);
        theme.setColor(ImGuiCol.Border, 0.298f, 0.337f, 0.415f, 1f);
        theme.setColor(ImGuiCol.BorderShadow, 0.180f, 0.203f, 0.250f, 0.3f);
        theme.setColor(ImGuiCol.FrameBg, 0.262f, 0.298f, 0.368f, 1f);
        theme.setColor(ImGuiCol.FrameBgActive, 0.015f, 0.070f, 0.035f, 1f);
        theme.setColor(ImGuiCol.FrameBgHovered, 0.505f, 0.631f, 0.756f, 1f);
        theme.setColor(ImGuiCol.TitleBg, 0.298f, 0.337f, 0.415f, 1f);
        theme.setColor(ImGuiCol.TitleBgActive, 0.505f, 0.631f, 0.756f, 1f);
        theme.setColor(ImGuiCol.TitleBgCollapsed, 0.298f, 0.337f, 0.415f, 1f);
        theme.setColor(ImGuiCol.MenuBarBg, 0.180f, 0.203f, 0.250f, 1f);
        theme.setColor(ImGuiCol.Tab, 0.262f, 0.298f, 0.368f, 1f);
        theme.setColor(ImGuiCol.TabActive, 0.368f, 0.505f, 0.674f, 1f);
        theme.setColor(ImGuiCol.TabHovered, 0.368f, 0.505f, 0.674f, 1f);
        theme.setColor(ImGuiCol.TabUnfocusedActive, 0.262f, 0.298f, 0.368f, 1f);
        theme.setColor(ImGuiCol.TabUnfocused, 0.231f, 0.258f, 0.321f, 1f);
        theme.setColor(ImGuiCol.ResizeGrip, 0.298f, 0.337f, 0.415f, 1f);
        theme.setColor(ImGuiCol.ResizeGripActive, 0.368f, 0.505f, 0.674f, 1f);
        theme.setColor(ImGuiCol.ResizeGripHovered, 0.505f, 0.631f, 0.756f, 1f);
        theme.setColor(ImGuiCol.Separator, 0.298f, 0.337f, 0.415f, 1f);
        theme.setColor(ImGuiCol.SeparatorHovered, 0.505f, 0.631f, 0.756f, 1f);
        theme.setColor(ImGuiCol.SeparatorActive, 0.368f, 0.505f, 0.674f, 1f);
        theme.setColor(ImGuiCol.Button, 0.262f, 0.298f, 0.368f, 1f);
        theme.setColor(ImGuiCol.ButtonHovered, 0.298f, 0.337f, 0.415f, 1f);
        theme.setColor(ImGuiCol.ButtonActive, 0.231f, 0.258f, 0.321f, 1f);
        theme.setColor(ImGuiCol.SliderGrab, 0.262f, 0.298f, 0.368f, 1f);
        theme.setColor(ImGuiCol.SliderGrabActive, 0.298f, 0.337f, 0.415f, 1f);
        theme.setColor(ImGuiCol.ScrollbarBg, 0.180f, 0.203f, 0.250f, 1f);
        theme.setColor(ImGuiCol.ScrollbarGrab, 0.262f, 0.298f, 0.368f, 1f);
        theme.setColor(ImGuiCol.ScrollbarGrabHovered, 0.298f, 0.337f, 0.415f, 1f);
        theme.setColor(ImGuiCol.ScrollbarGrabActive, 0.231f, 0.258f, 0.321f, 1f);*/
    }

    private void setupDockspace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));
    }

    public PropertiesWindow getPropertiesWindow() {
        return this.inspector;
    }
}
