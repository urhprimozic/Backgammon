package leader;

/**
 * Enum type for the player type
 * 
 * C -computer H human
 * 
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
