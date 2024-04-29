import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Set the filename to read
        String fileName = "simulation4.txt";

        // Read simulation.txt and bootstrap the simulation
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Initialize simulation components
            Map<Integer, SimpleHost> hosts = new HashMap<>();
            FutureEventList fel = new LinkedEventList();

            // Read the first line to get the first host address
            int firstHostAddress = Integer.parseInt(reader.readLine());

            // Create the first host
            SimpleHost firstHost = new SimpleHost(firstHostAddress);
            firstHost.setHostParameters(firstHostAddress, fel); // Set host parameters
            hosts.put(firstHostAddress, firstHost);

            // Read host connections
            String line;
            while ((line = reader.readLine()) != null && !line.equals("-1")) {
                String[] parts = line.split(" ");
                int neighborAddress = Integer.parseInt(parts[0]);
                int distance = Integer.parseInt(parts[1]);
                // Add neighbor connections for the first host
                SimpleHost neighborHost = hosts.getOrDefault(neighborAddress, new SimpleHost(neighborAddress));
                neighborHost.setHostParameters(neighborAddress, fel); // Set host parameters
                hosts.put(neighborAddress, neighborHost);
                firstHost.addNeighbor(neighborHost, distance);

                // Since connections are bi-directional, add first host as a neighbor for the read host
                neighborHost.addNeighbor(firstHost, distance);
            }

            // Read ping request information and schedule events
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                int senderAddress = Integer.parseInt(parts[0]);
                int receiverAddress = Integer.parseInt(parts[1]);
                int pingInterval = Integer.parseInt(parts[2]);
                int pingDuration = Integer.parseInt(parts[3]);

                // Pass ping request information to relevant host
                SimpleHost sender = hosts.get(senderAddress);
                if (sender != null) {
                    sender.setPingParameters(receiverAddress, pingInterval, pingDuration);
                    sender.sendPings(receiverAddress, pingInterval, pingDuration);
                } else {
                    System.err.println("Host with address " + senderAddress + " not found.");
                }
            }

            // Run the simulation
            runSimulation(fel);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private static void runSimulation(FutureEventList fel) {
        while (fel.size() > 0) {
            Event event = fel.removeFirst();
            event.handle();
            // Add handling for ping requests received during simulation
            if (event instanceof Message) {
                Message msg = (Message) event;
                if (msg.getMessage().equals("PING_REQUEST")) {
                    // Print the ping request
                    System.out.println("[" + msg.getArrivalTime() + "ts] Host " + msg.getDestAddress() + ": Ping request from host " + msg.getSrcAddress());
                }
            }
        }
    }
}
