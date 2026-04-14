package org.example;

import org.example.config.CrawlerConfig;
import org.example.http.HttpClient;
import org.example.service.ErrorTableService;
import org.example.service.HistoryScraperService;
import org.example.service.TissComponentService;

import java.io.IOException;
import java.nio.file.Files;

public class AnsTissCrawler {

    public static void main(String[] args) {
        try {
            setupDirectory();

            HttpClient httpClient = new HttpClient(CrawlerConfig.REQUEST_TIMEOUT_MS);

            new TissComponentService(httpClient).execute();
            new HistoryScraperService(httpClient).execute();
            new ErrorTableService(httpClient).execute();

            System.out.println("Crawler finalizado com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro durante a execução do Crawler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setupDirectory() throws IOException {
        if (!Files.exists(CrawlerConfig.DOWNLOAD_DIR)) {
            Files.createDirectories(CrawlerConfig.DOWNLOAD_DIR);
        }
    }
}