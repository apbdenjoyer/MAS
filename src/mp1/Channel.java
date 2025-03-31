package mp1;

import java.util.ArrayList;
import java.util.List;

public class Channel extends ObjectPlus {
    private String name;
    private final List<Message> messages;

    public Channel(String name) {
        super();
        this.name = name;
        this.messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String channelName) {
        this.name = channelName;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if (messages == null) {
            throw new NullPointerException("Message cannot be null.");
        }
        messages.add(message);
    }

    @Override
    public String toString() {
        return String.format("%s (%d messages)", this.name, this.messages.size());
    }
}

