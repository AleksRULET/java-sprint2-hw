package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import http.HttpTaskServer;
import http.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDateTime;

class HttpTaskServerTest {
    KVServer kvServer;
    HttpTaskServer taskServer;
    HttpClient httpClient;
    Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    String task;
    String epic;
    Epic e;
    String subtask;
    String url = "http://localhost:8080/tasks";

    @BeforeEach
    void init() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
        httpClient = HttpClient.newHttpClient();
        task = gson.toJson(new Task("Задача", "Описание", 10, LocalDateTime.of(2022, 5, 15, 19, 30)));
        e = new Epic("Эпик", "Описание");
        epic = gson.toJson(e);
        subtask = gson.toJson(new Subtask("Подзадача", "Описание", 10, LocalDateTime.of(2022, 6, 15, 19, 30), e));
    }

    @AfterEach
    void after(){
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void tasks() {

    }

    @Test
    void taskById() {
    }

    @Test
    void createTask() {

    }

    @Test
    void updateTask() {
    }

    @Test
    void deleteTask() {
    }

    @Test
    void deleteAllTasks() {
    }


    @Test
    void subtasksOfEpic() {
    }

    @Test
    void allTasks() {
    }

    @Test
    void history() {
    }
}