package co.icesi.buscaminas.client;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import co.icesi.buscaminas.client.dtos.Response;
import co.icesi.buscaminas.client.model.Cell;

public class MainClient {

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        BuscaminasTCPClient client = new BuscaminasTCPClient(host, port);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Servidor destino: " + host + ":" + port);

        boolean running = true;
        while (running) {
            printMenu();
            String option = readLine(scanner);
            if (option == null) {
                break;
            }
            try {
                switch (option.trim()) {
                    case "1":
                        initGame(client, scanner);
                        break;
                    case "2":
                        selectCell(client, scanner);
                        break;
                    case "3":
                        markCell(client, scanner);
                        break;
                    case "4":
                        showResponse(client, client.getBoard(), "Estado actual del tablero");
                        break;
                    case "5":
                        showResponse(client, client.showAll(), "Te rendiste: tablero revelado");
                        break;
                    case "6":
                        running = false;
                        break;
                    default:
                        System.out.println("Opcion no valida, elija un numero entre 1 y 6.");
                }
            } catch (IOException e) {
                System.out.println(RED + "Error de red: " + e.getMessage() + RESET);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Entrada invalida: se esperaba un numero entero." + RESET);
            }
        }

        scanner.close();
        System.out.println("Cliente finalizado.");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=============================================");
        System.out.println("     BUSCAMINAS DISTRIBUIDO - CLIENTE TCP");
        System.out.println("=============================================");
        System.out.println("[1] Iniciar nueva partida (Filas, Columnas, Minas)");
        System.out.println("[2] Destapar celda (Fila, Columna)");
        System.out.println("[3] Marcar / Desmarcar bandera (Fila, Columna)");
        System.out.println("[4] Consultar estado actual del tablero");
        System.out.println("[5] Rendirse y revelar tablero completo");
        System.out.println("[6] Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private static void initGame(BuscaminasTCPClient client, Scanner scanner) throws IOException {
        int n = readInt(scanner, "Filas: ");
        int m = readInt(scanner, "Columnas: ");
        int mines = readInt(scanner, "Minas: ");
        if (n <= 0 || m <= 0 || mines < 0 || mines > n * m) {
            System.out.println(RED + "Parametros invalidos: filas y columnas deben ser > 0 y las minas no pueden superar "
                    + "el total de celdas." + RESET);
            return;
        }
        showResponse(client, client.initGame(n, m, mines), "Nueva partida iniciada (" + n + "x" + m + ", " + mines + " minas)");
    }

    private static void selectCell(BuscaminasTCPClient client, Scanner scanner) throws IOException {
        int i = readInt(scanner, "Fila: ");
        int j = readInt(scanner, "Columna: ");
        Response response = client.selectCell(i, j);

        if (client.isError(response)) {
            System.out.println(RED + "El servidor rechazo la jugada: " + client.messageOf(response) + RESET);
            BoardRenderer.render(client.boardOf(response));
            return;
        }

        boolean gameEnd = client.flagOf(response, "gameEnd");
        boolean win = client.flagOf(response, "win");

        if (gameEnd && win) {
            BoardRenderer.render(client.boardOf(response));
            System.out.println(GREEN + ">>> FELICITACIONES, GANASTE LA PARTIDA <<<" + RESET);
        } else if (gameEnd) {
            System.out.println(RED + ">>> BOOM! Pisaste una mina. Fin de la partida <<<" + RESET);
            BoardRenderer.render(client.boardOf(client.showAll()));
        } else {
            BoardRenderer.render(client.boardOf(response));
        }
    }

    private static void markCell(BuscaminasTCPClient client, Scanner scanner) throws IOException {
        int i = readInt(scanner, "Fila: ");
        int j = readInt(scanner, "Columna: ");
        showResponse(client, client.markCell(i, j), "Bandera alternada en (" + i + "," + j + ")");
    }

    private static void showResponse(BuscaminasTCPClient client, Response response, String successMessage) {
        if (client.isError(response)) {
            System.out.println(RED + "Error del servidor: " + client.messageOf(response) + RESET);
        } else {
            System.out.println(successMessage);
        }
        BoardRenderer.render(client.boardOf(response));
    }

    private static int readInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String line = readLine(scanner);
        if (line == null) {
            throw new NumberFormatException("fin de entrada");
        }
        return Integer.parseInt(line.trim());
    }

    private static String readLine(Scanner scanner) {
        try {
            return scanner.hasNextLine() ? scanner.nextLine() : null;
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
