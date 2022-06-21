package util;

import managers.*;

public class Managers {

    public static TaskManager getDefault() {
        return new HttpTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
