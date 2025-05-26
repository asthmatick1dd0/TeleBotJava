package com.asthmatick1dd0.TeleBotJava.service;

import com.asthmatick1dd0.TeleBotJava.model.UserResponse;
import com.asthmatick1dd0.TeleBotJava.repository.UserResponseRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportService {

    private final UserResponseRepository userResponseRepository;

    public ReportService(UserResponseRepository userResponseRepository) {
        this.userResponseRepository = userResponseRepository;
    }

    /** Asynchronized generates report using apache poi XWPF methods */
    @Async
    public CompletableFuture<File> generateReport() throws Exception {
        List<UserResponse> responses = userResponseRepository.findAll();
        System.out.println(Thread.currentThread().getName());
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();

        // Проверка на асинхронность
        //Thread.sleep(10_000);

        // Заголовки
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("Имя");
        header.addNewTableCell().setText("Email");
        header.addNewTableCell().setText("Оценка");
        // Данные
        for (UserResponse r : responses) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(r.getName());
            row.getCell(1).setText(r.getEmail());
            row.getCell(2).setText(String.valueOf(r.getScore()));
        }

        // Сохраняем временный файл
        File tempFile = File.createTempFile("report-", ".docx");
        try (FileOutputStream out = new FileOutputStream(tempFile)){
            document.write(out);
        }
        document.close();

        return CompletableFuture.completedFuture(tempFile);
    }
}
