package rules;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import utils.Pair;

/**
 * @variables int
 * 
 */
public class Board {
    /**
     * int[24][2] board Array of 24 size 2 arrays (pairs)
     * 
     * @usage {@code board[i][0]} - number of chips
     * 
     *        {@code board[i][1]} - {@code 1} for white, {@code 0} for empty,
     *        {@code -1} for black
     * @representation i-th pair represents (i+1)-th triangle of the board. Since a
     *                 triangle can not be shared between two different colors we
     *                 can afford coding a triangle with (num of chips on it, color
     *                 of chips)
     */
    public int[][] board;

    public int whiteChipsCaptured;
    public int blackChipsCaptured;

    public boolean whiteFinal;
    public boolean blackFinal;

    private Random rand;
    public Pair<Integer, Integer> dice;

    /**
     * Pair<Integer, Integer> offboard
     * 
     * @usage offboard.getFirst() - number of white chips that have passed off the
     *        board offboard.getLast() - number of black chips that have passed off
     *        the board
     */
    public Pair<Integer, Integer> offboard;

    /**
     * Initalize a board.
     * 
     * @return Starting board
     */
    public Board() {
        board = new int[24][2];
        // White chips
        board[0][0] = 2; // number of chips
        board[0][1] = 1; // color - 1=white, -1=Black

        board[11][0] = 5; // number of chips
        board[11][1] = 1; // color - 1=white, -1=Black

        board[16][0] = 3; // number of chips
        board[16][1] = 1; // color - 1=white, -1=Black

        board[18][0] = 5; // number of chips
        board[18][1] = 1; // color - 1=white, -1=Black

        // Black chips
        board[23][0] = 2; // number of chips
        board[23][1] = -1; // color - 1=white, -1=Black

        board[12][0] = 5; // number of chips
        board[12][1] = -1; // color - 1=white, -1=Black

        board[7][0] = 3; // number of chips
        board[7][1] = -1; // color - 1=white, -1=Black

        board[5][0] = 5; // number of chips
        board[5][1] = -1; // color - 1=white, -1=Black

        // no chips are captured at the start
        whiteChipsCaptured = 0;
        blackChipsCaptured = 0;

        // chips cannot be moved off the board at the start
        whiteFinal = false;
        blackFinal = false;

        offboard = new Pair<Integer, Integer>(0, 0); // no chips have passed off the board at the start

        rand = new Random();
        dice = new Pair<Integer, Integer>(null, null);
    }

    public void rollDice() {
        dice.setFirst(rand.nextInt(6) + 1);
        dice.setLast(rand.nextInt(6) + 1);
    }
    
    private boolean isSensibleStart(int idx, int player) {
    	if (idx == -1) {
    		return player == 1;
    	}
    	else if (idx == 24) {
    		return player == -1;
    	}
    	else {
    		return board[idx][1] == player;
    	}
    }
    
    private int getEndTriangle(int start, int dice, int player) {
    	return Math.max(Math.min(start + player*dice, 24), -1);
    }
	
