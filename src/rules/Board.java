package rules;

import java.util.ArrayList;
import java.util.List;

import utils.Coordinates;

public class Board {
	public int n;
	public int[][] board;
	
	public Board(int n) {
		this.n = n;
		board = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = 0;
            }
        }
	}
	
	public List<Coordinates> getLegalMoves() {
        List<Coordinates> poteze = new ArrayList<utils.Coordinates>();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (board[i][j] == 0)
                    poteze.add(new utils.Coordinates(i, j));
        return poteze;
    }
	
	public void executeMove(Coordinates t, int p) {
		board[t.getX()][t.getY()] = p;
	}
}
