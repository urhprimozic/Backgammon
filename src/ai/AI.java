package ai;

import java.util.List;
import ai.djl.translate.TranslateException;
import rules.GameVisible;
import utils.WhoIsPlaying;
import utils.Coordinates;

public class AI extends WhoIsPlaying {
	
	private MonteCarloTreeSearch mcts;
	
	public AI() {
		super("JonMikos + UrhPrimozic");
		mcts = null;
		
	}
	
	public Coordinates izberiPotezo(GameVisible gameVisible) throws TranslateException {
		if (mcts == null) {
			mcts = new MonteCarloTreeSearch(new NNet(), gameVisible.player);
		}
		float[] probs = mcts.getActionProb(gameVisible.board);
		
		double sum = 0;
		double cuttoff = Math.random();
		for (int i = 0; i < probs.length; ++i) {
			if (sum + probs[i] >= cuttoff) {
				return new Coordinates(i / gameVisible.N, i % gameVisible.N);
			}
			sum += probs[i];	
		}
		
		// Fallback plan, should never ever happen but floats are scary
		List<Coordinates> mozne = gameVisible.board.getLegalMoves();
		return mozne.get((int)(Math.random() * mozne.size()));
	}
}
