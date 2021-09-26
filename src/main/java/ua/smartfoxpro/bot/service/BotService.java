package ua.smartfoxpro.bot.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.smartfoxpro.bot.pojo.City;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.smartfoxpro.bot.storage.CityNameStorage;
import ua.smartfoxpro.bot.validator.ContentValidator;
import ua.smartfoxpro.bot.validator.SymbolValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@PropertySource("classpath:bot.properties")
public class BotService extends TelegramLongPollingBot {

    private static final Logger logger = LogManager.getLogger(BotService.class);
    CityNameStorage storage = CityNameStorage.getInstance();
    @Autowired
    CityService service;
    @Autowired
    SymbolValidator symbolValidator;
    @Autowired
    ContentValidator contentValidator;

    @Value("${bot.userName}")
    private String botUserName;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            try {
                handleMessage(update.getMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String action = callbackQuery.getData();
        switch (action) {
            case "END":
                storage.clearStorage();
                try {
                    executeTextMessage(message, "Спасибо за игру");
                } catch (TelegramApiException e) {
                    logger.error(e);
                }
                break;
        }
    }

    private void handleMessage(Message message) throws TelegramApiException {
        // handle command
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/start_city_game":
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        buttons.add(Arrays.asList(InlineKeyboardButton.builder().text("End game")
                                .callbackData("END").build()));
                        List<City> cities = service.getAllCities();
                        storage.fillAllNames(cities);
                        String firstWord = storage.getRandomElement();
                        storage.addSystemAnswer(firstWord);
                        execute(SendMessage.builder().text(firstWord + ". Please input your city")
                                .chatId(message.getChatId().toString())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .build());
                        return;
                }
            }
        }
        if (message.hasText()) {
            String word = message.getText();
            if (word != null && !word.isBlank() && contentValidator.isStringMatches(word)) {
                if (!storage.findCity(word)) {
                    String lastSystemWord = storage.getLastSystemWord();
                    if (symbolValidator.validate(lastSystemWord, word)) {
                        storage.addUserAnswer(word);
                        String lastUserWordSymbol = word.substring(word.length() - 1);
                        Optional<String> nextWord = storage.getNextSystemWord(lastUserWordSymbol);
                        if (nextWord.isPresent()) {
                            executeTextMessage(message, nextWord.get());
                            storage.addSystemAnswer(nextWord.get());
                        } else {
                            storage.clearStorage();
                            executeTextMessage(message, "You win!");
                        }
                    } else {
                        executeTextMessage(message, "City name must starts with " +
                                lastSystemWord.substring(lastSystemWord.length() - 1));
                    }
                } else {
                    executeTextMessage(message, "This city is already been! Please insert another!");
                }
            } else {
                executeTextMessage(message, "Please, insert a correct data!");
            }
        }
    }

    private void executeTextMessage(Message message, String text) throws TelegramApiException {
        execute(
                SendMessage.builder()
                        .text(text)
                        .chatId(message.getChatId().toString())
                        .build());
    }
}
