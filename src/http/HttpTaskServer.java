package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.HttpTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    HttpTaskManager taskManager;
    Gson gson;

    public HttpTaskServer() throws IOException {
        taskManager = (HttpTaskManager) Managers.getDefault();
        gson = new GsonBuilder().serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        server.createContext("/tasks/task", this::task);
        server.createContext("/tasks/epic", this::epic);
        server.createContext("/tasks/subtask", this::subtask);
        server.createContext("/tasks/subtask/epic", this::subtasksOfEpic);
        server.createContext("/tasks", this::allTasks);
        server.createContext("/tasks/history", this::history);
    }

    public void task(HttpExchange h) {
        try {
            String path = h.getRequestURI().getPath();
            switch (h.getRequestMethod()) {
                case "GET":
                    if (path.contains("id")) {
                        sendText(h, gson.toJson(taskManager.getTaskByID(Integer.parseInt(path.split("=")[2]))));
                    } else {
                        sendText(h, gson.toJson(taskManager.getAllTask()));
                    }
                    return;
                case "POST":
                    String text = readText(h);
                    if (!text.isEmpty()) {
                        Task task = gson.fromJson(text, Task.class);
                        if (taskManager.getAllTask().contains(task.getID())) {
                            taskManager.updateTask(task.getID(), task, task.getStatus().toString());
                        } else {
                            taskManager.createTask(task);
                        }
                        h.sendResponseHeaders(200, 0);
                        return;
                    }
                case "DELETE":
                    if (path.contains("id")) {
                       taskManager.deleteTaskByID(Integer.parseInt(path.split("=")[2]));
                    } else {
                        taskManager.deleteAllTask();
                    }
                    h.sendResponseHeaders(200, 0);
                    return;
                default:
                System.out.println("Неподдерживаемый метод: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
            } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }


    public void  epic(HttpExchange h) {
        try {
            String path = h.getRequestURI().getPath();
            switch (h.getRequestMethod()) {
                case "GET":
                    if (path.contains("id")) {
                        sendText(h, gson.toJson(taskManager.getEpicByID(Integer.parseInt(path.split("=")[2]))));
                    } else {
                        sendText(h, gson.toJson(taskManager.getAllEpics()));
                    }
                    return;
                case "POST":
                    String text = readText(h);
                    if (!text.isEmpty()) {
                        Epic epic = gson.fromJson(text, Epic.class);
                        if (taskManager.getAllEpics().contains(epic.getID())) {
                            taskManager.updateEpic(epic.getID(), epic);
                        } else {
                            taskManager.createEpic(epic);
                        }
                        h.sendResponseHeaders(200, 0);
                        return;
                    }
                case "DELETE":
                    if (path.contains("id")) {
                        taskManager.deleteEpicByID(Integer.parseInt(path.split("=")[2]));
                    } else {
                        taskManager.deleteAllEpic();
                    }
                    h.sendResponseHeaders(200, 0);
                    return;
                default:
                    System.out.println("Неподдерживаемый метод: " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    public void  subtask(HttpExchange h) {
        try {
            String path = h.getRequestURI().getPath();
            switch (h.getRequestMethod()) {
                case "GET":
                    if (path.contains("id")) {
                        sendText(h, gson.toJson(taskManager.getSubtaskByID(Integer.parseInt(path.split("=")[2]))));
                    } else {
                        sendText(h, gson.toJson(taskManager.getAllSubtasks()));
                    }
                    return;
                case "POST":
                    String text = readText(h);
                    if (!text.isEmpty()) {
                        Subtask subtask = gson.fromJson(text, Subtask.class);
                        if (taskManager.getAllSubtasks().contains(subtask.getID())) {
                            taskManager.updateSubtask(subtask.getID(), subtask, subtask.getStatus().toString());
                        } else {
                            taskManager.createSubtask(subtask);
                        }
                        h.sendResponseHeaders(200, 0);
                        return;
                    }
                case "DELETE":
                    if (path.contains("id")) {
                        taskManager.deleteSubtaskByID(Integer.parseInt(path.split("=")[2]));
                    } else {
                        taskManager.deleteAllSubtask();
                    }
                    h.sendResponseHeaders(200, 0);
                    return;
                default:
                    System.out.println("Неподдерживаемый метод: " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    public void subtasksOfEpic(HttpExchange h) {
        try {
            if ("GET".equals(h.getRequestMethod())) {
                Integer key = Integer.parseInt(h.getRequestURI().getPath().split("=")[2]);
                if (key == null) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                sendText(h, gson.toJson(taskManager.getSubtasksOfEpic(key)));
            } else {
                System.out.println("/tasks/subtask/epic ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    public void  allTasks(HttpExchange h) {
        try {
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, gson.toJson(taskManager.getPrioritizedTasks()));
            } else {
                System.out.println("/tasks/subtask/epic ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    public void history(HttpExchange h) {
        try {
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, gson.toJson(taskManager.history()));
            } else {
                System.out.println("/tasks/subtask/epic ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }





    public static void main(String[] args) throws IOException {

    }

    public void start() {
        server.start();
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    public void stop () {
        server.stop(1);
    }
        /*servers.KVServer (8078) <--> {[KVClient <--> HttpTaskManager] <--> HttpTaskServer} (8080)*/
}

