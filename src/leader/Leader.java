package leader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingWorker;

import ai.AI;
import gui.MainFrame;
import rules.Board;
import utils.Pair;

/**
 * A collection of static methods and variables, needed for the proper working
 * of the game loop. The needed game components are:
 * <ul>
 * <li>{@link #Board Board} the implementation of the game rules
 * <li>{@link #MainFrame MainFrame} the graphical user interface
 * <li>{@link #AI AI} the implementation of a computer opponent
 * </ul>
 *
 * Alongside the game components, the following variables keep track of the game
 * state:
 * <ul>
 * <li><code>playerType</code> a map keeping track of computer and human players
 * <li><code>player</code> the current player
 * <li><code>movesMade</code> the amount of moves the current player has made
 * this turn
 * <li><code>humanRound</code> whether the current player is a human player
 * <li><code>diceRolled</code> whether the dice have been rolled already for
 * this turn
 * <li><code>legalMoves</code> a list of all possible move orders the current
 * player can make after the dice have been rolled
 * <li><code>movesPlayed</code> the moves made this turn
 * <li><code>maxMoves</code> the number of moves the player must make, given by
 * the longest sequence of legal moves
 * <li><code>comp1</code> and <code>comp2</code> the computer opponents, one for
 * each player spot
 * </ul>
 */
public class Leader {

	public static Map<Integer, PlayerType> playerType;

	public static Board board = null;
	public static int player = 1;
	public static int movesMade = 0;

	public static MainFrame frame;

	public static boolean humanRound = true;
	public static boolean diceRolled = false;

	public static Set<List<Pair<Integer, Integer>>> legalMoves = null;
	public static List<Pair<Integer, Integer>> movesPlayed = null;
	public static int maxMoves = 0;

	public static AI comp1;
	public static AI comp2;
	// public static int mctsTimeLimit;

	/**
	 * Resets the state to the starting position and starts the game loop. This
	 * includes:
	 * <ul>
	 * <li>constructing a new {@link #Board Board}
	 * <li>setting the current player to white
	 * <li>clearing the legal moves, currently made moves and maximum moves
	 * <li>setting the {@link #AI AI} opponents with the time limit that the user
	 * has selected from {@link #MainFrame MainFrame}
	 * <li>starting the game loop
	 * </ul>
	 */
	public static void newGame() {
		board = new Board();
		player = 1;
		movesMade = 0;
		diceRolled = false;
		legalMoves = null;
		movesPlayed = null;
		maxMoves = 0;

		if (playerType.get(1) == PlayerType.C) {
			comp1 = new AI(1);
			// sets time limit
			comp1.setTimeLimit(frame.mctsTimeLimit);
		} else {
			comp1 = null;
		}
		if (playerType.get(-1) == PlayerType.C) {
			comp2 = new AI(-1);
			// sets time limit
			comp2.setTimeLimit(frame.mctsTimeLimit);
		} else {
			comp2 = null;
		}
		play();
	}

	/**
	 * <p>
	 * Runs the game loop.
	 * </p>
	 *
	 * This is done by refreshing the {@link MainFrame}, checking whether the game
	 * is over and setting <code>humanRound</code> based on if a human or computer
	 * is playing this turn. If it is a human player's turn, the user interface
	 * calls both <code>rollDice</code> and <code>playHumanMove</code> methods, as
	 * their calls are tied to human interactions with the interface. If it is the
	 * computer's move, the <code>computerTurn</code> method is called.
	 */
	public static void play() {
		frame.refreshGUI();
		double state = board.getGameEnded(player);

		if (state != 0) {
			return;
		}

		PlayerType type = playerType.get(player);
		switch (type) {
		case H:
			humanRound = true;
			break;
		case C:
			humanRound = false;
			computerTurn();
			break;
		}
	}

