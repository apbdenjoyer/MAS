package mp1;

import java.time.LocalDate;

public class UserOnServer extends ObjectPlus {
    private final User user;
    private final Server server;
    private final LocalDate joinDate;

    public UserOnServer(User user, Server server) {
        super();
        this.user = user;
        this.server = server;
        this.joinDate = LocalDate.now();
    }

    public User getUser() {
        return user;
    }

    public Server getServer() {
        return server;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

}