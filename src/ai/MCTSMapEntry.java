package ai;

import java.util.List;
import java.util.Map;

import utils.Pair;

public class MCTSMapEntry {
	public int N;
	public float E;
	public List<List<Pair<Integer, Integer>>> V;
	public Map<List<Pair<Integer, Integer>>, Float> P;
	
	public MCTSMapEntry() {
		this.N = Integer.MIN_VALUE;
		this.E = -Float.MAX_VALUE;
		this.V = null;
		this.P = null;
	}
	
	public MCTSMapEntry(int N, float E, List<List<Pair<Integer, Integer>>> V, Map<List<Pair<Integer, Integer>>, Float> P) {
		this.N = N;
		this.E = E;
		this.V = V;
		this.P = P;
	}
}
