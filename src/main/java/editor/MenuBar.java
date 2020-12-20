package editor;

import imgui.ImGui;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

public class MenuBar {

    public void imgui() {
        ImGui.beginMainMenuBar();

        if(ImGui.beginMenu("File")) {
            if(ImGui.menuItem("New", "Ctrl+N")) {
                EventSystem.notify(null, new Event(EventType.NewLevel));
            }
            if(ImGui.menuItem("Open", "Ctrl+O")) {
                EventSystem.notify(null, new Event(EventType.OpenLevel));
            }
            if(ImGui.menuItem("Save", "Ctrl+S")) {
                EventSystem.notify(null, new Event(EventType.SaveLevel));
            }
            if(ImGui.menuItem("Load", "Ctrl+O")) {
                EventSystem.notify(null, new Event(EventType.LoadLevel));
            }
            if(ImGui.menuItem("Reload", "Ctrl+R")) {
                EventSystem.notify(null, new Event(EventType.ReloadLevel));
            }
            ImGui.endMenu();
        }

        ImGui.endMainMenuBar();
    }
}
