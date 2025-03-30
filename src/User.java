import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User extends ObjectPlus {
    String displayName;
    String email;
    int hashedPassword;
    LocalDate creationDate;
    LocalDate deletionDate;
    LocalDate lastNameChangeDate;

    Set<User> friendList = new HashSet<>();
    Set<UserOnServer> servers = new HashSet<>();


    public User(String displayName, String email, String password) throws UserException, ClassNotFoundException {
        super();
        setDisplayName(displayName);
        setEmail(email);
        setHashedPassword(password);
        this.creationDate = LocalDate.now();
    }

    //    In case user doesn't provide an email address, create the name based on the email's username part
    public User(String email, String password) throws ClassNotFoundException, UserException {
        this(email.split("@")[0], email, password);
    }

    public boolean addFriend(User addressee) throws ClassNotFoundException {
        for (User user : getExtent(User.class)) {
            if (user.equals(addressee)) {
                friendList.add(addressee);
                return true;
            }
        }
        return false;       //user not found
    }

    private boolean isNameValid(String name) {
        if (name == null || name.isEmpty()) {
            throw new NullPointerException("Name cannot be empty");
        }

        return name.matches("\\w{3,20}");
    }


    private boolean isEmailValid(String email) {
        return email.matches("^\\w+@\\w+");
    }

    private boolean isEmailAvailable(String email) throws ClassNotFoundException {
        for (User user : getExtent(User.class)) {
            if (user.displayName.equals(email)) {
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


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) throws UserException {
        long daysFromLastNameChange = ChronoUnit.DAYS.between(lastNameChangeDate, LocalDate.now());
        if (daysFromLastNameChange < 30) {
            throw new UserException(String.format("You cannot change your name now. Please try again in %s days." , 30 - daysFromLastNameChange));
        }
        if (!isNameValid(displayName)) {
            throw new NullPointerException("Name cannot be empty");
        }

        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ClassNotFoundException, UserException {
        if (!isEmailValid(email)) {
            throw new NullPointerException("Email is not valid.");
        }
        if (!isEmailAvailable(email)) {
            throw new UserException(String.format("Email %s is already used." , email));
        }
        if (isEmailSame(email)) {
            throw new UserException(String.format("User's email: %s cannot be the same." , email));
        }
    }

    public int getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String password) {
        if (isPasswordSame(password)) {
            throw new NullPointerException("Password cannot be the same.");
        }

        this.hashedPassword = hashPassword(password);
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate() {
        if (deletionDate != null && deletionDate.isBefore(creationDate)) {
            throw new DateTimeException("Deletion date cannot be before creation date.");
        }
        ;
        this.deletionDate = LocalDate.now();
    }

    public LocalDate getLastNameChangeDate() {
        return lastNameChangeDate;
    }

    public void setLastNameChangeDate() {
        if (lastNameChangeDate != null && lastNameChangeDate.isBefore(creationDate)) {
            throw new DateTimeException("Last name change date cannot be before creation date.");
        }
        this.lastNameChangeDate = LocalDate.now();
    }

    public Period getAccountAge(){
        return Period.between(creationDate, LocalDate.now());
    }
}

class UserException extends Exception {
    protected UserException(String message) {
        super(message);
    }


}
