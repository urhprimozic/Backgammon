package rules;

import utils.Pair;

public class Game {
	

	public static String stringRepresentation(Board b) {
		String ans = "";
		// upper numbers
		for (int i = 13; i<= 24;i++){
			ans += " " + String.valueOf(i) + " ";
		}
		ans+="------------------------";

		for (int l = 1; l <= 5; l++){
			for(int s = 0; s< 12;s++){
				if (b.board[s][0] >= l){
					ans += " TODOOO"+" ";
				}
			}
		}

		//lower lines
		ans+="------------------------";
		for (int i = 1; i<= 12;i++){
			ans += " " + String.valueOf(i) + " ";
		}
		return ans;
	}
	
	public static int[][] getInitBoard() {
		Board b = new Board();
		return b.board;
	}
	
	//public static utils.Pair<Integer, Integer> getBoardSize(Board b) {
	//	return new utils.Pair<Integer, Integer>(b.n, b.n);
	//}
	
	/**
	 * TODO
	 * @param b
	 * @return
	 */
	public static int getActionSize(Board b) {
		//TODO
		return -1;
	}
	
	//public static utils.Pair<Board, Integer> getNextState(Board b, int p, int a) {
		//Board next = new Board(b.n);
		//next.board = new int[b.n][b.n];
		//for (int i = 0; i < b.n; ++i) {
		//	for (int j = 0; j < b.n; ++j) {
		//		next.board[i][j] = b.board[i][j];
		//	}
		//}
		//next.executeMove(new utils.Coordinates(a / b.n, a % b.n), p);
		//return new utils.Pair<Board, Integer>(next, -p);
	//}
	
	//public static int[] getValidMoves(Board b, int p) {
	//	int[] valids = new int[getActionSize(b)];
	//	for (int i = 0; i < b.n; ++i) {
	//		for (int j = 0;  j < b.n; ++j) {
	//			valids[b.n * i + j] = b.board[i][j] == 0 ? 1 : 0;
	//		}
	//	}
	//	return valids;
	//}
	
	public static  float  getGameEnded(Board b, int player) {
		//TODO 
		return (float) 0.;
	}

    public static int[] getValidMoves(Board board, int i) {
        return null;
    }

	public static Pair<Board, Integer> getNextState(Board board, int i, int a) {
		return null;
	}

    public static Board getCannonicalForm(Board nextBoard, int nextPlayer) {
        return null;
    }
	
	//public static Board getCannonicalForm(Board b, int player) {
//TODO
	//}
}

