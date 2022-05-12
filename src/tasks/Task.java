package tasks;

public class Task {
    protected String name;
    protected String description;
    protected int ID;
    protected Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int ID, String description, String name, Status status) {
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String tasksToString() {
        return getID() + "," + getClass().getSimpleName().toUpperCase() + "," + getName() + "," + getStatus() + ","
                + getDescription();
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

        return result + ", status=" + status + '}';
    }
}

