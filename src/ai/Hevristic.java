package ai;

import rules.Board;

/**
 * Hevristic that inputs current board and outs Pair(v, p),
 * 
 * @Pair(v, p)
 * v - quality of the board from [-1,1],
 * p - propability for taken action...?
 * Current idea: SUM_over_triangles_(CONSTANT(triangle, player) * NUM_OF_CHIPS(triangle) ),
 * constants increase with being closer to the home board
 * 
 * only 1 chip - very bad , therefore if NUM_OF_CHIPS == 1 --> CONSTANT = -BADCONSTANT
 */
public class Hevristic {
    private double[] constants;
    private double badConstant;

    public Hevristic(double[] constants, double badConstant){
        this.constants = constants;
        this.badConstant = badConstant;
    }
    /**
     * Calculates Pair(v, p) for current board
     * @param board 
     * @return
     */
    public int get(Board board){
        return 1;
    }
}
