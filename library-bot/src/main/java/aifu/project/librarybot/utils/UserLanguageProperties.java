package aifu.project.librarybot.utils;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class UserLanguageProperties {

    private static final Properties properties = new Properties();
    private static final Path filePath = Paths.get("library-bot/src/main/resources/user-language.properties");

    static  {
        if (Files.exists(filePath)) {
            try (InputStream in = Files.newInputStream(filePath)) {
                properties.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getLanguage(String userId) {
        return properties.getProperty(userId, null);
    }

    @SneakyThrows
    public static void setLanguage(String userId, String language)  {
        properties.setProperty(userId, language);
        try (OutputStream out = Files.newOutputStream(filePath)) {
            properties.store(out, "User Language Settings");
        }
    }

}
