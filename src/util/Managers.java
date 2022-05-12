package util;

import managers.*;

import java.io.IOException;

public class Managers {

    public static TaskManager  getDefault() throws IOException {
        return new FileBackedTasksManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
