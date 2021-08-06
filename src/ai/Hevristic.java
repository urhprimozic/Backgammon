package ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    
    private static final double[] regularScores = new double[] {1, -1./3, 0.25, 0.125}; // {blackCaptured, soloChips, offboard, final}
    private static final double[] bearingScores = new double[] {1, -1./3, 0.25, 0.125}; // {blackCaptured, soloChips, offboard, final}

    public Hevristic(Pair<double[], Double> parameters) {
        this.constants = parameters.getFirst();
        this.badConstant = parameters.getLast();
    }

    /**
     * Calculates Pair(p, v) for current board
     * 
     * @param board , dice- current dice throws
     * @return
     */
    public Pair<Map<List<Pair<Integer, Integer>>, Double>, Double> get(Board board, Pair<Integer, Integer> dice) {
    	int player = 1;
        Map<List<Pair<Integer, Integer>>, Double> p = new HashMap<List<Pair<Integer, Integer>>, Double>();
        List<List<Pair<Integer, Integer>>> legalMoves = board.getLegalMoves(player, dice);
        
        int[][] currBoard = new int[24][2];
    	int currWhiteCaptured = board.whiteChipsCaptured;
    	int currBlackCaptured = board.blackChipsCaptured;
    	Pair<Integer, Integer> currOffboard = new Pair<Integer, Integer>(board.offboard.getFirst(), board.offboard.getLast());
    	boolean currFinal = board.whiteFinal;
    	
    	int currSoloChips = 0;
    	for (int k = 0; k <= 23; ++k) {
    		currBoard[k][0] = board.board[k][0];
    		currBoard[k][1] = board.board[k][1];
    		
    		if (board.board[k][1] == 1 && board.board[k][0] == 1) {
    			currSoloChips++;
    		}
    	}
    	
    	double maxScore = -Double.MAX_VALUE;
    	int deltaBlackCaptured = 0;
    	int deltaSoloChips = 0;
    	int deltaOffboard = 0;
    	int deltaFinal = 0;
    	
        for (List<Pair<Integer, Integer>> moveOrder : legalMoves) {
        	for (Pair<Integer, Integer> move : moveOrder) {
        		board.executeMove(move);
        	}
        	
        	deltaBlackCaptured = board.blackChipsCaptured - currBlackCaptured;
        	deltaOffboard = board.offboard.getFirst() - currOffboard.getFirst();
        	if (currFinal) {
        		deltaFinal = 0;
        	}
        	else {
        		deltaFinal = board.whiteFinal ? 1 : 0;
        	}
            
            int soloChips = 0;
            for (int idx = 0; idx <= 23; ++idx) {				
				if (board.board[idx][1] == 1 && board.board[idx][0] == 1) {
	    			soloChips++;
	    		}
			}
			deltaSoloChips = soloChips - currSoloChips;
			
			double score = 0;
			double moveSize = moveOrder.size();
			
			if (currFinal) {
				if (board.blackChipsCaptured == 0) {					
					int rightBlack = -1;
					for (int i = 0; i <= 5; ++i) {
						if (board.board[i][1] == -1) {
							rightBlack = i;
							break;
						}
					}
					score += bearingScores[2] * deltaOffboard / moveSize;
					if (rightBlack != -1) {
						for (int i = rightBlack; i <= 5; ++i) {
							if (board.board[i][1] == 1 && board.board[i][0] == 1) {
								score -= bearingScores[0] / moveSize;
							}
						}
					}
				}
				else {
					score -= bearingScores[1] * deltaSoloChips / moveSize;
				}
			}
			else {
				score += regularScores[0] * deltaBlackCaptured / moveSize;
				score += regularScores[1] * deltaSoloChips / moveSize;
				score += regularScores[2] * deltaOffboard / moveSize;
				score += regularScores[3] * deltaFinal / moveSize;
			}
			
			maxScore = Math.max(maxScore, score);
			p.put(moveOrder, score);
			
			for (int idx = 0; idx <= 23; ++ idx) {
            	board.board[idx][0] = currBoard[idx][0];
				board.board[idx][1] = currBoard[idx][1];
            }
			board.whiteChipsCaptured = currWhiteCaptured;
			board.blackChipsCaptured = currBlackCaptured;
			board.offboard = new Pair<Integer, Integer>(currOffboard.getFirst(), currOffboard.getLast());
        }
        
        for (Entry<List<Pair<Integer, Integer>>, Double> e : p.entrySet()) {
        	if (maxScore < 0) {
        		if (e.getValue() < maxScore * (3./2.)) {
        			e.setValue(0.);
        		}
        		else {
        			e.setValue(e.getValue() - (maxScore * (3./2.)));
        		}
        	}
        	else if (maxScore == 0.0) {
        		if (e.getValue() < 0) {
        			e.setValue(0.);
        		}
        		else {
        			e.setValue(1.);
        		}
        	}
        	else {
        		if (e.getValue() < maxScore * (2./3.)) {
        			e.setValue(0.);
        		}
        	}
        }

        // v - quality of the board
        // TODO a to spremenim v [-1, 1] ??
        double v = 0;
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
        v = 2 * Math.atan(v) / Math.PI;

        return new Pair<Map<List<Pair<Integer, Integer>>, Double>, Double>(p, v);
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
        return new Pair<double[], Double>(constants, badConstant);
    }
}
