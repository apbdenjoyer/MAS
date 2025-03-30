import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Server extends ObjectPlus {
    String name;

    Set<UserOnServer> users = new HashSet<>();
    ArrayList<Channel> channels = new ArrayList<>();
}

enum UserStatus {
    ADMIN,
    DEFAULT,
    MUTED,
    BANNED
}

class UserOnServer extends ObjectPlus {
    User user;
    Server server;
    UserStatus status;
    LocalDate joinDate;
    LocalDate leaveDate;

    public UserOnServer(User user, Server server) {
        super();
        this.user = user;
        this.server = server;
        this.joinDate = LocalDate.now();
    }

}