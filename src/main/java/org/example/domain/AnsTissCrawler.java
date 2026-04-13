package org.example.domain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnsTissCrawler {

    private static final Path DOWNLOAD_DIR = Paths.get("Downloads", "Arquivos_padrao_TISS");
    private static final int REQUEST_TIMEOUT_MS = 30_000;

    private static final String TISS_URL = "https://www.gov.br/ans/pt-br/assuntos/prestadores/padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss";
    private static final String HISTORY_URL = "https://www.gov.br/ans/pt-br/assuntos/prestadores/padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss/padrao-tiss-historico-das-versoes-dos-componentes-do-padrao-tiss";
    private static final String ERROR_TABLE_URL = "https://www.gov.br/ans/pt-br/assuntos/prestadores/padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss/padrao-tiss-tabelas-relacionadas";

    private static final String COMPONENT_FILE_NAME = "Componente_de_Comunicacao_Atual.zip";
    private static final String HISTORY_FILE_NAME = "historico_tiss.csv";
    private static final String ERROR_FILE_NAME = "Tabela_de_Erros_ANS.xlsx";

    public static void main(String[] args) {
        try {
            setupDirectory();

            downloadLatestTissComponent();

            scrapeHistoryTable();

            downloadErrorTable();

            System.out.println("Crawler finalizado com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro durante a execução do Crawler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setupDirectory() throws IOException {
        if (!Files.exists(DOWNLOAD_DIR)) {
            Files.createDirectories(DOWNLOAD_DIR);
        }
    }

    private static void downloadLatestTissComponent() throws IOException {
        System.out.println("Iniciando Task 1: Navegando para TISS...");

        Document doc = fetchDocument(TISS_URL);
        Element latestVersionLink = doc.selectFirst("a:contains(TISS -)");
        if (latestVersionLink == null) {
            throw new IOException("Nao foi possivel encontrar o link da ultima versao do padrao TISS.");
        }

        String versionUrl = requireAbsoluteHref(latestVersionLink, "link da ultima versao do padrao TISS");
        Document versionDoc = fetchDocument(versionUrl);

        Element comLink = versionDoc.selectFirst("a:contains(Componente)");
        if (comLink == null) {
            throw new IOException("Nao foi possivel encontrar o link do componente no detalhe da versao TISS.");
        }

        String fileUrl = requireAbsoluteHref(comLink, "link do componente TISS");
        downloadFile(fileUrl, COMPONENT_FILE_NAME);
    }

    private static void scrapeHistoryTable() throws IOException {
        System.out.println("Iniciando Task 2: Extraindo histórico de versões...");

        Document doc = fetchDocument(HISTORY_URL);

        Element table = doc.selectFirst("table");
        if (table == null) {
            throw new IOException("Tabela de historico de versoes nao encontrada na pagina da ANS.");
        }
        Elements rows = table.select("tr");

        List<String> csvData = new ArrayList<>();
        csvData.add("Competencia;Publicacao;Inicio_Vigencia");

        for (int i = 1; i < rows.size(); i++) {
            Elements cols = rows.get(i).select("td");
            if (cols.size() >= 3) {
                String competencia = normalizeCell(cols.get(0).text());
                String publicacao = normalizeCell(cols.get(1).text());
                String vigencia = normalizeCell(cols.get(2).text());

                csvData.add(toCsvCell(competencia) + ";" + toCsvCell(publicacao) + ";" + toCsvCell(vigencia));

                if (competencia.toLowerCase(Locale.ROOT).contains("jan/2016")) {
                    break;
                }
            }
        }

        Path output = DOWNLOAD_DIR.resolve(HISTORY_FILE_NAME);
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            for (String line : csvData) {
                writer.write(line + "\n");
            }
            System.out.println("Historico salvo em " + HISTORY_FILE_NAME);
        }
    }

    private static void downloadErrorTable() throws IOException {
        System.out.println("Iniciando Task 3: Baixando tabela de erros...");

        Document doc = fetchDocument(ERROR_TABLE_URL);

        Element errorLink = doc.selectFirst("a:contains(Clique aqui para baixar a tabela de erros)");
        if (errorLink == null) {
            throw new IOException("Nao foi possivel encontrar o link da tabela de erros da ANS.");
        }

        String fileUrl = requireAbsoluteHref(errorLink, "link da tabela de erros");
        downloadFile(fileUrl, ERROR_FILE_NAME);
    }

    private static void downloadFile(String fileUrl, String fileName) throws IOException {
        System.out.println("Baixando: " + fileName);
        Connection.Response response = Jsoup.connect(fileUrl)
                .ignoreContentType(true)
                .timeout(REQUEST_TIMEOUT_MS)
                .maxBodySize(0)
                .execute();

        try (FileOutputStream out = new FileOutputStream(DOWNLOAD_DIR.resolve(fileName).toFile())) {
            out.write(response.bodyAsBytes());
        }
    }

    private static Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(REQUEST_TIMEOUT_MS)
                .get();
    }

    private static String requireAbsoluteHref(Element element, String description) throws IOException {
        String href = element.attr("abs:href");
        if (href.isBlank()) {
            throw new IOException("URL invalida para " + description + ".");
        }
        return href;
    }

    private static String normalizeCell(String value) {
        return value == null ? "" : value.trim().replace('\u00A0', ' ');
    }

    private static String toCsvCell(String value) {
        String escaped = value.replace("\"", "\"\"");
        return '"' + escaped + '"';
    }
}
