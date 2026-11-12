import io.javalin.Javalin;

public class HealthServer {
    public static void start() {
        // Render сам передаст порт в переменную окружения PORT
        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        // Запускаем микро-сайт
        Javalin app = Javalin.create().start(port);

        // Этот адрес будет открываться, если зайти на сайт Render
        app.get("/", ctx -> ctx.result("Bot is running!"));

        System.out.println("Веб-сервер запущен на порту " + port);
    }
}