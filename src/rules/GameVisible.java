package rules;

import utils.Pair;

/**
 * TODO Nvm a to res rabva, to je kao IGra.java v gomoku
 */
public class GameVisible {

	public Board board;
	public int player;
	
	public int movesMade;

	public GameVisible() {
		board = new Board();
		player = 1;
		movesMade = 0;
	}
	/**
	 * Plays a move, if its possible. False otherwise
	 * @param move move to play
	 * @return true, if move is possible. Otherwise false
	 */
	public boolean playMove(Pair<Integer, Integer> move) {
		if (board.executeMove(move)) {
			movesMade++;
			if (movesMade == 2) { // will have to be changed to max possible moves in the position
				player *= -1;
				movesMade = 0;
			}
			return true;
		}
		return false;
	}
}
