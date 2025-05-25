package com.asthmatick1dd0.TeleBotJava.service;

import com.asthmatick1dd0.TeleBotJava.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot  {

    final BotConfig cfg;

    public TelegramBot(BotConfig cfg) {
        this.cfg = cfg;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                default: sendMessage(chatId, "Извините, я не знаю такой команды");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return cfg.getBotName();
    }


    @Override
    public String getBotToken() {
        return cfg.getToken();
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
