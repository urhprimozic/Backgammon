package rules;

import utils.Pair;

/**
 * TODO Nvm a to res rabva, to je kao IGra.java v gomoku
 */
public class GameVisible {

	public Board board;
	public int player;

	public GameVisible() {
		board = new Board();
		player = 1;
	}
	/**
	 * Plays a move, if its possible. False otherwise
	 * @param move move to play
	 * @return true, if move is possible. Otherwise false
	 */
	public boolean playMove(Pair<Integer, Integer> move) {
		if (board.executeMove(move)) {
			player *= -1;
			return true;
		}
		return false;
	}
}
