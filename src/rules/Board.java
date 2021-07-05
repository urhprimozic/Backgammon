package rules;

import java.util.List;

import utils.Coordinates;
import utils.Pair;
import utils.PlayerColor;
/**
 * @variables int
 * 
 */
public class Board {
    /**
     * int[24][2] board
     * Array of 24 size 2 arrays (pairs) 
     * @usage
     * board[i][0] - number of checkers
     * 
     * board[i][1] - 1 for white, 0 for empty, -1 for black
     * @representation
     * i-th pair represents i-th triangle of the board. 
     * Since a triangle can not be shared between two different colors
     * we can affor coding a triangle with (num of checkers on it, color of checkers)
     */
	public int[][] board;
	
	/**
     * Initalize a board.
     *@return Starting board
     */
    public Board(){
        board = new int[24][2];
        //White coins
        board[1][0] = 2;//number of coins
        board[1][1] = 1;//color - 1=white, -1=Black
        
        board[12][0] = 5;//number of coins
        board[12][1] = 1;//color - 1=white, -1=Black
        
        board[17][0] = 3;//number of coins
        board[17][1] = 1;//color - 1=white, -1=Black
        
        board[19][0] = 5;//number of coins
        board[19][1] = 1;//color - 1=white, -1=Black
        //Black coins
        board[24][0] = 2;//number of coins
        board[24][1] = -1;//color - 1=white, -1=Black
        
        board[13][0] = 5;//number of coins
        board[13][1] = -1;//color - 1=white, -1=Black
        
        board[8][0] = 3;//number of coins
        board[8][1] = -1;//color - 1=white, -1=Black
        
        board[6][0] = 5;//number of coins
        board[6][1] = -1;//color - 1=white, -1=Black


    }

	
	/**
     * TODO - should return all the possible moves. (maybe for certian player?)
     * @return List of legal moves. Move - Pair<int,int> {starting position, final position}
     */
    public List<Pair<Integer, Integer>> getLegalMoves() {
        
        System.out.println("getLegalMoves not (yet) implemented.");
        return null;
    }
	
    /**
     * 
     * @param move Pair<int,int> {starting position, final position}
     * @return 1 if move was executed,  otherwise
     */
	public boolean executeMove(Pair<Integer, Integer> move) {
		int start = move.getFirst();
        int end = move.getLast();
        //checks if the move is legal
        String errorMsg = "ERROR - could not move coin from " + String.valueOf(start) + " to " + String.valueOf(end);
        if (board[start][0] <= 0){
            System.out.println(errorMsg);
            System.out.println("Starting position is empty!");
            return false;
	    }
        else if (board[end][0] >= 5){
            System.out.println(errorMsg);
            System.out.println("Final position is full!");
            return false;
        }
        //enough coins in the begiining, enough space in the end
        else if (board[end][1] == board[start][1]){
            board[start][0] -= 1; //we have 1 coin less here
            if (board[start][0] == 0){
                board[start][1] = 0;
            }
            //and one coin more in the end:
            board[end][0] += 1;
            System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
            return true;
        }

        else if (board[end][1] != board[start][1]){
            //slots have differend colors (or final slot is empty)
            if (board[end][0] <= 1){
                board[end][0] = 1;//we have exactly 1 coin here
                board[end][1] = board[start][1];//of the same color
                if (board[start][0] == 0){
                    board[start][1] = 0;
                }
                System.out.println("Placed a coin from " + String.valueOf(start) + " to " + String.valueOf(end));
                return true;
            }
            else{
                System.out.println(errorMsg);
                System.out.println("There is more than one coin of differend colo in the final position!");
                return false;
            }
        }
        // we returned True in the ok case
        return false;
}
