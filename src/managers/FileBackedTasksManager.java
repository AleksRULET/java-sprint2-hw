package managers;

import Exeptions.ManagerSaveException;
import tasks.*;
import util.Managers;

import java.io.*;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final static File file = new File("task-manager.csv");

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    //<<<Тест функционала пятого спринта>>>//
    public static void main(String[] args) throws IOException {
        FileBackedTasksManager taskManager = (FileBackedTasksManager) Managers.getDefault();
        Task study = new Task("Учёба", "Описание");
        taskManager.createTask(study);

        Task workout = new Task("Тренировка", "Описание");
        taskManager.createTask(workout);

        Epic relocation = new Epic("Переезд", "Описание");
        taskManager.createEpic(relocation);

        Subtask getAVisa = new Subtask("Получить визу", "Описание", relocation);
        taskManager.createSubtask(getAVisa);

        Subtask accumulateFunds = new Subtask("Накопить деньги", "Описание",  relocation);
        taskManager.createSubtask(accumulateFunds);

        Subtask prepareDocuments = new Subtask("Подготовить документы", "Описание",  relocation);
        taskManager.createSubtask(prepareDocuments);

        Epic survival = new Epic("Выживание", "Описание");
        taskManager.createEpic(survival);
        //Создали задачи//

        taskManager.getTaskByID(0);
        taskManager.getTaskByID(1);
        taskManager.getEpicByID(2);
        taskManager.getSubtaskByID(3);
        taskManager.getSubtaskByID(4);
        taskManager.getSubtaskByID(5);
        taskManager.getEpicByID(6);
        taskManager.save();
        System.out.println("\n" + taskManager.history());
        //Вызвали методы и истоию//

        FileBackedTasksManager newFileBakedManager = loadFromFile(file);
        System.out.println("\n" + newFileBakedManager.history());
        //Загрузили новый объект из файла и вызвали его историю//
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            BufferedWriter bf = new BufferedWriter(fileWriter);
            bf.write("id,type,name,status,description,epic\n");
            for (Task t: tasks.values()) {
                bf.write(t.tasksToString() + "\n");
            }
            for (Epic e: epics.values()) {
                bf.write(e.tasksToString() + "\n");
            }
            for (Subtask s: subtasks.values()) {
                bf.write(s.tasksToString() + "\n");
            }
            bf.write("\n" + historyManager.toString());
            bf.flush();
        } catch (IOException e) {
            throw new ManagerSaveException("File saving process error");
        }
    }

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager newManager = new FileBackedTasksManager(new InMemoryHistoryManager());
        List<Integer> list = null;
        String line ;
        int maxID = 0;
        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    if (tasksFromString(line).getClass().equals(Task.class)) {
                        Task t = tasksFromString(line);
                        newManager.tasks.put(t.getID(), t);
                    } else if (tasksFromString(line).getClass().equals(Epic.class)) {
                        Epic e = (Epic) tasksFromString(line);
                        newManager.epics.put(e.getID(), e);
                    } else if (tasksFromString(line).getClass().equals(Subtask.class)) {
                        Subtask s = (Subtask) tasksFromString(line);
                        newManager.subtasks.put(tasksFromString(line).getID(), s);
                        if (s != null) {
                            newManager.epics.get(s.getEpicID()).addSubtask(s);
                        }
                    }
                    if (tasksFromString(line).getID() > maxID) maxID = tasksFromString(line).getID();
                } else {
                    line = br.readLine();
                    list =  newManager.historyManager.fromString(line);
                }
            }
            if (list != null) {
                for (Integer id : list) {
                    newManager.getTaskByID(id);
                    newManager.getEpicByID(id);
                    newManager.getSubtaskByID(id);
                }
            } else {
            }
            newManager.IDNumber = maxID+1;
            return newManager;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        subtasks.clear();
        save();
    }

    @Override
    public Task getTaskByID(int ID) {
        historyManager.add(tasks.get(ID));
        save();
        return tasks.get(ID);
    }

    @Override
    public Epic getEpicByID(int ID) {
        historyManager.add(epics.get(ID));
        save();
        return epics.get(ID);
    }

    @Override
    public Subtask getSubtaskByID(int ID) {
        historyManager.add(subtasks.get(ID));
        save();
        return subtasks.get(ID);
    }

    @Override
    public void updateTask(int ID, Task newTask, String newStatus) {
        newTask.setID(ID);
        newTask.setStatus(newStatus);
        tasks.put(ID, newTask);
        save();
    }

    @Override
    public void updateEpic(int ID, Epic newEpic) {
        newEpic.setSubtaskList(epics.get(ID).getSubtaskList());
        newEpic.setID(ID);
        epics.put(ID, newEpic);
        save();
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
        save();
    }

    @Override
    public void deleteTaskByID(int ID) {
        tasks.remove(ID);
        historyManager.remove(ID);
        save();
    }

    @Override
    public void deleteEpicByID(int ID) {
        for (Subtask subtask : epics.get(ID).getSubtaskList()) {
            subtasks.remove(subtask.getID());
            historyManager.remove(subtask.getID());
        }
        epics.remove(ID);
        historyManager.remove(ID);
        save();
    }

    @Override
    public void deleteSubtaskByID(Integer ID) {
        epics.get(subtasks.get(ID).getEpicID()).getSubtaskList().remove(subtasks.get(ID));
        epics.get(subtasks.get(ID).getEpicID()).checkStatus();
        subtasks.remove(ID);
        historyManager.remove(ID);
        save();
    }

    private static Task tasksFromString(String value) {
        int index = 0;
        Scanner scanner = null;
        Task t;
        Subtask s;
        Epic e;
        int taskID = 0;
        Types type = null;
        String desc = null;
        String name = null;
        Status status = null;
        int epicID = 0;
        scanner = new Scanner(value);
        scanner.useDelimiter(",");
        while (scanner.hasNext()) {
            String data = scanner.next();
            switch (index) {
                case 0:
                    int intData = Integer.parseInt(data);
                    if (intData > IDNumber) {
                        IDNumber = intData;
                    }
                    taskID = intData;
                    break;
                case 1:
                    type = Types.valueOf(data);
                    break;
                case 2:
                    name = data;
                    break;
                case 3:
                    status = Status.valueOf(data);
                    break;
                case 4:
                    desc = data;
                    break;
                case 5:
                    epicID = Integer.parseInt(data);
                    break;
            }
            index++;
        }
        switch (type) {
            case TASK:
                t = new Task(taskID, desc, name, status);
                return t;
            case EPIC:
                e = new Epic(taskID, desc, name, status);
                return e;
            case SUBTASK:
                s = new Subtask(taskID, desc, name, status, epicID);
                return s;
        }
        return null;
    }
}
