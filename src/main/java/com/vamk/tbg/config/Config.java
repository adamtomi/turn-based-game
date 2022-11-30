package com.vamk.tbg.config;

import com.vamk.tbg.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final String LOCATION = "/config.txt";
    private static final String COMMENT_SIGN = "#";
    private final Map<String, String> internalConfig;

    public Config() {
        this.internalConfig = new HashMap<>();
    }

    public void load() throws IOException {
        try (InputStream in = Main.class.getResourceAsStream(LOCATION)) {
            if (in == null) throw new IOException("Config not found");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                for (String line : reader.lines().toList()) {
                    // Ignore empty lines and comments
                    if (line.isBlank() || line.startsWith(COMMENT_SIGN)) continue;

                    String[] data = line.split(":", 2);
                    if (data.length != 2) throw new RuntimeException("Detected invalid configuration entry: '%s'".formatted(line));

                    // Trim strings to remove unwanted whitespace
                    this.internalConfig.put(data[0].trim(), data[1].trim());
                }
            }
        }
    }

    public <T> T get(ConfigKey<T> key) {
        T cached = key.getCached();
        if (cached != null) return cached;

        String stored = this.internalConfig.get(key.getPath());
        T result = key.map(stored);
        key.cache(result);

        return result;
    }
}
