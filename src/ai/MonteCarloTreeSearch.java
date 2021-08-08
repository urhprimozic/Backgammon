//  still heavily inspired by the work at https://github.com/suragnair/alpha-zero-general
// also resebles our work for gomoku

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

public class MonteCarloTreeSearch {

	private int timeMilli = 1000;
	private final double cpuct = 1.0;
	private final double EPS = 1e-8;

	private Map<Pair<String, List<Pair<Integer, Integer>>>, Pair<Double, Integer>> stateActionMap;
	private Map<String, MCTSMapEntry> stateMap;

	private int gamePlayer;
	private Hevristic hevristic;

	public MonteCarloTreeSearch(Hevristic hevristic, int p) {
		this.hevristic = hevristic;
		//	this.nnet = nnet;
		this.gamePlayer = p;

		stateActionMap = new HashMap<Pair<String, List<Pair<Integer, Integer>>>, Pair<Double, Integer>>();
		stateMap = new HashMap<String, MCTSMapEntry>();
	}

	/**
	 * Sets a new time limit for mcts
	 *
	 * @param s time in miliseconds
	 */
	protected void setTimeLimit(int s) {
		timeMilli = s;
	}

	public void pruneTree() {
		if (stateActionMap.size() > 20000) {
			stateActionMap.clear();
		}
		if (stateMap.size() > 20000) {
			stateMap.clear();
		}
	}

	public Map<List<Pair<Integer, Integer>>, Float> getActionProb(Board board, Pair<Integer, Integer> dice) {

		Board canonicalBoard = Game.getCannonicalForm(board, gamePlayer);
		long start = System.currentTimeMillis();
		int n = 0;
		while (start + timeMilli > System.currentTimeMillis()) {
			//		while (n < 10000) {
			search(canonicalBoard, dice);
			n++;
		}

		System.out.println("Stevilo simulacij: " + n);

		String s = Game.stringRepresentation(canonicalBoard, dice);
		List<List<Pair<Integer, Integer>>> legalMoves = canonicalBoard.getLegalMoves(1, dice);
		Map<List<Pair<Integer, Integer>>, Integer> counts = new HashMap<List<Pair<Integer, Integer>>, Integer>();

		for (List<Pair<Integer, Integer>> moveOrder : legalMoves) {
			Pair<String, List<Pair<Integer, Integer>>> sa = new Pair<String, List<Pair<Integer, Integer>>>(s,
					moveOrder);
			Pair<Double, Integer> entry = stateActionMap.get(sa);
			counts.put(moveOrder, entry == null ? 0 : entry.getLast());

		}

		pruneTree();

		Map<List<Pair<Integer, Integer>>, Float> probs = new HashMap<List<Pair<Integer, Integer>>, Float>();
		float sum = 0;
		for (Entry<List<Pair<Integer, Integer>>, Integer> e : counts.entrySet()) {
			sum += e.getValue();
		}

		for (Entry<List<Pair<Integer, Integer>>, Integer> e : counts.entrySet()) {
			if (gamePlayer == 1) {
				probs.put(e.getKey(), e.getValue() / sum);
			} else {
				List<Pair<Integer, Integer>> moveOrder = new LinkedList<Pair<Integer, Integer>>();
				for (Pair<Integer, Integer> move : e.getKey()) {
					moveOrder.add(new Pair<Integer, Integer>(23 - move.getFirst(), 23 - move.getLast()));
				}
				probs.put(moveOrder, e.getValue() / sum);
			}
		}
		return probs;
	}

	public double search(Board board, Pair<Integer, Integer> dice) {
		String s = Game.stringRepresentation(board, dice);

		MCTSMapEntry entry = stateMap.get(s);

		if (entry == null) {
			entry = new MCTSMapEntry();
			stateMap.put(s, entry);
		}

		if (entry.E == -Double.MAX_VALUE) {
			entry.E = board.getGameEnded(1);
		}
		if (entry.E != 0) {
			return -entry.E;
		}
		if (entry.P == null) {
			Pair<Map<List<Pair<Integer, Integer>>, Double>, Double> result = hevristic.get(board, dice); // nnet.predict(board);
			entry.P = result.getFirst();
			double v = result.getLast();

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

			if (saVal != null) {
				double QVal = saVal.getFirst();
				int NVal = saVal.getLast();
				u = QVal + cpuct * entry.P.get(moveOrder) * Math.sqrt(entry.N) / (1 + NVal);
			} else {
				u = cpuct * entry.P.get(moveOrder) * Math.sqrt(entry.N + EPS);
			}
			if (u > currBest) {
				currBest = u;
				bestAction = moveOrder;
			}
		}

		List<Pair<Integer, Integer>> a = bestAction;

		Board nextBoard = Game.getNextState(board, a);
		nextBoard = Game.getCannonicalForm(nextBoard, -1);

		double v = search(nextBoard, nextBoard.dice);

		Pair<String, List<Pair<Integer, Integer>>> bestCombo = new Pair<String, List<Pair<Integer, Integer>>>(s, a);
		Pair<Double, Integer> saVal = stateActionMap.get(bestCombo);

		if (saVal != null) {
			double QVal = saVal.getFirst();
			int NVal = saVal.getLast();

			saVal.setFirst((NVal * QVal + v) / (NVal + 1));
			saVal.setLast(NVal + 1);
		} else {
			stateActionMap.put(bestCombo, new Pair<Double, Integer>(v, 1));
		}
		entry.N += 1;
		return -v;
	}

}
