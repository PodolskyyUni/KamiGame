package org.example.game;

public class Grid {
    private int length = (int) (Math.random() * 15 + 5);
    private int height = (int) (Math.random() * 15 + 5);
    private Sector[][] board;

    public Grid() {
        board = new Sector[length][height];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                board[i][j] = new Sector(i, j);
            }
        }
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public Sector[][] getBoard() {
        return board;
    }

    public Sector getBoardSector(int x, int y) {
        return board[x][y];
    }

    public boolean sameColored(Sector[][] board) {
        Color color = board[0][0].getColor();
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                if (board[i][j].getColor() != color) return false;
            }
        }
        return true;
    }
}

