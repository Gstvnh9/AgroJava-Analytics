package org.example;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class Main {

    static Scanner entrada = new Scanner(System.in);

    static int[][] fazenda = new int[4][4];
    static double[] chuva = new double[7];

    static double mediaPluviosidade = 0;
    static double maiorIndice = 0;
    static double total = 0;

    static boolean chuvasCadastradas = false;
    static boolean talhoesCadastrados = false;

    static void main(String[] args) {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        entrada.useLocale(Locale.US);

        menuPrincipal();
    }

    public static void menuPrincipal() {

        int opcao;

        do {

            System.out.println("\n========== AGROJAVA ==========");
            System.out.println("1 - Registrar Semana Agrícola");
            System.out.println("2 - Cadastrar Talhões");
            System.out.println("3 - Exibir Mapa do Campo");
            System.out.println("4 - Relatório de Alertas de Irrigação");
            System.out.println("5 - Consultar Histórico de Chuvas");
            System.out.println("6 - Identificar Semana Mais Chuvosa");
            System.out.println("7 - Dashboard Climático");
            System.out.println("8 - Relatório Gerencial da Fazenda");
            System.out.println("9 - Exportar Relatório Gerencial");
            System.out.println("0 - Sair");
            System.out.println("==============================");

            opcao = lerInteiro("Escolha uma opção: ", 0, 9);

            switch (opcao) {

                case 1:
                    cadastrarChuvas();
                    break;

                case 2:
                    cadastrarTalhoes();
                    break;

                case 3:
                    exibirMapaCampo();
                    break;

                case 4:
                    gerarRelatorioIrrigacao();
                    break;

                case 5:
                    HistoricoChuvas.consultarHistoricoChuvas();
                    break;

                case 6:
                    HistoricoChuvas.identificarSemanaMaisChuvosa();
                    break;

                case 7:
                    HistoricoChuvas.exibirDashboardClimatico();
                    break;

                case 8:
                    MapaUmidade.gerarRelatorioGerencial();
                    break;

                case 9:
                    MapaUmidade.exportarRelatorioGerencial();
                    break;

                case 0:
                    System.out.println("Obrigado por usar nosso sistema.");
                    System.out.println("Até logo!");
                    break;
            }

        } while (opcao != 0);
    }

    // RF04 - Registrar Semana Agrícola
    public static void cadastrarChuvas() {

        System.out.println("\n===== REGISTRAR SEMANA AGRÍCOLA =====");

        String data;

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

        while (true) {

            System.out.print("Informe a data da semana (dd/MM/aaaa): ");
            data = entrada.nextLine().trim();

            try {
                LocalDate.parse(data, formato);
                break;

            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Digite uma data válida.");
            }
        }

        String[] dias = {"Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado", "Domingo"};

        total = 0;
        maiorIndice = 0;

        for (int i = 0; i < chuva.length; i++) {

            chuva[i] = lerChuva("Volume de chuva de " + dias[i] + " (mm): ");

            total += chuva[i];

            if (chuva[i] > maiorIndice) {
                maiorIndice = chuva[i];
            }
        }

        mediaPluviosidade = total / chuva.length;
        chuvasCadastradas = true;

        System.out.println("\n===== RESUMO SEMANAL =====");

        System.out.printf("Média semanal: %.2f mm%n", mediaPluviosidade);

        System.out.printf("Total de precipitação: %.2f mm%n", total);

        System.out.printf("Maior índice diário: %.2f mm%n", maiorIndice);

        try {

            HistoricoChuvas.salvarHistoricoChuvas(data, chuva);

        } catch (IOException e) {

            System.out.println("Erro ao salvar a semana: " + e.getMessage());
        }
    }

    public static void cadastrarTalhoes() {

        System.out.println("\n===== CADASTRAR TALHÕES =====");

        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 4; j++) {

                String talhao = "" + (char) ('A' + i) + (j + 1);

                fazenda[i][j] = lerInteiro("Informe a umidade do talhão " + talhao + " (%): ", 0, 100);
            }
        }

        talhoesCadastrados = true;

        System.out.println("Talhões cadastrados com sucesso!");


        try {
            MapaUmidade.salvarMapaUmidade(fazenda);
        } catch (IOException e) {
            System.out.println("Erro ao salvar o mapa de umidade: "
                    + e.getMessage());
        }


    }

    public static void exibirMapaCampo() {

        if (chuvasCadastradas) {

            System.out.println("\n===== RELATÓRIO CLIMÁTICO =====");

            System.out.printf("Média semanal: %.2f mm%n", mediaPluviosidade);

            System.out.printf("Maior índice diário: %.2f mm%n", maiorIndice);
        }

        if (!talhoesCadastrados) {

            System.out.println("Cadastre os talhões primeiro.");
            return;
        }

        System.out.println("\n===== MAPA DO CAMPO =====");

        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 4; j++) {

                String talhao = "" + (char) ('A' + i) + (j + 1);

                System.out.println(talhao + " - " + fazenda[i][j] + "%");

                if (fazenda[i][j] < 30) {

                    System.out.println("É necessária uma melhor irrigação!");
                }
            }
        }
    }


    // RF10 - Gerar Relatório de Irrigação
    public static void gerarRelatorioIrrigacao() {

        System.out.println("\n===== ALERTAS DE IRRIGAÇÃO =====");

        int totalAlertas = 0;

        try (java.io.BufferedReader leitor =
                     new java.io.BufferedReader(
                             new java.io.FileReader(
                                     "mapa_umidade.csv",
                                     StandardCharsets.UTF_8))) {

            // Ignorar o cabeçalho
            leitor.readLine();

            String linha;

            while ((linha = leitor.readLine()) != null) {

                String[] dados = linha.split(",");

                if (dados.length != 3) {
                    continue;
                }

                String talhao = dados[0];
                String status = dados[2];

                if (status.equals("Crítico")
                        || status.equals("Atenção")) {

                    System.out.println(talhao + " - " + status);

                    totalAlertas++;
                }
            }

            System.out.println("----------------------------");
            System.out.println("Total de alertas: " + totalAlertas);

            if (totalAlertas == 0) {
                System.out.println("Nenhum talhão necessita de irrigação.");
            }

        } catch (java.io.FileNotFoundException e) {

            System.out.println("Nenhum mapa de umidade cadastrado.");

        } catch (IOException e) {

            System.out.println("Erro ao ler o mapa de umidade: "
                    + e.getMessage());
        }
    }


    public static int lerInteiro(String mensagem, int minimo, int maximo) {

        while (true) {

            System.out.print(mensagem);

            try {

                int valor = Integer.parseInt(entrada.nextLine().trim());

                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }

                System.out.println("Digite um valor entre " + minimo + " e " + maximo + ".");

            } catch (NumberFormatException e) {

                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    public static double lerChuva(String mensagem) {

        while (true) {

            System.out.print(mensagem);

            try {

                double valor = Double.parseDouble(entrada.nextLine().trim().replace(",", "."));

                if (Double.isFinite(valor) && valor >= 0) {
                    return valor;
                }

                System.out.println("O volume de chuva deve ser um número válido e não negativo.");

            } catch (NumberFormatException e) {

                System.out.println("Digite um número válido.");
            }
        }
    }
}