	/**
     * @param   player int
     * @param   dice Pair<Integer, Integer> {first dice throw, second dice throw}
     * @return List of legal moves. Move - Pair<Integer, Integer> {starting position, final position}
     */
    public List<List<Pair<Integer, Integer>>> getLegalMoves(int player, Pair<Integer, Integer> dice) {
    	/*
    	 * Rules: 
    	 *   - If there are captured chips, they must be placed onto the board before any other move can be made.
    	 *   - Can only move off the board if all chips are in the final sector.
    	 *   - Can only capture lone opponent chips, not stacks.
    	 *   - Must make as many moves as possible.
    	 *   - When there are no more moves possible, the turn ends.
    	 *   - Doubles mean both dice must be used twice.
    	 *   - If either one dice or the other can be used but not both, the higher number one must be used.
    	 *   
    	 * Things to keep in mind:
    	 *   - Changing the order of the considered moves might allow one to get a legal position when the other
    	 *     move order would not allow that.
    	 *   - Again, you must make as many moves as possible ---> only save the longest sequences.
    	 */
    	
    	int[][] startBoard = new int[24][2];
    	int startWhiteCaptured = whiteChipsCaptured;
    	int startBlackCaptured = blackChipsCaptured;
    	Pair<Integer, Integer> startOffboard = new Pair<Integer, Integer>(offboard.getFirst(), offboard.getLast());
    	for (int i = 0; i <= 23; ++i) {
    		startBoard[i] = new int[] {board[i][0], board[i][1]};
    	}
    	
    	List<List<Pair<Integer, Integer>>> legalMoves = new LinkedList<List<Pair<Integer, Integer>>>();
    	int longestSequence = 0;
    	int biggestDice = 0;
    	
    	Set<Pair<Integer, Integer>> diceOrders = new HashSet<Pair<Integer, Integer>>();
    	diceOrders.add(dice);
    	diceOrders.add(new Pair<Integer, Integer>(dice.getLast(), dice.getFirst()));
    	
		for (Pair<Integer, Integer> diceOrder : diceOrders) {
    		for (int i = -1; i <= 24; ++i) {
    			if (!isSensibleStart(i, player)) {
    				continue;
    			}
	    		Pair<Integer, Integer> move1 = new Pair<Integer, Integer>(i, getEndTriangle(i, diceOrder.getFirst(), player));
    			if (executeMove(move1)) {
    				int[][] currBoard = new int[24][2];
        	    	int currWhiteCaptured = whiteChipsCaptured;
        	    	int currBlackCaptured = blackChipsCaptured;
        	    	Pair<Integer, Integer> currOffboard = new Pair<Integer, Integer>(offboard.getFirst(), offboard.getLast());
        	    	for (int k = 0; k <= 23; ++k) {
        	    		currBoard[k] = new int[] {board[k][0], board[k][1]};
        	    	}
        	    	for (int j = -1; j <= 24; ++j) {
        	    		if (!isSensibleStart(j, player)) {
            				continue;
            			}
        	    		Pair<Integer, Integer> move2 = new Pair<Integer, Integer>(j, getEndTriangle(j, diceOrder.getLast(), player));
						if (executeMove(move2)) {
							List<Pair<Integer, Integer>> move = new LinkedList<Pair<Integer, Integer>>();
							move.add(move1);
							move.add(move2);
							if (longestSequence != 2) {
								legalMoves.clear();
							}
							legalMoves.add(move);
							
							longestSequence = 2;
							biggestDice = 7;
							
							for (int idx = 0; idx <= 23; ++idx) {
								board[idx] = new int[] {currBoard[idx][0], currBoard[idx][1]};
							}
							whiteChipsCaptured = currWhiteCaptured;
							blackChipsCaptured = currBlackCaptured;
							offboard = new Pair<Integer, Integer>(currOffboard.getFirst(), currOffboard.getLast());
						}
						else {
							if (longestSequence == 2 || (diceOrder.getFirst() < biggestDice)) {
								continue;
							}
							List<Pair<Integer, Integer>> move = new LinkedList<Pair<Integer, Integer>>();
							move.add(move1);
							biggestDice = diceOrder.getFirst();
							longestSequence = 1;
							legalMoves.add(move);
						}
        	    	}
	    			for (int idx = 0; idx <= 23; ++idx) {
						board[idx] = new int[] {startBoard[idx][0], startBoard[idx][1]};
					}
					whiteChipsCaptured = startWhiteCaptured;
					blackChipsCaptured = startBlackCaptured;
					offboard = new Pair<Integer, Integer>(startOffboard.getFirst(), startOffboard.getLast());
    			}
    		}
		}
		
		if (dice.getFirst() != dice.getLast() || longestSequence != 2) {
			return legalMoves;
		}
		
		List<List<Pair<Integer, Integer>>> finalLegalMoves = new LinkedList<List<Pair<Integer, Integer>>>();
		
		for (int a = 0; a < legalMoves.size(); ++a) {
			List<Pair<Integer, Integer>> moveOrder = legalMoves.get(a);
			
			for (Pair<Integer, Integer> move : moveOrder) {
				executeMove(move);
			}
			
			int[][] midBoard = new int[24][2];
        	int midWhiteCaptured = whiteChipsCaptured;
        	int midBlackCaptured = blackChipsCaptured;
        	Pair<Integer, Integer> midOffboard = new Pair<Integer, Integer>(offboard.getFirst(), offboard.getLast());
        	for (int i = 0; i <= 23; ++i) {
        		midBoard[i] = new int[] {board[i][0], board[i][1]};
        	}
        	
        	for (int i = -1; i <= 24; ++i) {
    			if (!isSensibleStart(i, player)) {
    				continue;
    			}
	    		Pair<Integer, Integer> move1 = new Pair<Integer, Integer>(i, getEndTriangle(i, dice.getFirst(), player));
    			if (executeMove(move1)) {
    				int[][] currBoard = new int[24][2];
        	    	int currWhiteCaptured = whiteChipsCaptured;
        	    	int currBlackCaptured = blackChipsCaptured;
        	    	Pair<Integer, Integer> currOffboard = new Pair<Integer, Integer>(offboard.getFirst(), offboard.getLast());
        	    	for (int k = 0; k <= 23; ++k) {
        	    		currBoard[k] = new int[] {board[k][0], board[k][1]};
        	    	}
        	    	for (int j = -1; j <= 24; ++j) {
        	    		if (!isSensibleStart(j, player)) {
            				continue;
            			}
        	    		Pair<Integer, Integer> move2 = new Pair<Integer, Integer>(j, getEndTriangle(j, dice.getLast(), player));
						if (executeMove(move2)) {
							List<Pair<Integer, Integer>> finalMoveOrder = new LinkedList<Pair<Integer, Integer>>();
							for (Pair<Integer, Integer> move : moveOrder) {
								finalMoveOrder.add(move);
							}
							
							finalMoveOrder.add(move1);
							finalMoveOrder.add(move2);
							if (longestSequence != 4) {
								finalLegalMoves.clear();
							}
							finalLegalMoves.add(finalMoveOrder);
							longestSequence = 4;
							
							for (int idx = 0; idx <= 23; ++idx) {
								board[idx] = new int[] {currBoard[idx][0], currBoard[idx][1]};
							}
							whiteChipsCaptured = currWhiteCaptured;
							blackChipsCaptured = currBlackCaptured;
							offboard = new Pair<Integer, Integer>(currOffboard.getFirst(), currOffboard.getLast());
						}
						else {
							if (longestSequence > 3) {
								continue;
							}
							List<Pair<Integer, Integer>> finalMoveOrder = new LinkedList<Pair<Integer, Integer>>();
							for (Pair<Integer, Integer> move : moveOrder) {
								finalMoveOrder.add(move);
							}
							finalMoveOrder.add(move1);
							longestSequence = 3;
							finalLegalMoves.add(finalMoveOrder);
						}
        	    	}
	    			for (int idx = 0; idx <= 23; ++idx) {
						board[idx] = new int[] {midBoard[idx][0], midBoard[idx][1]};
					}
					whiteChipsCaptured = midWhiteCaptured;
					blackChipsCaptured = midBlackCaptured;
					offboard = new Pair<Integer, Integer>(midOffboard.getFirst(), midOffboard.getLast());
    			}
    		}
        	
        	for (int idx = 0; idx <= 23; ++idx) {
				board[idx] = new int[] {startBoard[idx][0], startBoard[idx][1]};
			}
			whiteChipsCaptured = startWhiteCaptured;
			blackChipsCaptured = startBlackCaptured;
			offboard = new Pair<Integer, Integer>(startOffboard.getFirst(), startOffboard.getLast());
		}
		if (finalLegalMoves.size() == 0) {
			return legalMoves;
		}
		else {
			return finalLegalMoves;
		}
    }

