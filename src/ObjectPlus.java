import java.io.*;
import java.util.*;

public abstract class ObjectPlus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static Map<Class<? extends ObjectPlus>, List<ObjectPlus>> extents = new HashMap<>();


    public ObjectPlus() {
        List<ObjectPlus> extent = new ArrayList<>();

        Class<? extends ObjectPlus> type = this.getClass();
        if (extents.containsKey(type)) {
            extent = extents.get(type);
        } else {
            extent = new ArrayList<>();
            extents.put(type, extent);
        }
        extent.add(this);
    }

    public static void writeExtents(ObjectOutputStream stream) throws IOException {
        stream.writeObject(extents);
    }

    public static void readExtents(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        extents = (HashMap) stream.readObject();
    }

    public static <T>Iterable<T> getExtent(Class<T> type) throws ClassNotFoundException {
        if (extents.containsKey(type)) {
            return (Iterable<T>)  extents.get(type);
        }

        throw new ClassNotFoundException(
                String.format("%s. Stored extents: %s",
                        type.toString(),
                        extents.keySet()));
    }

    public static void showExtent(Class type) throws Exception {
        List<ObjectPlus> extent = null;

        if(extents.containsKey(type)) {
            extent = extents.get(type);
        } else {
            throw new ClassNotFoundException("Unknown class: " + type);
        }

        System.out.printf("Extent of class \"%s\": %n" ,type.getSimpleName());

        for (Object obj : extent) {
            System.out.println(obj);
        }
    }
}
