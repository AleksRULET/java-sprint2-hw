package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import http.LocalDateTimeAdapter;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient kvClient;
    private Gson gson;

    public HttpTaskManager(HistoryManager historyManager) {
        super(historyManager);
        kvClient = new KVTaskClient("http://localhost:8078");
        gson = new GsonBuilder().serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    }

    @Override
    public void save() {
        try {
            kvClient.put("tasks", gson.toJson(tasks));
            kvClient.put("epics", gson.toJson(tasks));
            kvClient.put("subtasks", gson.toJson(tasks));
            ArrayList<Integer> history = new ArrayList<>();
            for (Task task: history()) {
                history.add(task.getID());
            }
            kvClient.put("history", gson.toJson(history));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public HttpTaskManager load(String path)  {
        HttpTaskManager loadedManager;
        InMemoryHistoryManager loadedHistoryManager = new InMemoryHistoryManager();

        ArrayList<Integer> history = gson.fromJson(kvClient.load("history"), new TypeToken<ArrayList<Integer>>(){}.getType());
        for (Integer id: history) {
            loadedHistoryManager.add(tasks.get(id));
            loadedHistoryManager.add(epics.get(id));
            loadedHistoryManager.add(subtasks.get(id));
        }
        loadedManager = new HttpTaskManager(loadedHistoryManager);

        HashMap<Integer, Task> loadedTasks = gson.fromJson(kvClient.load("tasks"), new TypeToken<HashMap<Integer, Task>>(){}.getType());
        HashMap<Integer, Epic> loadedEpics = gson.fromJson(kvClient.load("epics"), new TypeToken<HashMap<Integer, Epic>>(){}.getType());
        HashMap<Integer, Subtask> loadedSubtasks = gson.fromJson(kvClient.load("subtasks"), new TypeToken<HashMap<Integer, Subtask>>(){}.getType());
        loadedManager.tasks = loadedTasks;
        loadedManager.epics = loadedEpics;
        loadedManager.subtasks = loadedSubtasks;

        return loadedManager;
    }
}
