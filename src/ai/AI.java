package ai;

import java.util.LinkedList;
import java.util.List;
import leader.Leader;
import rules.Game;
import utils.WhoIsPlaying;
import utils.Pair;

public class AI extends WhoIsPlaying {
	
	private MonteCarloTreeSearch mcts;
	
	public AI(int p) {
		super("JonMikos + UrhPrimozic");
		double[] consts = new double[24];
		for (int i = 0; i < consts.length; ++i) {
			consts[i] = 1;
		}
		mcts = new MonteCarloTreeSearch(new Hevristic(consts, 100), p);
		
	}

	public List<Pair<Integer, Integer>> chooseMoveOrder() {
		float[] probs = mcts.getActionProb(Leader.board, Leader.board.dice);
		List<List<Pair<Integer, Integer>>> legalMoves = Leader.board.getLegalMoves(Leader.player, Leader.board.dice);
		
		double sum = 0;
		double cuttoff = Math.random();
		for (int i = 0; i < probs.length; ++i) {
			if (sum + probs[i] >= cuttoff) {
				return legalMoves.get(i);
			}
			sum += probs[i];	
		}
		
		// Fallback plan, should never ever happen but floats are scary		
		if (legalMoves.size() == 0) {
			return new LinkedList<Pair<Integer, Integer>>();
		}
		else {
			return legalMoves.get((int)(Math.random() * legalMoves.size()));
		}
	}
}
