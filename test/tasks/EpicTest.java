package tasks;

import managers.FileBackedTasksManager;
import managers.InMemoryHistoryManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    FileBackedTasksManager tasksManager;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void init() {
        tasksManager = new FileBackedTasksManager(new InMemoryHistoryManager());
        epic = new Epic("Пустой эпик", "Описание");
        tasksManager.createEpic(epic);
        subtask1 = new Subtask("Подзадача1", "Описание", 10, LocalDateTime.of(2022, 1, 1, 00, 00), epic);
        subtask2 = new Subtask("Подзадача2", "Описание", 10, LocalDateTime.of(2022, 1, 2, 00, 00), epic);
    }

    @Test
    void checkStatus() {
        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус");

        tasksManager.createSubtask(subtask1);
        tasksManager.createSubtask(subtask2);
        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус");

        tasksManager.updateSubtask(subtask1.getID(), subtask1, "DONE");
        tasksManager.updateSubtask(subtask2.getID(), subtask2, "DONE");
        assertEquals(Status.DONE, epic.getStatus(), "Неверный статус");

        tasksManager.updateSubtask(subtask2.getID(), subtask2, "NEW");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус");

        tasksManager.updateSubtask(subtask1.getID(), subtask1, "IN_PROGRESS");
        tasksManager.updateSubtask(subtask2.getID(), subtask2, "IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус");
    }

    @Test
    void calculateTime() {
        assertEquals(null, epic.getStartTime());
        assertEquals(null, epic.getEndTime());
        assertEquals(0, epic.getDuration());

        tasksManager.createSubtask(subtask1);
        assertEquals(LocalDateTime.of(2022, 1, 1, 00, 00), epic.getStartTime(), "Неверное время начала эпика");
        assertEquals(LocalDateTime.of(2022, 1, 1, 00, 10), epic.getEndTime(), "Неверное время конца эпика");
        assertEquals(10, epic.getDuration(), "Неверная продолжительность эпика");

        tasksManager.createSubtask(subtask2);
        assertEquals(LocalDateTime.of(2022, 1, 1, 00, 00), epic.getStartTime(), "Неверное время начала эпика");
        assertEquals(LocalDateTime.of(2022, 1, 2, 00, 10), epic.getEndTime(), "Неверное время конца эпика");
        assertEquals(1450, epic.getDuration(), "Неверная продолжительность эпика");
    }
}