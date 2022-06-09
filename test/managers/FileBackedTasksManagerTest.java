package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest {

    @BeforeEach
    @Override
    void init() {
        taskManager = new FileBackedTasksManager(new InMemoryHistoryManager());
        super.init();
    }

    @Test
    void saveAndLoadTest() throws IOException {
        List<Task> list = new ArrayList<>();
        list.add(taskManager.getTaskByID(0));
        list.add(taskManager.getEpicByID(1));
        list.add(taskManager.getSubtaskByID(2));

        FileBackedTasksManager newManager = saveLoad(taskManager);
        assertArrayEquals(taskManager.getAllTask().toArray(), newManager.getAllTask().toArray(), "Задачи не совпадают");
        assertArrayEquals(taskManager.getAllSubtasks().toArray(), newManager.getAllSubtasks().toArray(), "Подзадачи не совпадают");
        assertArrayEquals(taskManager.getAllEpics().toArray(), newManager.getAllEpics().toArray(), "Эпики не совпадают");
        assertArrayEquals(taskManager.history().toArray(), newManager.history().toArray(), "Истории не совпадают");

        taskManager = new FileBackedTasksManager(new InMemoryHistoryManager());
        newManager = saveLoad(taskManager);
        assertEquals(taskManager.getAllTask().size(), newManager.getAllTask().size(), "Список задач не пуст");
        assertEquals(taskManager.getAllTask(), newManager.getAllTask(), "Задачи не совпадают");
        assertArrayEquals(taskManager.getAllSubtasks().toArray(), newManager.getAllSubtasks().toArray(), "Подзадачи не совпадают");
        assertArrayEquals(taskManager.getAllEpics().toArray(), newManager.getAllEpics().toArray(), "Эпики не совпадают");
        assertArrayEquals(taskManager.history().toArray(), newManager.history().toArray(), "Истории не совпадают");

        taskManager.createEpic(new Epic("Эпик без подзадач", "Описание"));
        newManager = saveLoad(taskManager);
        assertEquals(taskManager.getEpicByID(0), newManager.getEpicByID(0), "Эпики не совпадают");
        assertEquals(taskManager.getAllTask(), newManager.getAllTask(), "Задачи не совпадают");
        assertArrayEquals(taskManager.getAllSubtasks().toArray(), newManager.getAllSubtasks().toArray(), "Подзадачи не совпадают");
        assertArrayEquals(taskManager.history().toArray(), newManager.history().toArray(), "Истории не совпадают");

        FileNotFoundException exception = assertThrows(
                FileNotFoundException.class,
                () -> FileBackedTasksManager.loadFromFile(new File("task.csv")));
        assertEquals("File not found", exception.getMessage(), "Ошибка в выбрасывании исключения");
    }


    FileBackedTasksManager saveLoad(TaskManager manager) throws IOException {
        ((FileBackedTasksManager)  manager).save();
        return FileBackedTasksManager.loadFromFile(new File("task-manager.csv"));

    }
}