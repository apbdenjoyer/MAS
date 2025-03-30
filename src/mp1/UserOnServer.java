package mp1;

import java.time.LocalDate;

public class UserOnServer extends ObjectPlus {
    User user;
    Server server;
    UserStatus status;
    LocalDate joinDate;
    LocalDate leaveDate;

    public UserOnServer(User user, Server server) {
        super();
        this.user = user;
        this.server = server;
        this.status = UserStatus.CLEAN;
        this.joinDate = LocalDate.now();
        leaveDate = null;
    }

    public void setStatus(UserStatus status) throws ServerAppException {
        if (this.status == status) {
            return;
        }

        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public Server getServer() {
        return server;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public LocalDate getLeaveDate() {
        return leaveDate;
    }


    public void setLeaveDate(LocalDate leaveDate) {
        if (leaveDate == null) {
            throw new NullPointerException("Leave date cannot be null");
        }
        if (leaveDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Leave date cannot be before current date");
        }
        this.leaveDate = leaveDate;
    }

}