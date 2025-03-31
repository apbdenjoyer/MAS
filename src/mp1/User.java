package mp1;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
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
// 🙃
        this.name = "";

        if (!isNameValid(name)) {
            throw new ServerAppException("Name is invalid. Please make sure it contains between 3 and 20 allowed characters: (letters, numbers, underscores)");
        }
        if (!isNameAvailable(name)) {
            throw new ServerAppException(String.format("Name %s is already in use.", name));
        }

        if (!isEmailValid(email)) {
            throw new ServerAppException(String.format("Email %s is not valid.", email));
        }
        if (!isEmailAvailable(email)) {
            throw new ServerAppException(String.format("Email %s is already in use.", email));
        }

        setName(name);
        setEmail(email);
        setHashedPassword(password);
        this.creationDate = LocalDate.now();

//        allow user to change email once from the get-go
        this.lastEmailChangeDate = LocalDate.now().minusDays(7);
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
            if (user != null) {
                if (user.getName().equals(name) && !user.equals(this)) {
                    return false;
                }
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

    private void setName(String name) {
        if (isNameValid(name)) {
            this.name = name;
        }
    }

    public String getEmail() {
        return email;
    }

    public void changeEmail(String email) throws ClassNotFoundException, ServerAppException {
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
        this.email = email;
        this.lastEmailChangeDate = LocalDate.now();
    }

    public void setEmail(String email) {
        if (isEmailValid(email)) {
            this.email = email;
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

    public void addFriend(String username) throws ServerAppException {

        User foundUser = null;
        try {
            for (User user : getExtent(User.class)) {
                if (user.getName().equals(username)) {
                    foundUser = user;
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (foundUser == null) {
            throw new ServerAppException(String.format("User %s doesn't exist.", username));
        }

        try {
            for (Friendship friendship : getExtent(Friendship.class)) {
                if (friendship.getRequester().equals(this) && friendship.getAddressee().equals(foundUser) || friendship.getAddressee().equals(this) && friendship.getRequester().equals(foundUser)) {
                    throw new ServerAppException(String.format("Friendship between %s and %s already exists.", this.getName(), foundUser.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
        }
        new Friendship(this, foundUser);
    }


    public void removeFriend(String username) throws ClassNotFoundException, ServerAppException {

        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null.");
        }

        try {
            User foundUser = null;

            for (User user : getExtent(User.class)) {
                if (user.getName().equals(username)) {
                    foundUser = user;
                    break;
                }
            }

            if (foundUser == null) {
                throw new ServerAppException(String.format("User %s doesn't exist.", username));
            }

            Iterator<Friendship> iterator = getExtent(Friendship.class).iterator();
            while (iterator.hasNext()) {
                Friendship friendship = iterator.next();
                if (friendship.getRequester().equals(this) && friendship.getAddressee().equals(foundUser) || friendship.getAddressee().equals(this) && friendship.getRequester().equals(foundUser)) {
                    iterator.remove();
                    return;
                }
            }
        } catch (ClassNotFoundException _) {
        } catch (ServerAppException e) {
            throw new ServerAppException(String.format("Friendship between %s and %s doesn't exist.", this.getName(), username));
        }
    }

    public void joinServer(String serverPath) throws ClassNotFoundException, ServerAppException {
        if (serverPath == null) {
            throw new IllegalArgumentException("Server path cannot be null.");
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
            throw new ServerAppException(String.format("Server %s made by %s does not exist.", serverName, ownerName));
        }

        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
            if (userOnServer.getServer().equals(foundServer) && userOnServer.getUser().equals(this)) {
                throw new ServerAppException(String.format("User %s has already joined server %s by %s.", this.getName(), foundServer.getName(), ownerName));
            }
        }
        new UserOnServer(this, foundServer);
    }

    public void leaveServer(Server server) throws ClassNotFoundException {
        if (server == null) {
            throw new IllegalArgumentException("Server cannot be null.");
        }

        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
            if (userOnServer.getUser().equals(this) && userOnServer.getServer().equals(server)) {
                userOnServer.setLeaveDate(LocalDate.now());
            }
        }
    }

    public void writeMessage(Server server, Channel channel, String contents, Message parent) throws ClassNotFoundException, ServerAppException {
        if (server == null) {
            throw new IllegalArgumentException("Server cannot be null.");
        }
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null.");
        }
        if (contents == null) {
            throw new IllegalArgumentException("Contents cannot be null.");
        }

        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {

//            User has been a server member.
            if (userOnServer.getUser().equals(this) && userOnServer.getServer().equals(server) && userOnServer.getLeaveDate() == null) {


                if (userOnServer.getStatus().equals(UserStatus.MUTED)) {
                    throw new ServerAppException(String.format("User %s is muted on server %s.", this.getName(), server.getName()));
                }


                if (userOnServer.getServer().getChannels().contains(channel)) {
                    channel.addMessage(new Message(parent, this, contents));
                    return;
                } else {
                    throw new IllegalArgumentException(String.format("Channel %s doesn't exist in server %s.", channel.getName(), server.getName()));
                }
            }
        }
        throw new ServerAppException(String.format("User %s isn't a member of server %s.", this.getName(), server.getName()));
    }

//
//    public void deleteUser() throws ClassNotFoundException, ServerAppException {
//        for (UserOnServer userOnServer : getExtent(UserOnServer.class)) {
//            if (userOnServer.getUser().equals(this)) {
//                removeFromExtent(userOnServer);
//            }
//        }
//        for (Friendship friendship : getExtent(Friendship.class)) {
//            if (friendship.getRequester().equals(this)) {
//                removeFromExtent(friendship);
//            }
//        }
//        for (User user : getExtent(User.class)) {
//            removeFromExtent(user);
//        }
//    }

    public void createServer(String serverName) throws ServerAppException {
        if (serverName == null) {
            throw new IllegalArgumentException("Server name cannot be null.");
        }

        try {
            for (Server server : getExtent(Server.class)) {
                if (server.getOwner().equals(this) && server.getName().equals(serverName)) {
                    throw new ServerAppException(String.format("User %s already owns server named %s.", this.getName(), serverName));
                }
            }
        } catch (ClassNotFoundException _) {
        }
        new Server(serverName, this);
    }

    public void removeServer(String serverName) throws ServerAppException {
        if (serverName == null) {
            throw new IllegalArgumentException("Server name cannot be null.");
        }

        try {
            Server foundServer = null;

            for (Server server : getExtent(Server.class)) {
                if (server.getOwner().equals(this) && server.getName().equals(serverName)) {
                    foundServer = server;
                    break;
                }
            }

            if (foundServer == null) {
                throw new ServerAppException(String.format("User %s doesn't own a server named %s.", this.getName(), serverName));
            }

            Iterator<UserOnServer> iterator = getExtent(UserOnServer.class).iterator();
            while (iterator.hasNext()) {
                UserOnServer userOnServer = iterator.next();
                if (userOnServer.getServer().equals(foundServer)) {
                    iterator.remove();
                }
            }

            removeFromExtent(foundServer);
        } catch (ClassNotFoundException _) {
        }
    }


    @Override
    public String toString() {
        return
                String.format(
                        "Name: %s;  Email: %s;  Creation Date:  %s;", this.name, this.email, this.creationDate);
    }
}
