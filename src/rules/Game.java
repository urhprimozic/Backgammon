package rules;

import java.util.List;

import utils.Pair;

/**
 * A collection of static methods used by MCTS to get modified boards.
 */
public class Game {

	/**
	 * Gets the string representation of the current game to be used as a key in a
	 * {@code Map}.
	 *
	 * @param b    the current board
	 * @param dice the current dice throw
	 * @return A {@code String} that uniquely describes the current board state.
	 */
	public static String stringRepresentation(Board b, Pair<Integer, Integer> dice) {
		/*
		 * each white chip counts as 1, each black chip counts as -1
		 * format: "{1st dice},{2nd dice},{# of white captured chips},{# chips on triangle 1}, ...,{# chips on triangle 24}, {# of black captured chips}"
		 */
		StringBuilder ans = new StringBuilder();
		ans.append(dice.getFirst()).append(",");
		ans.append(dice.getLast()).append(",");
		ans.append(b.whiteChipsCaptured).append(",");
		for (int i = 0; i <= 23; ++i) {
			ans.append(b.board[i][0] * b.board[i][1]).append(",");
		}
		ans.append(b.blackChipsCaptured);
		return ans.toString();
	}

	/**
	 * Gets the state after a sequence of moves without mutating the original board
	 * and rolls the dice. This makes the new board ready for the next step of MCTS.
	 *
	 * @param board     the current board
	 * @param moveOrder the sequence of moves to be executed
	 * @return A new {@code Board} after the moves in {@code moveOrder} have been
	 *         made.
	 */
	public static Board getNextState(Board board, List<Pair<Integer, Integer>> moveOrder) {
		// make a new board and copy the state
		Board nextBoard = new Board();
		for (int i = 0; i < 24; i++) {
			nextBoard.board[i][0] = board.board[i][0];
			nextBoard.board[i][1] = board.board[i][1];
		}
		nextBoard.whiteChipsCaptured = board.whiteChipsCaptured;
		nextBoard.blackChipsCaptured = board.blackChipsCaptured;
		nextBoard.offboard = new Pair<Integer, Integer>(board.offboard.getFirst(), board.offboard.getLast());

		// execute moves and roll the dice
		for (Pair<Integer, Integer> move : moveOrder) {
			nextBoard.executeMove(move);
		}
		nextBoard.rollDice();
		return nextBoard;
	}

	/**
	 * Gets the canonical form where {@code player} sees the board as if he were
	 * white. This includes the direction his chips move, the indicies of the board
	 * as well as the amount of captured and offboard chips of each color.
	 *
	 * @param board  the current board
	 * @param player the player whose perspective we want
	 * @return A new {@code Board} where {@code player} has the white chips.
	 */
	public static Board getCannonicalForm(Board board, int player) {
		Board canonicalBoard = new Board();
		// from white's perspective nothing changes, so we just copy the state
		if (player == 1) {
			for (int i = 0; i < 24; i++) {
				canonicalBoard.board[i][0] = board.board[i][0];
				canonicalBoard.board[i][1] = board.board[i][1];
			}
			canonicalBoard.whiteChipsCaptured = board.whiteChipsCaptured;
			canonicalBoard.blackChipsCaptured = board.blackChipsCaptured;
			canonicalBoard.offboard = new Pair<Integer, Integer>(board.offboard.getFirst(), board.offboard.getLast());
		}
		// not only do we need to switch the colors, we also need to change the positions
		else {
			for (int i = 0; i < 24; i++) {
				canonicalBoard.board[i][0] = board.board[23 - i][0];
				canonicalBoard.board[i][1] = -board.board[23 - i][1];
			}
			canonicalBoard.whiteChipsCaptured = board.blackChipsCaptured;
			canonicalBoard.blackChipsCaptured = board.whiteChipsCaptured;
			canonicalBoard.offboard = new Pair<Integer, Integer>(board.offboard.getLast(), board.offboard.getFirst());
		}
		canonicalBoard.dice = new Pair<Integer, Integer>(board.dice.getFirst(), board.dice.getLast());
		return canonicalBoard;
	}
}
