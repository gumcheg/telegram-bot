import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) throws Exception {
        String botToken = System.getenv("BOT_TOKEN"); // токен бота из @BotFather
        if (botToken == null) {
            throw new IllegalStateException("Set BOT_TOKEN environment variable");
        }

        try (TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication()) {
            app.registerBot(botToken, new Bot(botToken));
            System.out.println("Bot started!");
            Thread.currentThread().join(); // держим бота запущенным
        }
    }
}