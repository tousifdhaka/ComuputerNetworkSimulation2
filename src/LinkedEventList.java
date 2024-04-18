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

    private Node head; // Head node of the linked list

    public LinkedEventList() {
        this.head = null;
    }

    /**
     * Removes and returns the Event at the front of the list.
     *
     * @return the Event at the front of the list, or null if the list is empty
     */
    @Override
    public Event removeFirst() {
        if (head == null) {
            return null; // Empty list
        }
        Node removedNode = head;
        head = head.next;
        if (head != null) {
            head.prev = null;
        }
        return removedNode.event;
    }

    /**
     * Removes the specified Event from the list, if it exists.
     *
     * @param e the Event to remove from the list
     * @return true if the Event was removed, false otherwise
     */
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
                return true;
            }
            current = current.next;
        }
        return false; // Event not found
    }

    /**
     * Inserts an Event into the list, maintaining the list sorted by arrival time.
     *
     * @param e the Event to insert into the list
     */
    @Override
    public void insert(Event e) {
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

    /**
     * Returns the number of Events in the list.
     *
     * @return the number of Events in the list
     */
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

    /**
     * Returns the total number of Events the list can store before having to resize it.
     *
     * @return the capacity of the list
     */
    @Override
    public int capacity() {
        return size();
    }

    /**
     * Returns the current simulation time (arrival time of the last Event).
     *
     * @return the current simulation time
     */
    @Override
    public int getSimulationTime() {
        if (head == null) {
            return 0; // No events, simulation time is 0
        }
        return head.event.getArrivalTime();
    }
}
