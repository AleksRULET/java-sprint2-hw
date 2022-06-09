package tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicID;

    public Subtask(String name, String description, int duration, LocalDateTime startTime, Epic epic) {
        super(name, description, duration, startTime);
        this.epicID = epic.getID();
    }

    public Subtask(int ID, String description, String name,  Status status, int duration, LocalDateTime startTime, LocalDateTime endTime, int epicID) {
        super(ID, description, name, status, duration, startTime, endTime);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public String tasksToString() {
        return getID() + "," + getClass().getSimpleName().toUpperCase() + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + getDuration()  + "," + getStartTime()  + "," + getEndTime() + "," + getEpicID();
    }

    @Override
    public String toString() {
        String result = "Subtask{" +
                "name='" + name + '\'' +
                ", ID='" + ID + '\'';
        if(description != null) {
            result = result + ", description.length=" + description.length();
        } else {
            result = result + ", description=null";
        }
        return result + ", status=" + status + '\'' + ", epicID=" + epicID + '}';
    }
}
