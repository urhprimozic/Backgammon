package leader;

/**
 * Enum for the player type. Has two entries:
 * <ul>
 * <li>{@code C} computer
 * <li>{@code H} human
 * </ul>
 */
public enum PlayerType {
	C, H;

	@Override
	public String toString() {
		switch (this) {
		case H:
			return "human";
		case C:
			return "computer";
		default:
			assert false;
			return "";
		}
	}

}
