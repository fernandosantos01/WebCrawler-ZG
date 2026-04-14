package org.example.config;

import java.nio.file.Path;
import java.nio.file.Paths;


public final class CrawlerConfig {

    public static final Path DOWNLOAD_DIR = Paths.get("Downloads", "Arquivos_padrao_TISS");

    public static final int REQUEST_TIMEOUT_MS = 30_000;

    public static final String TISS_URL =
            "https://www.gov.br/ans/pt-br/assuntos/prestadores/" +
                    "padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss";

    public static final String HISTORY_URL =
            "https://www.gov.br/ans/pt-br/assuntos/prestadores/" +
                    "padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss/" +
                    "padrao-tiss-historico-das-versoes-dos-componentes-do-padrao-tiss";

    public static final String ERROR_TABLE_URL =
            "https://www.gov.br/ans/pt-br/assuntos/prestadores/" +
                    "padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss/" +
                    "padrao-tiss-tabelas-relacionadas";

    public static final String COMPONENT_FILE_NAME = "Componente_de_Comunicacao_Atual.zip";
    public static final String HISTORY_FILE_NAME = "historico_tiss.csv";
    public static final String ERROR_FILE_NAME = "Tabela_de_Erros_ANS.xlsx";

    public static final String CSV_DELIMITER = ";";
    public static final String HISTORY_STOP_MARKER = "jan/2016"; // linha de corte (inclusive)

    private CrawlerConfig() {
    }
}
