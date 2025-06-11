package final_project;

import javafx.scene.image.Image;
import java.util.*;

public class Game {
    private int[][] board;
    private int rows, cols;
    private int step = 20;
    private int score = 0;

    public Game(int r, int c) {
        rows = r;
        cols = c;
        board = new int[r][c];
        randomize();
        step = 5;
        score = 0;
    }

    public int getRows(){ return rows; }
    public int getCols(){ return cols; }
    public String getStep(){ return String.valueOf(step); }
    public String getScore(){ return String.valueOf(score);}
    public void move(){step--;}

    public void decreaseStep() {
        step--;
    }

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
        return !getMatchedCoords().isEmpty();
    }

    public List<int[]> getMatchedCoords() {
        List<int[]> matched = new ArrayList<>();
        boolean[][] marked = new boolean[rows][cols];
        boolean triggerFullClear = false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 2; ) {
                int val = board[i][j];
                if (val == -1) { j++; continue; }
                int count = 1;
                while (j + count < cols && board[i][j + count] == val) count++;

                if (count >= 3) {
                    if (count >= 5) {
                        triggerFullClear = true;
                    } else if (count == 4) {
                        for (int col = 0; col < cols; col++) marked[i][col] = true;
                    } else {
                        for (int k = 0; k < count; k++) marked[i][j + k] = true;
                    }
                }
                j += count;
            }
        }

        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows - 2; ) {
                int val = board[i][j];
                if (val == -1) { i++; continue; }
                int count = 1;
                while (i + count < rows && board[i + count][j] == val) count++;

                if (count >= 3) {
                    if (count >= 5) {
                        triggerFullClear = true;
                    } else if (count == 4) {
                        for (int row = 0; row < rows; row++) marked[row][j] = true;
                    } else {
                        for (int k = 0; k < count; k++) marked[i + k][j] = true;
                    }
                }
                i += count;
            }
        }

        if (triggerFullClear) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) matched.add(new int[]{i, j});
            }
        } else {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (marked[i][j]) matched.add(new int[]{i, j});
                }
            }
        }

        return matched;
    }

    public int clearMarked(List<int[]> matched, int combo) {
        int baseScore = 10;
        int gained = matched.size() * (baseScore * combo);
        score += gained;
        for (int[] pos : matched) {
            board[pos[0]][pos[1]] = -1;
        }
        return gained;
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
        int combo = 1;
        while (hasMatch()) {
            List<int[]> matched = getMatchedCoords();
            clearMarked(matched, combo);
            dropCandies();
            fillNewCandies();
            combo++;
        }
    }
}
