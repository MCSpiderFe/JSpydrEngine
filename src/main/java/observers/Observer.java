package observers;

import observers.events.Event;
import spydr.GameObject;

public interface Observer {
    void onNotify(GameObject object, Event event);
}
