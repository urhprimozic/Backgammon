
/**
 * talk about the game itself
 *
 * @author      Jon Mikoš
 * @author      Urh Primožič
 * @version     %I%, %G%
 */

import gui.MainFrame;
import leader.Leader;

public class Backgammon {

	/**
	 * The starting point of this program. Constructs a {@link #MainFrame MainFrame}
	 * and hands control to {@link #Leader Leader}.
	 *
	 * @param args required for main, not used
	 */
	public static void main(String[] args) {
		MainFrame window = new MainFrame();
		window.pack();
		window.setVisible(true);
		Leader.frame = window;
	}

}
