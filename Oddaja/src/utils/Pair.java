package utils;

import java.util.Objects;

/**
 * An implementation of a pair of objects that can be used as a key.
 *
 * @param <T1> the type of the first object
 * @param <T2> the type of the last object
 */
public class Pair<T1, T2> {
	private T1 x;
	private T2 y;

	public Pair(T1 x, T2 y) {
		this.x = x;
		this.y = y;
	}

	public T1 getFirst() {
		return x;
	}

	public T2 getLast() {
		return y;
	}

	public void setFirst(T1 x) {
		this.x = x;
	}

	public void setLast(T2 y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (getClass() != other.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Pair<T1, T2> o = (Pair<T1, T2>) other;
		return Objects.equals(x, o.x) && Objects.equals(y, o.y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
