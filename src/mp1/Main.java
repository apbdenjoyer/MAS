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
    private static final Scanner intScanner = new Scanner(System.in);
    private static final Scanner stringScanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n====== Main Menu ======");
            System.out.println("\n -- Options: --");
            System.out.println("1. Save Current Extent to File");
            System.out.println("2. Read Extent from File");
            System.out.println("3. Go to Data Management");
            System.out.println("4. Fill with Test Data");
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
                case 4:
                    fillWithTestData();
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
        String filename = getStringInput("Please provide a file name (with or without '.bin'): ");

        if (!filename.endsWith(".bin")) {
            filename += ".bin";
        }

        System.out.println("Saving extent to file...");
        try {
            ObjectPlus.writeExtents(filename);
            System.out.printf("Extent saved to file %s.%n", filename);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readExtentFromFile() {
        String filename = getStringInput("Please provide a file name (with or without '.bin'): ");

        if (!filename.endsWith(".bin")) {
            filename += ".bin";
        }

        if (!Files.exists(Paths.get(filename))) {
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
            System.out.println("\n====== Manage Data ======");
            System.out.println("\n -- Options: --");
            System.out.println("1. Servers (See servers, See servers with " +
                    "more than n users" +
                    " )");
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
            System.out.println("\n====== Users ======");
            System.out.println("\n -- Options: --");
            System.out.println("1. Select an user from list.");
            System.out.println("2. Create a new user.");
            System.out.println("0. Go back");

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
        List<User> users;

        while (true) {
            try {
                users = (List<User>) ObjectPlus.getExtent(User.class);
            } catch (ClassNotFoundException e) {
                System.out.println("\t*No users found. Add at least one user to see them here.");
                return;
            }

            System.out.println("\n======Available users:====== ");
            for (User user : users) {
                System.out.println("\t*" + user);
            }
            String choice = getStringInput("Choose a user by typing their " +
                    "name, or type '$' to go back: ");

            if (choice.equals("$")) {
                return;
            } else {
                User foundUser = null;
                for (User u : users) {
                    if (u.getName().equals(choice)) {
                        foundUser = u;
                        manageSelectedUser(foundUser);
                        break;
                    }
                }
                if (foundUser == null) {
                    System.out.println("\t*User " + choice + " not found.");
                }
            }
        }

    }

    private static void manageSelectedUser(User user) {
        while (true) {
            System.out.println("\n====== Managing User: " + user.getName() +
                    " ======");
            System.out.println("\n -- Options: --");
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
            } catch (ClassNotFoundException _) {
            }
            System.out.printf("%n====== %s's servers: ======%n", user.getName());
            if (servers.isEmpty()) {
                System.out.printf("\t*No servers for user %s found. Create or join at least one server to see them here.%n", user.getName());
            } else {
                for (Server server : servers) {
                    if (user.getFavoriteServer() != null && user.getFavoriteServer().equals(server)) {
                        System.out.println("\t*" + server + " (favorited)");
                    } else {
                        System.out.println("\t*" + server);
                    }
                }
            }
            System.out.println("\n -- Options: --");
            System.out.println("1. Choose a server to go to.");
            System.out.println("2. Create a server.");
            System.out.println("3. Remove a server.");
            System.out.println("4. Join a server.");
            System.out.println("5. Leave a server.");
            System.out.println("0. Go back");

            int choice = getIntInput("Choose an option: ");

            switch (choice) {
                case 1:
                    chooseServer(user, servers);
                    break;
                case 2:
                    createServer(user);
                    break;
                case 3:
                    removeServer(user);
                    break;
                case 4:
                    joinServer(user);
                    break;
                case 5:
                    leaveServer(user);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void chooseServer(User user, Set<Server> servers) {
        System.out.printf("%n====== %s's servers: ======%n", user.getName());
        if (servers.isEmpty()) {
            System.out.printf("\t*No servers for user %s found. Create or join at least one server to see them here.%n", user.getName());
            return;
        } else {
            for (Server server : servers) {
                System.out.println("\t*" + server);
            }
        }

        while (true) {
            String serverChoice = getStringInput("\nEnter the server name " +
                    "(ownerName/serverName) to enter, or type '$' to go back:" +
                    " ");
            if (serverChoice.equals("$")) {
                return;
            }
            String[] parts = serverChoice.split("/");
            if (parts.length != 2) {
                System.out.println("Invalid server path. Must be in the format 'ownerName/serverName'.");
                continue;
            }
            for (Server server : servers) {
                if (server.getName().equals(parts[1]) && server.getOwner().getName().equals(parts[0])) {
                    viewChosenServer(user, server);
                }
            }
        }
    }

    private static void viewChosenServer(User user, Server server) {
        while (true) {
            System.out.printf("%n====== Visiting server '%s' (owner: %s) as %s ======%n", server.getName(), server.getOwner().getName(), user.getName());
            if (server.getOwner().equals(user)) {
                System.out.println("\n -- Options: --");
                System.out.println("1. View Channels");
                System.out.println("2. Add Channel");
                System.out.println("3. Rename Channel");
                System.out.println("4. Remove Channel");
                System.out.println("5. Choose a Channel to Write a Message");
                System.out.println("6. Mark server as favorite");
                System.out.println("0. Go back");

                int choice = getIntInput("Enter your choice:");

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
                    case 5:
                        chooseChannel(user, server);
                        break;
                    case 6:
                        favoriteServer(user, server);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("\n -- Options: --");
                System.out.println("1. View Channels");
                System.out.println("2. Choose a Channel to Write a Message");
                System.out.println("3. Mark server as favorite");
                System.out.println("0. Exit Server");

                int choice = getIntInput("Enter your choice:");

                switch (choice) {
                    case 1:
                        viewChannels(server);
                        break;
                    case 2:
                        chooseChannel(user, server);
                        break;
                    case 3:
                        favoriteServer(user, server);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private static void favoriteServer(User user, Server server) {
        System.out.printf("Mark server %s as favorite? (It will overwrite the" +
                        " current favourite server %s) [Y/N]%n", server.getName(),
                user.getFavoriteServer() == null ? "-" :
                        user.getFavoriteServer());

        while (true) {
            String choice = getStringInput("Choose an option: ");
            if (choice.equalsIgnoreCase("y")) {
                user.setFavoriteServer(server);
                System.out.printf("Server %s set as favourite.%n", server.getName());
                return;
            } else if (choice.equalsIgnoreCase("n")) {
                System.out.println("Cancelling server favoriting...");
                return;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void chooseChannel(User user, Server server) {
        List<Channel> channels = server.getChannels();

        System.out.printf("%n====== %s Channels: ======%n", server.getName());
        for (Channel channel : channels) {
            System.out.println(channel);
        }

        while (true) {

            String choice = getStringInput("Type a channel's name to write to it, or type '$' to go back: ");

            if (choice.equals("$")) {
                return;
            }

            Channel foundChannel = null;

            for (Channel channel : channels) {
                if (channel.getName().equals(choice)) {
                    foundChannel = channel;
                    break;
                }
            }

            if (foundChannel == null) {
                System.out.printf("Channel %s doesn't exist. Try again.%n", choice);
                continue;
            }
            String contents = getStringInput("Type your message: ");
            try {
                user.writeMessage(server, foundChannel, contents);
                System.out.println("Message sent to channel: " + foundChannel.getName());
                break;
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
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
            } catch (ClassNotFoundException _) {
            }

            if (servers.isEmpty()) {
                System.out.println("\t*You don't own any servers.");
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
            String serverPath = getStringInput("Enter the server name (ownerName/serverName) or type '$' to go back: ");
            if (serverPath.equals("$")) {
                return;
            }
            try {
                user.joinServer(serverPath);
                System.out.printf("Successfully joined server %s.%n", serverPath);
                return;
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void leaveServer(User user) {
        Set<Server> servers = new LinkedHashSet<>();

        try {
            for (UserOnServer userOnServer : ObjectPlus.getExtent(UserOnServer.class)) {
                if (userOnServer.getUser().equals(user) && !userOnServer.getServer().getOwner().equals(user)) {
                    servers.add(userOnServer.getServer());
                }
            }
        } catch (ClassNotFoundException _) {
        }

        if (servers.isEmpty()) {
            System.out.println("\t*You are not a member of any servers you don't own.");
            return;
        }

        System.out.printf("%s's joined servers:%n", user.getName());
        for (Server server : servers) {
            System.out.println("\t*" + server);
        }

        while (true) {
            String choice = getStringInput("Enter the server name (ownerName/serverName) to leave, or type '$' to go back: ");
            if (choice.equals("$")) {
                return;
            }
            String[] parts = choice.split("/");
            if (parts.length != 2) {
                System.out.println("Invalid server path. Must be in the format 'ownerName/serverName'.");
                continue;
            }
            for (Server server : servers) {
                if (server.getName().equals(parts[1]) && server.getOwner().getName().equals(parts[0])) {
                    try {
                        user.leaveServer(choice);
                        System.out.printf("Successfully left server %s.%n", choice);
                        return;
                    } catch (ServerAppException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
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
        } catch (ClassNotFoundException _) {
        }

        if (!friends.isEmpty()) {
            System.out.printf("%n====== %s's friend list: ======%n", user.getName());
            for (User friend : friends) {
                System.out.println("\t*" + friend);
            }
        } else {
            System.out.printf("\t*No friends for user %s found. Add at least one user to see them here.%n", user.getName());
        }
        while (true) {
            System.out.println("\n -- Options: --");
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
            String newPassword = getStringInput("Please provide a new password, or type '$' to go back: ");
            if (newPassword.equals("$")) {
                return;
            }
            try {
                user.setPassword(newPassword);
                System.out.println("Password changed successfully.");
                return;
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void changeUserEmail(User user) {
        while (true) {
            String newEmail = getStringInput(String.format("%s's current email: %s.%nPlease provide a new email, or type '$' to go back: ", user.getName(), user.getEmail()));
            if (newEmail.equals("$")) {
                return;
            }

            try {
                user.changeEmail(newEmail);
                System.out.printf("Email changed to %s.%n", newEmail);
                return;
            } catch (ServerAppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void handleServersData() {
        while (true) {
            System.out.println("\n====== Servers ======");
            System.out.println("\n -- Options: --");
            System.out.println("1. Select a server from list.");
            System.out.println("2. See servers with more than N users.");
            System.out.println("0. Go back");


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
        Set<Server> servers;
        try {
            servers = new LinkedHashSet<>((Collection<Server>) ObjectPlus.getExtent(Server.class));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            System.out.println(" \n--Available servers:====== ");
            if (!servers.isEmpty()) {
                for (Server s : servers) {
                    System.out.println(s);
                }
            } else {
                System.out.println("\t*No servers available");
            }

            String choice = getStringInput("\nChoose a server by typing 'ownerName/serverName' or type '$' to go back: ");

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
                for (Server s : servers) {
                    if (s.getName().equals(serverName) && s.getOwner().getName().equals(ownerName)) {
                        checkSelectedServer(s);
                        break;
                    }
                }
                System.out.printf("Server %s by %s not found.%n", serverName, ownerName);
            }
        }
    }

    private static void checkSelectedServer(Server server) {
        while (true) {
            System.out.printf("\n====== Server %s/%s ======\n", server.getOwner().getName(), server.getName());
            System.out.println("\n -- Options: --");
            System.out.println("1. See members.");
            System.out.println("2. See channels");
            System.out.println("0. Go back");

            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    seeServerMembers(server);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void seeServerMembers(Server server) {
        System.out.printf("\n====== Members of server %s/%s ======\n",
                server.getOwner().getName(), server.getName());


        List<UserOnServer> users = null;
        try {
            users = server.getUsersOnServer();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (UserOnServer u : users) {
            System.out.println(u.getUser().getName() +
                    "(joined:" +
                    " " + u.getJoinDate() + ")");
        }
    }

    private static void getServersWithNUsers() {
        int choice = getIntInput("Input the minimum amount of users: ");

        Map<Server, Integer> servers = Server.getServersWithNUsers(choice);
        List<Map.Entry<Server, Integer>> list = new ArrayList<>(servers.entrySet());
        list.sort(Map.Entry.comparingByValue());

        System.out.printf("\n====== Servers with at least %d users ======\n", choice);;
        for (Map.Entry<Server, Integer> entry : list) {
            System.out.println(entry.getKey());
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
                server.renameChannel(choice, rename);
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
        System.out.printf("%n====== %s's channels: ======%n", server.getName());
        for (Channel channel : server.getChannels()) {
            System.out.println(channel);
        }

        while (true) {

            String choice = getStringInput("Type a channel's name to see its " +
                    "message history, or type '$' to go back: ");

            if (choice.equals("$")) {
                return;
            }

            Channel foundChannel = null;

            for (Channel channel : server.getChannels()) {
                if (channel.getName().equals(choice)) {
                    foundChannel = channel;
                    break;
                }
            }

            if (foundChannel == null) {
                System.out.printf("Channel %s doesn't exist. Try again.%n", choice);
                continue;
            }

            System.out.printf("%n====== Channel %s log start: ======%n",
                    foundChannel.getName());
            for (Message message : foundChannel.getMessages()) {
                System.out.println(message);
            }
            System.out.printf("%n====== Channel %s log end: ======%n",
                    foundChannel.getName());
            return;
        }

    }


    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!intScanner.hasNextInt()) {
            System.out.println("Invalid input. Enter a valid number.");
            intScanner.next();
        }
        return intScanner.nextInt();
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return stringScanner.nextLine().trim();
    }

    private static void fillWithTestData(){
        try {
            User john = new User("john", "john@work", "work");
            User mary = new User("mary", "mary@work", "work");
            User steve =new User("steve", "steve@work", "work");

            new Friendship(john, steve);
            new Friendship(steve, mary);

            Server serv1 = new Server("serv", john);
            Server serv2 = new Server("serv", steve);

            john.joinServer("steve/serv");
            mary.joinServer("steve/serv");

            Channel ch1 = new Channel("general2");
            serv2.addChannel(ch1);

            mary.writeMessage(serv2,  ch1, "hejka");
            steve.writeMessage(serv2, ch1, "czesc");

            System.out.println("\nProgram filled with test data.");

        } catch (ServerAppException | ClassNotFoundException _) {}
    }
}