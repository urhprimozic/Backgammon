// still heavily inspired by the work at https://github.com/suragnair/alpha-zero-general

package ai;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import rules.Board;
import rules.Game;
import utils.Pair;

/**
 * The implementation of MCTS. It optimizes the probabilities of a move being
 * played in a position (policy optimization) and the random playout is replaced
 * with a heuristic for evaluating each possible move as well as giving a value
 * to the board as a whole.
 */
public class MonteCarloTreeSearch {

	/**
	 * The allowed computation time before deciding on a move order.
	 */
	private int timeMilli = 1000;
	private final double cpuct = 1.0;
	private final double EPS = 1e-8;

	/**
	 * <p>
	 * Keeps track of the move evaluation and the number of visits for a given move
	 * order in the position.
	 * </p>
	 * {@code key = Pair<String, List<Pair<Integer, Integer>>>(String representation, move order)}<br>
	 * {@code value = Pair<Double, Integer>(move evaluation, number of visits)}
	 */
	private Map<Pair<String, List<Pair<Integer, Integer>>>, Pair<Double, Integer>> stateActionMap;

	/**
	 * Stores the nodes of MCTS. Under each positions' string representation is
	 * stored a {@link MCTSMapEntry}.
	 */
	private Map<String, MCTSMapEntry> stateMap;

	private int gamePlayer;
	private Heuristic heuristic;

	public MonteCarloTreeSearch(Heuristic heuristic, int p) {
		this.heuristic = heuristic;
		this.gamePlayer = p;

		stateActionMap = new HashMap<Pair<String, List<Pair<Integer, Integer>>>, Pair<Double, Integer>>();
		stateMap = new HashMap<String, MCTSMapEntry>();
	}

	/**
	 * Sets a new time limit for MCTS.
	 *
	 * @param s time in miliseconds
	 */
	protected void setTimeLimit(int s) {
		timeMilli = s;
	}

	/**
	 * Clears the saved nodes when there are too many of them. In this case when
	 * there are over {@code 20000}. (There are smarter ways of doing this)
	 */
	public void pruneTree() {
		if (stateActionMap.size() > 20000) {
			stateActionMap.clear();
		}
		if (stateMap.size() > 20000) {
			stateMap.clear();
		}
	}

	/**
	 * Gets the probabilities that a move is played in the current position for each
	 * legal move. The probability that a move will be played is given by
	 * {@code numOfVisits / numAllVisits}.
	 *
	 * @param board the current board state
	 * @return A {@code Map} assigning each legal move a probability. These
	 *         probabilities sum up to {@code 1}.
	 */
	public Map<List<Pair<Integer, Integer>>, Float> getActionProb(Board board) {
		// we search the canonical form as there is no functional difference between black or white
		Board canonicalBoard = Game.getCanonicalForm(board, gamePlayer);
		long start = System.currentTimeMillis();
		// perform Monte Carlo searches for as long as timeMilli allows
		while (start + timeMilli > System.currentTimeMillis()) {
			search(canonicalBoard);
		}

		String s = Game.stringRepresentation(canonicalBoard);
		Set<List<Pair<Integer, Integer>>> legalMoves = canonicalBoard.getLegalMoves(1);
		Map<List<Pair<Integer, Integer>>, Integer> counts = new HashMap<List<Pair<Integer, Integer>>, Integer>();

		// store the amount of times each move has been considered
		for (List<Pair<Integer, Integer>> moveOrder : legalMoves) {
			Pair<String, List<Pair<Integer, Integer>>> sa = new Pair<String, List<Pair<Integer, Integer>>>(s,
					moveOrder);
			Pair<Double, Integer> entry = stateActionMap.get(sa);
			counts.put(moveOrder, entry == null ? 0 : entry.getLast());

		}
		// we can clean up the tree only after counting up the visits
		System.out.println("state action map: " + stateActionMap.size());
		System.out.println("state map: " + stateMap.size());
		pruneTree();

		// sum up the number of visits
		Map<List<Pair<Integer, Integer>>, Float> probs = new HashMap<List<Pair<Integer, Integer>>, Float>();
		float sum = 0;
		for (Entry<List<Pair<Integer, Integer>>, Integer> e : counts.entrySet()) {
			sum += e.getValue();
		}

		// the probability of a move being played is the number of visits divided by the total number of visits
		for (Entry<List<Pair<Integer, Integer>>, Integer> e : counts.entrySet()) {
			if (gamePlayer == 1) {
				probs.put(e.getKey(), e.getValue() / sum);
			}
			// since the search was done on the canonical form, the move needs to be translated to black's point of view
			else {
				List<Pair<Integer, Integer>> moveOrder = new LinkedList<Pair<Integer, Integer>>();
				for (Pair<Integer, Integer> move : e.getKey()) {
					moveOrder.add(new Pair<Integer, Integer>(23 - move.getFirst(), 23 - move.getLast()));
				}
				probs.put(moveOrder, e.getValue() / sum);
			}
		}
		return probs;
	}

