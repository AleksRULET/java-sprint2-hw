package tasks;

public class Subtask extends Task {
    private int epicID;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epicID = epic.getID();
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        String result = "Subtask{" +
                "name='" + name + '\'' +
                ", ID='" + ID + '\'';
/*
        if(description != null) {
            result = result + ", description.length=" + description.length();
        } else {
            result = result + ", description=null";
        }
*/
        return result + ", status=" + status + '\'' + ", epicID=" + epicID + '}';
    }
}
