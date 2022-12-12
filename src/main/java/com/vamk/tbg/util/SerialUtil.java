package com.vamk.tbg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SerialUtil {

    private SerialUtil() {}

    /**
     * Serializes the specified object to the specified file.
     *
     * @param serializable The object to serialize
     * @param file The file to serialize the object to
     * @throws IOException If this operation fails
     */
    public static <O extends Serializable> void writeObject(O serializable, File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(serializable);
        }
    }

    /**
     * Deserializes an object from the specified file and tries to
     * convert it to the specified class.
     *
     * @param type The type to convert the object to
     * @param file The file to read the object from
     * @throws ClassNotFoundException If the class doesn't exist
     * @throws IOException If the desizialization fails
     * @throws IllegalStateException If the object cannot be cast to the specified type
     * @return The deserialized object
     */
    public static <O extends Serializable> O readObject(Class<O> type, File file) throws ClassNotFoundException, IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object result = in.readObject();
            if (!type.isInstance(result)) throw new IllegalStateException("Object %s is not of type %s".formatted(result, type.getName()));

            return type.cast(result);
        }
    }
}
