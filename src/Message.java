public class Message extends Event {
    private int srcAddress;
    private int destAddress;
    private String message;
    private Host nextHop;
    private int distance;
    private int sentTime;

    public Message(int srcAddress, int destAddress, String message) {
        this.srcAddress = srcAddress;
        this.destAddress = destAddress;
        this.message = message;
    }

    @Override
    public void setInsertionTime(int currentTime) {
        this.insertionTime = currentTime;
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

    public int getDistance() {
        return this.distance;
    }

    public int getSentTime() {
        return this.sentTime;
    }

    public void setSentTime(int sentTime) {
        this.sentTime = sentTime;
    }

    public void setNextHop(Host destination, int distance) {
        this.nextHop = destination;
        this.distance = distance;
    }
}
