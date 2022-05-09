import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Managers;

public class Main {
    public static void main(String[] args) {
        //TaskManager taskManager = Managers.getDefault();
      /*
        //Тест функционала второго спринта//
        Task study = new Task("Учёба", "Описание");
        taskManager.createTask(study);

        Task workout = new Task("Тренировка", "Описание");
        taskManager.createTask(workout);

        Epic relocation = new Epic("Переезд", "Описание");
        taskManager.createEpic(relocation);

        Subtask getAVisa = new Subtask("Получить  визу", "Описание", relocation);
        taskManager.createSubtask(getAVisa);

        Subtask accumulateFunds = new Subtask("Накопить деньги", "Описание",  relocation);
        taskManager.createSubtask(accumulateFunds);

        Epic survival = new Epic("Выживание", "Описание");
        taskManager.createEpic(survival);

        Subtask buyBuckwheat = new Subtask("Купить гречку", "Описание", survival);
        taskManager.createSubtask(buyBuckwheat);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        Task newStudy = new Task("Учёба", "Спринт 2");
        taskManager.updateTask(study.getID(), newStudy, "IN_PROGRESS");

        Task newWorkout = new Task("Тренировка", "Силовые упражнения");
        taskManager.updateTask(workout.getID(), newWorkout, "DONE");

        Epic newRelocation = new Epic("Переезд", "За границу");
        taskManager.updateEpic(relocation.getID(), newRelocation);

        Subtask newGetAVisa = new Subtask("Получить  визу", "Подготовить документы, прийти в посольство", newRelocation);
        taskManager.updateSubtask(getAVisa.getID(), newGetAVisa, "NEW");

        Subtask newAccumulateFunds = new Subtask("Накопить деньги", "Окладывать с зарплаты", newRelocation);
        taskManager.updateSubtask(accumulateFunds.getID(), newAccumulateFunds, "IN_PROGRESS");

        Epic newSurvival = new Epic("Выживание", "Прожить нелёгких условиях");
        taskManager.updateEpic(survival.getID(), newSurvival);

        Subtask newBuyBuckwheat = new Subtask("Купить гречку", "Найти, в каком магазине осталась гречка", newSurvival);
        taskManager.updateSubtask(buyBuckwheat.getID(), newBuyBuckwheat, "DONE");

        System.out.println("\n"+taskManager.getAllTask());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.deleteTaskByID(1);
        taskManager.deleteEpicByID(5);

        System.out.println("\n"+taskManager.getAllTask());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        //Тест функционала третьего спринта//
        taskManager.getTaskByID(0);
        taskManager.getSubtaskByID(4);
        taskManager.getSubtaskByID(3);
        taskManager.getSubtaskByID(4);
        taskManager.getEpicByID(2);
      //Вызвали 5 задач по ID//
        System.out.println("\n" + "\n" + taskManager.history());

        taskManager.getEpicByID(2);
        taskManager.getSubtaskByID(4);
        taskManager.getTaskByID(0);
        taskManager.getTaskByID(0);
        taskManager.getSubtaskByID(3);
      //Вызвали 10 задач по ID//
        System.out.println("\n" + taskManager.history());

        taskManager.getTaskByID(0);
        taskManager.getSubtaskByID(3);
      //Вызвали ещё и проверили что отображаеться только 10 результатов в истории//
        System.out.println("\n" + taskManager.history());


        //Тест функционала четвёртого спринта!!!
        Task study = new Task("Учёба", "Описание");
        taskManager.createTask(study);

        Task workout = new Task("Тренировка", "Описание");
        taskManager.createTask(workout);

        Epic relocation = new Epic("Переезд", "Описание");
        taskManager.createEpic(relocation);

        Subtask getAVisa = new Subtask("Получить  визу", "Описание", relocation);
        taskManager.createSubtask(getAVisa);

        Subtask accumulateFunds = new Subtask("Накопить деньги", "Описание",  relocation);
        taskManager.createSubtask(accumulateFunds);

        Subtask prepareDocuments = new Subtask("Подготовить документы", "Описание",  relocation);
        taskManager.createSubtask(prepareDocuments);

        Epic survival = new Epic("Выживание", "Описание");
        taskManager.createEpic(survival);
        //Создали 7 задач//

        taskManager.getTaskByID(0);
        taskManager.getTaskByID(1);
        taskManager.getEpicByID(2);
        taskManager.getSubtaskByID(3);
        taskManager.getSubtaskByID(4);
        taskManager.getSubtaskByID(5);
        taskManager.getEpicByID(6);
        //Вызвали 7 задач по ID//
        System.out.println("\n" + taskManager.history());

        taskManager.getTaskByID(0);
        taskManager.getEpicByID(2);
        taskManager.getSubtaskByID(5);
        //Вызвали задачи повторно//
        System.out.println("\n" + taskManager.history());

        taskManager.deleteTaskByID(0);
        //Удалили задачу//
        System.out.println("\n" + taskManager.history());

        taskManager.deleteEpicByID(2);
        //Удалили эпик с тремя подзадачами//
        System.out.println("\n" + taskManager.history());
        */
    }
}
