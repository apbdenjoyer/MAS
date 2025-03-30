package mp1;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Save Current Extent to File");
            System.out.println("2. Read Extent from File");
            System.out.println("3. Go to Managing Data");
            System.out.println("0. Exit");

            int choice = getIntInput("Choose an option: ");

            switch (choice) {
                case 1:
                    saveExtentToFile();
                    break;
                case 2:
                    readExtentFromFile();
                    break;
                case 3:
                    manageData();
                    break;
                case 0:
                    System.out.println("Exiting application...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void saveExtentToFile() {
        String filename = getStringInput("Please provide a file name: ");
        System.out.println("Saving extent to file...");
        try {
            ObjectPlus.writeExtents(filename);
            System.out.printf("Extent saved to file %s.%n", filename + ".bin");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readExtentFromFile() {
        String filename = getStringInput("Please provide a file name: ");
        System.out.println("Reading extent from file...");
        try {
            ObjectPlus.readExtents(filename);
            System.out.printf("Extent read from file %s.%n", filename + ".bin");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void manageData() {
        System.out.println("\n--- Manage Data ---");
        System.out.println("1. Servers (Manage Channels)");
        System.out.println("2. Users (User Settings, Join/Leave Servers, Message in server, Manage Friend list)");
        System.out.println("0. Back to Main Menu");

        while (true) {
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    manageServers();
                    break;
                case 2:
                    manageUsers();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void manageUsers() {
        System.out.println("\n--- Users ---");

    }

    private static void manageServers() {
        System.out.println("\n--- Servers ---");

        List<Server> servers = null;
        try {
            servers = (List<Server>) ObjectPlus.getExtent(Server.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("1. See available servers.");
        System.out.println("2. See servers with more than N users.");
        System.out.println("0. Back to Main Menu");
        System.out.println("Available Servers:");

        while (true) {
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    System.out.println("Available servers:");
                    for (Server s : servers) {
                        System.out.println(s);
                    }
                    break;
                case 2:
            }

        }



        String choice = getStringInput("Type a server's name in format 'ownerName/serverName' to edit that server");

        while (true) {
            for (Server s : servers) {
                if (s.getName().equals(choice)) {
                    handleServerOptions(s);
                    return;
                }
            }
            System.out.println("Server not found. Try again.");
            choice = getStringInput("Type a server's name in format 'ownerName/serverName' to edit that server");
        }
    }

    private static void handleServerOptions(Server server) {
        System.out.printf("\n--- Managing Server: %s ---%n", server.getName());
        while (true) {
            System.out.println("1. View Channels");
            System.out.println("2. View Users by Status");
            System.out.println("0. Back to Server List");

            int choice = getIntInput("Choose an option: ");

            switch (choice) {
                case 1:
                    viewChannels(server);
                    break;
                case 2:
                    viewUsersByStatus(server);
                    break;
                case 3:
                    addChannelToServer(server);
                    break;
                case 4:
                    removeChannelFromServer(server);
                    break;
                case 5:
                    findServersWithMoreThanNUsers();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewChannels(Server server) {

        for (Channel channel : server.getChannels()) {
            System.out.println(channel);
        }

    }


    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Enter a valid number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.next();
    }
}