package ai;

import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Pair;

/**
 * A container for the necessary information about the node that
 * {@link MonteCarloTreeSearch} needs.
 */
public class MCTSMapEntry {
	/**
	 * The number of times this node has been visited.
	 */
	public int N;

	/**
	 * The evaluation of the position.
	 */
	public double E;

	/**
	 * The valid move orders that can be made.
	 */
	public Set<List<Pair<Integer, Integer>>> V;

	/**
	 * A {@code Map} assigning each move order the probability that it will be
	 * played.
	 */
	public Map<List<Pair<Integer, Integer>>, Double> P;

	/**
	 * Sets the default values that mean the node hasn't been visited yet.
	 */
	public MCTSMapEntry() {
		this.N = Integer.MIN_VALUE;
		this.E = -Double.MAX_VALUE;
		this.V = null;
		this.P = null;
	}

	/**
	 * Sets the contents of the node with the given parameters.
	 *
	 * @param N the number of times this node has been visited
	 * @param E the evaluation of the position
	 * @param V a {@code Set} of the valid move orders that can be made
	 * @param P A {@code Map} assigning each move order the probability that it will
	 *          be played
	 */
	public MCTSMapEntry(int N, float E, Set<List<Pair<Integer, Integer>>> V,
			Map<List<Pair<Integer, Integer>>, Double> P) {
		this.N = N;
		this.E = E;
		this.V = V;
		this.P = P;
	}
}
