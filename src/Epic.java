import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{
    private ArrayList<Integer> subTaskID;

    public Epic(String name, String description) {
        super(name, description);
        subTaskID = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskID() {
        return subTaskID;
    }

    public void addSubTaskID(int subID) {
        subTaskID.add(subID);
    }


    public void checkStatus(HashMap<Integer, Subtask> subtasks) {
        status = "DONE";
        if (subTaskID.isEmpty()) {
            status = "NEW";
        } else {
            for (Integer subtaskID : subTaskID) {
                String compare = subtasks.get(subtaskID).getStatus();
                if ((compare.equals("NEW")) && status.equals("NEW")) {
                    status = "NEW";
                } else if (compare.equals("DONE") && status.equals("DONE")) {
                    status = "DONE";
                } else {
                    status = "IN_PROGRESS";
                    break;
                }
            }
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

        return result + ", status=" + status + '\'' + ", subTaskID=" + subTaskID + '}';
    }
}