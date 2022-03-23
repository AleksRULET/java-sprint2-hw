import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int IDNumber = 0;
    HashMap <Integer, Task> tasks = new HashMap<>();
    HashMap <Integer, Epic> epics = new HashMap<>();
    HashMap <Integer, Subtask> subtasks = new HashMap<>();
    HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Collection<Task> getAllTask() {
        return tasks.values();
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
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
        task.setID(IDNumber);
        tasks.put(IDNumber, task);
        IDNumber++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setID(IDNumber);
        epics.put(IDNumber, epic);
        IDNumber++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setID(IDNumber);
        epics.get(subtask.getEpicID()).addSubtask(subtask);
        subtasks.put(IDNumber, subtask);
        IDNumber++;
    }

    @Override
    public void updateTask(int ID, Task newTask, String newStatus) {
        newTask.setID(ID);
        newTask.setStatus(newStatus);
        tasks.put(ID, newTask);
    }

    @Override
    public void updateEpic(int ID, Epic newEpic) {
        newEpic.setSubtaskList(epics.get(ID).getSubtaskList());
        newEpic.setID(ID);
        epics.put(ID, newEpic);
    }

    @Override
    public void updateSubtask(int ID, Subtask newSubtask, String newStatus) {
        newSubtask.setID(ID);
        newSubtask.setStatus(newStatus);
        ListIterator<Subtask> iterator = epics.get(newSubtask.getEpicID()).getSubtaskList().listIterator();
        while (iterator.hasNext()) {
            Subtask next = iterator.next();
            if (next.equals(subtasks.get(ID))) {
                iterator.set(newSubtask);
            }
        }
        subtasks.put(ID, newSubtask);
        epics.get(newSubtask.getEpicID()).checkStatus();
    }

    @Override
    public void deleteTaskByID(int ID) {
        tasks.remove(ID);
    }

    @Override
    public void deleteEpicByID(int ID) {
        for (Subtask subtask : epics.get(ID).getSubtaskList()) {
            subtasks.remove(subtask.getID());
        }
        epics.remove(ID);
    }

    @Override
    public void deleteSubtaskByID(Integer ID) {
        epics.get(subtasks.get(ID).getEpicID()).getSubtaskList().remove(subtasks.get(ID));
        epics.get(subtasks.get(ID).getEpicID()).checkStatus();
        subtasks.remove(ID);
    }

    @Override
    public ArrayList <Subtask> getSubtasksOfEpic(int ID) {
        return epics.get(ID).getSubtaskList();
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();

    }
}
