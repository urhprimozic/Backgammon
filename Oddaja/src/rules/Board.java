package rules;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import utils.Pair;

/**
 * The board keeping track of positions of chips as well as the dice. It is also
 * responsible for executing moves and figuring out the legal moves in the
 * position.
 */
public class Board {
	/**
	 * {@code int[24][2]} size array. As {@code i} ranges from 0 to 23:
	 * <ul>
	 * <li>{@code board[i][0]} - number of chips on the (i+1)-th triangle
	 * <li>{@code board[i][1]} - {@code 1} for white, {@code 0} for empty,
	 * {@code -1} for black
	 * </ul>
	 */
	public int[][] board;

	public int whiteChipsCaptured;
	public int blackChipsCaptured;

	/**
	 * Keeps track if white is ready to take chips off the board.
	 */
	public boolean whiteFinal;

	/**
	 * Keeps track if black is ready to take chips off the board.
	 */
	public boolean blackFinal;

	private Random rand;
	public Pair<Integer, Integer> dice;

	/**
	 * {@code offboard.getFirst()} - number of white chips that have passed off the
	 * board<br>
	 * {@code offboard.getLast()} - number of black chips that have passed off the
	 * board
	 */
	public Pair<Integer, Integer> offboard;

	/**
	 * Initalizes the starting position. It also sets both dice to {@code null}.
	 */
	public Board() {
		board = new int[24][2];
		// White chips
		board[0][0] = 2;
		board[0][1] = 1;

		board[11][0] = 5;
		board[11][1] = 1;

		board[16][0] = 3;
		board[16][1] = 1;

		board[18][0] = 5;
		board[18][1] = 1;

		// Black chips
		board[23][0] = 2;
		board[23][1] = -1;

		board[12][0] = 5;
		board[12][1] = -1;

		board[7][0] = 3;
		board[7][1] = -1;

		board[5][0] = 5;
		board[5][1] = -1;

		// no chips are captured at the start
		whiteChipsCaptured = 0;
		blackChipsCaptured = 0;

		// chips cannot be moved off the board at the start
		whiteFinal = false;
		blackFinal = false;

		// no chips have passed off the board at the start
		offboard = new Pair<Integer, Integer>(0, 0);

		rand = new Random();
		dice = new Pair<Integer, Integer>(null, null);
	}

	/**
	 * Sets both dice to a random number from 1 to 6.
	 */
	public void rollDice() {
		dice.setFirst(rand.nextInt(6) + 1);
		dice.setLast(rand.nextInt(6) + 1);
	}

	/**
	 * Checks whether the game has ended.
	 *
	 * @param player the player relative to whom the result is given
	 * @return {@code 1} if {@code player} has won, {@code -1} if {@code player} has
	 *         lost and 0 otherwise.
	 */
	public int getGameEnded(int player) {
		if (offboard.getFirst() == 15) {
			return player;
		} else if (offboard.getLast() == 15) {
			return -player;
		} else {
			return 0;
		}

	}

	/**
	 * Gets the index of the end triangle for the given move.
	 *
	 * @param start  the starting index
	 * @param dice   the amount of spaces moved
	 * @param player the player whose move it is, determines the direction
	 * @return Index of the end triangle for the given move.
	 */
	private int getEndTriangle(int start, int dice, int player) {
		return Math.max(Math.min(start + player * dice, 24), -1);
	}

	/**
	 * Checks if there is a chip of color {@code player} at position {@code idx}.
	 * Since index {@code -1} represents the captured chips for white, it checks for
	 * any captured white chips. Similarly, since index {@code 24} represents the
	 * captured chips for black, it checks for any captured black chips.
	 *
	 * @param idx    the starting index
	 * @param player the player who we are checking for
	 * @return Whether there is a chip of color {@code player} at position
	 *         {@code idx}.
	 */
	private boolean isPlayerChipAtIndex(int idx, int player) {
		if (idx == -1) {
			return (player == 1 && whiteChipsCaptured != 0);
		} else if (idx == 24) {
			return (player == -1 && blackChipsCaptured != 0);
		} else if (idx < -1 || idx > 24) {
			return false;
		} else {
			return board[idx][1] == player;
		}
	}

