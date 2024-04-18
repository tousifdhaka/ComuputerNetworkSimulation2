public class Message extends Event {
    private final int srcAddress;
    private final int destAddress;
    private final String message;

    public Message(int srcAddress, int destAddress, String message) {
        super();
        this.srcAddress = srcAddress;
        this.destAddress = destAddress;
        this.message = message;
    }

    @Override
    public void setInsertionTime(int currentTime) {
        // Insertion time not needed for Message events
    }
    @Override
    public int getCurrentTime() {
        return super.getCurrentTime();
    }
    @Override
    public void cancel() {
        // Cancellation not applicable for Message events
    }

    @Override
    public void handle() {
        // Handling not applicable for Message events
    }

    public String getMessage() {
        return message;
    }

    public int getSrcAddress() {
        return srcAddress;
    }

    public int getDestAddress() {
        return destAddress;
    }

    public void setNextHop(Host destination, int distance) {
        // To simplify, assume 1 distance = 1 simulation time
        int arrivalTime = getCurrentTime() + distance;
        setInsertionTime(arrivalTime);
        setArrivalTime(arrivalTime);
    }
}
