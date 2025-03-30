import java.time.LocalDateTime;
import java.util.List;

public class Channel extends ObjectPlus {
    String name;
    List<Message> messages;
}

class Message extends ObjectPlus {
    String contents;
    User author;
    LocalDateTime timestamp;
    boolean isEdited;

//    null if no parent
    Message parentMessage;
    List<Message> replies;

    public Message(Message parentMessage, User author, String contents) {
        super();
        isEdited = false;
        this.author = author;
        this.contents = contents;
        this.timestamp = LocalDateTime.now();

        if (parentMessage != null) {
            parentMessage.addReply(this);
        }
    }

    private void addReply(Message message) {
        if (!this.replies.contains(message)) {
            this.replies.add(message);
        }
    }

    public void editMessage(String newContents) {
        if (newContents == null) {
            throw new NullPointerException("Message contents cannot be null");
        }
        if (newContents.isBlank()) {
            this.contents = "<This message has been deleted>";
            this.timestamp = LocalDateTime.now();
        }
        if (!newContents.equals(this.contents)) {
            isEdited = true;
            this.contents = newContents;
            this.timestamp = LocalDateTime.now();
        }
    }
}
