package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    InMemoryHistoryManager historyManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
        task = new Task(1, "Описание", "Задача", Status.NEW, 10, LocalDateTime.of(2022, 5, 15, 19, 30), LocalDateTime.of(2022, 5, 15, 19, 40));
        epic = new Epic(2, "Описание", "Эпик", Status.NEW, 10, LocalDateTime.of(2022, 6, 15, 19, 30), LocalDateTime.of(2022, 6, 15, 19, 40));
        subtask = new Subtask(3,"Описание", "Подзадача", Status.NEW, 10, LocalDateTime.of(2022, 6, 15, 19, 30), LocalDateTime.of(2022, 6, 15, 19, 40), epic.getID());
    }

    @Test
    void addAndGetHistoryTest() {
        assertEquals(new ArrayList<>(), historyManager.getHistory(), "Пустая история задач");

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size(), "Размер списка истории неверен");
        assertEquals(task, historyManager.getHistory().get(0), "Задачи не равны");
        assertEquals(epic, historyManager.getHistory().get(1), "Задачи не равны");
        assertEquals(subtask, historyManager.getHistory().get(2), "Задачи не равны");

        historyManager.add(epic);
        assertEquals(3, historyManager.getHistory().size(), "Дублирование работает некорректно");
        assertEquals(epic, historyManager.getHistory().get(2), "Дублирование работает некорректно");
    }

    @Test
    void removeTest() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.remove(task.getID());
        assertEquals(2, historyManager.getHistory().size(), "Удаление из начала истории работает некорректно");
        assertEquals(epic, historyManager.getHistory().get(0), "Удаление из начала истории работает некорректно");
        assertEquals(subtask, historyManager.getHistory().get(1), "Удаление из начала истории работает некорректно");
        historyManager.add(task);

        historyManager.remove(subtask.getID());
        assertEquals(2, historyManager.getHistory().size(), "Удаление из середины истории работает некорректно");
        assertEquals(epic, historyManager.getHistory().get(0), "Удаление из середины истории работает некорректно");
        assertEquals(task, historyManager.getHistory().get(1), "Удаление из середины истории работает некорректно");
        historyManager.add(subtask);

        historyManager.remove(subtask.getID());
        assertEquals(2, historyManager.getHistory().size(), "Удаление из конца истории работает некорректно");
        assertEquals(epic, historyManager.getHistory().get(0), "Удаление из конца истории работает некорректно");
        assertEquals(task, historyManager.getHistory().get(1), "Удаление из конца истории работает некорректно");
    }
}