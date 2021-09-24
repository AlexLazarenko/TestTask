package ua.smartfoxpro.bot.service;

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

import java.util.Optional;

@Component
@PropertySource("classpath:bot.properties")
public class BotService extends TelegramLongPollingBot {

    private static final Logger logger = LogManager.getLogger(BotService.class);

    @Value("${bot.userName}")
    private String botUserName;

    @Value("${bot.token}")
    private String botToken;

    private static final String NO_SUCH_CITY = "Sorry! No such city in our base!";

    @Autowired
    CityService service;

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
        Optional<City> thatCity;
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText() && !message.isCommand()) {
                String text = message.getText();
                thatCity = service.findByCityName(text);
                try {
                    if (thatCity.isPresent()) {
                        execute(SendMessage.builder().chatId(message.getChatId().toString()).text(thatCity.get().getText()).build());
                    } else {
                        execute(SendMessage.builder().chatId(message.getChatId().toString()).text(NO_SUCH_CITY).build());
                    }
                } catch (TelegramApiException e) {
                    logger.error(e);
                }
            }
        }
    }

}