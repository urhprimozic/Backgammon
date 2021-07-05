// a tole sploh rabva kje?

package utils;

import java.util.List;

public class Utils {
	
	public static int maxInIntArray(int[] arr) {
		int max = Integer.MIN_VALUE;
		for (int x : arr) {
			max = Math.max(max, x);
		}
		return max;
	}
	
	public static int sumIntArray(int[] arr) {
		int sum = 0;
		for (int x : arr) {
			sum += x;
		}
		return sum;
	}
	
	public static double sumDoubleArray(double[] arr) {
		double sum = 0;
		for (double x : arr) {
			sum += x;
		}
		return sum;
	}
	
	public static float sumFloatArray(float[] arr) {
		float sum = 0;
		for (float x : arr) {
			sum += x;
		}
		return sum;
	}
	
	public static int sumIntegerList(List<Integer> li) {
		int sum = 0;
		for (int x : li) {
			sum += x;
		}
		return sum;
	}
}
