package com.vamk.tbg.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class IOUtil {

    private IOUtil() {}

    public static <O extends Serializable> void writeObject(O serializable, String path) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(serializable);
        }
    }

    public static <O extends Serializable> O readObject(Class<O> type, String path) throws ClassNotFoundException, IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            Object result = in.readObject();
            if (!type.isInstance(type)) throw new IllegalStateException("Object %s is not of type %s".formatted(result, type.getName()));

            return type.cast(result);
        }
    }
}
