package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int IDNumber;
    protected HashMap <Integer, Task> tasks;
    protected HashMap <Integer, Epic> epics;
    protected HashMap <Integer, Subtask> subtasks;
    protected HistoryManager historyManager;
    protected TreeSet <Task> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime() != null && task2.getStartTime() != null) {
                return task1.getStartTime().compareTo(task2.getStartTime());
            }
            return (task1.getStartTime() != null) ? -1 : 1;
        });
        this.IDNumber = 0;
        this.historyManager = historyManager;
    }

    public void setIDNumber(int ID) {
        IDNumber = ID;
    }

    public int getIDNumber() {
        return IDNumber;
    }

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTask() {
        for (Task task: tasks.values()) {
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        for (Subtask subtask: subtasks.values()) {
            prioritizedTasks.remove(subtask);
        }
        epics.clear();
        subtasks.clear();

    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask: subtasks.values()) {
            prioritizedTasks.remove(subtask);
        }
        for (Subtask subtask : subtasks.values()) {
            epics.get(subtask.getEpicID()).removeSubtask(subtask);
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskByID(int ID) {
        historyManager.add(tasks.get(ID));
        return tasks.get(ID);
    }

    @Override
    public Epic getEpicByID(int ID) {
        historyManager.add(epics.get(ID));
        return epics.get(ID);
    }

    @Override
    public Subtask getSubtaskByID(int ID) {
        historyManager.add(subtasks.get(ID));
        return subtasks.get(ID);
    }

    @Override
    public void createTask(Task task) {
        if (checkIntersections(task)) {
            task.setID(IDNumber);
            tasks.put(IDNumber, task);
            prioritizedTasks.add(task);
            IDNumber++;
        }
    }

    @Override
    public void createEpic(Epic epic) {
            epic.setID(IDNumber);
            epics.put(IDNumber, epic);
            IDNumber++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if(checkIntersections(subtask)) {
            subtask.setID(IDNumber);
            epics.get(subtask.getEpicID()).addSubtask(subtask);
            subtasks.put(IDNumber, subtask);
            prioritizedTasks.add(subtask);
            IDNumber++;
        }
    }

    @Override
    public void updateTask(int ID, Task newTask, String newStatus) {
        if(checkIntersections(newTask)) {
            if (tasks.containsKey(ID)) {
                newTask.setID(ID);
                newTask.setStatus(newStatus);
                prioritizedTasks.remove(tasks.get(ID));
                tasks.put(ID, newTask);
                prioritizedTasks.add(newTask);
            } else throw new RuntimeException("Неккоректный идентификатор");
        }
    }

    @Override
    public void updateEpic(int ID, Epic newEpic) {
        if (epics.containsKey(ID)) {
            newEpic.setSubtaskList(epics.get(ID).getSubtaskList());
            newEpic.setID(ID);
            epics.put(ID, newEpic);
        } else throw new RuntimeException("Неккоректный идентификатор");
    }

    @Override
    public void updateSubtask(int ID, Subtask newSubtask, String newStatus) {
        if(checkIntersections(newSubtask)) {
            if (subtasks.containsKey(ID)) {
                newSubtask.setID(ID);
                newSubtask.setStatus(newStatus);
                ListIterator<Subtask> iterator = epics.get(newSubtask.getEpicID()).getSubtaskList().listIterator();
                while (iterator.hasNext()) {
                    Subtask next = iterator.next();
                    if (next.equals(subtasks.get(ID))) {
                        iterator.set(newSubtask);
                    }
                }
                prioritizedTasks.remove(subtasks.get(ID));
                subtasks.put(ID, newSubtask);
                prioritizedTasks.add(newSubtask);
                epics.get(newSubtask.getEpicID()).checkStatus();
                epics.get(newSubtask.getEpicID()).calculateTime();
            } else throw new RuntimeException("Неккоректный идентификатор");
        }
    }

    @Override
    public void deleteTaskByID(int ID) throws RuntimeException {
        if (tasks.containsKey(ID)) {
            prioritizedTasks.remove(tasks.get(ID));
            tasks.remove(ID);
            historyManager.remove(ID);
        } else throw new RuntimeException("Неккоректный идентификатор");
    }

    @Override
    public void deleteEpicByID(int ID) {
        if (epics.containsKey(ID)) {
            for (Subtask subtask : epics.get(ID).getSubtaskList()) {
                subtasks.remove(subtask.getID());
                historyManager.remove(subtask.getID());
            }
            epics.remove(ID);
            historyManager.remove(ID);
        } else throw new RuntimeException("Неккоректный идентификатор");
    }

    @Override
    public void deleteSubtaskByID(Integer ID) {
        if (subtasks.containsKey(ID)) {
            epics.get(subtasks.get(ID).getEpicID()).getSubtaskList().remove(subtasks.get(ID));
            epics.get(subtasks.get(ID).getEpicID()).checkStatus();
            epics.get(subtasks.get(ID).getEpicID()).calculateTime();
            prioritizedTasks.remove(subtasks.get(ID));
            subtasks.remove(ID);
            historyManager.remove(ID);
        } else throw new RuntimeException("Неккоректный идентификатор");
    }

    @Override
    public ArrayList <Subtask> getSubtasksOfEpic(int ID) {
        if (epics.containsKey(ID)) {
            return epics.get(ID).getSubtaskList();
        } else throw new RuntimeException("Неккоректный идентификатор");
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    boolean checkIntersections(Task checkTask) {
        for (Task task: prioritizedTasks) {
            if (((checkTask.getStartTime() != null) && (checkTask.getEndTime() != null))
                && ((task.getStartTime() != null) && (task.getEndTime() != null))) {
                if (checkTask.getStartTime().isAfter(task.getStartTime()) && checkTask.getStartTime().isBefore(task.getEndTime())) {
                    return false;
                }
                if (checkTask.getEndTime().isAfter(task.getStartTime()) && checkTask.getEndTime().isBefore(task.getEndTime())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}