	/**
	 * Calculates all the possible move orders in the current position for the given
	 * {@code player}.
	 *
	 * @param player the player whose moves it considers
	 * @return A {@code Set} of possible move orders. A move order is a {@code List}
	 *         of moves, where each move is a {@code Pair<Integer, Integer>} of the
	 *         start and end index.
	 */
	public Set<List<Pair<Integer, Integer>>> getLegalMoves(int player) {
		return getLegalMoves(player, dice);
	}

	/**
	 * Calculates all the possible move orders in the current position for the given
	 * {@code player} and with specific {@code dice}.
	 *
	 * @param player the player whose moves it considers
	 * @param dice   a pair of dice that determine the amount of spaces moved
	 * @return A {@code Set} of possible move orders. A move order is a {@code List}
	 *         of moves, where each move is a {@code Pair<Integer, Integer>} of the
	 *         start and end index.
	 */
	public Set<List<Pair<Integer, Integer>>> getLegalMoves(int player, Pair<Integer, Integer> dice) {
		/*
		 * Rules:
		 *     - If there are captured chips, they must be placed onto the board before any other move can be made.
		 *     - Can only move off the board if all chips are in the final sector.
		 *     - Can only capture lone opponent chips, not stacks.
		 *     - Must make as many moves as possible.
		 *     - When there are no more moves possible, the turn ends.
		 *     - Doubles mean both dice must be used twice.
		 *     - If either one dice or the other can be used but not both, the higher number one must be used.
		 *
		 * Things to keep in mind:
		 *     - Changing the order of the considered moves might allow one to get a legal position when the other move order would not allow that.
		 *     - You must make as many moves as possible, so only save the longest sequences.
		 */

		// save the starting state
		int[][] startBoard = new int[24][2];
		int startWhiteCaptured = whiteChipsCaptured;
		int startBlackCaptured = blackChipsCaptured;
		Pair<Integer, Integer> startOffboard = new Pair<Integer, Integer>(offboard.getFirst(), offboard.getLast());
		for (int i = 0; i <= 23; ++i) {
			startBoard[i][0] = board[i][0];
			startBoard[i][1] = board[i][1];
		}

		Set<List<Pair<Integer, Integer>>> legalMoves = new HashSet<List<Pair<Integer, Integer>>>();
		int longestSequence = 0;
		int biggestDice = 0;

		// consider both dice orders
		Set<Pair<Integer, Integer>> diceOrders = new HashSet<Pair<Integer, Integer>>();
		diceOrders.add(dice);
		diceOrders.add(new Pair<Integer, Integer>(dice.getLast(), dice.getFirst()));

		for (Pair<Integer, Integer> diceOrder : diceOrders) {
			for (int i = -1; i <= 24; ++i) {
				Pair<Integer, Integer> move1 = new Pair<Integer, Integer>(i,
						getEndTriangle(i, diceOrder.getFirst(), player));
				if (isPlayerChipAtIndex(i, player) && executeMove(move1)) {

					// save the state after the first move
					int[][] currBoard = new int[24][2];
					int currWhiteCaptured = whiteChipsCaptured;
					int currBlackCaptured = blackChipsCaptured;
					Pair<Integer, Integer> currOffboard = new Pair<Integer, Integer>(offboard.getFirst(),
							offboard.getLast());
					for (int k = 0; k <= 23; ++k) {
						currBoard[k][0] = board[k][0];
						currBoard[k][1] = board[k][1];
					}

					for (int j = -1; j <= 24; ++j) {
						Pair<Integer, Integer> move2 = new Pair<Integer, Integer>(j,
								getEndTriangle(j, diceOrder.getLast(), player));
						if (isPlayerChipAtIndex(j, player) && executeMove(move2)) {

							// we found a legal move order
							List<Pair<Integer, Integer>> moveOrder = new LinkedList<Pair<Integer, Integer>>();
							moveOrder.add(move1);
							moveOrder.add(move2);

							// we only want the longest sequences of moves
							if (longestSequence != 2) {
								legalMoves.clear();
							}
							legalMoves.add(moveOrder);

							longestSequence = 2;
							// with a sequence of length 2, the biggest dice is no longer a concern
							biggestDice = 7;

							// restore the state to after the first move
							for (int idx = 0; idx <= 23; ++idx) {
								board[idx][0] = currBoard[idx][0];
								board[idx][1] = currBoard[idx][1];
							}
							whiteChipsCaptured = currWhiteCaptured;
							blackChipsCaptured = currBlackCaptured;
							offboard = new Pair<Integer, Integer>(currOffboard.getFirst(), currOffboard.getLast());

						} else {
							// we want the longest sequence or the bigger dice
							if (longestSequence == 2 || (diceOrder.getFirst() < biggestDice)) {
								continue;
							}
							List<Pair<Integer, Integer>> moveOrder = new LinkedList<Pair<Integer, Integer>>();
							moveOrder.add(move1);
							biggestDice = diceOrder.getFirst();
							longestSequence = 1;
							legalMoves.add(moveOrder);
						}
					}

					// after considering all second move options, reset the state to the starting one
					for (int idx = 0; idx <= 23; ++idx) {
						board[idx][0] = startBoard[idx][0];
						board[idx][1] = startBoard[idx][1];
					}
					whiteChipsCaptured = startWhiteCaptured;
					blackChipsCaptured = startBlackCaptured;
					offboard = new Pair<Integer, Integer>(startOffboard.getFirst(), startOffboard.getLast());
				}
			}
		}

		if (dice.getFirst() != dice.getLast() || longestSequence != 2) {
			return legalMoves;
		}

		/*
		 * In case of doubles there could be a sequence of more than 2 moves, so we do the entire procedure again with
		 * small simplifications:
		 *     - we know that both dice are the same, so there is only 1 order to consider
		 *     - we don't have to bother with the rule of using the biggest dice
		 */

		Set<List<Pair<Integer, Integer>>> finalLegalMoves = new HashSet<List<Pair<Integer, Integer>>>();

		for (List<Pair<Integer, Integer>> moveOrder : legalMoves) {
			for (Pair<Integer, Integer> move : moveOrder) {
				executeMove(move);
			}

			// save the state after the initial 2 moves
			int[][] midBoard = new int[24][2];
			int midWhiteCaptured = whiteChipsCaptured;
			int midBlackCaptured = blackChipsCaptured;
			Pair<Integer, Integer> midOffboard = new Pair<Integer, Integer>(offboard.getFirst(), offboard.getLast());
			for (int i = 0; i <= 23; ++i) {
				midBoard[i][0] = board[i][0];
				midBoard[i][1] = board[i][1];
			}

			for (int i = -1; i <= 24; ++i) {
				Pair<Integer, Integer> move1 = new Pair<Integer, Integer>(i,
						getEndTriangle(i, dice.getFirst(), player));
				if (isPlayerChipAtIndex(i, player) && executeMove(move1)) {

					// save the state after the third move
					int[][] currBoard = new int[24][2];
					int currWhiteCaptured = whiteChipsCaptured;
					int currBlackCaptured = blackChipsCaptured;
					Pair<Integer, Integer> currOffboard = new Pair<Integer, Integer>(offboard.getFirst(),
							offboard.getLast());
					for (int k = 0; k <= 23; ++k) {
						currBoard[k][0] = board[k][0];
						currBoard[k][1] = board[k][1];
					}

					for (int j = -1; j <= 24; ++j) {
						Pair<Integer, Integer> move2 = new Pair<Integer, Integer>(j,
								getEndTriangle(j, dice.getLast(), player));
						if (isPlayerChipAtIndex(j, player) && executeMove(move2)) {

							// we found a sequence of 4 moves
							List<Pair<Integer, Integer>> finalMoveOrder = new LinkedList<Pair<Integer, Integer>>();
							for (Pair<Integer, Integer> move : moveOrder) {
								finalMoveOrder.add(move);
							}

							finalMoveOrder.add(move1);
							finalMoveOrder.add(move2);
							// we only want the longest sequence
							if (longestSequence != 4) {
								finalLegalMoves.clear();
							}
							finalLegalMoves.add(finalMoveOrder);
							longestSequence = 4;

							// restore the state to after the third move
							for (int idx = 0; idx <= 23; ++idx) {
								board[idx][0] = currBoard[idx][0];
								board[idx][1] = currBoard[idx][1];
							}
							whiteChipsCaptured = currWhiteCaptured;
							blackChipsCaptured = currBlackCaptured;
							offboard = new Pair<Integer, Integer>(currOffboard.getFirst(), currOffboard.getLast());
						} else {
							// we only want the longest sequence
							if (longestSequence > 3) {
								continue;
							}
							List<Pair<Integer, Integer>> finalMoveOrder = new LinkedList<Pair<Integer, Integer>>();
							for (Pair<Integer, Integer> move : moveOrder) {
								finalMoveOrder.add(move);
							}
							finalMoveOrder.add(move1);
							finalLegalMoves.add(finalMoveOrder);
							// we found a sequence of length 3
							longestSequence = 3;
						}
					}

					// after considering all fourth move options, restore the state to after the initial 2 moves
					for (int idx = 0; idx <= 23; ++idx) {
						board[idx][0] = midBoard[idx][0];
						board[idx][1] = midBoard[idx][1];
					}
					whiteChipsCaptured = midWhiteCaptured;
					blackChipsCaptured = midBlackCaptured;
					offboard = new Pair<Integer, Integer>(midOffboard.getFirst(), midOffboard.getLast());
				}
			}

			// after considering all possibilities after the initial two moves, reset the state to the initial one
			for (int idx = 0; idx <= 23; ++idx) {
				board[idx][0] = startBoard[idx][0];
				board[idx][1] = startBoard[idx][1];
			}
			whiteChipsCaptured = startWhiteCaptured;
			blackChipsCaptured = startBlackCaptured;
			offboard = new Pair<Integer, Integer>(startOffboard.getFirst(), startOffboard.getLast());
		}

		// if we found no additional moves, return the original move orders of size 2
		if (finalLegalMoves.size() == 0) {
			return legalMoves;
		} else {
			return finalLegalMoves;
		}
	}

