package mp1;

import java.util.List;

public class Channel extends ObjectPlus {
    private String name;
    private List<Message> messages;

    public Channel(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String channelName) {
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if (messages == null) {
            throw new NullPointerException("mp1.Message cannot be null.");
        }
        messages.add(message);
    }


}

