import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Set the filename to read
        String fileName = "simulation1.txt";

        // Read simulation.txt and bootstrap the simulation
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Read the first line to get the first host address
            int firstHostAddress = Integer.parseInt(reader.readLine());

            // Create the first host
            FutureEventList fel = new LinkedEventList();
            SimpleHost firstHost = new SimpleHost(firstHostAddress, fel);

            // Read host connections
            String line;
            while ((line = reader.readLine()) != null && !line.equals("-1")) {
                String[] parts = line.split(" ");
                int neighborAddress = Integer.parseInt(parts[0]);
                int distance = Integer.parseInt(parts[1]);

                // Add neighbor connections for the first host
                SimpleHost neighborHost = new SimpleHost(neighborAddress, fel);
                firstHost.addNeighbor(neighborHost, distance);

                // Since connections are bi-directional, add first host as a neighbor for the read host
                neighborHost.addNeighbor(firstHost, distance);
            }

            // Read ping request information
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                int senderAddress = Integer.parseInt(parts[0]);
                int receiverAddress = Integer.parseInt(parts[1]);
                int pingInterval = Integer.parseInt(parts[2]);
                int pingDuration = Integer.parseInt(parts[3]);

                // Pass ping request information to relevant host
                SimpleHost sender = getHostByAddress(senderAddress, firstHost);
                if (sender != null) {
                    sender.setPingParameters(receiverAddress, pingInterval, pingDuration);
                    sender.sendPingRequest();
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
        }
    }

    private static SimpleHost getHostByAddress(int address, SimpleHost firstHost) {
        // Check if the first host matches the given address
        if (firstHost.getHostAddress() == address) {
            return firstHost;
        }

        // Check the neighbors of the first host
        for (Host neighbor : firstHost.getNeighbors().values()) {
            if (neighbor instanceof SimpleHost) {
                SimpleHost simpleNeighbor = (SimpleHost) neighbor;
                if (simpleNeighbor.getHostAddress() == address) {
                    return simpleNeighbor;
                }
            }
        }

        // Host not found
        return null;
    }
}
