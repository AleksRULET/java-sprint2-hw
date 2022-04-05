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
            Node newHead = new Node(task);
            if (head != null) {
                Node oldHead = head;
                oldHead.setPrev(newHead);
                places.put(oldHead.getTask().getID(), oldHead);
                newHead.setNext(oldHead);
                tail = oldHead;
            }
            if (tail == null) {
                tail = newHead;
            }
            head = newHead;
            places.put(newHead.getTask().getID(), newHead);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    private void removeNode (int id) {
        if (places.get(id) != null) {
            Node nodeToDelete = places.get(id);
            Node prev = nodeToDelete.getPrev();
            Node next = nodeToDelete.getNext();
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
            places.remove(id);
        }
    }

    private List getTasks() {
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

    class Node {
        private final Task task;
        private Node next;
        private Node prev;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }

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


