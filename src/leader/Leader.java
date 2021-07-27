package leader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gui.GamePanel;
import gui.MainFrame;
import rules.GameVisible;
import utils.Pair;

public class Leader {

    public static Map<Integer, PlayerType> playerType;

    public static GameVisible gameVisible;// = new GameVisible();
    public static MainFrame frame;

    public static boolean humanRound = true;
  
    public static boolean diceRolled = false;
  
    public static List<List<Pair<Integer, Integer>>> legalMoves = null;
    private static List<Pair<Integer, Integer>> movesPlayed = null; 

    //public static Inteligenca comp1;
    //public static Inteligenca comp2;

    public static void newGame() {
        gameVisible = new GameVisible();
        diceRolled = false;
    //
    // if (vrstaIgralca.get(1) == VrstaIgralca.R) {
    // comp1 = new Inteligenca();
    // }
    // else {
    // comp1 = null;
    // }
    //
    // if (vrstaIgralca.get(-1) == VrstaIgralca.R) {
    // comp2 = new Inteligenca();
    // }
    // else {
    // comp2 = null;
    // }
    //
    // igramo();
  }
  
    public static void rollDice() {
	    gameVisible.board.rollDice();
	    diceRolled = true;
	    legalMoves = gameVisible.board.getLegalMoves(gameVisible.player, gameVisible.board.dice);
	    movesPlayed = null;
    }
    
    public static boolean playMove(Pair<Integer, Integer> move) {
    	outer_loop:
    	for (int i = 0; i < legalMoves.size(); ++i) {
			List<Pair<Integer, Integer>> moveOrder = legalMoves.get(i);
			if (movesPlayed == null) {
				if (move.equals(moveOrder.get(0))) {
					gameVisible.playMove(move);
					movesPlayed = new LinkedList<Pair<Integer,Integer>>();
					movesPlayed.add(move);
					return true;
				}
			}
			else {
				for (int j = 0; j < movesPlayed.size(); ++j) {
					if (!movesPlayed.get(j).equals(moveOrder.get(j))) {
						continue outer_loop;
					}
				}
				if (move.equals(moveOrder.get(movesPlayed.size()))) {
					gameVisible.playMove(move);
					movesPlayed.add(move);
					return true;
				}
			}
		}
    	return false;
    }
}
