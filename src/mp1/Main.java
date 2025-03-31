package mp1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

//TODO:
/*
 * Finish user page:
 * 1. make sure creating users works FULLY
 * 2. show friendlists and have two options: add new friend, delete existing friend
 * 3. show servers:
 *       *show your servers (select a channel and write message, add/rename/delete channel)
 *       *show joined servers
 *       *join servers by name
 *
 * Fix up server page:
 *
 * Add test data as a method
 */

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

        if (filename.isEmpty() || !Files.exists(Paths.get(filename))) {
            System.out.println("File not found: " + filename);
            return;
        }
        System.out.println("Reading extent from file...");
        try {
            ObjectPlus.readExtents(filename);
            System.out.printf("Extent read from file %s.%n", filename);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void manageData() {
        while (true) {
            System.out.println("\n--- Manage Data ---");
            System.out.println("1. Servers (Manage Channels)");
            System.out.println("2. Users (User Settings, Create/Remove/Join/Leave Servers, Message in server, Manage Friend list)");
            System.out.println("0. Back to Main Menu");
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    handleServersData();
                    break;
                case 2:
                    handleUsersData();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void handleUsersData() {
        while (true) {
            System.out.println("\n--- Users ---");
            System.out.println("1. Select an user from list.");
            System.out.println("2. Create a new user.");
            System.out.println("0. Back to Main Menu");

            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    seeUsers();
                    break;
                case 2:
                    createUser();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void createUser() {
        while (true) {
            String choice = getStringInput("Please provide an username, email and password in form of 'username;email;password' or type $ to go back. You can omit the username to have one generated from your email address: ");
            if (choice.equals("$")) {
                System.out.println("\nCanceling user creation...");
                return;
            }
            try {
                String[] parts = choice.split(";");
                if (parts.length == 2) {
                    new User(parts[0], parts[1]);
                    System.out.printf("User %s added.%n", parts[0]);
                    return;
                } else if (parts.length == 3) {
                    new User(parts[0], parts[1], parts[2]);
                    System.out.printf("User %s added.%n", parts[0]);
                    return;
                } else {
                    System.out.println("Invalid format. Try again.");
                }
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void seeUsers() {
        List<User> users = new ArrayList<>();

        while (true) {
            try {
                users = (List<User>) ObjectPlus.getExtent(User.class);
            } catch (ClassNotFoundException e) {
                System.out.println("No users found. Add at least one user to see them here.");
                return;
            }

            System.out.println(" ---Available users:--- ");
            for (User user : users) {
                System.out.println(user);
            }
            String choice = getStringInput("Choose a user by typing their name or leave $ to go back: ");

            if (choice.equals("$")) {
                return;
            } else {
                User foundUser = null;
                for (User u : users) {
                    if (u.name.equals(choice)) {
                        foundUser = u;
                        manageSelectedUser(foundUser);
                        break;
                    }
                }
                if (foundUser == null) {
                    System.out.println("err: User " + choice + " not found.");
                }
            }
        }

    }

    private static void manageSelectedUser(User user) {
        while (true) {
            System.out.println("\n--- Managing User: " + user.getName() + " ---");
            System.out.println("1. Change user's email.");
            System.out.println("2. Change user's password.");
            System.out.println("3. Go to user's friend list");
            System.out.println("4. Go to user's servers");
            System.out.println("0. Go back");

            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    changeUserEmail(user);
                    break;
                case 2:
                    changeUserPassword(user);
                    break;
                case 3:
                    goToUserFriendList(user);
                    break;
                case 4:
                    goToUserServers(user);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void goToUserServers(User user) {
        Set<Server> servers = new LinkedHashSet<>();
        while (true) {
            servers.clear();
            try {
                for (UserOnServer userOnServer : ObjectPlus.getExtent(UserOnServer.class)) {
                    if (userOnServer.getUser().equals(user)) {
                        servers.add(userOnServer.getServer());
                    }
                }
            } catch (ClassNotFoundException _) {}

            if (servers.isEmpty()) {
                System.out.printf("\t*No servers for user %s found. Create or join at least one server to see them here.%n", user.getName());
            } else {
                System.out.printf("%s's servers:%n", user.getName());
                for (Server server : servers) {
                    System.out.println("\t*" + server);
                }
            }

            System.out.println("1. Create a server.");
            System.out.println("2. Remove a server.");
            System.out.println("3. Join a server.");
            System.out.println("4. Leave a server.");
            System.out.println("0. Go back");

            int choice = getIntInput("Choose an option: ");

            switch (choice) {
                case 1:
                    createServer(user);
                    break;
                case 2:
                    removeServer(user);
                    break;
                case 3:
                    joinServer(user);
                    break;
                case 4:
                    leaveServer(user);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }


    private static void createServer(User user) {
        while (true) {
            String choice = getStringInput("Provide a server name, or type $ to go back: ");
            if (choice.equals("$")) {
                System.out.println("\nCanceling server creation...");
                return;
            }
            try {
                user.createServer(choice);
                System.out.printf("Server %s created successfully.%n", choice);
                return;
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void removeServer(User user) {

        Set<Server> servers = new LinkedHashSet<>();
        while (true) {
            servers.clear();
            try {
                for (UserOnServer userOnServer : ObjectPlus.getExtent(UserOnServer.class)) {
                    if (userOnServer.getUser().equals(user) && userOnServer.getServer().getOwner().equals(user)) {
                        servers.add(userOnServer.getServer());
                    }
                }
            } catch (ClassNotFoundException _) {}

            if (servers.isEmpty()) {
                return;
            } else {
                System.out.printf("%s's owned servers:%n", user.getName());
                for (Server server : servers) {
                    System.out.println("\t*" + server);
                }

                String choice = getStringInput("Remove a server by typing its name, or type '$' to go back: ");

                if (choice.equals("$")) {
                    return;
                }
                for (Server server : servers) {
                    if (server.getName().equals(choice)) {
                        try {
                            user.removeServer(choice);
                            System.out.printf("Server %s removed successfully.%n", choice);
                            return;
                        } catch (ServerAppException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private static void joinServer(User user) {
        while (true) {
            String choice = getStringInput("Enter the server name in the format 'ownername/servername' or type '$' to go back: ");

            if (choice.equals("$")) {
                return;
            }

            String[] parts = choice.split("/");
            if (parts.length != 2) {
                System.out.println("Invalid format. Please use 'ownername/servername'.");
                continue;
            }

            String ownerName = parts[0];
            String serverName = parts[1];
            Server targetServer = null;

            try {
                for (UserOnServer userOnServer : ObjectPlus.getExtent(UserOnServer.class)) {
                    Server server = userOnServer.getServer();
                    if (server.getOwner().getName().equals(ownerName) && server.getName().equals(serverName)) {
                        targetServer = server;
                        break;
                    }
                }
            } catch (ClassNotFoundException _) {}

            if (targetServer == null) {
                System.out.println("Server not found. Make sure you entered the correct format and the server exists.");
            } else {
                try {
                    user.joinServer(targetServer);
                    System.out.printf("Successfully joined server %s/%s.%n", ownerName, serverName);
                    return;
                } catch (ServerAppException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private static void leaveServer(User user) {
        Set<Server> joinableServers = new LinkedHashSet<>();

        try {
            for (UserOnServer userOnServer : ObjectPlus.getExtent(UserOnServer.class)) {
                if (userOnServer.getUser().equals(user) && !userOnServer.getServer().getOwner().equals(user)) {
                    joinableServers.add(userOnServer.getServer());
                }
            }
        } catch (ClassNotFoundException _) {}

        if (joinableServers.isEmpty()) {
            System.out.println("You are not a member of any servers that you do not own.");
            return;
        }

        System.out.printf("%s's joinable servers:%n", user.getName());
        for (Server server : joinableServers) {
            System.out.println("\t*" + server);
        }

        while (true) {
            String choice = getStringInput("Enter the server name to leave, or type '$' to go back: ");

            if (choice.equals("$")) {
                return;
            }

            for (Server server : joinableServers) {
                if (server.getName().equals(choice)) {
                    try {
                        user.leaveServer(server);
                        System.out.printf("Successfully left server %s.%n", choice);
                        return;
                    } catch (ServerAppException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

            System.out.println("Server not found in your joined servers. Please try again.");
        }
    }



    private static void goToUserFriendList(User user) {
        Set<User> friends = new LinkedHashSet<>();
        try {
            for (Friendship friendship : ObjectPlus.getExtent(Friendship.class)) {
                if (friendship.getRequester().equals(user)) {
                    friends.add(friendship.getAddressee());
                } else if (friendship.getAddressee().equals(user)) {
                    friends.add(friendship.getRequester());
                }
            }
        } catch (ClassNotFoundException _) {}

        if (!friends.isEmpty()) {
            System.out.printf("%s's friend list:%n", user.getName());
            for (User friend : friends) {
                System.out.println(friend);
            }
        } else {
            System.out.printf("No friends for user %s found. Add at least one user to see them here.%n", user.getName());
        }
        while (true) {
            System.out.println("1. Add a friend.");
            System.out.println("2. Remove a friend.");
            System.out.println("0. Go back");

            int choice = getIntInput("Choose an option: ");

            switch (choice) {
                case 1:
                    addFriend(user);
                    break;
                case 2:
                    removeFriend(user);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void addFriend(User user) {
        while (true) {
            String choice = getStringInput("Add a friend by typing their name, or type '$' to go back: ");

            if (choice.equals("$")) {
                return;
            } else {
                try {
                    user.addFriend(choice);
                    System.out.printf("Friend %s added successfully.%n", choice);
                    return;
                } catch (ServerAppException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private static void removeFriend(User user) {
        while (true) {
            String choice = getStringInput("Remove a friend by typing their name, or type '$' to go back: ");

            if (choice.equals("$")) {
                return;
            } else {
                try {
                    user.removeFriend(choice);
                    System.out.printf("Friend %s removed successfully.%n", choice);
                    return;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (ServerAppException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }


    private static void changeUserPassword(User user) {
        while (true) {
            String newPassword = getStringInput("Please provide a new password: ");
            try {
                user.setHashedPassword(newPassword);
                System.out.println("Password changed successfully.");
                return;
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void changeUserEmail(User user) {
        while (true) {
            String newEmail = getStringInput(String.format(
                    "%s's current email: %s.%nPlease provide a new email: ", user.getName(), user.getEmail()
            ));
            try {
                user.changeEmail(newEmail);
                System.out.printf("Email changed to %s.%n", newEmail);
                return;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void handleServersData() {
        while (true) {
            System.out.println("\n--- Servers ---");

            System.out.println("1. Select a server from list.");
            System.out.println("2. See servers with more than N users.");
            System.out.println("0. Back to Main Menu");


            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    seeServers();
                    break;
                case 2:
                    getServersWithNUsers();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void seeServers() {
        System.out.println(" ---Available servers:--- ");
        Set<Server> servers = new LinkedHashSet<>();
        try {
            servers = (LinkedHashSet<Server>) ObjectPlus.getExtent(Server.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (Server s : servers) {
            System.out.println(s);
        }

        while (true) {
            String choice = getStringInput("\n Choose a server by typing 'ownerName/serverName' or type '$' to go back: ");

            if (choice.equals("$")) {
                return;
            } else {
                String[] parts = choice.split("/");
                if (parts.length != 2) {
                    System.out.println("Invalid server name. Must be in the format 'ownerName/serverName'.");
                    continue;
                }
                String ownerName = parts[0];
                String serverName = parts[1];
                Server server = null;
                for (Server s : servers) {
                    if (s.getName().equals(serverName) && s.getOwner().equals(ownerName)) {
                        manageSelectedServer(s);
                        break;
                    }
                }
                System.out.printf("Server %s by %s not found.%n", serverName, ownerName);
            }
        }
    }

    private static void manageSelectedServer(Server server) {
        System.out.println("\n--- Managing Server: " + server.getName() + " ---");
        System.out.println("1. Manage channels. (Add, rename, remove)");
        System.out.println("2. See members.");
        System.out.println("0. Go back");

        while (true) {
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    handleChannels(server);
                    break;
                case 2:
                    seeServerMembers(server);
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void seeServerMembers(Server server) {
        System.out.println("Seeing members: ");
        System.out.println("1. See clean members.");
        System.out.println("2. See muted members.");
        System.out.println("0. Go back");


        while (true) {
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    try {
                        List<UserOnServer> users = server.getUsersByStatus(UserStatus.CLEAN);
                        System.out.println("Clean users: ");
                        for (UserOnServer u : users) {
                            System.out.println(u.getServer().getName() + "(joined: " + u.getJoinDate() + ")");
                        }
                        break;
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                case 2:
                    try {
                        List<UserOnServer> users = server.getUsersByStatus(UserStatus.MUTED);
                        System.out.println("Muted users: ");
                        for (UserOnServer u : users) {
                            System.out.println(u.getServer().getName() + "(joined: " + u.getJoinDate() + ")");
                        }
                        break;
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void getServersWithNUsers() {
        int choice = getIntInput("Input the minimum amount of users: ");

        try {
            Map<Server, Integer> servers = Server.getServersWithNUsers(choice);
            List<Map.Entry<Server, Integer>> list = new ArrayList<>(servers.entrySet());
            list.sort(Map.Entry.comparingByValue());

            System.out.println("Servers with at least " + choice + " users:");
            for (Map.Entry<Server, Integer> entry : list) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " users");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleChannels(Server server) {
        System.out.printf("\n--- Managing Server: %s ---%n", server.getName());
        while (true) {
            System.out.println("1. View channels");
            System.out.println("2. Add channel");
            System.out.println("3. Rename channel");
            System.out.println("4. Remove channel");
            System.out.println("0. Go back");

            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    viewChannels(server);
                    break;
                case 2:
                    addChannel(server);
                    break;
                case 3:
                    renameChannel(server);
                    break;
                case 4:
                    removeChannel(server);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void renameChannel(Server server) {
        while (true) {
            String choice = getStringInput("Please provide a channel name you wish to rename or type '$' to go back: ");
            if (choice.equals("$")) {
                return;
            }
            try {
                String rename = getStringInput("Please provide a new channel name: ");
                server.renameChannel(rename);
                System.out.printf("Channel %s renamed to %s.%n", choice, rename);
                return;
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void removeChannel(Server server) {
        while (true) {
            String choice = getStringInput("Please provide a channel name you wish to remove or type '$' to go back: ");
            if (choice.equals("$")) {
                return;
            }
            try {
                server.removeChannel(choice);
                System.out.printf("Channel %s removed from server %s.%n", choice, server.getName());
                return;
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void addChannel(Server server) {
        while (true) {
            String choice = getStringInput("Please provide a channel name or type '$' to go back:");
            if (choice.equals("$")) {
                return;
            }
            try {
                server.addChannel(choice);
                System.out.printf("Channel %s added to server %s.%n", choice, server.getName());
                return;
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void viewChannels(Server server) {
        System.out.printf("%s's channels:%n", server.getName());
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
        String input = scanner.next();
        return input.trim();
    }


}