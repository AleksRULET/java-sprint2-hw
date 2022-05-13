package util;

import managers.*;

public class Managers {

    public static TaskManager  getDefault() {
        return new FileBackedTasksManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
