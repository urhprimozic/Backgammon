package ai;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import leader.Leader;
import rules.Game;
import utils.WhoIsPlaying;
import utils.Pair;

public class AI extends WhoIsPlaying {

	private MonteCarloTreeSearch mcts;

	public AI(int p) {
		super("JonMikos + UrhPrimozic");
		mcts = new MonteCarloTreeSearch(new Hevristic(Hevristic.getConstants()), p);

	}

	/**
	 * Sets te time limit for mcts
	 * 
	 * @param s time in miliseconds
	 */
	public void setTimeLimit(int s) {
		if (mcts != null)
			mcts.setTimeLimit(s);
	}

	@SuppressWarnings("unchecked")
	public List<Pair<Integer, Integer>> chooseMoveOrder() {
		Map<List<Pair<Integer, Integer>>, Float> probs = mcts.getActionProb(Leader.board, Leader.board.dice);
		if (probs.size() == 0) {
			return new LinkedList<Pair<Integer, Integer>>();
		}

		double sum = 0;
		double cuttoff = Math.random();
		for (Entry<List<Pair<Integer, Integer>>, Float> e : probs.entrySet()) {
			if (sum + e.getValue() >= cuttoff) {
				return e.getKey();
			}
			sum += e.getValue();
		}
		System.out.println("probs failed");
		return (List<Pair<Integer, Integer>>) probs.keySet().toArray()[(int) (Math.random() * probs.size())];
	}
}
