package rules;


import utils.Coordinates;
import utils.Pair;

public class Game {
	public static final int nir = 5;
	
	public static String stringRepresentation(Board b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.n; ++i) {
			for (int j = 0; j < b.n; ++j) {
				sb.append(Integer.toString(b.board[i][j]));
			}
		}
		return sb.toString();
	}
	
	public static int[][] getInitBoard(int size) {
		Board b = new Board(size);
		return b.board;
	}
	
	public static utils.Pair<Integer, Integer> getBoardSize(Board b) {
		return new utils.Pair<Integer, Integer>(b.n, b.n);
	}
	
	public static int getActionSize(Board b) {
		return b.n * b.n;
	}
	
	public static utils.Pair<Board, Integer> getNextState(Board b, int p, int a) {
		Board next = new Board(b.n);
		next.board = new int[b.n][b.n];
		for (int i = 0; i < b.n; ++i) {
			for (int j = 0; j < b.n; ++j) {
				next.board[i][j] = b.board[i][j];
			}
		}
		next.executeMove(new utils.Coordinates(a / b.n, a % b.n), p);
		return new utils.Pair<Board, Integer>(next, -p);
	}
	
	public static int[] getValidMoves(Board b, int p) {
		int[] valids = new int[getActionSize(b)];
		for (int i = 0; i < b.n; ++i) {
			for (int j = 0;  j < b.n; ++j) {
				valids[b.n * i + j] = b.board[i][j] == 0 ? 1 : 0;
			}
		}
		return valids;
	}
	
	public static float getGameEnded(Board b, int player) {
		for (int w = 0; w < b.n; ++w) {
			for (int h = 0; h < b.n; ++h) {
				if_1:
				if (w < b.n - nir + 1 && b.board[w][h] != 0) {
					int color = b.board[w][h];
					for (int i = w; i < w + nir; ++i) {
						if (b.board[i][h] != color) {
							break if_1;
						}
					} 
					return color;
				}
				
				if_2:
				if (h < b.n - nir + 1 && b.board[w][h] != 0) {
					int color = b.board[w][h];
					for (int j = h; j < h + nir; ++j) {
						if (b.board[w][j] != color) {
							break if_2;
						}
					}
					return color;
				}
				
				if_3:
				if (w < b.n - nir + 1 && h < b.n - nir + 1 && b.board[w][h] != 0) {
					int color = b.board[w][h];
					for (int k = 0; k < nir; ++k) {
						if (b.board[w+k][h+k] != color) {
							break if_3;
						}
					}
					return color;
				}
				
				if_4:
				if (w < b.n - nir + 1 && h < b.n && h >= nir - 1 && b.board[w][h] != 0) {
					int color = b.board[w][h];
					for (int l = 0; l < nir; ++l) {
						if (b.board[w+l][h-l] != color) {
							break if_4;
						}
					}
					return color;
				}
			}
		}
		for (int[] row : b.board) {
			for (int c : row) {
				if (c == 0) {
					return 0;
				}
			}
		}
		return (float) 1e-4;
	}
	
	public static Board getCannonicalForm(Board b, int player) {
		for (int i = 0; i < b.n; ++i) {
			for(int j = 0; j < b.n; ++j) {
				b.board[i][j] *= player;
			}
		}
		return b;
	}
}

