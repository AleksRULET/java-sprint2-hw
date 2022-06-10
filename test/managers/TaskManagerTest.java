package managers;

import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

 abstract class TaskManagerTest<T extends TaskManager> {
     T taskManager;
     Task task;
     Epic epic;
     Subtask subtask;

     void init() {
        task = new Task("Задача", "Описание", 10, LocalDateTime.of(2022, 5, 15, 19, 30));
        taskManager.createTask(task);
        epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);
        subtask = new Subtask("Подзадача", "Описание", 10, LocalDateTime.of(2022, 6, 15, 19, 30), epic);
        taskManager.createSubtask(subtask);
    }

    void clear() {
         taskManager.deleteAllTask();
         taskManager.deleteAllEpic();
         taskManager.deleteAllSubtask();
    }

    @Test
    void getAllTask() {
        List<Task> list = taskManager.getAllTask();

        assertNotNull(list, "Список задач не создан");
        assertEquals(1 , list.size(), "Размер спика задач не совпадает");
        assertEquals(0, list.get(0).getID(), "Идентификаторы элементов списков не совпадают");
        assertEquals(task, list.get(0), "Задачи не совпадают");

        clear();
        list = taskManager.getAllTask();
        assertNotNull(list, "Список задач не создан");
        assertEquals(0 , list.size(), "Список задач не пуст");
    }

    @Test
    void getAllEpics() {
        List<Epic> list = taskManager.getAllEpics();

        assertNotNull(list, "Список задач не создан");
        Assertions.assertEquals(1 , list.size(), "Размер спика задач не совпадает");
        Assertions.assertEquals(1, list.get(0).getID(), "Идентификаторы элементов списков не совпадают");
        Assertions.assertEquals(epic, list.get(0), "Задачи не совпадают");

        clear();
        list = taskManager.getAllEpics();
        assertNotNull(list, "Список задач не создан");
        assertEquals(0 , list.size(), "Список задач не пуст");
    }

    @Test
    void getAllSubtasks() {
        List<Subtask> list = taskManager.getAllSubtasks();

        assertNotNull(list, "Список задач не создан");
        assertEquals(1, list.size(), "Размер спика задач не совпадает");
        assertEquals(2, list.get(0).getID(), "Идентификаторы элементов списков не совпадают");
        assertEquals(subtask, list.get(0), "Задачи не совпадают");

        clear();
        list = taskManager.getAllSubtasks();
        assertNotNull(list, "Список задач не создан");
        assertEquals(0 , list.size(), "Список задач не пуст");
    }

    @Test
    void deleteAllTask() {
        taskManager.deleteAllTask();
        assertNull(taskManager.getTaskByID(0), "Список задач не пуст");
    }

    @Test
    void deleteAllEpic() {
        taskManager.deleteAllEpic();
        assertNull(taskManager.getEpicByID(1), "Список задач не пуст");
        assertNull(taskManager.getSubtaskByID(2), "Список подзадач не пуст");
    }

    @Test
    void deleteAllSubtask() {
        taskManager.deleteAllSubtask();
        assertNull(taskManager.getSubtaskByID(2), "Список подзадач не пуст");
    }

    @Test
    void getTaskByID() {
        assertEquals(task, taskManager.getTaskByID(0), "Задачи не совпадают");
        assertEquals(null, taskManager.getTaskByID(1), "Неверный идентификатор");
    }

    @Test
    void getEpicByID() {
        assertEquals(epic, taskManager.getEpicByID(1), "Задачи не совпадают");
        assertEquals(null, taskManager.getEpicByID(2), "Неверный идентификатор");
    }

    @Test
    void getSubtaskByID() {
        assertEquals(subtask, taskManager.getSubtaskByID(2), "Задачи не совпадают");
        assertEquals(null, taskManager.getTaskByID(3), "Неверный идентификатор");
    }

    @Test
    void createTask() {
         Task newTask = new Task("НоваяЗадача", "Описание", 10, LocalDateTime.now());
         taskManager.createTask(newTask);
        assertEquals(2, taskManager.getAllTask().size(), "Размер списков не совпадает");
         assertEquals(newTask, taskManager.getTaskByID(3), "Задачи не совпадают");
    }

    @Test
    void createEpic() {
        Epic newEpic = new Epic("НовыйЭпик", "Описание");
        taskManager.createEpic(newEpic);
        assertEquals(2, taskManager.getAllEpics().size(), "Размер списков не совпадает");
        assertEquals(newEpic, taskManager.getEpicByID(3), "Задачи не совпадают");
    }

    @Test
    void createSubtask() {
        Subtask newSubtask = new Subtask("НоваяПодзадача", "Описание", 10, LocalDateTime.now(), epic);
        taskManager.createSubtask(newSubtask);
        assertEquals(2, taskManager.getAllSubtasks().size(), "Размер списков не совпадает");
        assertEquals(newSubtask, taskManager.getSubtaskByID(3), "Задачи не совпадают");
        assertEquals(epic, taskManager.getEpicByID(newSubtask.getEpicID()), "Нверная принадлежность к эпикам");
    }

    @Test
    void updateTask() {
        Task newTask = new Task("НоваяЗадача", "Описание", 10, LocalDateTime.now());

        assertEquals(Status.NEW, newTask.getStatus(), "Статус не равен NEW");
        assertEquals(Status.NEW, taskManager.getTaskByID(0).getStatus(), "Статус не равен NEW");
        taskManager.updateTask(0, newTask, "IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS, newTask.getStatus(), "Неправильный статус");
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskByID(0).getStatus(), "Неправильный статус");


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.updateTask(4, newTask, "IN_PROGRESS"));
        assertEquals("Неккоректный идентификатор", exception.getMessage(), "Ошибка в выбрасывании исключения");
    }

    @Test
    void updateEpic() {
        Epic newEpic = new Epic("НовыйЭпик", "Описание");
        taskManager.updateEpic(1, newEpic);
        assertEquals(newEpic, taskManager.getEpicByID(1));
        List<Subtask> list = taskManager.getEpicByID(1).getSubtaskList();
        assertEquals(list.get(0), new ArrayList<>(taskManager.getAllSubtasks()).get(0));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.updateEpic(3, newEpic));
        assertEquals("Неккоректный идентификатор", exception.getMessage(), "Ошибка в выбрасывании исключения");
    }

    @Test
    void updateSubtask() {
        Subtask newSubtask = new Subtask("НоваяЗадача", "Описание", 10, LocalDateTime.now(), epic);
        assertEquals(Status.NEW, newSubtask.getStatus());
        assertEquals(Status.NEW, taskManager.getSubtaskByID(2).getStatus());
        taskManager.updateSubtask(2, newSubtask, "IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS, newSubtask.getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskByID(2).getStatus());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.updateSubtask(3, newSubtask, "IN_PROGRESS"));
        assertEquals("Неккоректный идентификатор", exception.getMessage(), "Ошибка в выбрасывании исключения");
    }

    @Test
    void deleteTaskByID() {
         taskManager.deleteTaskByID(0);
         assertEquals(0, new ArrayList<>(taskManager.getAllTask()).size());
         assertNull(taskManager.getTaskByID(0));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.deleteTaskByID(3));
        assertEquals("Неккоректный идентификатор", exception.getMessage(), "Ошибка в выбрасывании исключения");
    }

    @Test
    void deleteEpicByID() {
        taskManager.deleteEpicByID(1);
        assertEquals(0, new ArrayList<>(taskManager.getAllEpics()).size());
        assertNull(taskManager.getEpicByID(1));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.deleteEpicByID(4));
        assertEquals("Неккоректный идентификатор", exception.getMessage(), "Ошибка в выбрасывании исключения");
    }

    @Test
    void deleteSubtaskByID() {
        taskManager.deleteSubtaskByID(2);
        assertEquals(0, new ArrayList<>(taskManager.getAllSubtasks()).size());
        assertNull(taskManager.getSubtaskByID(2));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.deleteSubtaskByID(3));
        assertEquals("Неккоректный идентификатор", exception.getMessage(), "Ошибка в выбрасывании исключения");
    }

    @Test
    void getSubtasksOfEpic() {
         List<Subtask> list = taskManager.getSubtasksOfEpic(1);
         assertEquals(1, list.size());
         assertEquals(subtask, list.get(0));

         Epic newEpic = new Epic("Эпик без подзадач", "Описание");
         taskManager.createEpic(newEpic);
         assertEquals(new ArrayList<>(), taskManager.getSubtasksOfEpic(3));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.getSubtasksOfEpic(4));
        assertEquals("Неккоректный идентификатор", exception.getMessage(), "Ошибка в выбрасывании исключения");
    }

    @Test
    void getPrioritizedTasks() {
        Task task1 = new Task("Задача с пересечением", "Описание", 50, LocalDateTime.of(2022, 5, 15, 19, 34));
        Task task2 = new Task("Задача без пересечения", "Описание", 100, LocalDateTime.of(2022, 4, 15, 19, 30));
        Subtask subtask1 = new Subtask("Задача с пересечением", "Описание", 100, LocalDateTime.of(2022, 4, 15, 18, 30), epic);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createSubtask(subtask1);
        TreeSet<Task> treeSet = taskManager.getPrioritizedTasks();
        assertEquals(task2, treeSet.toArray()[0], "Элементы списка не совпадают");
        assertEquals(task, treeSet.toArray()[1], "Элементы списка не совпадают");
        assertEquals(subtask, treeSet.toArray()[2], "Элементы списка не совпадают");

        Task taskNull2 = new Task(9, "Задача с двумя Null", "Описание", Status.NEW, 50, null, null);
        Task task1Null = new Task(7,"Задача с Null в начале", "Описание", Status.NEW, 0,  null, LocalDateTime.of(2022, 10, 15, 18, 30));
        Task taskNull1 = new Task(8, "Задача с Null в конце", "Описание", Status.NEW, 50, LocalDateTime.of(2022, 9, 15, 18, 30), null);
        taskManager.createTask(taskNull1);
        taskManager.createTask(task1Null);
        taskManager.createTask(taskNull2);
        assertEquals(task2, treeSet.toArray()[0], "Элементы списка не совпадают");
        assertEquals(task, treeSet.toArray()[1], "Элементы списка не совпадают");
        assertEquals(subtask, treeSet.toArray()[2], "Элементы списка не совпадают");
        assertEquals(taskNull1, treeSet.toArray()[3], "Элементы списка не совпадают");
        assertEquals(task1Null, treeSet.toArray()[4], "Элементы списка не совпадают");
        assertEquals(taskNull2, treeSet.toArray()[5], "Элементы списка не совпадают");
    }

    @Test
    void history() {
        assertEquals(new ArrayList<>(), taskManager.history(), "Пустая история задач");
        List<Task> list = new ArrayList<>();

        list.add(taskManager.getTaskByID(0));
        list.add(taskManager.getEpicByID(1));
        list.add(taskManager.getSubtaskByID(2));

        assertEquals(list.size(), taskManager.history().size(), "Размер списка истории неверен");
        assertEquals(list.get(0), taskManager.history().get(0), "Задачи не равны");
        assertEquals(list.get(1), taskManager.history().get(1), "Задачи не равны");
        assertEquals(list.get(2), taskManager.history().get(2), "Задачи не равны");

        clear();
        assertEquals(list.size(), taskManager.history().size(), "Размер списка истории неверен");
        assertEquals(list.get(0), taskManager.history().get(0), "Задачи не равны");
        assertEquals(list.get(1), taskManager.history().get(1), "Задачи не равны");
        assertEquals(list.get(2), taskManager.history().get(2), "Задачи не равны");
    }
}