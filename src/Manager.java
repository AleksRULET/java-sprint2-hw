import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

public class Manager {
    private int IDNumber = 0;
    HashMap <Integer, Task> tasks = new HashMap<>();
    HashMap <Integer, Epic> epics = new HashMap<>();
    HashMap <Integer, Subtask> subtasks = new HashMap<>();

    public Object getAllTask() {
        return tasks.values();
    }

    public Object getAllEpics() {
        return epics.values();
    }

    public Object getAllSubtasks() {
        return subtasks.values();
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtask() {
        subtasks.clear();
    }

    public Object getTaskByID(int ID) {
        return tasks.get(ID).getName();
    }

    public Object getEpicByID(int ID) {
        return epics.get(ID).getName();
    }

    public Object getSubtaskByID(int ID) {
        return subtasks.get(ID).getName();
    }

    public void createTask(Task task) {
        task.setID(IDNumber);
        tasks.put(IDNumber, task);
        IDNumber++;
    }

    public void createEpic(Epic epic) {
        epic.setID(IDNumber);
        epics.put(IDNumber, epic);
        IDNumber++;
    }

    public void createSubtask(Subtask subtask) {
        subtask.setID(IDNumber);
        epics.get(subtask.getEpicID()).addSubtask(subtask);
        subtasks.put(IDNumber, subtask);
        IDNumber++;
    }

    public void updateTask(int ID, Task newTask, String newStatus) {
        newTask.setID(ID);
        newTask.setStatus(newStatus);
        tasks.put(ID, newTask);
    }

    public void updateEpic(int ID, Epic newEpic) {
        newEpic.setSubtaskList(epics.get(ID).getSubtaskList());
        newEpic.setID(ID);
        epics.put(ID, newEpic);
    }

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

    public void deleteTaskByID(int ID) {
        tasks.remove(ID);
    }

    public void deleteEpicByID(int ID) {
        for (Subtask subtask : epics.get(ID).getSubtaskList()) {
            subtasks.remove(subtask.getID());
        }
        epics.remove(ID);
    }

    public void deleteSubtaskByID(Integer ID) {
        epics.get(subtasks.get(ID).getEpicID()).getSubtaskList().remove(subtasks.get(ID));
        epics.get(subtasks.get(ID).getEpicID()).checkStatus();
        subtasks.remove(ID);
    }

    public ArrayList <Subtask> getSubtasksOfEpic(int ID) {
        return epics.get(ID).getSubtaskList();
    }

}
