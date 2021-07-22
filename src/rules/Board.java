package rules;

import java.util.List;

import utils.Pair;
/**
 * @variables int
 * 
 */
public class Board {
    /**
     * int[24][2] board
     * Array of 24 size 2 arrays (pairs) 
     * @usage
     * {@code board[i][0]} - number of chips
     * 
     * {@code board[i][1]} - {@code 1} for white, {@code 0} for empty, {@code -1} for black
     * @representation
     * i-th pair represents (i+1)-th triangle of the board. 
     * Since a triangle can not be shared between two different colors
     * we can afford coding a triangle with (num of chips on it, color of chips)
     */
	public int[][] board;
	
	public int whiteChipsCaptured;
	public int blackChipsCaptured;
	
	public boolean whiteFinal;
	public boolean blackFinal;
	
	/**
	 * Pair<Integer, Integer> offboard
	 * @usage
	 * offboard.getFirst() - number of white chips that have passed off the board
	 * offboard.getLast() - number of black chips that have passed off the board 
	 */
	public Pair<Integer, Integer> offboard;
	
	/**
     * Initalize a board.
     * @return Starting board
     */
    public Board(){
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
    }

	
	/**
     * TODO - Currently takes in a dice throw, maybe repeat for all possible combos of dice?
     *      - Implementation
     * 
     * @param dice Pair<Integer, Integer> {first dice throw, second dice throw}
     * @return List of legal moves. Move - Pair<int,int> {starting position, final position}
     */
    public List<Pair<Integer, Integer>> getLegalMoves(Pair<Integer, Integer> dice) {
    	/*
    	 * Rules: 
    	 *   - If there are captured chips, they must be placed onto the board before any other move can be made.
    	 *   - Can only move off the board if all chips are in the final sector.
    	 *   - Can only capture lone opponent chips, not stacks.
    	 *   - Cannot place oneself into a position with no legal moves.
    	 *   
    	 * Things to keep in mind:
    	 *   - Changing the order of the considered moves might allow one to get a legal position when the other
    	 *     move order would not allow that.
    	 */
        
        System.out.println("getLegalMoves not (yet) implemented.");
        return null;
    }
    
    private void removeChip(int pos) {
    	board[pos][0] -= 1;
    	if (board[pos][0] == 0) {
    		board[pos][1] = 0;
    	}
    }
	
    /**
     * TODO - Implement moving the chips off the board in the final sector and placing captured chips on the board.
     * 
     * @param move Pair<Integer, Integer> {starting position, final position}
     * @return true if move was executed, false otherwise
     */
	public boolean executeMove(Pair<Integer, Integer> move) {
		int start = move.getFirst();
        int end = move.getLast();
        String errorMsg = "ERROR - could not move chip from " + String.valueOf(start) + " to " + String.valueOf(end);
        
        if (whiteChipsCaptured != 0) {
        	whiteFinal = false;
        }
        else {
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
        }
        else {
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
         * Special meanings:
         *   - start = -1: White is placing a captured chip.
         *   - start = 24: Black is placing a captured chip.
         *   - end = 24: White is moving a piece off the board.
         *   - end = -1: Black is moving a piece off the board.
         */
        
        if (start <= -2) {
        	System.out.println(errorMsg);
            System.out.println("Starting position too small!");
            return false;
        }
        if (start >= 25) {
        	System.out.println(errorMsg);
            System.out.println("Starting position too large!");
            return false;
        }
        if (end <= -2) {
        	System.out.println(errorMsg);
            System.out.println("End position too small!");
            return false;
        }
        if (start >= 25) {
        	System.out.println(errorMsg);
            System.out.println("End position too large!");
            return false;
        }
        
        // special move
        // placing a captured chip
        if (start == -1 || start == 24) {
        	if (board[end][0] >= 5){
                System.out.println(errorMsg);
                System.out.println("Final position is full!");
                return false;
            }
        	if (start == -1 && whiteChipsCaptured >= 1) {
        		// if there's a black chip, we need to do some work
        		if (board[end][1] == -1) {
        			if (board[end][0] >= 2) {
        				System.out.println(errorMsg);
                        System.out.println("There is more than one chip of different color in the final position!");
                        return false;
        			}
        			else {
        				board[end][0] = 1;
        				board[end][1] = 1;
        				blackChipsCaptured += 1;
        				whiteChipsCaptured -= 1;
        				System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                        return true;
        			}
        		}
        		// otherwise we just place
        		else {
        			board[end][0] += 1;
        			board[end][1] = 1;
        			whiteChipsCaptured -= 1;
        			System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                    return true;
        		}
        	}
        	else if (start == 24 && blackChipsCaptured >= 1) {
        		// if there's a white chip, we need to do some work
        		if (board[end][1] == 1) {
        			if (board[end][0] >= 2) {
        				System.out.println(errorMsg);
                        System.out.println("There is more than one chip of different color in the final position!");
                        return false;
        			}
        			else {
        				board[end][0] = 1;
        				board[end][1] = -1;
        				whiteChipsCaptured += 1;
        				blackChipsCaptured -= 1;
        				System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                        return true;
        			}
        		}
        		// otherwise we just place
        		else {
        			board[end][0] += 1;
        			board[end][1] = -1;
        			blackChipsCaptured -= 1;
        			System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                    return true;
        		}
        	}
        	else {
        		System.out.println(errorMsg);
                System.out.println("There are no captured chips to place!");
                return false;
        	}
        }
        // taking a chip off the board
        if (end == -1 || end == 24) {
        	if (board[start][0] <= 0){
                System.out.println(errorMsg);
                System.out.println("Starting position is empty!");
                return false;
    	    }
        	if (end == 24) {
        		if (!whiteFinal) {
        			System.out.println(errorMsg);
        			System.out.println("Not all white chips are in the final sector!");
        			return false;
        		}
        		else {
        			removeChip(start);
        			offboard.setFirst(offboard.getFirst() + 1);
        			System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                	return true;
        		}
        	}
        	if (end == -1) {
        		if (!blackFinal) {
        			System.out.println(errorMsg);
        			System.out.println("Not all black chips are in the final sector!");
        			return false;
        		}
        		else {
        			removeChip(start);
        			offboard.setLast(offboard.getLast() + 1);
        			System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
        			return true;
        		}
        	}
        }
        
        
        // regular move
        //checks if the move is legal
        if (board[start][0] <= 0){
            System.out.println(errorMsg);
            System.out.println("Starting position is empty!");
            return false;
	    }
        if (board[end][0] >= 5){
            System.out.println(errorMsg);
            System.out.println("Final position is full!");
            return false;
        }
        //enough chips in the beginning, enough space in the end
        // end is empty
        if (board[end][0] == 0) {
        	board[end][0] = 1;
        	board[end][1] = board[start][1];
        	removeChip(start);
        	System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
            return true;
        }
        // end is same color
        else if (board[end][1] == board[start][1]){
            board[end][0] += 1;
            removeChip(start);
            System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
            return true;
        }
        // end is different color
        else {
            if (board[end][0] == 1){
            	if (board[end][1] == 1) {
            		whiteChipsCaptured += 1;
            	}
            	else {
            		blackChipsCaptured += 1;
            	}
                board[end][0] = 1;//we have exactly 1 chip here
                board[end][1] = board[start][1];//of the same color
                removeChip(start);
                
                System.out.println("Placed a chip from " + String.valueOf(start) + " to " + String.valueOf(end));
                return true;
            }
            else{
                System.out.println(errorMsg);
                System.out.println("There is more than one chip of different color in the final position!");
                return false;
            }
        }
    }
}
