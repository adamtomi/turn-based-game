package com.vamk.tbg.config;

import com.vamk.tbg.Main;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple configuration class. The parsed entries are
 * stored in the internalConfig map.
 */
@Singleton
public class Config {
    private static final String LOCATION = "/config.txt";
    private static final String COMMENT_SIGN = "#";
    private static final String SEPARATOR = ":";
    /* Store parsed entries in this map */
    private final Map<String, String> internalConfig;


    @Inject
    public Config() {
        this.internalConfig = new HashMap<>();
    }

    /**
     * Loads the configuration file. All lines, that
     * are either blank or start with a comment sign
     * are ignored, the rest are parsed (well, at least
     * it tries to parse them, which could fail if the
     * format of the line is wrong for example).
     * The value is allowed to contain the separator
     * character as the line will be split into two
     * at the first occurence of that character, but
     * no more split operations will be performed.
     *
     * @throws IOException If an IO error occures
     * @throws RuntimeException If the line is invalid
     * @throws IllegalStateException If duplicate keys are present
     */
    public void load() throws IOException {
        try (InputStream in = Main.class.getResourceAsStream(LOCATION)) {
            if (in == null) throw new IOException("Config not found");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                for (String line : reader.lines().toList()) {
                    // Ignore empty lines and comments
                    if (line.isBlank() || line.startsWith(COMMENT_SIGN)) continue;

                    String[] data = line.split(SEPARATOR, 2);
                    if (data.length != 2) throw new RuntimeException("Detected invalid configuration entry: '%s'".formatted(line));

                    String key = data[0].trim();
                    // Don't allow duplicate keys
                    if (this.internalConfig.containsKey(key)) throw new IllegalStateException("Duplicate entries found for key '%s' ".formatted(key));

                    this.internalConfig.put(key, data[1].trim());
                }
            }
        }
    }

    /**
     * Returns the parsed value mapped to the
     * specified configuration key. The conversion
     * process is done by the keys themselves. If the
     * key has a cached value, return that, otherwise
     * it'll transform the stored string value into
     * the actual value (that is type correct),
     * then cache it and return it.
     */
    public <T> T get(ConfigKey<T> key) {
        T cached = key.getCached();
        if (cached != null) return cached;

        String stored = this.internalConfig.get(key.getPath());
        T result = key.map(stored);
        key.cache(result);

        return result;
    }
}
