package inteligenca;

public class MCTSMapEntry {
	public int N;
	public float E;
	public int[] V;
	public float[] P;
	
	public MCTSMapEntry() {
		this.N = Integer.MIN_VALUE;
		this.E = -Float.MAX_VALUE;
		this.V = null;
		this.P = null;
	}
	
	public MCTSMapEntry(int N, float E, int[] V, float[] P) {
		this.N = N;
		this.E = E;
		this.V = V;
		this.P = P;
	}
}
