package com.vamk.tbg.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            if (!type.isInstance(result)) throw new IllegalStateException("Object %s is not of type %s".formatted(result, type.getName()));

            return type.cast(result);
        }
    }

    public static boolean fileExists(String path) {
        Path file = Paths.get(path);
        return Files.exists(file);
    }

    public static void remove(String path) throws IOException {
        Path file = Paths.get(path);
        Files.deleteIfExists(file);
    }
}
