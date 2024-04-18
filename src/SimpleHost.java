import java.util.Map;
import java.util.HashMap;

public class SimpleHost extends Host {

    private final int myAddress;
    private int pingDestAddress;
    private int pingInterval;
    private int pingDuration;
    private int pingTimerId = -1;
    private final Map<Integer, Host> neighbors;

    public SimpleHost(int myAddress, FutureEventList fel) {
        super();
        this.myAddress = myAddress;
        this.neighbors = new HashMap<>();
        setHostParameters(myAddress, fel); // Set host parameters
    }

    // Method to retrieve neighbors
    protected Map<Integer, Host> getNeighbors() {
        return neighbors;
    }

    // Method to handle expired timers
    @Override
    protected void timerExpired(int eventId) {
        // Timer expired for sending pings
        if (eventId == pingTimerId) {
            // Send ping request
            sendPingRequest();
            // Reschedule timer for next ping
            int nextPingTime = getCurrentTime() + pingInterval;
            if (nextPingTime <= getCurrentTime() + pingDuration) {
                pingTimerId = newTimer(pingInterval);
            } else {
                System.out.println("[" + getCurrentTime() + "ts] Host " + myAddress + ": Stopped sending pings");
            }
        }
    }

    // Method to handle cancelled timers
    @Override
    protected void timerCancelled(int eventId) {
        // Not needed for this implementation
    }

    // Method to handle received messages
    @Override
    protected void receive(Message msg) {
        // Determine message type
        String messageType = msg.getMessage();

        // Handle different types of messages
        switch (messageType) {
            case "PING_REQUEST":
                // Respond with a ping response
                sendPingResponse(msg.getSrcAddress());
                break;
            case "PING_RESPONSE":
                // Compute RTT (Round Trip Time)
                int currentTime = getCurrentTime();
                int rtt = currentTime - msg.getInsertionTime();
                System.out.println("[" + currentTime + "ts] Host " + myAddress + ": Ping response from host " + msg.getSrcAddress() + " (RTT = " + rtt + "ts)");
                break;
            default:
                // Unknown message type
                throw new EventException("Unknown message type received: " + messageType);
        }
    }

    // Method to set host parameters
    @Override
    public void setHostParameters(int address, FutureEventList fel) {
        super.setHostParameters(address, fel);
        // Start sending pings when simulation time is 0
        if (address == myAddress && getCurrentTime() == 0) {
            // Schedule timer to start sending pings
            pingTimerId = newTimer(pingInterval);
        }
    }

    // Method to set ping parameters
    public void setPingParameters(int destAddress, int pingInterval, int pingDuration) {
        this.pingDestAddress = destAddress;
        this.pingInterval = pingInterval;
        this.pingDuration = pingDuration;
    }

    // Method to send ping request
    public void sendPingRequest() {
        if (neighbors.containsKey(pingDestAddress)) {
            // Create and send ping request message
            Message pingRequest = new Message(myAddress, pingDestAddress, "PING_REQUEST");
            sendToNeighbor(pingRequest);
            System.out.println("[" + getCurrentTime() + "ts] Host " + myAddress + ": Sent ping to host " + pingDestAddress);
        } else {
            System.out.println("Destination address " + pingDestAddress + " is not a neighbor.");
        }
    }

    // Method to send ping response
    private void sendPingResponse(int destAddress) {
        // Create and send ping response message
        Message pingResponse = new Message(myAddress, destAddress, "PING_RESPONSE");
        sendToNeighbor(pingResponse);
    }

    // Method to add a neighbor
    public void addNeighbor(Host neighbor, int distance) {
        int neighborAddress = neighbor.getHostAddress();
        if (!neighbors.containsKey(neighborAddress)) {
            neighbors.put(neighborAddress, neighbor);
            neighbor.addNeighbor(this, distance); // Make connection bi-directional
        }
    }

    // Override sendToNeighbor method to handle unknown destinations
    @Override
    public void sendToNeighbor(Message msg) {
        int destAddress = msg.getDestAddress();
        Host neighbor = neighbors.get(destAddress);
        if (neighbor != null) {
            neighbor.receive(msg);
        } else {
            // Handle the situation where destination address is unknown
            System.out.println("Destination address " + destAddress + " is not a neighbor.");
        }
    }
}
