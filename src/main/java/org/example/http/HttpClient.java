package org.example.http;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class HttpClient {

    private final int timeoutMs;

    public HttpClient(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Document fetchDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(timeoutMs)
                .get();
    }

    public void downloadFile(String fileUrl, Path destPath) throws IOException {
        System.out.println("Baixando: " + destPath.getFileName());

        Connection.Response response = Jsoup.connect(fileUrl)
                .ignoreContentType(true)
                .timeout(timeoutMs)
                .maxBodySize(0)
                .execute();

        try (FileOutputStream out = new FileOutputStream(destPath.toFile())) {
            out.write(response.bodyAsBytes());
        }
    }
}