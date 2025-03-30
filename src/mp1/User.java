package mp1;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class User extends ObjectPlus {
    String name;
    String email;
    int hashedPassword;
    LocalDate creationDate;
    LocalDate lastEmailChangeDate;

    static int DAYS_UNTIL_EMAIL_CHANGE_ALLOWED = 7;

    public User(String name, String email, String password) throws ServerAppException, ClassNotFoundException {
        super();
        setName(name);
        setEmail(email);
        setHashedPassword(password);
        this.creationDate = LocalDate.now();
        this.lastEmailChangeDate = LocalDate.now();
    }

    //    In case user doesn't provide a specific name, take email's name (before @)
    public User(String email, String password) throws ClassNotFoundException, ServerAppException {
        this(email.split("@")[0], email, password);
    }


    private boolean isNameValid(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return name.matches("\\w{3,20}");
    }

    private boolean isNameAvailable(String name) throws ClassNotFoundException {
        for (User user : getExtent(User.class)) {
            if (user.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }


    private boolean isEmailValid(String email) {
        return email.matches("^\\w+@\\w+");
    }

    private boolean isEmailAvailable(String email) throws ClassNotFoundException {
        for (User user : getExtent(User.class)) {
            if (user.name.equals(email)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPasswordSame(String password) {
        return hashPassword(password) == this.hashedPassword;
    }

    private boolean isEmailSame(String newEmail) {
        return newEmail.equals(this.email);
    }

    private int hashPassword(String password) {
        return Objects.hash(password);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) throws ServerAppException, ClassNotFoundException {
        if (!isNameValid(name)) {
            throw new ServerAppException("Name is invalid. Please make sure it contains between 3 and 20 allowed characters: (letters, numbers, underscores)");
        }
        if (!isNameAvailable(name)) {
            throw new ServerAppException(String.format("Name %s is already in use.", name));
        }

        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ClassNotFoundException, ServerAppException {
        if (!isEmailValid(email)) {
            throw new ServerAppException(String.format("Email %s is not valid.", email));
        }

        long daysFromLastEmailChange = ChronoUnit.DAYS.between(lastEmailChangeDate, LocalDate.now());
        if (daysFromLastEmailChange < DAYS_UNTIL_EMAIL_CHANGE_ALLOWED) {
            throw new ServerAppException(String.format("You cannot change your email now. Please try again in %d days.", DAYS_UNTIL_EMAIL_CHANGE_ALLOWED - daysFromLastEmailChange));
        }

        if (!isEmailAvailable(email)) {
            throw new ServerAppException(String.format("Email %s is already in use.", email));
        }
        if (isEmailSame(email)) {
            throw new ServerAppException(String.format("Email %s cannot be the same.", email));
        }
    }

    public int getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String password) throws ServerAppException {
        if (isPasswordSame(password)) {
            throw new ServerAppException("Password cannot be the same.");
        }

        this.hashedPassword = hashPassword(password);
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getLastEmailChangeDate() {
        return lastEmailChangeDate;
    }

    public void setLastNameChangeDate() {
        if (lastEmailChangeDate == null) {
            throw new NullPointerException("Last name change date cannot be null.");
        }
        if (lastEmailChangeDate.isBefore(creationDate)) {
            throw new IllegalArgumentException("Last name change date cannot be before creation date.");
        }
        this.lastEmailChangeDate = LocalDate.now();
    }


    public static int getDaysUntilEmailChangeAllowed() {
        return DAYS_UNTIL_EMAIL_CHANGE_ALLOWED;
    }

    public static void setDaysUntilEmailChangeAllowed(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days until email change allowed cannot be negative.");
        }

        DAYS_UNTIL_EMAIL_CHANGE_ALLOWED = days;
    }

    public Period getAccountAge() {
        return Period.between(creationDate, LocalDate.now());
    }

    public void addFriend(User addressee) throws ClassNotFoundException, ServerAppException {
        for (Friendship friendship : getExtent(Friendship.class)) {
            if (friendship.getRequester().equals(this) && friendship.getAddressee().equals(addressee)) {
                throw new ServerAppException(String.format("mp1.Friendship between %s and %s already exists.", this.getName(), addressee.getName()));
            }
        }
        new Friendship(this, addressee);
    }

    public void removeFriend(User addressee) throws ClassNotFoundException, ServerAppException {
        for (Friendship friendship : getExtent(Friendship.class)) {
            if (friendship.getRequester().equals(this) && friendship.getAddressee().equals(addressee)) {
                removeFromExtent(this);
                return;
            }
        }
        throw new ServerAppException(String.format("mp1.Friendship between %s and %s doesn't exist.", this.getName(), addressee.getName()));
    }

    public void joinServer(String serverPath) throws ClassNotFoundException, ServerAppException {
        if (serverPath == null) {
            throw new IllegalArgumentException("mp1.Server path cannot be null.");
        }

        String[] pathParts = serverPath.split("/");
        if (pathParts.length < 2) {
            throw new IllegalArgumentException("Invalid server path. Must be in the format 'ownerName/serverName'.");
        }

        String ownerName = pathParts[0];
        String serverName = pathParts[1];

        Server foundServer = null;

        for (Server server : getExtent(Server.class)) {
            if (server.getName().equals(serverName) && server.getOwner().equals(ownerName)) {
                foundServer = server;
                break;
            }
        }

        if (foundServer == null) {
            throw new ServerAppException(String.format("mp1.Server %s made by %s does not exist.", serverName, ownerName));
        }

        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
            if (userOnServer.getServer().equals(foundServer) && userOnServer.getUser().equals(this)) {
                throw new ServerAppException(String.format("mp1.User %s has already joined server %s by %s.", this.getName(), foundServer.getName(), ownerName));
            }
        }
        new UserOnServer(this, foundServer);
    }

    public void leaveServer(Server server) throws ClassNotFoundException {
        if (server == null) {
            throw new IllegalArgumentException("mp1.Server cannot be null.");
        }

        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
            if (userOnServer.getUser().equals(this) && userOnServer.getServer().equals(server)) {
                userOnServer.setLeaveDate(LocalDate.now());
            }
        }
    }

    public void writeMessage(Server server, Channel channel, String contents, Message parent) throws ClassNotFoundException, ServerAppException {
        if (server == null) {
            throw new IllegalArgumentException("mp1.Server cannot be null.");
        }
        if (channel == null) {
            throw new IllegalArgumentException("mp1.Channel cannot be null.");
        }
        if (contents == null) {
            throw new IllegalArgumentException("Contents cannot be null.");
        }

        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {

//            mp1.User has been a server member.
            if (userOnServer.getUser().equals(this) && userOnServer.getServer().equals(server) && userOnServer.getLeaveDate() == null) {


                if (userOnServer.getStatus().equals(UserStatus.MUTED)) {
                    throw new ServerAppException(String.format("mp1.User %s is muted on server %s.", this.getName(), server.getName()));
                }


                if (userOnServer.getServer().getChannels().contains(channel)) {
                    channel.addMessage(new Message(parent, this, contents));
                    return;
                } else {
                    throw new IllegalArgumentException(String.format("mp1.Channel %s doesn't exist in server %s.", channel.getName(), server.getName()));
                }
            }
        }
        throw new ServerAppException(String.format("mp1.User %s isn't a member of server %s.", this.getName(), server.getName()));
    }

    public void deleteUser() throws ClassNotFoundException, ServerAppException {
        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
            if (userOnServer.getUser().equals(this)) {
                removeFromExtent(userOnServer);
            }
        }
        for (Friendship friendship : getExtent(Friendship.class)) {
            if (friendship.getRequester().equals(this)) {
                removeFromExtent(friendship);
            }
        }
        for (User user : getExtent(User.class)) {
            removeFromExtent(user);
        }
    }

    public void createServer(String serverName) throws ClassNotFoundException, ServerAppException {
        for (Server server : getExtent(Server.class)) {
            if (server.getOwner().equals(this) && server.getName().equals(serverName)) {
                throw new ServerAppException(String.format("mp1.User %s already owns server named %s.", this.getName(), serverName));
            }
        }
        new Server(serverName, this);
    }

    @Override
    public String toString() {
        return
                String.format(
                        "Name: %s;  Email: %s;  Creation Date:  %s;", this.name, this.email, this.creationDate);
    }
}
