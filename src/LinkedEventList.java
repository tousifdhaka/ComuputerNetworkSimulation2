public class LinkedEventList implements FutureEventList {

    // Node class for the doubly-linked list
    private static class Node {
        Event event;
        Node prev;
        Node next;

        public Node(Event event) {
            this.event = event;
            this.prev = null;
            this.next = null;
        }
    }

    private int updatedTime;
    private Node head; // Head node of the linked list

    public LinkedEventList() {
        this.head = null;
        this.updatedTime = 0; // Initialize updatedTime to 0
    }

    @Override
    public Event removeFirst() {
        if (head == null) {
            throw new IllegalStateException("Cannot remove from an empty list");
        }
        Node removedNode = head;
        head = head.next;
        if (head != null) {
            head.prev = null;
        }

        // Update updatedTime to the arrival time of the removed event
        updatedTime = removedNode.event.getArrivalTime();
        return removedNode.event;
    }

    @Override
    public boolean remove(Event e) {
        Node current = head;
        while (current != null) {
            if (current.event.equals(e)) {
                if (current.prev != null) {
                    current.prev.next = current.next;
                } else {
                    head = current.next;
                }
                if (current.next != null) {
                    current.next.prev = current.prev;
                }
                updatedTime = current.event.getArrivalTime();

                return true;
            }
            current = current.next;
        }
        return false; // Event not found
    }

    @Override
    public void insert(Event e) {
        e.setInsertionTime(getSimulationTime()); // Set insertion time
        Node newNode = new Node(e);
        if (head == null || head.event.getArrivalTime() >= e.getArrivalTime()) {
            newNode.next = head;
            if (head != null) {
                head.prev = newNode;
            }
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null && current.next.event.getArrivalTime() < e.getArrivalTime()) {
                current = current.next;
            }
            newNode.next = current.next;
            newNode.prev = current;
            if (current.next != null) {
                current.next.prev = newNode;
            }
            current.next = newNode;
        }
    }

    @Override
    public int size() {
        int count = 0;
        Node current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    @Override
    public int capacity() {
        return size();
    }

    public int getSimulationTime() {
        return updatedTime;
    }
}
