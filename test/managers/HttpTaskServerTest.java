package managers;

import com.google.gson.*;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    KVServer kvServer;
    HttpTaskServer taskServer;
    HttpClient httpClient;
    Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    String task;
    String epic;
    Epic e;
    String subtask;
    final String url = "http://localhost:8080/tasks";
    HttpRequest createdTask;
    HttpRequest createdEpic;
    HttpRequest createdSubtask;

    @BeforeEach
    void init() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        task = gson.toJson(new Task("Задача", "Описание", 10, LocalDateTime.of(2022, 5, 15, 19, 30)));
        e = new Epic("Эпик", "Описание");
        epic = gson.toJson(e);
        subtask = gson.toJson(new Subtask("Подзадача", "Описание", 10, LocalDateTime.of(2023, 6, 15, 19, 30), e));

        createdTask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(task))
                .build();

        createdEpic = HttpRequest.newBuilder()
                .uri(URI.create(url + "/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(epic))
                .header("Content-Type", "application/json")
                .build();

        createdSubtask = HttpRequest.newBuilder()
                .uri(URI.create(url + "/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtask))
                .build();
    }


    @AfterEach
    void after(){
        taskServer.stop();
        kvServer.stop();
    }

    @Test
    void getAllTasks() {
        try {
            Task task2 = new Task("Задача2", "Описание", 10, LocalDateTime.of(2023, 5, 15, 19, 30));
            HttpRequest request12 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            httpClient.send(createdTask, HttpResponse.BodyHandlers.ofString());
            httpClient.send(request12, HttpResponse.BodyHandlers.ofString());

            Task t = gson.fromJson(task, Task.class);
            t.setID(0);
            task2.setID(1);

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task/"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
                JsonElement element = JsonParser.parseString(response.body());
                JsonArray jsonArray = element.getAsJsonArray();
                assertEquals(200, response.statusCode(), "Неккоректный код запроса");
                assertEquals(gson.fromJson(jsonArray.get(0), Task.class), t, "Задачи не равны");
                assertEquals(gson.fromJson(jsonArray.get(1), Task.class), task2, "Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void createAndGetTaskById() {
        try {
           HttpResponse response1 = httpClient.send(createdTask, HttpResponse.BodyHandlers.ofString());
           assertEquals(201, response1.statusCode(), "Неккоректный код запроса");

            Task t = gson.fromJson(task, Task.class);
            t.setID(0);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task"+ "?id=0"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();

            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response2.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(gson.fromJson(jsonArray.get(0), Task.class), t,"Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void updateTask() {
        Task task2 = new Task("Задача2", "Описание", 10, LocalDateTime.of(2023, 5, 15, 19, 30));
        try {
            httpClient.send(createdTask, HttpResponse.BodyHandlers.ofString());
            JsonArray jsonArray1 = new JsonArray();
            jsonArray1.add(gson.toJson(task2));
            jsonArray1.add(gson.toJson("NEW"));
            HttpRequest request1 = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(url + "/task"+ "?id=0"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(jsonArray1)))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response2 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response2.statusCode(), "Неккоректный код запроса");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task/"+ "?id=0"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response.body());
            JsonArray jsonArray = element.getAsJsonArray();
            task2.setID(0);
            assertEquals(gson.fromJson(jsonArray.get(0), Task.class), task2,"Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void deleteTask() {
        try {
            httpClient.send(createdTask, HttpResponse.BodyHandlers.ofString()).statusCode();

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task/"+ "?id=0"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .DELETE()
                    .build();
            httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).statusCode();
            assertEquals(200, httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).statusCode(),"Неккоректный код запроса");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task/"+ "?id=0"))
                    .GET()
                    .build();
            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response2.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(new JsonArray(), jsonArray,"Неккоректное удаление задачи");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void deleteAllTasks() {
        try {
            Task task2 = new Task("Задача2", "Описание", 10, LocalDateTime.of(2023, 5, 15, 19, 30));
            HttpRequest request12 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            httpClient.send(createdTask, HttpResponse.BodyHandlers.ofString());
            httpClient.send(request12, HttpResponse.BodyHandlers.ofString());

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task/"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .DELETE()
                    .build();
            assertEquals(200, httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).statusCode(),"Неккоректный код запроса");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task/"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();
            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response2.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(new JsonArray(), jsonArray,"Неккоректное удаление задач");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    void getAllEpics() {
        try {
            Epic epic2 = new Epic("Эпик2", "Описание");
            HttpRequest request12 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/epic/"))
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                    .build();
            httpClient.send(createdEpic, HttpResponse.BodyHandlers.ofString());
            httpClient.send(request12, HttpResponse.BodyHandlers.ofString());

            Epic t = gson.fromJson(epic, Epic.class);
            t.setID(0);
            epic2.setID(1);

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/epic/" ))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response.statusCode(), "Неккоректный код запроса");
            assertEquals(t, gson.fromJson(jsonArray.get(0), Epic.class), "Задачи не равны");
            assertEquals(epic2, gson.fromJson(jsonArray.get(1), Epic.class), "Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void createAndGetEpicById() {
        try {
            int s = httpClient.send(createdEpic, HttpResponse.BodyHandlers.ofString()).statusCode();
            assertEquals(201, s, "Неккоректный код запроса");

            Epic t = gson.fromJson(epic, Epic.class);
            t.setID(0);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/epic/"+ "?id=0"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();

            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response2.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(gson.fromJson(jsonArray.get(0), Epic.class), t,"Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void updateEpic() {
        Epic epic2 = new Epic("Задача2", "Описание");
        try {
            httpClient.send(createdEpic, HttpResponse.BodyHandlers.ofString());
            JsonArray jsonArray1 = new JsonArray();
            jsonArray1.add(gson.toJson(epic2));
            jsonArray1.add(gson.toJson("NEW"));
            HttpRequest request1 = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(url + "/epic/"+ "?id=0"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(jsonArray1)))
                    .header("Content-Type", "application/json")
                    .build();
            request1.bodyPublisher();
            HttpResponse response2 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response2.statusCode(), "Неккоректный код запроса");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(url + "/epic/"+ "?id=0"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response.body());
            JsonArray jsonArray = element.getAsJsonArray();
            epic2.setID(0);
            assertEquals(gson.fromJson(jsonArray.get(0), Epic.class), epic2,"Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void deleteEpic() {
        try {
            httpClient.send(createdEpic, HttpResponse.BodyHandlers.ofString()).statusCode();

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/epic/"+ "?id=0"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .DELETE()
                    .build();
            httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).statusCode();
            assertEquals(200, httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).statusCode(),"Неккоректный код запроса");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/epic/"+ "?id=0"))
                    .GET()
                    .build();
            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response2.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(new JsonArray(), jsonArray,"Неккоректное удаление задачи");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void deleteAllEpics() {
        try {
            httpClient.send(createdSubtask, HttpResponse.BodyHandlers.ofString());
            Epic epic2 = new Epic("Задача2", "Описание");
            HttpRequest request12 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/epic"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            httpClient.send(createdEpic, HttpResponse.BodyHandlers.ofString());
            httpClient.send(request12, HttpResponse.BodyHandlers.ofString());

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/epic/"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .DELETE()
                    .build();
            assertEquals(200, httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).statusCode(),"Неккоректный код запроса");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/epic/"))
                    .GET()
                    .build();
            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response2.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(new JsonArray(), jsonArray,"Неккоректное удаление задач");

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask/"))
                    .GET()
                    .build();
            HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());
            JsonElement element2 = JsonParser.parseString(response2.body());
            JsonArray jsonArray2 = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(new JsonArray(), jsonArray,"Неккоректное удаление задач");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void getAllSubtasks() {
        try {
            Subtask subtask2 = new Subtask("Задача2", "Описание", 10, LocalDateTime.of(2023, 5, 15, 19, 30), e);
            HttpRequest request12 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            httpClient.send(createdSubtask, HttpResponse.BodyHandlers.ofString());
            httpClient.send(request12, HttpResponse.BodyHandlers.ofString());

            Subtask t = gson.fromJson(subtask, Subtask.class);
            t.setID(0);
            subtask2.setID(1);

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask/"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response.statusCode(), "Неккоректный код запроса");
            assertEquals(gson.fromJson(jsonArray.get(0), Subtask.class), t, "Задачи не равны");
            assertEquals(gson.fromJson(jsonArray.get(1), Subtask.class), subtask2, "Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void createAndGetSubtaskById() {
        try {
            int s = httpClient.send(createdSubtask, HttpResponse.BodyHandlers.ofString()).statusCode();
            assertEquals(201, s, "Неккоректный код запроса");

            Subtask t = gson.fromJson(subtask, Subtask.class);
            t.setID(0);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask/"+ "?id=0"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();

            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response2.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(gson.fromJson(jsonArray.get(0), Subtask.class), t,"Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void updateSubtask() {
        Subtask subtask2 = new Subtask("Задача2", "Описание", 10, LocalDateTime.of(2023, 5, 15, 19, 30), e);
        try {
            httpClient.send(createdSubtask, HttpResponse.BodyHandlers.ofString());
            JsonArray jsonArray1 = new JsonArray();
            jsonArray1.add(gson.toJson(subtask2));
            jsonArray1.add(gson.toJson("NEW"));
            HttpRequest request1 = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create(url + "/subtask/"+ "?id=0"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(jsonArray1)))
                    .header("Content-Type", "application/json")
                    .build();
            request1.bodyPublisher();
            HttpResponse response2 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response2.statusCode(), "Неккоректный код запроса");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask/"+ "?id=0"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response.body());
            JsonArray jsonArray = element.getAsJsonArray();
            subtask2.setID(0);
            assertEquals(gson.fromJson(jsonArray.get(0), Subtask.class), subtask2,"Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void deleteSubtask() {
        try {
            httpClient.send(createdSubtask, HttpResponse.BodyHandlers.ofString()).statusCode();

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask/"+ "?id=0"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .DELETE()
                    .build();
            httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).statusCode();
            assertEquals(200, httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).statusCode(),"Неккоректный код запроса");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask/"+ "?id=0"))
                    .GET()
                    .build();
            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response2.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(new JsonArray(), jsonArray,"Неккоректное удаление задачи");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void deleteAllSubtasks() {
        try {
            Subtask subtask2 = new Subtask("Задача2", "Описание", 10, LocalDateTime.of(2023, 5, 15, 19, 30), e);
            HttpRequest request12 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            httpClient.send(createdSubtask, HttpResponse.BodyHandlers.ofString());
            httpClient.send(request12, HttpResponse.BodyHandlers.ofString());

            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask/"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .DELETE()
                    .build();
            assertEquals(200, httpClient.send(request1, HttpResponse.BodyHandlers.ofString()).statusCode(),"Неккоректный код запроса");

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask/"))
                    .GET()
                    .build();
            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response2.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response2.statusCode(), "Неккоректный код запроса");
            assertEquals(new JsonArray(), jsonArray,"Неккоректное удаление задач");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void subtasksOfEpic() {
    }

    @Test
    void allTasks() {
        try {
            Subtask subtask2 = new Subtask("Задача2", "Описание", 10, LocalDateTime.of(2024, 5, 15, 19, 30), e);
            HttpRequest request12 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            httpClient.send(createdTask, HttpResponse.BodyHandlers.ofString());
            httpClient.send(createdEpic, HttpResponse.BodyHandlers.ofString());
            httpClient.send(createdSubtask, HttpResponse.BodyHandlers.ofString());
            httpClient.send(request12, HttpResponse.BodyHandlers.ofString());

            Task t = gson.fromJson(subtask, Subtask.class);
            Subtask s = gson.fromJson(subtask, Subtask.class);
            t.setID(0);
            s.setID(2);
            subtask2.setID(3);

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response.statusCode(), "Неккоректный код запроса");
            assertEquals(gson.fromJson(jsonArray.get(0), Task.class), t, "Задачи не равны");
            assertEquals(gson.fromJson(jsonArray.get(1), Subtask.class), s, "Задачи не равны");
            assertEquals(gson.fromJson(jsonArray.get(1), Subtask.class), s, "Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void history() {
        try {
            httpClient.send(createdTask, HttpResponse.BodyHandlers.ofString());
            httpClient.send(createdEpic, HttpResponse.BodyHandlers.ofString());
            httpClient.send(createdSubtask, HttpResponse.BodyHandlers.ofString());

            Task t = gson.fromJson(subtask, Subtask.class);
            Subtask s = gson.fromJson(subtask, Subtask.class);
            t.setID(0);
            e.setID(1);
            s.setID(2);

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/task"+ "?id=0"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/epic"+ "?id=1"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();
            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/subtask"+ "?id=2"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();

            httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            httpClient.send(request3, HttpResponse.BodyHandlers.ofString());
            httpClient.send(request4, HttpResponse.BodyHandlers.ofString());

            HttpRequest request5 = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());
            JsonElement element = JsonParser.parseString(response.body());
            JsonArray jsonArray = element.getAsJsonArray();
            assertEquals(200, response.statusCode(), "Неккоректный код запроса");
            assertEquals(gson.fromJson(jsonArray.get(0), Task.class), t, "Задачи не равны");
            assertEquals(gson.fromJson(jsonArray.get(1), Subtask.class), s, "Задачи не равны");
            assertEquals(gson.fromJson(jsonArray.get(1), Subtask.class), s, "Задачи не равны");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}