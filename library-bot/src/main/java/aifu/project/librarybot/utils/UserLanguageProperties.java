package aifu.project.librarybot.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

@Slf4j
public class UserLanguageProperties {
    private static final String FILE_PATH = "data/user-language.properties";
    private static final Properties properties = new Properties();

    static {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
            try (InputStream input = Files.newInputStream(path)) {
                properties.load(input);
            }
        } catch (IOException e) {
            log.error("Failed to load user-language.properties file", e);
        }
    }

    public static String getLanguage(String userId) {
        return properties.getProperty(userId);
    }

    public static void setLanguage(String userId, String language) {
        properties.setProperty(userId, language);
        try (OutputStream output = Files.newOutputStream(Paths.get(FILE_PATH))) {
            properties.store(output, null);
        } catch (IOException e) {
            log.error("Failed to load user-language.properties file", e);
        }
    }

    private UserLanguageProperties() {
    }
}
