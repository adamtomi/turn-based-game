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

    public static <O extends Serializable> void writeObject(O serializable, File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(serializable);
        }
    }

    public static <O extends Serializable> O readObject(Class<O> type, File file) throws ClassNotFoundException, IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object result = in.readObject();
            if (!type.isInstance(result)) throw new IllegalStateException("Object %s is not of type %s".formatted(result, type.getName()));

            return type.cast(result);
        }
    }
}
