package mp1;

import java.io.*;
import java.util.*;

public abstract class ObjectPlus implements Serializable {

    private static Map<Class<? extends ObjectPlus>, List<ObjectPlus>> extents = new HashMap<>();


    public ObjectPlus() {
        List<ObjectPlus> extent;

        Class<? extends ObjectPlus> type = this.getClass();
        if (extents.containsKey(type)) {
            extent = extents.get(type);
        } else {
            extent = new ArrayList<>();
            extents.put(type, extent);
        }
        extent.add(this);
    }

    public static void writeExtents(String filename) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename + ".bin"));
        out.writeObject(extents);

    }

    public static void readExtents(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename + ".bin"));
        extents = (HashMap) in.readObject();

    }

    public static <T> Iterable<T> getExtent(Class<T> type) throws ClassNotFoundException {
        if (extents.containsKey(type)) {
            return (Iterable<T>) extents.get(type);
        }

        throw new ClassNotFoundException(
                String.format("%s. Stored extents: %s",
                        type.toString(),
                        extents.keySet()));
    }

    public static void showExtent(Class<? extends ObjectPlus> type) throws Exception {
        List<ObjectPlus> extent = null;

        if (extents.containsKey(type)) {
            extent = extents.get(type);
        } else {
            throw new ClassNotFoundException("Unknown class: " + type);
        }

        System.out.printf("Extent of class \"%s\": %n", type.getSimpleName());

        for (Object obj : extent) {
            System.out.println(obj);
        }
    }

    public void removeFromExtent(ObjectPlus obj) {
        if (extents.containsKey(obj.getClass())) {
            extents.get(obj.getClass()).remove(obj);
        }
    }
}
