import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Bot implements LongPollingSingleThreadUpdateConsumer {


    private final TelegramClient telegramClient;

    public Bot(String botToken) {


        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update);
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(Update update) throws TelegramApiException {
        String text = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if (text.equals("/start")) {
            SendMessage msg = SendMessage.builder()
                    .chatId(chatId)
                    .text("Бот запущен. Выберите количество игроков:")
                    .replyMarkup(buildPlayersKeyboard())
                    .build();
            telegramClient.execute(msg);
        }
    }

    public enum Perk {
        BERSERK(1, "Берсерк"),
        COMMANDO(2, "Коммандос"),
        SUPPORT(3, "Боец поддержки"),
        MEDIC(4, "Медик"),
        DEMOMAN(5, "Подрывник"),
        PYRO(6, "Поджигатель"),
        SHOOTER(7, "Стрелок"),
        SNIPER(8, "Снайпер"),
        SPECIAL_FORCES(9, "Спецназ"),
        SURVIVALIST(10, "Выживальщик");

        private final int id;
        private final String title;

        Perk(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        // Вспомогательный метод, чтобы по числу (1-10) получить объект Perk
        public static Perk getById(int id) {
            for (Perk perk : values()) {
                if (perk.id == id) {
                    return perk;
                }
            }
            return null; // Если число не от 1 до 10
        }
    }

    private static final String[] ROUND_QUOTES = {
            "«Одежда человека красит, а броня спасает жизнь...иди нахуй»",
            "«Те кто остались в живых, будут завидовать, хых, МЕРТВЫМ»",
            "«Чтоб тебе так бабы давали, как ты мне зедтайм протянуть»",
            "«Когда ты идешь в ад, не забудь взять с собой дробовик»",
            "«Победа нам только снится»",
            "«RIP AND TEAR... UNTIL IT IS DONE»",
            "«Смерть — это лишь начало, а у меня большое кончало»",
            "МЕТОЛОапокалипсис",
    };

    private String checkEvents(List<Perk> perks) {
        // Считаем, сколько раз выпал каждый перк
        int countBerserk = 0; // 1 - Берсерк
        int countShooter = 0; // 7 - Стрелок
        int countSpecnaz = 0; // 9 - Спецназ
        int countSniper = 0;  // 8 - Снайпер
        int countMedic = 0;
        int countDemoman = 0;
        int countPyro = 0;
        int countSurv = 0;
        int countCom = 0;
        int countSup = 0;

        for (Perk perk : perks) {
            switch (perk) {
                case BERSERK -> countBerserk++;
                case SHOOTER -> countShooter++;
                case SPECIAL_FORCES -> countSpecnaz++;
                case SNIPER -> countSniper++;
                case MEDIC -> countMedic++;
                case DEMOMAN -> countDemoman++;
                case PYRO -> countPyro++;
                case SURVIVALIST -> countSurv++;
                case SUPPORT -> countSup++;
                case COMMANDO -> countCom++;
            }
        }

        // 1. Если ВСЕ выпавшие перки - Берсерки
        // (И игроков больше 1, чтобы это не срабатывало на одного игрока)
        if (countMedic > 1 && perks.size() > 1) {
            return "💉 ВЫЛЕЧУ ДО СМЕРТИ!";
        }
        if (countDemoman > 0 && countPyro > 0) {
            return "🧨💥ДВА БРАТА ЕБАНАТА!🎇🎆";
        }
        if (countPyro > 1 && perks.size() > 1 || countDemoman > 1 && perks.size() > 1) {
            return "🔥🔥АПОКАЛИПСИС СЕГОДНЯ!";
        }
        if (countSpecnaz > 2 && perks.size() > 1) {
            return "👮‍♂️РАСТОЛКАТЬ МИТИНГ!";
        }
        if (countBerserk > 1 && perks.size() > 1) {
            return "🩸 RIP AND TEAR! Ни одна мразь не доживет до завтра!";
        }
        if (countCom > 1 && perks.size() > 1) {
            return "ПОЗЫВНОЙ — Надежда. Потому что все надеются, что я справлюсь";
        }
        if (countSup > 1 && perks.size() > 1) {
            return "РАЗДАЧА ПАТРОНОВ НА СПАВНЕ!";
        }

        // 2. Если ВСЕ выпавшие перки - Снайперы
        if (countSniper > 2 && perks.size() > 1) {
            return "🎯 КТО САМЫЙ КОСОЙ? Тут одни снайперы...пиздец";
        }
        if (countSurv > 2 && perks.size() > 1) {
            return "🚑ЭТО ПРОГРАММА СДОХНИ ИЛИ УМРИ!";
        }
        // 3. Если в ролле есть и Берсерк (1), и Стрелок (7), и Спецназ (9) (при этом неважно, сколько там еще других перков)
        if (countBerserk > 0 && countShooter > 0 &&
                countSpecnaz > 0 || countBerserk > 0 && countShooter > 0 || countBerserk > 0 &&
                countSpecnaz > 0 || countShooter > 0 && countSpecnaz > 0) {
            return "🏃‍♂️ БЫСТРЫЕ НОГИ, ПИЗДЫ НЕ ПОЛУЧАТ!";
        }

        // Если ничего не совпало - ивента нет
        return null;
    }

    private void handleCallback(CallbackQuery query) throws TelegramApiException {
        String data = query.getData();
        long chatId = query.getMessage().getChatId();
        int messageId = query.getMessage().getMessageId();

        // Подтверждаем нажатие (убираем "часики" на кнопке)
        telegramClient.execute(new AnswerCallbackQuery(query.getId()));

        if (data.startsWith("players_")) {
            int players = Integer.parseInt(data.substring("players_".length()));

            // 1. Генерируем перки
            List<Perk> perks = new ArrayList<>();
            for (int i = 0; i < players; i++) {
                int randomId = ThreadLocalRandom.current().nextInt(1, 11);
                perks.add(Perk.getById(randomId));
            }

            // 2. Проверяем на ивенты
            String eventMessage = checkEvents(perks);

            // 3. Выбираем случайную цитату
            int quoteIndex = ThreadLocalRandom.current().nextInt(ROUND_QUOTES.length);
            String randomQuote = ROUND_QUOTES[quoteIndex];

            // 4. Собираем текст сообщения
            StringBuilder sb = new StringBuilder();
            sb.append("Результат для ").append(players)
                    .append(players == 1 ? " игрока:" : " игроков:").append("\n\n");

            for (int i = 0; i < perks.size(); i++) {
                sb.append("Игрок ").append(i + 1).append(": ").append(perks.get(i).getTitle()).append("\n");
            }

            if (eventMessage != null) {
                sb.append("\n🔥 ВНИМАНИЕ! ИВЕНТ! 🔥\n");
                sb.append(eventMessage).append("\n");
            }

            sb.append("\n💬 " + "Цитата раунда: ").append(randomQuote);
            sb.append("\n\nВыберите количество игроков для нового раунда:"); // <-- Подсказка для игрока

            // 5. РЕДАКТИРУЕМ сообщение и ДОБАВЛЯЕМ кнопки прямо в него!
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(sb.toString())
                    .replyMarkup(buildPlayersKeyboard()) // <-- ВОТ ОНИ, КНОПКИ!
                    .build();

            telegramClient.execute(sendMessage);

            // Блок с SendMessage again УДАЛЕН, так как кнопки теперь в самом сообщении
        }
    }

    private InlineKeyboardMarkup buildPlayersKeyboard() {
        // Кнопки 1..6 в два ряда: [1 2 3] и [4 5 6]
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (int row = 0; row < 2; row++) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (int col = 1; col <= 3; col++) {
                int num = row * 3 + col;
                buttons.add(InlineKeyboardButton.builder()
                        .text(String.valueOf(num))
                        .callbackData("players_" + num)
                        .build());
            }
            rows.add(new InlineKeyboardRow(buttons));
        }
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

}