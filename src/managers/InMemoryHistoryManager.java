package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node> place = new HashMap<>();
    private Node head = null;
    private Node tail= null;

    public InMemoryHistoryManager() {
    }

    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    private void linkLast(Task task) {
        if (task != null) {
            Node newHead = new Node(task);
            if (head != null) {
                Node oldHead = head;
                oldHead.setPrev(newHead);
                place.put(oldHead.getTask().getID(), oldHead);
                newHead.setNext(oldHead);
                tail = oldHead;
            }
            if (tail == null) {
                tail = newHead;
            }
            head = newHead;
            place.put(newHead.getTask().getID(), newHead);
        }
    }
/*
    if (task != null) {
        if (place.containsKey(task.getID())) {
            remove(task.getID());
        }
        Node prevTail = this.tail;
        Node lastNode = new Node(task, this.tail, null);
        if (prevTail != null) {
            prevTail.setNext(lastNode);
        }
        this.tail = lastNode;
        place.put(task.getID(), lastNode);
    }
   */
    public void removeNode (int id) {
        if (place.get(id) != null) {
            Node nodeToDelete = place.get(id);
            Node prev = nodeToDelete.getPrev();
            Node next = nodeToDelete.getNext();

            if (prev != null) {
                prev.setNext(next);
            } else {
                next.setPrev(null);
                head = next;
            }
            if (next != null) {
                next.setPrev(prev);
            } else {
                tail.setNext(null);
                tail = prev;
            }
            place.remove(id);
        }
    }

    @Override
    public void remove(int id) {
        removeNode (id);
    }

    private List getTasks() {
        List <Task> historyList = new ArrayList<>();
        if (head != null) {
            Node first = head;
            while (first != null) {
                historyList.add(first.getTask());
                first = first.getPrev();
            }
        }
        return historyList;
    }

    class Node {
        private Task task;
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


