package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskList;


    public Epic(String name, String description) {
        super(name, description);
        subtaskList = new ArrayList<>();
    }

    public Epic(int ID, String description, String name,  Status status, int duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(ID, description, name, status, duration, startTime, endTime);
        subtaskList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Subtask> subTasksOfEpic) {
        this.subtaskList = subTasksOfEpic;
        checkStatus();
    }

    public void addSubtask(Subtask newSubtask) {
        subtaskList.add(newSubtask);
        checkStatus();
        calculateTime();
    }

    public void removeSubtask(Subtask subtask) {
        subtaskList.remove(subtask);
        checkStatus();
        calculateTime();
    }

    public void checkStatus() {
        if (subtaskList.isEmpty()) {
            status = Status.NEW;
        } else if (subtaskList.size() != 1) {
            for (Subtask subtask : subtaskList) {
                Status compare = subtask.getStatus();
                if ((compare.equals(Status.NEW)) && status.equals(Status.NEW)) {
                    status = Status.NEW;
                } else if (compare.equals(Status.DONE) && (status.equals(Status.DONE) || (status.equals(Status.IN_PROGRESS)))) {
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

    public void calculateTime() {
        if (!subtaskList.isEmpty()) {
            List<Subtask> list = getSubtaskList();
            LocalDateTime start = null;
            LocalDateTime end = null;

            for (Subtask subtask : getSubtaskList()) {
                if (start == null) {
                    start = subtask.getStartTime();
                    end = subtask.getEndTime();
                    duration = subtask.getDuration();
                } else if (subtask.getStartTime().isBefore(start)) {
                    start = subtask.getStartTime();
                } else if (subtask.getEndTime().isAfter(end)) {
                    end = subtask.getEndTime();
                }
            }
            duration = (int) Duration.between(start, end).getSeconds() / 60;
            startTime = start;
            endTime = end;
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