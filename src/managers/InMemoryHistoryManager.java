package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyList.size()>9) {
            historyList.remove(0);
        }
            historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}