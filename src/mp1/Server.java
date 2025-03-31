package mp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server extends ObjectPlus {
    String name;
    User owner;

    ArrayList<Channel> channels = new ArrayList<>();

    public Server(String name, User owner) {
        super();
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

    public void addChannel(String channelName) throws ServerAppException {
        if (channelName == null) {
            throw new IllegalArgumentException("Channel's name cannot be null. Please try something else.");
        }
        if (channelName.isEmpty()) {
            throw new IllegalArgumentException("Channel's name cannot be empty. Please try something else.");
        }

        for (Channel c : channels) {
            if (c.getName().equals(channelName)) {
                throw new ServerAppException("Channel with this name already exists. Please try something else.");
            }
        }

        channels.add(new Channel(channelName));
    }

    public void renameChannel(String channelName) throws ServerAppException {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("Channel's name cannot be null or empty.");
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

        foundChannel.setName(channelName);
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

    public List<UserOnServer> getUsersByStatus(UserStatus status) throws ClassNotFoundException {
        List<UserOnServer> users = new ArrayList<>();

        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
            if (userOnServer.getServer().equals(this) && userOnServer.getStatus().equals(status)) {
                users.add(userOnServer);
            }
        }
        return users;
    }

    public static Map<Server, Integer> getServersWithNUsers(int n) throws ClassNotFoundException {
        if (n <= 0) {
            throw new IllegalArgumentException("User count must be greater than 0.");
        }

        Map<Server, Integer> servers = new HashMap<>();

        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
            if (!servers.containsKey(userOnServer.getServer())) {
                servers.put(userOnServer.getServer(), 1);
            } else {
                servers.put(userOnServer.getServer(), servers.get(userOnServer.getServer()) + 1);
            }
        }

        return servers;
    }

    @Override
    public String toString() {
        return String.format("%s (Owner: %s) - %d channels", name, owner.getName(), channels.size());
    }
}
