package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface TaskManager {

    Collection<Task> getAllTask();

    Collection<Epic> getAllEpics();

    Collection<Subtask> getAllSubtasks();

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    Task getTaskByID(int ID);

    Epic getEpicByID(int ID);

    Subtask getSubtaskByID(int ID);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(int ID, Task newTask, String newStatus);

    void updateEpic(int ID, Epic newEpic);

    void updateSubtask(int ID, Subtask newSubtask, String newStatus);

    void deleteTaskByID(int ID);

    void deleteEpicByID(int ID);

    void deleteSubtaskByID(Integer ID);

    ArrayList<Subtask> getSubtasksOfEpic(int ID);

    List<Task> history();
}
