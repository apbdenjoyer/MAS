package mp1;

class Friendship extends ObjectPlus {
    private final User addressee;
    private final User requester;

    public Friendship(User requester, User addressee) throws ServerAppException {
        super();
        if (addressee == null) {
            throw new NullPointerException("Addressee cannot be null.");
        }
        if (addressee == requester) {
            throw new ServerAppException("Requester and addressee cannot be the same.");
        }
        this.requester = requester;
        this.addressee = addressee;
    }

    public User getAddressee() {
        return addressee;
    }

    public User getRequester() {
        return requester;
    }


}
