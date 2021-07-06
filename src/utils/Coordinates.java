//props redundant? 
//TODO REMOVE

package utils;

public class Coordinates {
	private int x;
	private int y;
	
	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() { 
		return x; 
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return "Coordinates [x=" + x + ", y=" + y + "]";
	}
	
	@Override 
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Coordinates k = (Coordinates) o;
		return this.x == k.x && this.y == k.y;
	}
	
	@Override
	public int hashCode () {
		int x = this.x ; int y = this.y;
		return (x + y) * (x + y + 1) / 2 + y;
	}
}
