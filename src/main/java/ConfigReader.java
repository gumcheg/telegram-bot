import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    public static String getToken() {
        // 1. Сначала пробуем взять токен из переменной окружения (как на Render)
        String envToken = System.getenv("BOT_TOKEN");
        if (envToken != null && !envToken.isBlank()) {
            return envToken;
        }

        // 2. Если переменной нет, ищем файл config.properties
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            String fileToken = props.getProperty("BOT_TOKEN");
            if (fileToken != null && !fileToken.isBlank()) {
                return fileToken;
            }
        } catch (IOException e) {
            System.err.println("Файл config.properties не найден, и переменная BOT_TOKEN не задана.");
        }

        return null;
    }
}