package mp1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends ObjectPlus {
    String contents;
    User author;
    LocalDateTime timestamp;
    boolean isEdited;

    //    null if no parent
    Message parentMessage;

    public Message(Message parentMessage, User author, String contents) {
        super();

        if (parentMessage != null) {
            this.parentMessage = parentMessage;
        }

        isEdited = false;
        this.author = author;
        this.contents = contents;
        this.timestamp = LocalDateTime.now();
    }

    public Message(User author, String contents) {
        super();
        isEdited = false;
        this.author = author;
        this.contents = contents;
        this.timestamp = LocalDateTime.now();
    }

    public void editMessage(String newContents) {
        if (newContents == null) {
            throw new IllegalArgumentException("Message contents cannot be null");
        }

        if (newContents.isBlank()) {
            this.contents = "<This message has been deleted>";
            this.timestamp = LocalDateTime.now();
        } else if (!newContents.equals(this.contents)) {
            isEdited = true;
            this.contents = newContents;
            this.timestamp = LocalDateTime.now();
        }
    }

    public List<Message> getReplies() throws ClassNotFoundException {
        List<Message> replies = new ArrayList<>();
        for (Message m : getExtent(Message.class)) {
            if (m.getParentMessage().equals(this)) {
                replies.add(m);
            }
        }
        return replies;
    }

    public String getContents() {
        return contents;
    }

    public User getAuthor() {
        return author;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public Message getParentMessage() {
        return parentMessage;
    }
}
