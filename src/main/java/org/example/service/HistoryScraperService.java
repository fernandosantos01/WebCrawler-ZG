package org.example.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.example.config.CrawlerConfig;
import org.example.http.HttpClient;
import org.example.util.CsvUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class HistoryScraperService {

    private final HttpClient httpClient;

    public HistoryScraperService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void execute() throws IOException {
        System.out.println("Iniciando Task 2: Extraindo histórico de versões...");

        Document doc = httpClient.fetchDocument(CrawlerConfig.HISTORY_URL);

        Element table = doc.selectFirst("table");
        if (table == null) {
            throw new IOException(
                    "Tabela de historico de versoes nao encontrada na pagina da ANS.");
        }

        Path output = CrawlerConfig.DOWNLOAD_DIR.resolve(CrawlerConfig.HISTORY_FILE_NAME);
        writecsv(table.select("tr"), output);

        System.out.println("Historico salvo em " + CrawlerConfig.HISTORY_FILE_NAME);
    }

    private void writecsv(Elements rows, Path output) throws IOException {
        CSVFormat format = CSVFormat.Builder.create()
                .setDelimiter(CrawlerConfig.CSV_DELIMITER)
                .setHeader("Competencia", "Publica\u00e7\u00e3o", "Inicio_Vigencia")
                .build();

        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (int i = 1; i < rows.size(); i++) {
                Elements cols = rows.get(i).select("td");
                if (cols.size() < 3) continue;

                String competencia = CsvUtils.normalizeCell(cols.get(0).text());
                String publicacao = CsvUtils.normalizeCell(cols.get(1).text());
                String vigencia = CsvUtils.normalizeCell(cols.get(2).text());

                if (competencia.toLowerCase(Locale.ROOT)
                        .contains(CrawlerConfig.HISTORY_STOP_MARKER)) {
                    break;
                }

                printer.printRecord(competencia, publicacao, vigencia);
            }
        }
    }
}