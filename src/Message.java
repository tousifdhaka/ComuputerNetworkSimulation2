public class Message extends Event {
    private int srcAddress;
    private int destAddress;
    private String message;
    private Host nextHop;
    private int distance;
    private static final int MESSAGE_DURATION = 1; // Assuming all messages have a duration of 1 time unit

    public Message(int srcAddress, int destAddress, String message) {
        this.srcAddress = srcAddress;
        this.destAddress = destAddress;
        this.message = message;
    }

    @Override
    public void setInsertionTime(int currentTime) {
        this.insertionTime = currentTime;

        // Compute arrival time based on insertion time and message duration
        this.arrivalTime = currentTime + MESSAGE_DURATION;
    }

    @Override
    public void cancel() {
        // Implementation not needed for this class
    }

    @Override
    public void handle() {
        Host destinationHost = getNextHop();
        if (destinationHost != null) {
            destinationHost.receive(this);
        } else {
            System.err.println("Destination host not found for message: " + this);
        }
    }

    public String getMessage() {
        return this.message;
    }

    public int getSrcAddress() {
        return this.srcAddress;
    }

    public int getDestAddress() {
        return this.destAddress;
    }

    public Host getNextHop() {
        return this.nextHop;
    }

    public void setNextHop(Host destination, int distance) {
        this.nextHop = destination;
        this.distance = distance;
    }
}
