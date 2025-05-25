package com.asthmatick1dd0.TeleBotJava.bot;

import com.asthmatick1dd0.TeleBotJava.config.BotConfig;
import com.asthmatick1dd0.TeleBotJava.service.FormService;
import com.asthmatick1dd0.TeleBotJava.service.StateService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot  {

    final BotConfig cfg;
    private final StateService stateService;
    private final FormService formService;

    public TelegramBot(BotConfig cfg, StateService stateService, FormService formService) {
        this.cfg = cfg;
        this.stateService = stateService;
        this.formService = formService;
    }

    @Override
    public String getBotUsername() {
        return cfg.getBotName();
    }


    @Override
    public String getBotToken() {
        return cfg.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/form":
                    stateService.clearState(userId);
                    sendMessage(chatId, formService.startForm(userId));
                    break;
                default: sendMessage(chatId, "Извините, я не знаю такой команды");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "Привет, " + name;

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText((textToSend));

        try {
            execute(message);
        }
        catch (TelegramApiException e) {

        }
    }
}
