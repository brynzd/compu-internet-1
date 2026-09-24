package co.icesi.buscaminas.client;

import co.icesi.buscaminas.client.model.Cell;

public class BoardRenderer {

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    public static void render(Cell[][] board) {
        if (board == null || board.length == 0 || board[0].length == 0) {
            System.out.println("  (el servidor no devolvio un tablero)");
            return;
        }

        int rows = board.length;
        int cols = board[0].length;

        StringBuilder header = new StringBuilder("    ");
        for (int j = 0; j < cols; j++) {
            header.append(String.format("  %s%-2s%s", CYAN, j, RESET));
        }
        System.out.println();
        System.out.println(header);

        String separator = "    " + "+---".repeat(cols) + "+";
        System.out.println(separator);
        for (int i = 0; i < rows; i++) {
            StringBuilder row = new StringBuilder(String.format("%s%3s%s ", CYAN, i, RESET));
            for (int j = 0; j < cols; j++) {
                row.append("| ").append(symbol(board[i][j])).append(' ');
            }
            row.append('|');
            System.out.println(row);
            System.out.println(separator);
        }
        System.out.println("    Leyenda: " + YELLOW + "M" + RESET + " bandera  |  . oculta  |  "
                + RED + "*" + RESET + " mina  |  1-8 minas adyacentes  |  (vacio) sin minas alrededor");
    }

    private static String symbol(Cell cell) {
        if (cell.isMarked()) {
            return YELLOW + "M" + RESET;
        }
        if (cell.isHide() && !cell.isShowAll()) {
            return ".";
        }
        if (cell.isLandMine()) {
            return RED + "*" + RESET;
        }
        return cell.getValue() == 0 ? " " : BLUE + cell.getValue() + RESET;
    }
}
