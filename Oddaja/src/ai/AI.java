package ai;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import leader.Leader;
import utils.Pair;
import utils.WhoIsPlaying;

/**
 * A wrapper around {@link MonteCarloTreeSearch}, it's purpose is selecting a
 * move order based on what {@code MonteCarloTreeSearch} computes.
 */
public class AI extends WhoIsPlaying {

	private MonteCarloTreeSearch mcts;

	/**
	 * Contructs a new {@code AI} object with a new {@code MonteCarloTreeSearch}
	 * where the random playout is replaced with a heuristic.
	 *
	 * @param p which player the computer plays as
	 */
	public AI(int p) {
		super("JonMikos + UrhPrimozic");
		mcts = new MonteCarloTreeSearch(new Heuristic(Heuristic.getConstants()), p);
	}

	/**
	 * Sets the time limit for the running time of a move for
	 * {@code MonteCarloTreeSearch}.
	 *
	 * @param s time in miliseconds
	 */
	public void setTimeLimit(int s) {
		if (mcts != null)
			mcts.setTimeLimit(s);
	}

	/**
	 * Chooses a move order based on the state of {@code MonteCarloTreeSearch}.
	 *
	 * @return A {@code List} containing the moves to be played. If there are no
	 *         legal moves, the {@code List} is empty and should the
	 *         {@code MonteCarloTreeSearch} fail, it returns a random legal move
	 *         order.
	 */
	@SuppressWarnings("unchecked")
	public List<Pair<Integer, Integer>> chooseMoveOrder() {
		Map<List<Pair<Integer, Integer>>, Float> probs = mcts.getActionProb(Leader.board);
		// there are board states with no legal moves
		if (probs.size() == 0) {
			return new LinkedList<Pair<Integer, Integer>>();
		}

		// a move is chosen by adding up probabilities until the uniformly chosen cutoff is reached or exceeded
		double sum = 0;
		double cutoff = Math.random();
		for (Entry<List<Pair<Integer, Integer>>, Float> e : probs.entrySet()) {
			if (sum + e.getValue() >= cutoff) {
				return e.getKey();
			}
			sum += e.getValue();
		}
		// in case there is some problems with float arithmetic, return a legal move order with uniform probability
		// System.out.println("probs failed");
		return (List<Pair<Integer, Integer>>) probs.keySet().toArray()[(int) (Math.random() * probs.size())];
	}
}
