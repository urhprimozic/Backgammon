package ai;

import java.util.LinkedList;
import java.util.List;
import ai.djl.translate.TranslateException;
import leader.Leader;
import rules.GameVisible;
import utils.WhoIsPlaying;
import utils.Coordinates;
import utils.Pair;

public class AI extends WhoIsPlaying {
	
	private MonteCarloTreeSearch mcts;
	
	public AI() {
		super("JonMikos + UrhPrimozic");
		mcts = null;
		
	}
	
	public List<Pair<Integer, Integer>> chooseMoveOrder() {
//		if (mcts == null) {
//			mcts = new MonteCarloTreeSearch(new NNet(), gameVisible.player);
//		}
//		float[] probs = mcts.getActionProb(gameVisible.board);
//		
//		double sum = 0;
//		double cuttoff = Math.random();
//		for (int i = 0; i < probs.length; ++i) {
//			if (sum + probs[i] >= cuttoff) {
//				return new Coordinates(i / gameVisible.N, i % gameVisible.N);
//			}
//			sum += probs[i];	
//		}
//		
//		// Fallback plan, should never ever happen but floats are scary
//		List<Coordinates> mozne = gameVisible.board.getLegalMoves();
//		return mozne.get((int)(Math.random() * mozne.size()));
		
		List<List<Pair<Integer, Integer>>> legalMoves = Leader.board.getLegalMoves(Leader.player, Leader.board.dice);
		if (legalMoves.size() == 0) {
			return new LinkedList<Pair<Integer, Integer>>();
		}
		else {
			return legalMoves.get((int)(Math.random() * legalMoves.size()));
		}
	}
}
