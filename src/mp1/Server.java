package mp1;

import java.util.*;

public class Server extends ObjectPlus {
    private final String name;
    private final User owner;

    ArrayList<Channel> channels = new ArrayList<>();

    private static final int MIN_SERVER_NAME_LEN = 3;

    public Server(String name, User owner) throws ServerAppException {
        super();

        if (name.length() < MIN_SERVER_NAME_LEN) {
            removeFromExtent(this);
            throw new ServerAppException(String.format("Server name cannot be shorter than " +
                    "%d", MIN_SERVER_NAME_LEN));
        }

        this.name = name;
        this.owner = owner;
        channels.add(new Channel("general"));
        new UserOnServer(owner, this);
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void addChannel(Channel channel) throws ServerAppException {
        if (channel.getName() == null) {
            throw new IllegalArgumentException("Channel's name cannot be null. Please try something else.");
        }
        if (channel.getName().isEmpty()) {
            throw new IllegalArgumentException("Channel's name cannot be empty. Please try something else.");
        }


        for (Channel c : channels) {
            if (c.equals(channel)) {
                throw new ServerAppException("Channel with this name already exists. Please try something else.");
            }
        }

        channels.add(channel);
    }

    public void addChannel(String channelName) throws ServerAppException {
        this.addChannel(new Channel(channelName));
    }

    public void renameChannel(String oldChannelName, String newChannelName) throws ServerAppException {
        if (oldChannelName == null || oldChannelName.isEmpty()) {
            throw new IllegalArgumentException("Channel's old name cannot be " +
                    "null or empty.");
        }
        if (newChannelName == null || newChannelName.isEmpty()) {
            throw new IllegalArgumentException("Channel's new name cannot be " +
                    "null or empty.");
        }

        Channel foundChannel = null;
        for (Channel c : channels) {
            if (c.getName().equals(oldChannelName)) {
                foundChannel = c;
            }
        }
        if (foundChannel == null) {
            throw new ServerAppException(String.format("Server %s doesn't " +
                    "have a channel named '%s'", this.name, oldChannelName));
        }

        foundChannel.setName(newChannelName);
    }

    public void removeChannel(String channelName) throws ServerAppException {
        if (channelName == null) {
            throw new NullPointerException("Channel's name cannot be null.");
        }

        Channel foundChannel = null;
        for (Channel c : channels) {
            if (c.getName().equals(channelName)) {
                foundChannel = c;
            }
        }
        if (foundChannel == null) {
            throw new ServerAppException(String.format("Server %s doesn't have a channel named '%s'", this.name, channelName));
        }

        channels.remove(foundChannel);
    }

    public List<UserOnServer> getUsersOnServer() throws ClassNotFoundException {
        List<UserOnServer> users = new ArrayList<>();

        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
            if (userOnServer.getServer().equals(this)) {
                users.add(userOnServer);
            }
        }
        return users;
    }

    public static Map<Server, Integer> getServersWithNUsers(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("User count must be greater than 0.");
        }

        Map<Server, Integer> servers = new HashMap<>();

        try {
            for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
                if (!servers.containsKey(userOnServer.getServer())) {
                    servers.put(userOnServer.getServer(), 1);
                } else {
                    servers.put(userOnServer.getServer(), servers.get(userOnServer.getServer()) + 1);
                }
            }
        } catch (ClassNotFoundException _) {
        }

        Iterator<Map.Entry<Server, Integer>> iterator =
                servers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Server, Integer> entry = iterator.next();
            if (entry.getValue() < n) {
                iterator.remove();
            }
        }

        return servers;
    }

    @Override
    public String toString() {
        int userCount = 0;
        try {
            userCount = getUsersOnServer().size();

        } catch (ClassNotFoundException _) {
        }

        return String.format("%s (Owner: %s) - %d channels, %d members", name,
                owner.getName(), channels.size(), userCount);
    }
}
