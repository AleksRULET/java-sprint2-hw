package managers;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final static File file = new File("task-manager.csv");

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
        IDNumber = 0;
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            BufferedWriter bf = new BufferedWriter(fileWriter);
            bf.write("id,type,name,status,description,duration,startTime,endTime,epic\n");
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

    static FileBackedTasksManager loadFromFile(File file) throws FileNotFoundException {
        FileBackedTasksManager newManager = new FileBackedTasksManager(new InMemoryHistoryManager());
        List<Integer> list = null;
        String line ;
        int maxID = 0;
        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    if (tasksFromString(line, newManager).getClass().equals(Task.class)) {
                        Task t = tasksFromString(line, newManager);
                        newManager.tasks.put(t.getID(), t);
                    } else if (tasksFromString(line, newManager).getClass().equals(Epic.class)) {
                        Epic e = (Epic) tasksFromString(line, newManager);
                        newManager.epics.put(e.getID(), e);
                    } else if (tasksFromString(line, newManager).getClass().equals(Subtask.class)) {
                        Subtask s = (Subtask) tasksFromString(line, newManager);
                        newManager.subtasks.put(tasksFromString(line, newManager).getID(), s);
                        if (s != null) {
                            newManager.epics.get(s.getEpicID()).addSubtask(s);
                        }
                    }
                    if (tasksFromString(line, newManager).getID() > maxID) maxID = tasksFromString(line, newManager).getID();
                } else {
                    line = br.readLine();
                    list =  newManager.historyManager.fromString(line);
                }
            }
            if (list != null) {
                for (Integer id : list) {
                    newManager.historyManager.add(newManager.tasks.get(id));
                    newManager.historyManager.add(newManager.epics.get(id));
                    newManager.historyManager.add(newManager.subtasks.get(id));
                }
            }
            newManager.setIDNumber((maxID+1));
            return newManager;
        } catch(FileNotFoundException e) {
            throw new FileNotFoundException("File not found");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
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
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
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
        if(checkIntersections(newTask)) {
            if (tasks.containsKey(ID)) {
                newTask.setID(ID);
                newTask.setStatus(newStatus);
                prioritizedTasks.remove(tasks.get(ID));
                tasks.put(ID, newTask);
                prioritizedTasks.add(newTask);
                save();
            } else throw new RuntimeException("Неккоректный идентификатор");
        }
    }

    @Override
    public void updateEpic(int ID, Epic newEpic) {
        if (epics.containsKey(ID)) {
            newEpic.setSubtaskList(epics.get(ID).getSubtaskList());
            newEpic.setID(ID);
            epics.put(ID, newEpic);
            save();
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
                save();
            } else throw new RuntimeException("Неккоректный идентификатор");
        }
    }

    @Override
    public void deleteTaskByID(int ID) {
        if (tasks.containsKey(ID)) {
            prioritizedTasks.remove(tasks.get(ID));
            tasks.remove(ID);
            historyManager.remove(ID);
            save();
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
            save();
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
            save();
        } else throw new RuntimeException("Неккоректный идентификатор");
    }

    private static Task tasksFromString(String value, FileBackedTasksManager newManager) {
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
        int duration = 0;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        int epicID = 0;
        scanner = new Scanner(value);
        scanner.useDelimiter(",");
        while (scanner.hasNext()) {
            String data = scanner.next();
            if (!data.equals("null")) {
                switch (index) {
                    case 0:
                        int intData = Integer.parseInt(data);
                        if (intData > newManager.getIDNumber()) {
                            newManager.setIDNumber(intData);
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
                        duration = Integer.parseInt(data);
                        break;
                    case 6:
                        startTime = LocalDateTime.parse(data);
                        break;
                    case 7:
                        endTime = LocalDateTime.parse(data);
                        break;
                    case 8:
                        epicID = Integer.parseInt(data);
                        break;
                }
            }
            index++;
        }
        switch (type) {
            case TASK:
                t = new Task(taskID, desc, name, status, duration, startTime, endTime);
                return t;
            case EPIC:
                e = new Epic(taskID, desc, name, status, duration, startTime, endTime);
                return e;
            case SUBTASK:
                s = new Subtask(taskID, desc, name, status, duration, startTime, endTime, epicID);
                return s;
        }
        return null;
    }
}
