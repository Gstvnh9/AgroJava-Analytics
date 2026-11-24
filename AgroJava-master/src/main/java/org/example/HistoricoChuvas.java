package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HistoricoChuvas {

    private static final String ARQUIVO = "historico_chuvas.csv";

    // RF04 - Registrar Semana Agrícola
    public static void salvarHistoricoChuvas(String data, double[] chuva) throws IOException {

        double total = 0;

        for (int i = 0; i < chuva.length; i++) {
            total += chuva[i];
        }

        double media = total / chuva.length;

        File arquivo = new File(ARQUIVO);

        boolean escreverCabecalho = !arquivo.exists() || arquivo.length() == 0;

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo, StandardCharsets.UTF_8, true))) {

            if (escreverCabecalho) {

                escritor.write("data,seg,ter,qua,qui,sex,sab,dom,media,total");

                escritor.newLine();
            }

            escritor.write(data);

            for (int i = 0; i < chuva.length; i++) {
                escritor.write("," + chuva[i]);
            }

            escritor.write("," + media + "," + total);
            escritor.newLine();
        }

        System.out.println("Semana agrícola salva com sucesso!");
    }

    // RF05 - Consultar Histórico de Chuvas
    public static void consultarHistoricoChuvas() {

        File arquivo = new File(ARQUIVO);

        System.out.println("\n===== HISTÓRICO DE CHUVAS =====");

        if (!arquivo.exists() || arquivo.length() == 0) {

            System.out.println("Nenhuma semana cadastrada.");
            return;
        }

        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo, StandardCharsets.UTF_8))) {

            // Ignorar o cabeçalho
            leitor.readLine();

            String linha;
            int quantidade = 0;

            while ((linha = leitor.readLine()) != null) {

                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] dados = linha.split(",", -1);

                if (dados.length != 10) {

                    System.out.println("Registro inválido: " + linha);
                    continue;
                }

                try {

                    String data = dados[0];

                    double media = Double.parseDouble(dados[8]);
                    double total = Double.parseDouble(dados[9]);

                    System.out.println("------------------------------");
                    System.out.println("Semana: " + data);
                    System.out.printf("Média: %.2f mm%n", media);
                    System.out.printf("Total: %.2f mm%n", total);

                    quantidade++;

                } catch (NumberFormatException e) {

                    System.out.println("Registro com valores inválidos: " + linha);
                }
            }

            System.out.println("------------------------------");
            System.out.println("Semanas registradas: " + quantidade);

        } catch (IOException e) {

            System.out.println("Erro ao consultar o histórico: " + e.getMessage());
        }
    }


    // RF06 - Identificar Semana Mais Chuvosa
    public static void identificarSemanaMaisChuvosa() {

        File arquivo = new File(ARQUIVO);

        System.out.println("\n===== SEMANA MAIS CHUVOSA =====");

        if (!arquivo.exists() || arquivo.length() == 0) {

            System.out.println("Nenhuma semana cadastrada.");
            return;
        }

        String semanaMaisChuvosa = "";
        double maiorTotal = -1;

        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo, StandardCharsets.UTF_8))) {

            // Ignorar o cabeçalho
            leitor.readLine();

            String linha;

            while ((linha = leitor.readLine()) != null) {

                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] dados = linha.split(",", -1);

                if (dados.length != 10) {
                    continue;
                }

                try {

                    String data = dados[0];

                    double total = Double.parseDouble(dados[9]);

                    if (!Double.isFinite(total) || total < 0) {
                        continue;
                    }

                    if (total > maiorTotal) {

                        maiorTotal = total;
                        semanaMaisChuvosa = data;
                    }

                } catch (NumberFormatException e) {

                    System.out.println("Registro inválido: " + linha);
                }
            }

            if (maiorTotal == -1) {

                System.out.println("Nenhum registro válido encontrado.");

            } else {

                System.out.println("Semana mais chuvosa: " + semanaMaisChuvosa);

                System.out.printf("Total: %.2f mm%n", maiorTotal);
            }

        } catch (IOException e) {

            System.out.println("Erro ao consultar o arquivo: " + e.getMessage());
        }
    }


    // RF07 - Dashboard Climático
    public static void exibirDashboardClimatico() {

        File arquivo = new File(ARQUIVO);

        System.out.println("\n===== DASHBOARD CLIMÁTICO =====");

        if (!arquivo.exists() || arquivo.length() == 0) {
            System.out.println("Nenhuma semana cadastrada.");
            return;
        }

        int quantidade = 0;

        double volumeAcumulado = 0;
        double maiorChuva = -1;
        double menorChuva = Double.MAX_VALUE;

        String semanaMaisChuvosa = "";
        String semanaMaisSeca = "";

        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo, StandardCharsets.UTF_8))) {

            leitor.readLine();

            String linha;

            while ((linha = leitor.readLine()) != null) {

                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] dados = linha.split(",", -1);

                if (dados.length != 10) {
                    continue;
                }

                try {

                    String data = dados[0];
                    double total = Double.parseDouble(dados[9]);

                    if (!Double.isFinite(total) || total < 0) {
                        continue;
                    }

                    quantidade++;
                    volumeAcumulado += total;

                    if (total > maiorChuva) {
                        maiorChuva = total;
                        semanaMaisChuvosa = data;
                    }

                    if (total < menorChuva) {
                        menorChuva = total;
                        semanaMaisSeca = data;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Registro inválido: " + linha);
                }
            }

            if (quantidade == 0) {
                System.out.println("Nenhum registro válido encontrado.");
                return;
            }

            double mediaGeral = volumeAcumulado / quantidade;

            System.out.println("Semanas cadastradas: " + quantidade);

            System.out.printf("Volume acumulado: %.2f mm%n", volumeAcumulado);

            System.out.println("\nMaior chuva:");
            System.out.println("Semana: " + semanaMaisChuvosa);
            System.out.printf("Total: %.2f mm%n", maiorChuva);

            System.out.println("\nMenor chuva:");
            System.out.println("Semana: " + semanaMaisSeca);
            System.out.printf("Total: %.2f mm%n", menorChuva);

            System.out.printf("%nMédia geral: %.2f mm%n", mediaGeral);

        } catch (IOException e) {
            System.out.println("Erro ao gerar o dashboard: " + e.getMessage());
        }
    }
}
