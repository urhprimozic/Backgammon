package ai;

import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Pair;

public class MCTSMapEntry {
	public int N;
	public double E;
	public Set<List<Pair<Integer, Integer>>> V;
	public Map<List<Pair<Integer, Integer>>, Double> P;
	
	public MCTSMapEntry() {
		this.N = Integer.MIN_VALUE;
		this.E = -Double.MAX_VALUE;
		this.V = null;
		this.P = null;
	}
	
	public MCTSMapEntry(int N, float E, Set<List<Pair<Integer, Integer>>> V, Map<List<Pair<Integer, Integer>>, Double> P) {
		this.N = N;
		this.E = E;
		this.V = V;
		this.P = P;
	}
}
