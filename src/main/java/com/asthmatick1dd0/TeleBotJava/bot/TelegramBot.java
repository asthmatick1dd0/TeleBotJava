package com.asthmatick1dd0.TeleBotJava.bot;

import com.asthmatick1dd0.TeleBotJava.config.BotConfig;
import com.asthmatick1dd0.TeleBotJava.service.FormService;
import com.asthmatick1dd0.TeleBotJava.service.ReportService;
import com.asthmatick1dd0.TeleBotJava.service.StateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot  {

    final BotConfig cfg;
    private final StateService stateService;
    private final FormService formService;
    private final ReportService reportService;

    public TelegramBot(BotConfig cfg, StateService stateService, FormService formService, ReportService reportService) {
        this.cfg = cfg;
        this.stateService = stateService;
        this.formService = formService;
        this.reportService = reportService;
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
                case "/report":
                    stateService.clearState(userId);
                    sendMessage(chatId, "Генерирую отчёт, пожалуйста, подождите...");
                    CompletableFuture<File> reportFuture = null;
                    try {
                        reportFuture = reportService.generateReport();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    reportFuture.thenAccept(file -> sendReport(chatId, file));
                    break;
                default:
                    // Обработка шага формы, если пользователь в процессе заполнения
                    if (stateService.getState(userId) != StateService.State.NONE) {
                        String reply = formService.handleSteps(userId, messageText);
                        sendMessage(chatId, reply);
                    } else {
                        sendMessage(chatId, "Я не знаю такой команды. Напишите /start.");
                    }
            }
        }
    }

    /** Method for /start command */
    private void startCommandReceived(long chatId, String name) {

        String answer = "Привет, " + name + "! Для того, чтобы заполнить форму опроса, напиши /form.\n" +
                "Если хочешь посмотреть отчёт по результатам опроса, то напиши /report.";
        log.info("Replied to user " + name + " with /start message");
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
            log.error("Error occurred while sending message: " + e.getMessage());
        }
    }

    private void sendReport(Long chatId, File file) {
        try {

            execute(SendDocument.builder()
                    .chatId(chatId.toString())
                    .document(new org.telegram.telegrambots.meta.api.objects.InputFile(file, "report.docx"))
                    .caption("Ваш отчёт.")
                    .build());
            file.delete();
        } catch (Exception e) {
            sendMessage(chatId, "Не удалось отправить отчёт" + e.getMessage());
            log.error("Error occurred while sending report: " + e.getMessage());
        }
    }
}
