public class SimpleHost extends Host {

    private final int myAddress;
    private int pingDestAddress;
    private int pingInterval;
    private int pingDuration;
    private int pingTimerId = -1;

    public SimpleHost(int myAddress) {
        super();
        this.myAddress = myAddress;
    }

    public void setPingParameters(int destAddress, int pingInterval, int pingDuration) {
        this.pingDestAddress = destAddress;
        this.pingInterval = pingInterval;
        this.pingDuration = pingDuration;
    }

    public void sendPings() {
        // Start sending pings when simulation time is 0
        if (getCurrentTime() == 0) {
            pingTimerId = newTimer(pingInterval);
        }
    }

    @Override
    protected void timerExpired(int eventId) {
        if (eventId == pingTimerId) {
            // Send ping request
            sendPingRequest(pingDestAddress);

            // Schedule timer for next ping
            int nextPingTime = getCurrentTime() + pingInterval;
            if (nextPingTime <= getCurrentTime() + pingDuration) {
                pingTimerId = newTimer(pingInterval);
            } else {
                System.out.println("[" + getCurrentTime() + "ts] Host " + myAddress + ": Stopped sending pings");
            }
        }
    }

    @Override
    protected void receive(Message msg) {
        String messageType = msg.getMessage();
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

    public void sendPingRequest(int destAddress) {
        // Create and send ping request message
        Message pingRequest = new Message(myAddress, destAddress, "PING_REQUEST");
        sendToNeighbor(pingRequest);
        System.out.println("[" + getCurrentTime() + "ts] Host " + myAddress + ": Sent ping to host " + destAddress);
    }

    private void sendPingResponse(int destAddress) {
        // Create and send ping response message
        Message pingResponse = new Message(myAddress, destAddress, "PING_RESPONSE");
        sendToNeighbor(pingResponse);
    }

    @Override
    protected void timerCancelled(int eventId) {
        // Implement timer cancellation logic here if needed
    }
}
