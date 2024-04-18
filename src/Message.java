public class Message extends Event {
    private int srcAddress;
    private int destAddress;
    private String message;
    private Host nextHop;
    private int distance;

    public Message(int srcAddress, int destAddress, String message) {
        this.srcAddress = srcAddress;
        this.destAddress = destAddress;
        this.message = message;
    }

    // Implement abstract methods of Event class

    @Override
    public void setInsertionTime(int currentTime) {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void handle() {
        // No action needed for handling a message

    }

    // Implement additional methods

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

    public int getDistance() {
        return this.distance;
    }

    public void setNextHop(Host destination, int distance) {
        this.nextHop = destination;
        this.distance = distance;
    }
}
