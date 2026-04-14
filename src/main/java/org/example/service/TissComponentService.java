package org.example.service;

import org.example.config.CrawlerConfig;
import org.example.http.HttpClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class TissComponentService {

    private final HttpClient httpClient;

    public TissComponentService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void execute() throws IOException {
        System.out.println("Iniciando Task 1: Navegando para TISS...");

        Document doc = httpClient.fetchDocument(CrawlerConfig.TISS_URL);

        Element latestVersionLink = doc.selectFirst("a:contains(TISS -)");
        if (latestVersionLink == null) {
            throw new IOException(
                    "Nao foi possivel encontrar o link da ultima versao do padrao TISS.");
        }

        String versionUrl = requireAbsoluteHref(latestVersionLink,
                "link da ultima versao do padrao TISS");
        Document versionDoc = httpClient.fetchDocument(versionUrl);

        Element comLink = versionDoc.selectFirst("a:contains(Componente)");
        if (comLink == null) {
            throw new IOException(
                    "Nao foi possivel encontrar o link do componente no detalhe da versao TISS.");
        }

        String fileUrl = requireAbsoluteHref(comLink, "link do componente TISS");
        httpClient.downloadFile(fileUrl,
                CrawlerConfig.DOWNLOAD_DIR.resolve(CrawlerConfig.COMPONENT_FILE_NAME));
    }

    private String requireAbsoluteHref(Element element, String description) throws IOException {
        String href = element.attr("abs:href");
        if (href.isBlank()) {
            throw new IOException("URL invalida para " + description + ".");
        }
        return href;
    }
}