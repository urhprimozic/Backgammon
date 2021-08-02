package ai;

import java.util.List;

import rules.Board;
import utils.Pair;

/**
 * Hevristic that inputs current board and outs Pair(p, v),
 * 
 * @Pair(v, p) v - quality of the board from {@code [-1,1]}, p - propability for
 * taken action...
 *
 * 
 * 
 * 
 * Current idea: SUM_over_triangles_(CONSTANT(triangle, player) *
 * NUM_OF_CHIPS(triangle) ), constants increase with being closer to the home
 * board
 * 
 * only 1 chip - very bad , therefore if NUM_OF_CHIPS == 1 --> CONSTANT =
 * -BADCONSTANT
 */
public class Hevristic {
    private double[] constants;
    private double badConstant;

    public Hevristic(double[] constants, double badConstant) {
        this.constants = constants;
        this.badConstant = badConstant;
    }

    /**
     * Calculates Pair(p, v) for current board
     * 
     * @param board , dice- current dice throws
     * @return
     */
    public Pair<float[], Float> get(Board board, Pair<Integer, Integer> dice) {
        int player = 1;
        List<List<Pair<Integer, Integer>>> legalMoves = board.getLegalMoves(player, dice);

        // propability - TODO mjbi daš trapastim potezam, aka da je slabo postavt enga
        // samega na trikontik
        float[] p = new float[legalMoves.size()];
        System.out.println("Calculating random propabitliy..");
        for (int i = 0; i < legalMoves.size(); i++) {
            p[i] = (float) Math.random();
        }

        // v - quality of the board
        // TODO a to spremenim v [-1, 1] ??
        float v = (float) 0.;
        for (int t = 0; t < 24; t++) {
            int i = t;
            // constants are giver for the white player
            if (player == -1)
                i = 23 - t;

            // check if a triangle has just one and is not in home board
            if (board.board[t][0] == 1 && t >= 6)
                v -= player * board.board[t][1] * badConstant;
            else if (board.board[t][0] == 0)
                continue;
            else {
                v += player * board.board[t][1] * board.board[t][0] * constants[i];
            }

        }

        return new Pair<float[], Float>(p, v);
    }

    /**
     * 
     * @return (consants, badconstant) for hevristic. Picked with very intense
     *         scientific method of AugenMass.
     */
    public static Pair<double[], Double> getConstants() {
        // constants
        double P = 1.5;
        double[] constants = new double[24];
        for (int i = 0; i < 24; i++) {
            if (i < 6) {
                // homeboard
                constants[i] = Math.pow((double) (24), P);
            } else// increase with 
                constants[i] = Math.pow((double) (24 - i), P);
        }

        //bad constant
        double badConstant =  Math.pow(23, P);
        return new Pair(constants, badConstant);
    }
}
