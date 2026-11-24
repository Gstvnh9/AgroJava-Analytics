
package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MapaUmidade {

    private static final String ARQUIVO =
            "mapa_umidade.csv";

    // RF09 - Classificar Talhões
    public static String classificarTalhao(int umidade) {

        if (umidade < 20) {
            return "Crítico";
        } else if (umidade <= 30) {
            return "Atenção";
        } else {
            return "Adequado";
        }
    }

    // RF08 e RF09 - Salvar Mapa de Umidade
    public static void salvarMapaUmidade(int[][] fazenda)
            throws IOException {

        try (BufferedWriter escritor = new BufferedWriter(
                new FileWriter(ARQUIVO,
                        StandardCharsets.UTF_8, false))) {

            escritor.write("talhao,umidade,status");
            escritor.newLine();

            for (int i = 0; i < fazenda.length; i++) {

                for (int j = 0; j < fazenda[i].length; j++) {

                    String talhao = String.valueOf(
                            (char) ('A' + i)) + (j + 1);

                    int umidade = fazenda[i][j];

                    String status = classificarTalhao(umidade);

                    escritor.write(talhao + "," + umidade
                            + "," + status);

                    escritor.newLine();
                }
            }
        }

        System.out.println("Mapa de umidade salvo com sucesso!");
    }

    // RF11 - Relatório Gerencial da Fazenda
    public static void gerarRelatorioGerencial() {

        System.out.println("\n===== RELATÓRIO GERENCIAL DA FAZENDA =====");

        int total = 0;
        int criticos = 0;
        int atencao = 0;
        int adequados = 0;

        double somaUmidade = 0;

        int menorUmidade = 101;
        int maiorUmidade = -1;

        String talhaoMaisSeco = "";
        String talhaoMaisUmido = "";

        try (java.io.BufferedReader leitor =
                     new java.io.BufferedReader(
                             new java.io.FileReader("mapa_umidade.csv"))) {

            leitor.readLine();

            String linha;

            while ((linha = leitor.readLine()) != null) {

                String[] dados = linha.split(",");

                if (dados.length != 3) {
                    continue;
                }

                String talhao = dados[0].trim();
                int umidade;

                try {
                    umidade = Integer.parseInt(dados[1].trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                if (umidade < 0 || umidade > 100) {
                    continue;
                }

                String status = classificarTalhao(umidade);

                total++;
                somaUmidade += umidade;

                if (status.equalsIgnoreCase("Crítico")) {
                    criticos++;
                } else if (status.equalsIgnoreCase("Atenção")) {
                    atencao++;
                } else {
                    adequados++;
                }

                if (umidade < menorUmidade) {
                    menorUmidade = umidade;
                    talhaoMaisSeco = talhao;
                }

                if (umidade > maiorUmidade) {
                    maiorUmidade = umidade;
                    talhaoMaisUmido = talhao;
                }
            }

            if (total == 0) {
                System.out.println("Nenhum talhão válido cadastrado.");
                return;
            }

            double mediaUmidade = somaUmidade / total;

            double percentualIrrigacao =
                    (criticos + atencao) * 100.0 / total;

            System.out.println("Total de talhões: " + total);

            System.out.printf("Média de umidade: %.2f%%%n",
                    mediaUmidade);

            System.out.println("\n===== CLASSIFICAÇÃO =====");

            System.out.println("Talhões críticos: " + criticos);
            System.out.println("Talhões em atenção: " + atencao);
            System.out.println("Talhões adequados: " + adequados);

            System.out.println("\n===== INDICADORES =====");

            System.out.printf("Irrigação necessária: %.2f%%%n",
                    percentualIrrigacao);

            System.out.println("Talhão mais seco: "
                    + talhaoMaisSeco + " - " + menorUmidade + "%");

            System.out.println("Talhão mais úmido: "
                    + talhaoMaisUmido + " - " + maiorUmidade + "%");

        } catch (java.io.FileNotFoundException e) {

            System.out.println("Nenhum mapa de umidade cadastrado.");

        } catch (java.io.IOException e) {

            System.out.println("Erro ao gerar relatório: "
                    + e.getMessage());
        }
    }

    // RF12 - Exportação do Relatório Gerencial
    public static void exportarRelatorioGerencial() {

        java.io.File arquivo = new java.io.File("mapa_umidade.csv");

        if (!arquivo.exists() || arquivo.length() == 0) {
            System.out.println("Nenhum mapa de umidade cadastrado.");
            return;
        }

        java.io.PrintStream consoleOriginal = System.out;

        try (java.io.PrintStream relatorio =
                     new java.io.PrintStream("relatorio_fazenda.txt", "UTF-8")) {

            try {
                System.setOut(relatorio);

                gerarRelatorioGerencial();

            } finally {
                System.setOut(consoleOriginal);
            }

            if (relatorio.checkError()) {
                System.out.println("Erro ao gravar o relatório.");
                return;
            }

            System.out.println("Relatório exportado com sucesso!");
            System.out.println("Arquivo: relatorio_fazenda.txt");

        } catch (java.io.IOException e) {

            System.out.println("Erro ao exportar relatório: "
                    + e.getMessage());
        }
    }

}
