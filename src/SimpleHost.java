public class SimpleHost extends Host {

    private final int myAddress;
    private int pingDestAddress;
    private int pingInterval;
    private int pingDuration;
    private int pingTimerId = -1;
    private int durationTimerId = -1;
    private int sentTime; // Initialize sent time to -1

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
        pingDestAddress = destAddr;
        pingInterval = interval;
        pingDuration = duration;
        sentTime = getCurrentTime(); // Record current simulation time as sent time

        pingTimerId = newTimer(pingInterval);
        scheduleDurationTimer();
    }

    private void scheduleDurationTimer() {
        durationTimerId = newTimer(pingDuration);
    }

    @Override
    protected void timerExpired(int eventId) {
        if (eventId == pingTimerId) {
            sendPingRequest(pingDestAddress);
            pingTimerId = newTimer(pingInterval);
        } else if (eventId == durationTimerId) {
            System.out.println("[" + getCurrentTime() + "ts] Host " + myAddress + ": Stopped sending pings");
            cancelTimer(pingTimerId);
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
                int rtt = currentTime - sentTime; // Calculate RTT using stored sent time
                System.out.println("[" + currentTime + "ts] Host " + myAddress + ": Ping response from host " + msg.getSrcAddress() + " (RTT = " + rtt + "ts)");
                break;
            default:
                // Unknown message type
                throw new EventException("Unknown message type received: " + messageType);
        }
    }

    public void sendPingRequest(int destAddress) {
        // Create ping request message
        Message pingRequest = new Message(myAddress, destAddress, "PING_REQUEST");

        // Record current simulation time as send time
        sentTime = getCurrentTime();
        pingRequest.setInsertionTime(sentTime);

        // Send ping request message to neighbor
        sendToNeighbor(pingRequest);

        // Print log message
        System.out.println("[" + sentTime + "ts] Host " + myAddress + ": Sent ping to host " + destAddress);
    }


    private void sendPingResponse(int destAddress) {
        Message pingResponse = new Message(myAddress, destAddress, "PING_RESPONSE");
        sendToNeighbor(pingResponse);
    }

    @Override
    protected void timerCancelled(int eventId) {
        // Implement timer cancellation logic here if needed
    }
}
