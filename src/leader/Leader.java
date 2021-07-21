package leader;

import java.util.Map;

import gui.MainFrame;
import rules.GameVisible;

public class Leader {

  public static Map<Integer, PlayerType> playerType;

  public static GameVisible gameVisible;// = new GameVisible();
  public static MainFrame frame;

  public static boolean humanRound = true;

  //public static Inteligenca comp1;
  //public static Inteligenca comp2;

  public static void newGame() {
    gameVisible = new GameVisible();
    
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
}
