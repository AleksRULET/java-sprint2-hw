package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int ID;
    protected Status status;
    protected int duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        duration = 0;
        startTime = null;
        endTime = null;
    }

    public Task(String name, String description, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = calculateEndTime(duration, startTime);
    }

    public Task(int ID, String description, String name,  Status status, int duration, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String tasksToString() {
        return getID() + "," + getClass().getSimpleName().toUpperCase() + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + getDuration()  + "," + getStartTime()  + "," + getEndTime();
    }

    private LocalDateTime calculateEndTime(int duration, LocalDateTime startTime) {
        if (startTime == null || duration==0) {
            return null;
        }
        return startTime.plusMinutes(duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status) &&
                this.ID == task.ID &&
                this.duration == task.duration &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, name, description, status, duration, startTime, endTime);
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "name='" + name + '\'' +
                ", ID='" + ID + '\'';
        if(description != null) {
            result = result + ", description.length=" + description.length();
        } else {
            result = result + ", description=null";
        }

        return result + ", status=" + status + ", startTime=" + startTime + ", endTime=" + endTime+ ", duration=" + duration + '}';
    }
}

