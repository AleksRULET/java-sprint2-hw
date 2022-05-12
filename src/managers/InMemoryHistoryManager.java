package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    HashMap<Integer, Node> places;
    private Node head;
    private Node tail;

    public  InMemoryHistoryManager() {
        this.places = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    @Override
    public List<Task> getHistory() {
            return getTasks();
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    private void linkLast(Task task) {
        if (task != null) {
            if (places.containsKey(task.getID())) {
                remove(task.getID());
            }
            Node prevTail = tail;
            Node lastNode = new Node(task, null, tail);
            if (prevTail != null) {
                prevTail.setNext(lastNode);
            } else {
                head = lastNode;
            }
            tail = lastNode;
            places.put(task.getID(), lastNode);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(places.get(id));
    }

    private void removeNode (Node node) {
        if (node != null) {
            Node prev = node.getPrev();
            Node next = node.getNext();
            if (prev != null) {
                prev.setNext(next);
            } else {
                head = next;
                head.setPrev(null);
            }
            if (next != null) {
                next.setPrev(prev);
            } else {
                tail = prev;
                tail.setNext(null);
            }
            places.remove(node);
        }
    }

    private List<Task> getTasks() {
        List <Task> historyList = new ArrayList<>();
        if (head != null) {
            Node first = head;
            while (first != null) {
                historyList.add(first.getTask());
                first = first.getNext();
            }
        }
        return historyList;
    }

    public String toString() {
        String result = "";
        for (Task t: getHistory()) {
            result = result + t.getID() + ",";
        }
        StringBuilder sb = new StringBuilder(result);
        if (result.length() != 0) {
            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }

    public List<Integer> fromString(String value) {
        ArrayList<Integer> list = new ArrayList<>();
        if (value != null) {
            Scanner scanner = new Scanner(value);
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                String data = scanner.next();
                list.add(Integer.parseInt(data));
            }
            return list;
        } return null;
    }

    class Node {
        private final Task task;
        private Node next;
        private Node prev;

        public Node(Task task, Node next, Node prev) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        public Task getTask() {
            return task;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }
    }
}


