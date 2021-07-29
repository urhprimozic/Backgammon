//  still heavily inspired by the work at https://github.com/suragnair/alpha-zero-general
// also resebles our work for gomoku

package ai;

import java.util.HashMap;
import java.util.Map;

import rules.Board;
import rules.Game;
import utils.Pair;
import utils.Utils;

public class MonteCarloTreeSearch {
		
	private final int timeMilli = 4500;
	private final double cpuct = 1.0;
	private final double EPS = 1e-8;
		
	private Map<Pair<String, Integer>, Pair<Float, Integer>> stateActionMap;
	private Map<String, MCTSMapEntry> stateMap;
	
	private int depth;
	private int gamePlayer;
	private Hevristic hevristic;
	
	public MonteCarloTreeSearch(Hevristic hevristic , int p) {
		this.hevristic = hevristic;
	//	this.nnet = nnet;
		this.gamePlayer = p;
		
		stateActionMap = new HashMap<Pair<String, Integer>, Pair<Float, Integer>>();
		stateMap = new HashMap<String, MCTSMapEntry>();
		depth = 0;
	}
	
	private long calcDepth(String s) {
		return s.chars().filter(c -> c == '1').count() / 2 + 1;
	}
	
	public void pruneTree() {
		stateActionMap.keySet().removeIf(e -> calcDepth(e.getFirst()) < depth);
		stateMap.keySet().removeIf(e -> calcDepth(e) < depth);
	}
	
	public float[] getActionProb(Board board, Pair<Integer, Integer> dice) {
		return getActionProb(board,dice,  1);
	}
	
	public float[] getActionProb(Board board, Pair<Integer, Integer> dice,double temp) {
		
		Board canonicalBoard = new Board();
		for (int i = 0; i<24;i++){
			canonicalBoard.board[i][0] = board.board[i][0];
			canonicalBoard.board[i][1] = board.board[i][1] * -gamePlayer;

		}
		
		depth += 1;
		long start = System.currentTimeMillis();
		int n = 0;
		while (start + timeMilli > System.currentTimeMillis()) {
//		while (n < 10000) {
			search(canonicalBoard, dice);
			n++;
		}
		
		System.out.println("Stevilo simulacij: " + n);
		
		String s = Game.stringRepresentation(canonicalBoard);
		int[] counts = new int[Game.getActionSize(board)];
		
		for (int i = 0; i < Game.getActionSize(board); ++i) {
			Pair<String, Integer> sa = new Pair<String, Integer>(s, i);
			Pair<Float, Integer> entry = stateActionMap.get(sa);
			counts[i] = entry == null ? 0 : entry.getLast();
		}
		
		pruneTree();
		
		float[] probs = new float[counts.length];
		int sum = Utils.sumIntArray(counts);
		
		for (int i = 0; i < counts.length; ++i) {
			float x = (float) Math.pow(counts[i], 1. / temp);
			probs[i] = x / sum;
		}
		return probs;
	}
	
	public float search(Board board, Pair<Integer, Integer> dice) {
		String s = Game.stringRepresentation(board);
		
		MCTSMapEntry entry = stateMap.get(s);
		
		if (entry == null) {
			entry = new MCTSMapEntry();
			stateMap.put(s, entry);
		}
		
		if (entry.E == -Float.MAX_VALUE) {
			entry.E = Game.getGameEnded(board, 1);
		}
		if (entry.E != 0) {
			return -entry.E;
		}
		if (entry.P == null) {
			Pair<float[], Float> result =hevristic.get(board, dice); // nnet.predict(board);
			entry.P = result.getFirst();
			float v = result.getLast();
			
			int[] valids = Game.getValidMoves(board, 1);
			float[] arr = entry.P;
			for (int i = 0; i < arr.length; ++i) {
				arr[i] = valids[i] == 1 ? arr[i] : 0;
			}
			float sum = Utils.sumFloatArray(arr);
			if (sum > 0) {
				for (int i = 0; i < arr.length; ++i) {
					arr[i] = arr[i] / sum;
				}
				entry.P = arr;
			}
			else {
				int vSum = Utils.sumIntArray(valids);
				float[] newArr = new float[valids.length];
				for (int i = 0; i < valids.length; ++i) {
					newArr[i] = valids[i] / vSum;
				}
				entry.P = newArr;
			}
			
			entry.V = valids;
			entry.N = 0;
			return -v;
		}

		int[] valids = entry.V;
		double curBest = -Double.MAX_VALUE;
		int bestAction = -1;
		
		
		for (int a = 0; a < Game.getActionSize(board); ++a) {
			if (valids[a] == 1) {
				double u = 0;
				Pair<String, Integer> p = new Pair<String, Integer>(s, a);
				Pair<Float, Integer> saVal = stateActionMap.get(p);
				
				if (saVal != null) {
					double QVal = saVal.getFirst();
					int NVal = saVal.getLast();
					u = QVal + cpuct * entry.P[a] * Math.sqrt(entry.N) / (1 + NVal);
				}
				else {
					u = cpuct * entry.P[a] * Math.sqrt(entry.N + EPS);
				}
				if (u > curBest) {
					curBest = u;
					bestAction = a;
				}
			}
		}
		
		int a = bestAction;
		
		Pair<Board, Integer> result = Game.getNextState(board, 1, a);
		Board nextBoard = result.getFirst();
		int nextPlayer = result.getLast();
		nextBoard = Game.getCannonicalForm(nextBoard, nextPlayer);
		
		float v = search(nextBoard, dice);
		
		Pair<String, Integer> bestCombo = new Pair<String, Integer>(s, a);
		Pair<Float, Integer> saVal = stateActionMap.get(bestCombo);
		
		if (saVal != null) {
			float QVal = saVal.getFirst();
			int NVal = saVal.getLast();
			
			saVal.setFirst((NVal * QVal + v) / (NVal + 1));
			saVal.setLast(NVal + 1);
		}
		else {
			stateActionMap.put(bestCombo, new Pair<Float, Integer>(v, 1));
		}
		entry.N += 1;
		return -v;
	}
	
}