    private void removeChip(int pos) {
        board[pos][0] -= 1;
        if (board[pos][0] == 0) {
            board[pos][1] = 0;
        }
    }

    /**
     * 
     * @param move Pair<Integer, Integer> {starting position, final position} if
     *             move.getLast() >= 24, we try to bear off
     * @return true if move was executed, false otherwise
     */

	public boolean executeMove(Pair<Integer, Integer> move) {
		int start = move.getFirst();
		int end = move.getLast();
        // String errorMsg = "ERROR - could not move chip from " + String.valueOf(start) + " to " + String.valueOf(end);

        if (whiteChipsCaptured != 0) {
            whiteFinal = false;
        } else {
            int numWhiteFinal = 0;
            for (int i = 18; i <= 23; ++i) {
                if (board[i][1] == 1) {
                    numWhiteFinal += board[i][0];
                }
            }
            numWhiteFinal += offboard.getFirst();
            if (numWhiteFinal == 15) {
                whiteFinal = true;
            }
        }
        if (blackChipsCaptured != 0) {
            blackFinal = false;
        } else {
            int numBlackFinal = 0;
            for (int i = 0; i <= 5; ++i) {
                if (board[i][1] == -1) {
                    numBlackFinal += board[i][0];
                }
            }
            numBlackFinal += offboard.getLast();
            if (numBlackFinal == 15) {
                blackFinal = true;
            }
        }

        /*
         * Special meanings: - start = -1: White is placing a captured chip. - start =
         * 24: Black is placing a captured chip. - end = 24: White is moving a piece off
         * the board. - end = -1: Black is moving a piece off the board.
         */

        if (start <= -2) {
        	// System.out.println(errorMsg);
            // System.out.println("Starting position too small!");
            return false;
        }
        if (start >= 25) {
        	// System.out.println(errorMsg);
            // System.out.println("Starting position too large!");
            return false;
        }
        if (end <= -2) {
        	// System.out.println(errorMsg);
            // System.out.println("End position too small!");
            return false;
        }
        if (start >= 25) {
        	// System.out.println(errorMsg);
            // System.out.println("End position too large!");
            return false;
        }

        // special move
        // placing a captured chip
        if (start == -1 || start == 24) {
        	if (start == -1 && whiteChipsCaptured >= 1) {
        		// if there's a black chip, we need to do some work
        		if (board[end][1] == -1) {
        			if (board[end][0] >= 2) {
        				// System.out.println(errorMsg);
                        // System.out.println("There is more than one chip of different color in the final position!");
                        return false;
        			}
        			else {
        				board[end][0] = 1;
        				board[end][1] = 1;
        				blackChipsCaptured += 1;
        				whiteChipsCaptured -= 1;
        				// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                        return true;
        			}
        		}
        		// otherwise we just place
        		else {
        			board[end][0] += 1;
        			board[end][1] = 1;
        			whiteChipsCaptured -= 1;
        			// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                    return true;
        		}
        	}
        	else if (start == 24 && blackChipsCaptured >= 1) {
        		// if there's a white chip, we need to do some work
        		if (board[end][1] == 1) {
        			if (board[end][0] >= 2) {
        				// System.out.println(errorMsg);
                        // System.out.println("There is more than one chip of different color in the final position!");
                        return false;
        			}
        			else {
        				board[end][0] = 1;
        				board[end][1] = -1;
        				whiteChipsCaptured += 1;
        				blackChipsCaptured -= 1;
        				// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                        return true;
        			}
        		}
        		// otherwise we just place
        		else {
        			board[end][0] += 1;
        			board[end][1] = -1;
        			blackChipsCaptured -= 1;
        			// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                    return true;
        		}
        	}
        	else {
        		// System.out.println(errorMsg);
                // System.out.println("There are no captured chips to place!");
                return false;
            }
        }
        // taking a chip off the board
        if (end == -1 || end == 24) {
        	if (board[start][0] <= 0){
                // System.out.println(errorMsg);
                //System.out.println("Starting position is empty!");
                return false;
    	    }
        	if (end == 24) {
        		if (!whiteFinal) {
        			// System.out.println(errorMsg);
        			// System.out.println("Not all white chips are in the final sector!");
        			return false;
        		}
        		else {
        			removeChip(start);
        			offboard.setFirst(offboard.getFirst() + 1);
        			// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                	return true;
        		}
        	}
        	if (end == -1) {
        		if (!blackFinal) {
        			// System.out.println(errorMsg);
        			// System.out.println("Not all black chips are in the final sector!");
        			return false;
        		}
        		else {
        			removeChip(start);
        			offboard.setLast(offboard.getLast() + 1);
        			// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
        			return true;
        		}
        	}
        }

        // regular move
        //checks if the move is legal
        if (board[start][0] <= 0){
            // System.out.println(errorMsg);
            // System.out.println("Starting position is empty!");
            return false;
	    }
        // enough chips in the beginning, enough space in the end
        // check for captured chips
        if ((board[start][1] == 1 && whiteChipsCaptured != 0) || (board[start][1] == -1 && blackChipsCaptured != 0)) {
        	// System.out.println(errorMsg);
        	// System.out.println("Captured chips need to be played first!");
        	return false;
        }
        
        // end is empty
        if (board[end][0] == 0) {
        	board[end][0] = 1;
        	board[end][1] = board[start][1];
        	removeChip(start);
        	// System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
            return true;
        }
        // end is same color
        else if (board[end][1] == board[start][1]) {
            board[end][0] += 1;
            removeChip(start);
            // System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
            return true;
        }
        // end is different color
        else {
            if (board[end][0] == 1) {
                if (board[end][1] == 1) {
                    whiteChipsCaptured += 1;
                } else {
                    blackChipsCaptured += 1;
                }
                board[end][0] = 1;// we have exactly 1 chip here
                board[end][1] = board[start][1];// of the same color
                removeChip(start);
                // System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                return true;
            }
            else{
                // System.out.println(errorMsg);
                // System.out.println("There is more than one chip of different color in the final position!");
                return false;
            }
        }
    }
}
