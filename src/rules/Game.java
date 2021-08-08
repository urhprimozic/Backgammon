package rules;

import java.util.List;

import utils.Pair;

public class Game {

	public static String stringRepresentation(Board b, Pair<Integer, Integer> dice) {
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

	// public static utils.Pair<Integer, Integer> getBoardSize(Board b) {
	// return new utils.Pair<Integer, Integer>(b.n, b.n);
	// }

	// public static utils.Pair<Board, Integer> getNextState(Board b, int p, int a)
	// {
	// Board next = new Board(b.n);
	// next.board = new int[b.n][b.n];
	// for (int i = 0; i < b.n; ++i) {
	// for (int j = 0; j < b.n; ++j) {
	// next.board[i][j] = b.board[i][j];
	// }
	// }
	// next.executeMove(new utils.Coordinates(a / b.n, a % b.n), p);
	// return new utils.Pair<Board, Integer>(next, -p);
	// }

	// public static int[] getValidMoves(Board b, int p) {
	// int[] valids = new int[getActionSize(b)];
	// for (int i = 0; i < b.n; ++i) {
	// for (int j = 0; j < b.n; ++j) {
	// valids[b.n * i + j] = b.board[i][j] == 0 ? 1 : 0;
	// }
	// }
	// return valids;
	// }

	public static float getGameEnded(Board b, int player) {
		if (b.offboard.getFirst() == 15) {
			return player;
		} else if (b.offboard.getLast() == 15) {
			return -player;
		} else {
			return 0;
		}

	}

	public static Board getNextState(Board board, List<Pair<Integer, Integer>> moveOrder) {
		Board nextBoard = new Board();
		for (int i = 0; i < 24; i++) {
			nextBoard.board[i][0] = board.board[i][0];
			nextBoard.board[i][1] = board.board[i][1];
		}
		nextBoard.whiteChipsCaptured = board.whiteChipsCaptured;
		nextBoard.blackChipsCaptured = board.blackChipsCaptured;
		nextBoard.offboard = new Pair<Integer, Integer>(board.offboard.getFirst(), board.offboard.getLast());

		for (Pair<Integer, Integer> move : moveOrder) {
			nextBoard.executeMove(move);
		}
		nextBoard.rollDice();
		return nextBoard;
	}

	public static Board getCannonicalForm(Board board, int player) {
		Board canonicalBoard = new Board();
		if (player == 1) {
			for (int i = 0; i < 24; i++) {
				canonicalBoard.board[i][0] = board.board[i][0];
				canonicalBoard.board[i][1] = board.board[i][1];
			}
			canonicalBoard.whiteChipsCaptured = board.whiteChipsCaptured;
			canonicalBoard.blackChipsCaptured = board.blackChipsCaptured;
			canonicalBoard.offboard = new Pair<Integer, Integer>(board.offboard.getFirst(), board.offboard.getLast());
		} else {
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

	// public static Board getCannonicalForm(Board b, int player) {
//TODO
	// }
}
