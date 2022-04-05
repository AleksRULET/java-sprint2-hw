package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskList;

    public Epic(String name, String description) {
        super(name, description);
        subtaskList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Subtask> subTasksOfEpic) {
        this.subtaskList = subTasksOfEpic;
    }

    public void addSubtask(Subtask newSubtask) {
        subtaskList.add(newSubtask);
    }

    public void checkStatus() {
        if (subtaskList.isEmpty()) {
            status = Status.NEW;
        } else if (subtaskList.size() != 1) {
            for (Subtask subtask : subtaskList) {
                Status compare = subtask.getStatus();
                if ((compare.equals(Status.NEW)) && status.equals(Status.NEW)) {
                    status = Status.NEW;
                } else if (compare.equals(Status.DONE) && status.equals(Status.DONE)) {
                    status = Status.DONE;
                } else {
                    status = Status.IN_PROGRESS;
                    break;
                }
            }
        } else {
            status = subtaskList.get(0).getStatus();
        }
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "name='" + name + '\'' +
                ", ID='" + ID + '\'';
        if(description != null) {
            result = result + ", description.length=" + description.length();
        } else {
            result = result + ", description=null";
        }
        return result + ", status=" + status + '\'' + ", subtaskList=" + subtaskList + '}';
    }
}