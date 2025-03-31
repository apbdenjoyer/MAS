package mp1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Message extends ObjectPlus {
    private String contents;
    private final User author;
    private LocalDateTime timestamp;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy 'at' HH:mm");



    public Message(User author, String contents) {
        super();
        this.author = author;
        this.contents = contents;
        this.timestamp = LocalDateTime.now();
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

    public String getFormattedTimestamp() {
        return timestamp.format(DATE_TIME_FORMATTER);
    }


    @Override
    public String toString() {
        return String.format("%s as %s: %s", getFormattedTimestamp(), getAuthor().getName(), getContents());
    }
}