	/**
	 * Removes a chip at index {@code pos}.
	 *
	 * @param pos the index from where it removes a chip
	 */
	private void removeChip(int pos) {
		board[pos][0] -= 1;
		// if we removed the last chip, there is no player at that position afterwards
		if (board[pos][0] == 0) {
			board[pos][1] = 0;
		}
	}

	/**
	 * If it is physically possible to move the chips specified by the {@code move},
	 * it executes that {@code move}.
	 *
	 * @param move a pair of the start and end position, where {@code -1} and
	 *             {@code 24} have special meaning
	 *             <ul>
	 *             <li>start = -1: White is placing a captured chip
	 *             <li>start = 24: Black is placing a captured chip
	 *             <li>end = 24: White is moving a piece off the board
	 *             <li>end = -1: Black is moving a piece off the board
	 *             </ul>
	 * @return Whether the move was successfully executed.
	 */
	public boolean executeMove(Pair<Integer, Integer> move) {
		int start = move.getFirst();
		int end = move.getLast();
		// String errorMsg = "ERROR - could not move chip from " + String.valueOf(start)
		// + " to " + String.valueOf(end);

		/*
		 * In order for white to bear off, all 15 of his chips need to be either offboard or in his last zone
		 * which is indicies from 18 to 23.
		 */
		if (whiteChipsCaptured != 0) {
			whiteFinal = false;
		} else {
			int numWhiteFinal = 0;
			for (int i = 18; i <= 23; ++i) {
				if (board[i][1] == 1) {
					numWhiteFinal += board[i][0];
				}
			}
			numWhiteFinal += offboard.getFirst();
			if (numWhiteFinal == 15) {
				whiteFinal = true;
			} else {
				whiteFinal = false;
			}
		}

		/*
		 * In order for black to bear off, all 15 of his chips need to be either offboard or in his last zone
		 * which is indicies from 0 to 5.
		 */
		if (blackChipsCaptured != 0) {
			blackFinal = false;
		} else {
			int numBlackFinal = 0;
			for (int i = 0; i <= 5; ++i) {
				if (board[i][1] == -1) {
					numBlackFinal += board[i][0];
				}
			}
			numBlackFinal += offboard.getLast();
			if (numBlackFinal == 15) {
				blackFinal = true;
			} else {
				blackFinal = false;
			}
		}

		if (start <= -2) {
			// System.out.println(errorMsg);
			// System.out.println("Starting position too small!");
			return false;
		}
		if (start >= 25) {
			// System.out.println(errorMsg);
			// System.out.println("Starting position too large!");
			return false;
		}
		if (end <= -2) {
			// System.out.println(errorMsg);
			// System.out.println("End position too small!");
			return false;
		}
		if (end >= 25) {
			// System.out.println(errorMsg);
			// System.out.println("End position too large!");
			return false;
		}

		// preventing index out of bounds errors in other parts
		if ((start == -1 && end == -1) || (start == -1 && end == 24) || (start == 24 && end == -1)
				|| (start == 24 && end == 24)) {
			// System.out.println(errorMsg);
			// System.out.println("Both start and end imply special moves!");
			return false;
		}

		// special move

		// placing a captured chip
		if (start == -1 || start == 24) {
			if (start == -1 && whiteChipsCaptured >= 1) {
				// if there's a black chip, we need to do some work
				if (board[end][1] == -1) {
					if (board[end][0] >= 2) {
						// System.out.println(errorMsg);
						// System.out.println("There is more than one chip of different color in the final position!");
						return false;
					} else {
						board[end][0] = 1;
						board[end][1] = 1;
						blackChipsCaptured += 1;
						whiteChipsCaptured -= 1;
						// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
						return true;
					}
				}
				// otherwise we just place
				else {
					board[end][0] += 1;
					board[end][1] = 1;
					whiteChipsCaptured -= 1;
					// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
					return true;
				}
			} else if (start == 24 && blackChipsCaptured >= 1) {
				// if there's a white chip, we need to do some work
				if (board[end][1] == 1) {
					if (board[end][0] >= 2) {
						// System.out.println(errorMsg);
						// System.out.println("There is more than one chip of different color in the final position!");
						return false;
					} else {
						board[end][0] = 1;
						board[end][1] = -1;
						whiteChipsCaptured += 1;
						blackChipsCaptured -= 1;
						// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
						return true;
					}
				}
				// otherwise we just place
				else {
					board[end][0] += 1;
					board[end][1] = -1;
					blackChipsCaptured -= 1;
					// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
					return true;
				}
			} else {
				// System.out.println(errorMsg);
				// System.out.println("There are no captured chips to place!");
				return false;
			}
		}
		// taking a chip off the board
		if (end == -1 || end == 24) {
			if (board[start][0] <= 0) {
				// System.out.println(errorMsg);
				// System.out.println("Starting position is empty!");
				return false;
			}
			if (end == 24) {
				if (!whiteFinal) {
					// System.out.println(errorMsg);
					// System.out.println("Not all white chips are in the final sector!");
					return false;
				} else {
					removeChip(start);
					offboard.setFirst(offboard.getFirst() + 1);
					// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
					return true;
				}
			}
			if (end == -1) {
				if (!blackFinal) {
					// System.out.println(errorMsg);
					// System.out.println("Not all black chips are in the final sector!");
					return false;
				} else {
					removeChip(start);
					offboard.setLast(offboard.getLast() + 1);
					// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
					return true;
				}
			}
		}

		// regular move

		// there need to be chips at the starting position
		if (board[start][0] <= 0) {
			// System.out.println(errorMsg);
			// System.out.println("Starting position is empty!");
			return false;
		}
		// captured chips need to be played first
		if ((board[start][1] == 1 && whiteChipsCaptured != 0) || (board[start][1] == -1 && blackChipsCaptured != 0)) {
			// System.out.println(errorMsg);
			// System.out.println("Captured chips need to be played first!");
			return false;
		}

		// end is empty
		if (board[end][0] == 0) {
			board[end][0] = 1;
			board[end][1] = board[start][1];
			removeChip(start);
			// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
			return true;
		}
		// end is same color
		else if (board[end][1] == board[start][1]) {
			board[end][0] += 1;
			removeChip(start);
			// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
			return true;
		}
		// end is different color
		else {
			if (board[end][0] == 1) {
				if (board[end][1] == 1) {
					whiteChipsCaptured += 1;
				} else {
					blackChipsCaptured += 1;
				}
				// we have exactly 1 chip here of the same color
				board[end][0] = 1;
				board[end][1] = board[start][1];
				removeChip(start);
				// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
				return true;
			} else {
				// System.out.println(errorMsg);
				// System.out.println("There is more than one chip of different color in the final position!");
				return false;
			}
		}
	}
}
