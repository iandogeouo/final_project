package final_project;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import java.util.*;

public class Game {
    private int[][] board;
    private int rows, cols;

    public Game(int r, int c) {
        rows = r;
        cols = c;
        board = new int[r][c];
        randomize();
    }

    public int getRows(){ return rows; }
    public int getCols(){ return cols; }

    public void randomize() {
        Random rand = new Random();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                int candy;
                do {
                    candy = rand.nextInt(6);
                } while ((j >= 2 && candy == board[i][j - 1] && candy == board[i][j - 2]) ||
                        (i >= 2 && candy == board[i - 1][j] && candy == board[i - 2][j]));
                board[i][j] = candy;
            }
        if (isDeadBoard()) randomize();
    }

    public int getCandy(int r, int c) {
        return board[r][c];
    }

    public Image getemoji(int type) {
        return switch (type) {
            case 0 -> new Image(getClass().getResourceAsStream("image/red_chinese.png"));
            case 1 -> new Image(getClass().getResourceAsStream("image/fire.png"));
            case 2 -> new Image(getClass().getResourceAsStream("image/dangerous.png"));
            case 3 -> new Image(getClass().getResourceAsStream("image/OK.png"));
            case 4 -> new Image(getClass().getResourceAsStream("image/white_heart.png"));
            case 5 -> new Image(getClass().getResourceAsStream("image/cross.png"));
            default -> null;
        };
    }

    public boolean isDeadBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j + 1 < cols && trySwapAndCheck(i, j, i, j + 1)) return false;
                if (i + 1 < rows && trySwapAndCheck(i, j, i + 1, j)) return false;
            }
        }
        return true;
    }

    private boolean trySwapAndCheck(int r1, int c1, int r2, int c2) {
        swap(r1, c1, r2, c2);
        boolean result = hasMatch();
        swap(r1, c1, r2, c2);
        return result;
    }

    public void swap(int r1, int c1, int r2, int c2) {
        int temp = board[r1][c1];
        board[r1][c1] = board[r2][c2];
        board[r2][c2] = temp;
    }

    public boolean hasMatch() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 2; j++) {
                int val = board[i][j];
                if (val == board[i][j + 1] && val == board[i][j + 2]) return true;
            }
        }
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows - 2; i++) {
                int val = board[i][j];
                if (val == board[i + 1][j] && val == board[i + 2][j]) return true;
            }
        }
        return false;
    }

    public boolean clearMatches() {
        boolean[][] marked = new boolean[rows][cols];
        boolean foundMatch = false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 2; j++) {
                int v = board[i][j];
                if (v != -1 && v == board[i][j + 1] && v == board[i][j + 2]) {
                    marked[i][j] = marked[i][j + 1] = marked[i][j + 2] = true;
                    foundMatch = true;
                }
            }
        }

        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows - 2; i++) {
                int v = board[i][j];
                if (v != -1 && v == board[i + 1][j] && v == board[i + 2][j]) {
                    marked[i][j] = marked[i + 1][j] = marked[i + 2][j] = true;
                    foundMatch = true;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (marked[i][j]) board[i][j] = -1;
            }
        }

        return foundMatch;
    }

    public void dropCandies() {
        for (int j = 0; j < cols; j++) {
            int writeRow = rows - 1;
            for (int i = rows - 1; i >= 0; i--) {
                if (board[i][j] != -1) {
                    board[writeRow][j] = board[i][j];
                    writeRow--;
                }
            }
            for (int i = writeRow; i >= 0; i--) {
                board[i][j] = -1;
            }
        }
    }

    public void fillNewCandies() {
        Random rand = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == -1) {
                    board[i][j] = rand.nextInt(6);
                }
            }
        }
    }

    public void processBoard() {
        while (clearMatches()) {
            dropCandies();
            fillNewCandies();
        }
    }

}