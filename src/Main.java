public class Main {
    public static void main(String[] args) {
        Manager manager =new Manager();

        Task study = new Task("Учёба", "Описание");
        manager.createTask(study);

        Task workout = new Task("Тренировка", "Описание");
        manager.createTask(workout);

        Epic relocation = new Epic("Переезд", "Описание");
        manager.createEpic(relocation);

        Subtask getAVisa = new Subtask("Получить  визу", "Описание", relocation);
        manager.createSubtask(getAVisa);

        Subtask accumulateFunds = new Subtask("Накопить деньги", "Описание",  relocation);
        manager.createSubtask(accumulateFunds);

        Epic survival = new Epic("Выживание", "Описание");
        manager.createEpic(survival);

        Subtask buyBuckwheat = new Subtask("Купить гречку", "Описание", survival);
        manager.createSubtask(buyBuckwheat);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getSubtasksOfEpic(2));

        Task newStudy = new Task("Учёба", "Спринт 2");
        manager.updateTask(study, newStudy, "IN_PROGRESS");

        Task newWorkout = new Task("Тренировка", "Силовые упражнения");
        manager.updateTask(workout, newWorkout, "DONE");

        Epic newRelocation = new Epic("Переезд", "За границу");
        manager.updateEpic(relocation, newRelocation);

        Subtask newGetAVisa = new Subtask("Получить  визу", "Подготовить документы, прийти в посольство", newRelocation);
        manager.updateSubtask(getAVisa, newGetAVisa, "NEW");

        Subtask newAccumulateFunds = new Subtask("Накопить деньги", "Окладывать с зарплаты", newRelocation);
        manager.updateSubtask(accumulateFunds, newAccumulateFunds, "IN_PROGRESS");

        Epic newSurvival = new Epic("Выживание", "Прожить нелёгких условиях");
        manager.updateEpic(survival, newSurvival);

        Subtask newBuyBuckwheat = new Subtask("Купить гречку", "Найти, в каком магазине осталась гречка", newSurvival);
        manager.updateSubtask(buyBuckwheat, newBuyBuckwheat, "DONE");

        System.out.println("\n"+manager.getAllTask());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        manager.deleteTask(1);
        manager.deleteEpic(5);

        System.out.println("\n"+manager.getAllTask());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

    }
}