	/**
	 * Gets the negative value of the evaluation of the current canonical form. This
	 * is the main MCTS loop.
	 *
	 * @param board the canonical form of the board
	 * @return The evaluation of the current canonical form.
	 */
	public double search(Board board) {
		String s = Game.stringRepresentation(board);
		MCTSMapEntry entry = stateMap.get(s);

		// if the state hasn't been visited before
		if (entry == null) {
			entry = new MCTSMapEntry();
			stateMap.put(s, entry);
		}
		if (entry.E == -Double.MAX_VALUE) {
			entry.E = board.getGameEnded(1);
		}
		// if the game is over in the current position, just return the negative value
		if (entry.E != 0) {
			return -entry.E;
		}
		if (entry.P == null) {
			Pair<Map<List<Pair<Integer, Integer>>, Double>, Double> result = heuristic.get(board);
			entry.P = result.getFirst();
			double v = result.getLast();

			// normalize the evaluations so that they become probabilities and sum up to 1
			Map<List<Pair<Integer, Integer>>, Double> arr = entry.P;
			double sum = 0;
			for (double val : arr.values()) {
				sum += val;
			}
			if (sum > 0) {
				for (Entry<List<Pair<Integer, Integer>>, Double> e : arr.entrySet()) {
					e.setValue(e.getValue() / sum);
				}
				entry.P = arr;
			} else {
				entry.P = new HashMap<List<Pair<Integer, Integer>>, Double>();
			}

			entry.V = arr.keySet();
			entry.N = 0;
			return -v;
		}

		Set<List<Pair<Integer, Integer>>> valids = entry.V;
		double currBest = -Double.MAX_VALUE;
		List<Pair<Integer, Integer>> bestAction = new LinkedList<Pair<Integer, Integer>>();

		for (List<Pair<Integer, Integer>> moveOrder : valids) {
			double u = 0;
			Pair<String, List<Pair<Integer, Integer>>> p = new Pair<String, List<Pair<Integer, Integer>>>(s, moveOrder);
			Pair<Double, Integer> saVal = stateActionMap.get(p);

			// the regular formula for the upper confidence bound
			if (saVal != null) {
				double QVal = saVal.getFirst();
				int NVal = saVal.getLast();
				u = QVal + cpuct * entry.P.get(moveOrder) * Math.sqrt(entry.N) / (1 + NVal);
			}
			// the version for when the node hasn't been visited yet
			else {
				u = cpuct * entry.P.get(moveOrder) * Math.sqrt(entry.N + EPS);
			}
			// we keep track of the best computed value so far
			if (u > currBest) {
				currBest = u;
				bestAction = moveOrder;
			}
		}

		// we get the next canonical state
		Board nextBoard = Game.getNextState(board, bestAction);
		nextBoard = Game.getCanonicalForm(nextBoard, -1);

		// the value of the current state is the negative evaluation of the next state
		double v = search(nextBoard);

		Pair<String, List<Pair<Integer, Integer>>> bestCombo = new Pair<String, List<Pair<Integer, Integer>>>(s,
				bestAction);
		Pair<Double, Integer> saVal = stateActionMap.get(bestCombo);

		// if this move order has been tried before, the new value is the updated average
		if (saVal != null) {
			double QVal = saVal.getFirst();
			int NVal = saVal.getLast();

			saVal.setFirst((NVal * QVal + v) / (NVal + 1));
			saVal.setLast(NVal + 1);
		}
		// otherwise we add the node to the previously saved ones
		else {
			stateActionMap.put(bestCombo, new Pair<Double, Integer>(v, 1));
		}
		entry.N += 1;

		// return the negative evaluation at the end
		return -v;
	}

}
