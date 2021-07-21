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
     * {@code board[i][0]} - number of checkers
     * 
     * {@code board[i][1]} - {@code 1} for white, {@code 0} for empty, {@code -1} for black
     * @representation
     * i-th pair represents (i+1)-th triangle of the board. 
     * Since a triangle can not be shared between two different colors
     * we can afford coding a triangle with (num of checkers on it, color of checkers)
     */
	public int[][] board;
	
	public int whiteStonesCaptured;
	public int blackStonesCaptured;
	
	public boolean whiteFinal;
	public boolean blackFinal;
	
	/**
	 * Pair<Integer, Integer> offboard
	 * @usage
	 * offboard.getFirst() - number of white stones that have passed off the board
	 * offboard.getLast() - number of black stones that have passed off the board 
	 */
	public Pair<Integer, Integer> offboard;
	
	/**
     * Initalize a board.
     * @return Starting board
     */
    public Board(){
        board = new int[24][2];
        // White coins
        board[0][0] = 2; // number of coins
        board[0][1] = 1; // color - 1=white, -1=Black
        
        board[11][0] = 5; // number of coins
        board[11][1] = 1; // color - 1=white, -1=Black
        
        board[16][0] = 3; // number of coins
        board[16][1] = 1; // color - 1=white, -1=Black
        
        board[18][0] = 5; // number of coins
        board[18][1] = 1; // color - 1=white, -1=Black
        
        // Black coins
        board[23][0] = 2; // number of coins
        board[23][1] = -1; // color - 1=white, -1=Black
        
        board[12][0] = 5; // number of coins
        board[12][1] = -1; // color - 1=white, -1=Black
        
        board[7][0] = 3; // number of coins
        board[7][1] = -1; // color - 1=white, -1=Black
        
        board[5][0] = 5; // number of coins
        board[5][1] = -1; // color - 1=white, -1=Black
        
        // no stones are captured at the start
        whiteStonesCaptured = 0;
        blackStonesCaptured = 0;
        
        // stones cannot be moved off the board at the start
        whiteFinal = false;
        blackFinal = false;
        
        offboard = new Pair<Integer, Integer>(0, 0); // no stones have passed off the board at the start
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
    	 *   - If there are captured stones, they must be placed onto the board before any other move can be made.
    	 *   - Can only move off the board if all stones are in the final sector.
    	 *   - Can only capture lone opponent stones, not stacks.
    	 *   - Cannot place oneself into a position with no legal moves.
    	 *   
    	 * Things to keep in mind:
    	 *   - Changing the order of the considered moves might allow one to get a legal position when the other
    	 *     move order would not allow that.
    	 */
        
        System.out.println("getLegalMoves not (yet) implemented.");
        return null;
    }
    
    private void removeStone(int pos) {
    	board[pos][0] -= 1;
    	if (board[pos][0] == 0) {
    		board[pos][1] = 0;
    	}
    }
	
    /**
     * TODO - Implement moving the stones off the board in the final sector and placing captured stones on the board.
     * 
     * @param move Pair<Integer, Integer> {starting position, final position}
     * @return true if move was executed, false otherwise
     */
	public boolean executeMove(Pair<Integer, Integer> move) {
		int start = move.getFirst();
        int end = move.getLast();
        String errorMsg = "ERROR - could not move coin from " + String.valueOf(start) + " to " + String.valueOf(end);
        
        if (whiteStonesCaptured != 0) {
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
        if (blackStonesCaptured != 0) {
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
         *   - start = -1: White is placing a captured stone.
         *   - start = 24: Black is placing a captured stone.
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
        // placing a captured stone
        if (start == -1 || start == 24) {
        	if (board[end][0] >= 5){
                System.out.println(errorMsg);
                System.out.println("Final position is full!");
                return false;
            }
        	if (start == -1 && whiteStonesCaptured >= 1) {
        		// if there's a black stone, we need to do some work
        		if (board[end][1] == -1) {
        			if (board[end][0] >= 2) {
        				System.out.println(errorMsg);
                        System.out.println("There is more than one coin of different color in the final position!");
                        return false;
        			}
        			else {
        				board[end][0] = 1;
        				board[end][1] = 1;
        				blackStonesCaptured += 1;
        				whiteStonesCaptured -= 1;
        				System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
                        return true;
        			}
        		}
        		// otherwise we just place
        		else {
        			board[end][0] += 1;
        			board[end][1] = 1;
        			whiteStonesCaptured -= 1;
        			System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
                    return true;
        		}
        	}
        	else if (start == 24 && blackStonesCaptured >= 1) {
        		// if there's a white stone, we need to do some work
        		if (board[end][1] == 1) {
        			if (board[end][0] >= 2) {
        				System.out.println(errorMsg);
                        System.out.println("There is more than one coin of different color in the final position!");
                        return false;
        			}
        			else {
        				board[end][0] = 1;
        				board[end][1] = -1;
        				whiteStonesCaptured += 1;
        				blackStonesCaptured -= 1;
        				System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
                        return true;
        			}
        		}
        		// otherwise we just place
        		else {
        			board[end][0] += 1;
        			board[end][1] = -1;
        			blackStonesCaptured -= 1;
        			System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
                    return true;
        		}
        	}
        	else {
        		System.out.println(errorMsg);
                System.out.println("There are no captured stones to place!");
                return false;
        	}
        }
        // taking a stone off the board
        if (end == -1 || end == 24) {
        	if (board[start][0] <= 0){
                System.out.println(errorMsg);
                System.out.println("Starting position is empty!");
                return false;
    	    }
        	if (end == 24) {
        		if (!whiteFinal) {
        			System.out.println(errorMsg);
        			System.out.println("Not all white stones are in the final sector!");
        			return false;
        		}
        		else {
        			removeStone(start);
        			offboard.setFirst(offboard.getFirst() + 1);
        			System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
                	return true;
        		}
        	}
        	if (end == -1) {
        		if (!blackFinal) {
        			System.out.println(errorMsg);
        			System.out.println("Not all black stones are in the final sector!");
        			return false;
        		}
        		else {
        			removeStone(start);
        			offboard.setLast(offboard.getLast() + 1);
        			System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
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
        //enough coins in the beginning, enough space in the end
        // end is empty
        if (board[end][0] == 0) {
        	board[end][0] = 1;
        	board[end][1] = board[start][1];
        	removeStone(start);
        	System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
            return true;
        }
        // end is same color
        else if (board[end][1] == board[start][1]){
            board[end][0] += 1;
            removeStone(start);
            System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
            return true;
        }
        // end is different color
        else {
            if (board[end][0] == 1){
            	if (board[end][1] == 1) {
            		whiteStonesCaptured += 1;
            	}
            	else {
            		blackStonesCaptured += 1;
            	}
                board[end][0] = 1;//we have exactly 1 coin here
                board[end][1] = board[start][1];//of the same color
                removeStone(start);
                
                System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
                return true;
            }
            else{
                System.out.println(errorMsg);
                System.out.println("There is more than one coin of different color in the final position!");
                return false;
            }
        }
    }
}