	/**
	 * Rolls the dice and sets the appropriate state variables.
	 *
	 * This includes:
	 * <ul>
	 * <li>calling the <code>Board.rollDice</code> method
	 * <li>setting that the dice have been rolled
	 * <li>storing the legal moves
	 * <li>setting the amount of moves that need to be played
	 * <li>clearing the currently played moves
	 * </ul>
	 */
	public static void rollDice() {
		board.rollDice();
		diceRolled = true;
		legalMoves = board.getLegalMoves(player, board.dice);

		//		System.out.println("GUI: Legal moves for player " + player);
		//		for (List<Pair<Integer, Integer>> moveOrder : legalMoves) {
		//			System.out.print("GUI: legal move: ");
		//			for (int j = 0; j < moveOrder.size(); ++j) {
		//				Pair<Integer, Integer> move = moveOrder.get(j);
		//				System.out.print(move.getFirst() + " -> " + move.getLast() + ", ");
		//			}
		//			System.out.println();
		//		}
		//		System.out.println();

		Set<List<Pair<Integer, Integer>>> legal = board.getLegalMoves(player, board.dice);

		if (legalMoves.size() == 0) {
			maxMoves = 0;
		} else {
			maxMoves = legalMoves.iterator().next().size();
		}
		movesPlayed = null;
	}

	/**
	 * <p>
	 * Recieves the move to be played, executes it and updates the current state.
	 * Upon updating the state it calls the <code>play</code> method to return to
	 * the game loop.
	 * </p>
	 *
	 * Should the move be <code>null</code>, this is taken as there being no legal
	 * moves and the player is passing the turn.
	 *
	 * @param move a <code>Pair</code> consisting of the start and end position of
	 *             the chip
	 */
	public static void playHumanMove(Pair<Integer, Integer> move) {
		if (move != null) {
			outer_loop: for (List<Pair<Integer, Integer>> moveOrder : legalMoves) {
				if (movesPlayed == null) {
					// the move needs to be the start of a legal move order
					if (move.equals(moveOrder.get(0))) {
						board.executeMove(move);
						movesPlayed = new LinkedList<Pair<Integer, Integer>>();
						movesPlayed.add(move);
						break;
					}
				} else {
					// filter out the legal move orders that do not agree with the already played moves
					for (int j = 0; j < movesPlayed.size(); ++j) {
						if (!movesPlayed.get(j).equals(moveOrder.get(j))) {
							continue outer_loop;
						}
					}
					// the move needs to be a part of a legal mover order
					if (move.equals(moveOrder.get(movesPlayed.size()))) {
						board.executeMove(move);
						movesPlayed.add(move);
						break;
					}
				}
			}
		}
		// if we made the maximum amount of legal moves, change turns
		// the case where there are no legal moves
		if (movesPlayed == null) {
			if (maxMoves == 0) {
				player *= -1;
				movesMade = 0;
				diceRolled = false;
			}
		}
		// the regular case
		else if (movesPlayed.size() == maxMoves) {
			player *= -1;
			movesMade = 0;
			diceRolled = false;
		}
		play();
	}

	/**
	 * <p>
	 * Handles the entire computer player's turn.
	 * </p>
	 *
	 * This is done by constructing a <code>SwingWorker</code> where the
	 * <code>doInBackground</code> and <code>done</code> methods are overwritten.
	 * The new <code>doInBackground</code> method rolls the dice, refreshes the user
	 * interface and requests the move order from the <code>AI</code>. The new
	 * <code>done</code> method receives the computed move order and executes each
	 * move in order. At the end it updates state variables.
	 */
	public static void computerTurn() {
		Board startBoard = board;
		SwingWorker<List<Pair<Integer, Integer>>, Void> worker = new SwingWorker<List<Pair<Integer, Integer>>, Void>() {
			@Override
			protected List<Pair<Integer, Integer>> doInBackground() {
				rollDice();
				frame.refreshGUI();
				if (player == 1) {
					return comp1.chooseMoveOrder();
				} else {
					return comp2.chooseMoveOrder();
				}
			}

			@Override
			protected void done() {
				List<Pair<Integer, Integer>> moveOrder = null;
				try {
					moveOrder = get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				;
				if (board == startBoard) {
					// System.out.println(board.dice.getFirst() + " " + board.dice.getLast());
					for (Pair<Integer, Integer> move : moveOrder) {
						// System.out.println(move.getFirst() + " -> " + move.getLast() + ", ");
						board.executeMove(move);
					}
					// System.out.println();
					player *= -1;
					movesMade = 0;
					diceRolled = false;
					play();
				}
			}
		};
		worker.execute();
	}
}
