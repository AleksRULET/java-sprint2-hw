package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, customNode> place = new HashMap<>();
    private customNode head = null;
    private customNode tail= null;


    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    private void linkLast(Task task) {
        if (task != null) {


            customNode newHead = new customNode(task);
            if (head != null) {
                customNode oldHead = head;
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
            customNode nodeToDelete = place.get(id);
            customNode prev = nodeToDelete.getPrev();
            customNode next = nodeToDelete.getNext();

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
            customNode first = head;
            while (first != null) {
                historyList.add(first.getTask());
                first = first.getNext();
            }
        }
        return historyList;
    }

    class customNode {
        private Task task;
        private customNode next;
        private customNode prev;

        public customNode(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }

        public customNode(Task task, customNode next, customNode prev) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        public boolean equals(customNode node) {
            return task.equals(node.task)
                    && next.equals(node.next)
                    && prev.equals(node.prev);
        }

        public int hashCode() {
            int result = task.hashCode();
            result = 31 * result + next.hashCode();
            result = 31 * result + prev.hashCode();
            return result;
        }

        public Task getTask() {
            return task;
        }

        public customNode getNext() {
            return next;
        }

        public void setNext(customNode next) {
            this.next = next;
        }

        public customNode getPrev() {
            return prev;
        }

        public void setPrev(customNode prev) {
            this.prev = prev;
        }
    }
}


