# 🏥 ANS TISS Crawler

> Crawler automatizado para coleta e download de dados do Padrão TISS (Troca de Informação de Saúde Suplementar) diretamente do portal da ANS.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Pré-requisitos](#pré-requisitos)
- [Configuração e Instalação](#configuração-e-instalação)
- [Como Usar](#como-usar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Estrutura de Saída](#estrutura-de-saída)
- [Detalhes Técnicos](#detalhes-técnicos)
- [Tratamento de Erros](#tratamento-de-erros)
- [Contribuindo](#contribuindo)

---

## 📖 Sobre o Projeto

O **ANS TISS Crawler** é uma aplicação Java que automatiza a coleta de documentos e dados do [Portal da ANS](https://www.gov.br/ans) referentes ao Padrão TISS. Ele realiza três tarefas principais em sequência: baixa o componente de comunicação mais recente, extrai o histórico de versões em formato CSV e obtém a tabela de erros oficial em XLSX.

---

## ✨ Funcionalidades

| # | Tarefa | Descrição | Saída |
|---|--------|-----------|-------|
| 1 | **Download do Componente TISS** | Navega até a versão mais recente do padrão e baixa o componente de comunicação | `Componente_de_Comunicacao_Atual.zip` |
| 2 | **Extração do Histórico de Versões** | Realiza scraping da tabela de histórico de versões (até Jan/2016) | `historico_tiss.csv` |
| 3 | **Download da Tabela de Erros** | Localiza e baixa a tabela oficial de erros da ANS | `Tabela_de_Erros_ANS.xlsx` |

---

## 🔧 Pré-requisitos

- **Java** 11 ou superior
- **Gradle** (Wrapper incluso no projeto)
- Conexão com a internet (acesso ao portal `gov.br`)

---

## ⚙️ Configuração e Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

### 2. Adicione as dependências no `build.gradle`

```groovy
dependencies {
    implementation 'org.jsoup:jsoup:1.22.1'
    implementation 'org.apache.commons:commons-csv:1.12.0'
}
```

### 3. Compile o projeto

```bash
./gradlew build
```

---

## ▶️ Como Usar

Execute a classe principal diretamente pelo Gradle:

```bash
./gradlew run
```

Ou, se preferir executar o JAR gerado:

```bash
java -cp build/libs/seu-projeto.jar org.example.AnsTissCrawler
```

O crawler irá rodar as três tarefas automaticamente e exibir o progresso no console:

```
Iniciando Task 1: Navegando para TISS...
Baixando: Componente_de_Comunicacao_Atual.zip
Iniciando Task 2: Extraindo histórico de versões...
Historico salvo em historico_tiss.csv
Iniciando Task 3: Baixando tabela de erros...
Baixando: Tabela_de_Erros_ANS.xlsx
Crawler finalizado com sucesso!
```

---

## 🗂️ Estrutura do Projeto

```
src/main/java/org/example/
│
├── AnsTissCrawler.java          # Ponto de entrada — orquestra as tasks
│
├── config/
│   └── CrawlerConfig.java       # URLs, paths, timeouts e constantes globais
│
├── http/
│   └── HttpClient.java          # Requisições HTTP e downloads de arquivos
│
├── service/
│   ├── TissComponentService.java   # Task 1: download do componente TISS
│   ├── HistoryScraperService.java  # Task 2: scraping do histórico de versões
│   └── ErrorTableService.java      # Task 3: download da tabela de erros
│
└── util/
    └── CsvUtils.java            # Normalização de células para o CSV
```

Cada pacote tem uma responsabilidade única e bem definida:

- **`config`** — único ponto de alteração para URLs e nomes de arquivo
- **`http`** — toda comunicação de rede isolada, facilitando testes com mocks
- **`service`** — regras de negócio de cada task, independentes entre si
- **`util`** — funções utilitárias reutilizáveis sem dependência de estado

---

## 📁 Estrutura de Saída

Todos os arquivos são salvos automaticamente na pasta:

```
Downloads/
└── Arquivos_padrao_TISS/
    ├── Componente_de_Comunicacao_Atual.zip   # Componente de comunicação TISS mais recente
    ├── historico_tiss.csv                    # Histórico de versões (desde Jan/2016)
    └── Tabela_de_Erros_ANS.xlsx              # Tabela oficial de erros ANS
```

> 📌 O diretório é criado automaticamente caso não exista.

### Formato do CSV (`historico_tiss.csv`)

```csv
Competencia;Publicação;Inicio_Vigencia
"Janeiro/2024";"15/01/2024";"01/02/2024"
...
```

---

## 🔍 Detalhes Técnicos

### URLs Monitoradas

| Fonte | URL |
|-------|-----|
| Padrão TISS (atual) | `gov.br/.../padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss` |
| Histórico de Versões | `gov.br/.../padrao-tiss-historico-das-versoes-dos-componentes-do-padrao-tiss` |
| Tabela de Erros | `gov.br/.../padrao-tiss-tabelas-relacionadas` |

### Configurações Internas

| Parâmetro | Valor |
|-----------|-------|
| Timeout de requisição | `30.000 ms` |
| Tamanho máximo do body | Ilimitado (`0`) |
| Encoding do CSV | `UTF-8` |
| Separador CSV | `;` (ponto e vírgula) |
| Linha de corte do histórico | `Jan/2016` (inclusive) |

### Dependências

```groovy
implementation 'org.jsoup:jsoup:1.22.1'
implementation 'org.apache.commons:commons-csv:1.12.0'
```

| Biblioteca | Uso |
|------------|-----|
| [Jsoup](https://jsoup.org/) | Requisições HTTP, parsing HTML e download de arquivos binários |
| [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) | Geração de CSV com escape automático via `CSVPrinter` e `CSVFormat` |

---

## ⚠️ Tratamento de Erros

O crawler lança `IOException` com mensagens descritivas nos seguintes cenários:

| Situação | Mensagem de Erro |
|----------|-----------------|
| Link da versão TISS não encontrado | `"Nao foi possivel encontrar o link da ultima versao do padrao TISS."` |
| Link do componente não encontrado | `"Nao foi possivel encontrar o link do componente no detalhe da versao TISS."` |
| Tabela de histórico não encontrada | `"Tabela de historico de versoes nao encontrada na pagina da ANS."` |
| Link da tabela de erros não encontrado | `"Nao foi possivel encontrar o link da tabela de erros da ANS."` |
| URL inválida em qualquer etapa | `"URL invalida para [descrição]."` |

> ⚡ **Atenção:** Caso a estrutura HTML do portal da ANS seja alterada, pode ser necessário atualizar os seletores CSS nas classes de serviço correspondentes.

---

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/minha-feature`)
3. Commit suas alterações (`git commit -m 'feat: adiciona nova feature'`)
4. Push para a branch (`git push origin feature/minha-feature`)
5. Abra um Pull Request

---

## 📄 Licença

Distribuído sob a licença MIT. Consulte o arquivo `LICENSE` para mais informações.

---

<div align="center">
  <sub>Desenvolvido para automatizar a coleta de dados do Padrão TISS — ANS</sub>
</div>