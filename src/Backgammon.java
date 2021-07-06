import gui.MainFrame;
import leader.Leader;
/**
 * 
 */

/**
 * @author urh and jon
 *
 */
public class Backgammon {

	/**
	 * runs the game
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame window = new MainFrame();
		window.pack();
		window.setVisible(true);
		Leader.frame = window;
	}

}
