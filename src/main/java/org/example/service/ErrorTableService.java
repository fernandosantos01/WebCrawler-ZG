package org.example.service;

import org.example.config.CrawlerConfig;
import org.example.http.HttpClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ErrorTableService {

    private final HttpClient httpClient;

    public ErrorTableService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void execute() throws IOException {
        System.out.println("Iniciando Task 3: Baixando tabela de erros...");

        Document doc = httpClient.fetchDocument(CrawlerConfig.ERROR_TABLE_URL);

        Element errorLink = doc.selectFirst(
                "a:contains(Clique aqui para baixar a tabela de erros)");
        if (errorLink == null) {
            throw new IOException(
                    "Nao foi possivel encontrar o link da tabela de erros da ANS.");
        }

        String fileUrl = requireAbsoluteHref(errorLink, "link da tabela de erros");
        httpClient.downloadFile(fileUrl,
                CrawlerConfig.DOWNLOAD_DIR.resolve(CrawlerConfig.ERROR_FILE_NAME));
    }

    private String requireAbsoluteHref(Element element, String description) throws IOException {
        String href = element.attr("abs:href");
        if (href.isBlank()) {
            throw new IOException("URL invalida para " + description + ".");
        }
        return href;
    }
}
