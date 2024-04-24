public class SimpleHost extends Host {

    private final int myAddress;
    private int pingDestAddress;
    private int pingInterval;
    private int pingDuration;
    private int pingTimerId = -1;
    private int durationTimerId = -1; // Declare durationTimerId as a field

    public SimpleHost(int myAddress) {
        super();
        this.myAddress = myAddress;
    }

    public void setPingParameters(int destAddress, int pingInterval, int pingDuration) {
        this.pingDestAddress = destAddress;
        this.pingInterval = pingInterval;
        this.pingDuration = pingDuration;
    }


    public void sendPings(int destAddr, int interval, int duration) {
        // Start sending pings when simulation time is 0
        pingDestAddress = destAddr; // Set ping destination address
        pingInterval = interval; // Set ping interval
        pingDuration = duration; // Set ping duration

        // Schedule the first ping request
        pingTimerId = newTimer(pingInterval);

        // Schedule duration timer
        scheduleDurationTimer();
    }

    private void scheduleDurationTimer() {
        // Schedule timer for ping duration
        durationTimerId = newTimer(pingDuration); // Assign to durationTimerId field
    }

    @Override
    protected void timerExpired(int eventId) {
        if (eventId == pingTimerId) {
            // Send ping request
            sendPingRequest(pingDestAddress);

            // Schedule timer for next ping
            pingTimerId = newTimer(pingInterval);
        } else if (eventId == durationTimerId) {
            // Duration timer expired, stop sending pings
            System.out.println("[" + getCurrentTime() + "ts] Host " + myAddress + ": Stopped sending pings");
            // Cancel the interval timer
            cancelTimer(pingTimerId);
        }
    }
    @Override
    protected void receive(Message msg) {
        String messageType = msg.getMessage();
        int currentTime = getCurrentTime(); // Get the current simulation time
        switch (messageType) {
            case "PING_REQUEST":
                // Respond with a ping response
                sendPingResponse(msg.getSrcAddress());
                break;
            case "PING_RESPONSE":
                // Compute RTT (Round Trip Time)
                int rtt = currentTime - msg.getInsertionTime(); // Calculate RTT
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
