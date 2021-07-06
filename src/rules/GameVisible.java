package rules;

import utils.Pair;

/**
 * TODO
 * Nvm a to res rabva, to je kao IGra.java v gomoku
 */
public class GameVisible {
	
	public Board board;
	public int player;
	
	public GameVisible() {
		board = new Board();
		player = 1;
	}
	
	public boolean playMove(Pair<Integer, Integer> move) {
			return  board.executeMove(move);
	}
}
