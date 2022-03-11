import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int IDNumber = 0;
    HashMap <Integer, Task> tasks = new HashMap<>();
    HashMap <Integer, Epic> epics = new HashMap<>();
    HashMap <Integer, Subtask> subtasks = new HashMap<>();

    public Object getAllTask() {
        return tasks;
    }

    public Object getAllEpics() {
        return epics;
    }

    public Object getAllSubtasks() {
        return subtasks;
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

    public Object getTask(int ID) {
        return tasks.get(ID);
    }

    public Object getEpic(int ID) {
        return epics.get(ID);
    }

    public Object getSubtask(int ID) {
        return subtasks.get(ID);
    }

    public void createTask(Task task) {
        tasks.put(IDNumber, task);
        task.setID(IDNumber);
        IDNumber++;
    }

    public void createEpic(Epic epic) {
        epics.put(IDNumber, epic);
        epic.setID(IDNumber);
        IDNumber++;
    }
    public void createSubtask(Subtask subtask) {
            subtasks.put(IDNumber, subtask);
            subtask.setID(IDNumber);
            epics.get(subtask.getEpicID()).addSubTaskID(subtask.getID());
            IDNumber++;
    }

    public void updateTask(Task task, Task newTask, String newStatus) {
        tasks.put(task.getID(), newTask);
        newTask.setID(task.getID());
        newTask.setStatus(newStatus);
    }

    public void updateEpic(Epic epic, Epic newEpic) {
        epics.put(epic.getID(), newEpic);
        newEpic.setID(epic.getID());
        for (Integer id : epic.getSubTaskID()) {
            newEpic.addSubTaskID(id);
        };
    }

    public void updateSubtask(Subtask subtask, Subtask newSubtask, String newStatus) {
        subtasks.put(subtask.getID(), newSubtask);
        newSubtask.setID(subtask.getID());
        newSubtask.setStatus(newStatus);
        int epicID = subtasks.get(newSubtask.getID()).getEpicID();
        epics.get(epicID).checkStatus(subtasks);

    }

    public void deleteTask(int ID) {
        tasks.remove(ID);
    }

    public void deleteEpic(int ID) {
        for (Integer subtaskID : epics.get(ID).getSubTaskID()) {
            subtasks.remove(subtaskID);
        }
        epics.remove(ID);
    }

    public void deleteSubtask(Integer ID) {
        epics.get(subtasks.get(ID).getEpicID()).getSubTaskID().remove(ID);
        epics.get(subtasks.get(ID).getEpicID()).checkStatus(subtasks);
        subtasks.remove(ID);




    }

    public ArrayList <Subtask> getSubtasksOfEpic(int ID) {
        ArrayList <Subtask> result = new ArrayList<>();
        for (Integer subTaskID : epics.get(ID).getSubTaskID()) {
            result.add(subtasks.get(subTaskID));
        }
        return result;
    }

}
