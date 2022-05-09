package managers;

import tasks.Task;

import java.io.IOException;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory() throws IOException;
    List<Integer> fromString(String line);
}
