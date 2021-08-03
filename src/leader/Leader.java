package leader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import ai.AI;
import gui.MainFrame;
import rules.Board;
import rules.Game;
import utils.Pair;

public class Leader {

    public static Map<Integer, PlayerType> playerType;

    public static Board board = null;
	public static int player = 1;
	public static int movesMade = 0;
    
    public static MainFrame frame;

    public static boolean humanRound = true;
    public static boolean diceRolled = false;
  
    public static List<List<Pair<Integer, Integer>>> legalMoves = null;
    private static List<Pair<Integer, Integer>> movesPlayed = null; 
    public static int maxMoves = 0;

    public static AI comp1;
    public static AI comp2;

    public static void newGame() {
        board = new Board();
        player = 1;
        movesMade = 0;
        diceRolled = false;
        legalMoves = null;
        movesPlayed = null;
        maxMoves = 0;
    
        if (playerType.get(1) == PlayerType.C) {
        	comp1 = new AI(1);
        }
        else {
        	comp1 = null;
        }
        if (playerType.get(-1) == PlayerType.C) {
        	comp2 = new AI(-1);
        }
        else {
        	comp2 = null;
        }
        play();
    }
    
    public static void play() {
    	frame.refreshGUI();
    	double state = Game.getGameEnded(board, player);
    	
    	if (state != 0) {
    		return;
    	}
    	
    	PlayerType type = playerType.get(player);
    	switch (type) {
    	case H:
    		humanRound = true;
    		break;
    	case C:
    		humanRound = false;
    		computerTurn();
    		break;
    	}
    }
  
    public static void rollDice() {
	    board.rollDice();
	    diceRolled = true;
	    legalMoves = board.getLegalMoves(player, board.dice);
	    if (legalMoves.size() == 0) {
	    	maxMoves = 0;
	    }
	    else {
	    	maxMoves = legalMoves.get(0).size();
	    }
	    movesPlayed = null;
    }
    
    public static void playHumanMove(Pair<Integer, Integer> move) {
    	if (move != null) {
	    	outer_loop:
	    	for (int i = 0; i < legalMoves.size(); ++i) {
				List<Pair<Integer, Integer>> moveOrder = legalMoves.get(i);
				if (movesPlayed == null) {
					if (move.equals(moveOrder.get(0))) {
						board.executeMove(move);
						movesPlayed = new LinkedList<Pair<Integer,Integer>>();
						movesPlayed.add(move);
						break;
					}
				}
				else {
					for (int j = 0; j < movesPlayed.size(); ++j) {
						if (!movesPlayed.get(j).equals(moveOrder.get(j))) {
							continue outer_loop;
						}
					}
					if (move.equals(moveOrder.get(movesPlayed.size()))) {
						board.executeMove(move);
						movesPlayed.add(move);
						break;
					}
				}
			}
    	}
    	// if we made the maximum amount of legal moves, change turns
    	if (movesPlayed == null) {
    		if (maxMoves == 0) {
    			player *= -1;
        		movesMade = 0;
        		diceRolled = false;
    		}
    	}
    	else if (movesPlayed.size() == maxMoves) {
    		player *= -1;
    		movesMade = 0;
    		diceRolled = false;
    	}
    	play();
    }
    
    public static void computerTurn() {
    	Board startBoard = board;
    	SwingWorker<List<Pair<Integer, Integer>>, Void> worker = new SwingWorker<List<Pair<Integer, Integer>>, Void> () {
    		@Override
    		protected List<Pair<Integer, Integer>> doInBackground() {
    			rollDice();
    			frame.refreshGUI();
    			if (player == 1) {
    				return comp1.chooseMoveOrder();
    			}
    			else {
    				return comp2.chooseMoveOrder();
    			}
    		}
    		
    		@Override
    		protected void done() {
    			List<Pair<Integer, Integer>> moveOrder = null;
    			try {moveOrder = get();} catch (Exception e) {e.printStackTrace();};
    			if (board == startBoard) {
    				System.out.println(board.dice.getFirst() + " " + board.dice.getLast());
    				for (Pair<Integer, Integer> move : moveOrder) {
    					System.out.print(move.getFirst() + " -> " + move.getLast() + ", ");
    					System.out.println();
    					board.executeMove(move);
    				}
    				player *= -1;
            		movesMade = 0;
            		diceRolled = false;
    				play();
    			}
    		}
    	};
    	worker.execute();
    }
}
