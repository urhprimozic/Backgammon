package utils;

/**
 * Enum for color of the player (black / white / empty)
 */
public enum PlayerColor {
	B, W, Empty;

	@Override
	public String toString() {
		switch (this) {
		case B:
			return "black";
		case W:
			return "white";
		case Empty:
			return "empty";
		default:
			assert false;
			return "";
		}
	}

	/**
	 *
	 * @param c integer from the set {-1, 0, 1}
	 * @return The enum representation of c.
	 */
	public PlayerColor fromInt(int c) {
		switch (c) {
		case -1:
			return B;
		case 1:
			return W;
		case 0:
			return Empty;
		default:
			assert false;
			return null;
		}
	}

	/**
	 * @return Integer representation of PlayerColor.
	 */
	public int toInt() {
		switch (this) {
		case B:
			return -1;
		case W:
			return 1;
		case Empty:
			return 0;
		default:
			assert false;
			return 0;
		}
	}

}
