package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import leader.Leader;
import leader.PlayerType;

/**
 * TODO
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	/**
	 * JPanel with the board and chips
	 */
	private GamePanel gamePanel;

	/** Status bar at the bottom */
	private JLabel status;

	// Buttons in the menus
	private JMenuItem button_HC;
	private JMenuItem button_CH;
	private JMenuItem button_HH;
	private JMenuItem button_CC;

	/**
	 * Creates window
	 */
	public MainFrame() {

		this.setTitle("Backgammon");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new GridBagLayout());

		// menu
		JMenuBar menu_bar = new JMenuBar();
		this.setJMenuBar(menu_bar);

		// new game
		JMenu new_game_menu = new JMenu("Nova igra");
		menu_bar.add(new_game_menu);
		button_HC = new JMenuItem("Človek – računalnik");
		new_game_menu.add(button_HC);
		button_HC.addActionListener(this);
		button_CH = new JMenuItem("Računalnik – človek");
		new_game_menu.add(button_CH);
		button_CH.addActionListener(this);
		button_HH = new JMenuItem("Človek – človek");
		new_game_menu.add(button_HH);
		button_HH.addActionListener(this);
		button_CC = new JMenuItem("Računalnik – računalnik");
		new_game_menu.add(button_CC);
		button_CC.addActionListener(this);

		// settings
		JMenu settings_menu = new JMenu("Nastavitve");
		menu_bar.add(settings_menu);

		// board and chips
		gamePanel = new GamePanel();

		GridBagConstraints gamePanel_layout = new GridBagConstraints();
		gamePanel_layout.gridx = 0;
		gamePanel_layout.gridy = 0;
		gamePanel_layout.fill = GridBagConstraints.BOTH;
		gamePanel_layout.weightx = 1.0;
		gamePanel_layout.weighty = 1.0;
		getContentPane().add(gamePanel, gamePanel_layout);

		// statusna vrstica za sporočila
		status = new JLabel();
		status.setFont(new Font(status.getFont().getName(), status.getFont().getStyle(), 20));
		GridBagConstraints status_layout = new GridBagConstraints();
		status_layout.gridx = 0;
		status_layout.gridy = 1;
		status_layout.anchor = GridBagConstraints.CENTER;
		getContentPane().add(status, status_layout);

		status.setText("Izberite igro!");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button_HC) {
			Leader.playerType = new HashMap<Integer, PlayerType>();
			Leader.playerType.put(1, PlayerType.H);
			Leader.playerType.put(-1, PlayerType.C);
			Leader.newGame();
			gamePanel.repaint();
		} else if (e.getSource() == button_CH) {
			Leader.playerType = new HashMap<Integer, PlayerType>();
			Leader.playerType.put(1, PlayerType.C);
			Leader.playerType.put(-1, PlayerType.H);
			Leader.newGame();
			gamePanel.repaint();
		} else if (e.getSource() == button_HH) {
			Leader.playerType = new HashMap<Integer, PlayerType>();
			Leader.playerType.put(1, PlayerType.H);
			Leader.playerType.put(-1, PlayerType.H);
			Leader.newGame();
			gamePanel.repaint();
		} else if (e.getSource() == button_CC) {
			Leader.playerType = new HashMap<Integer, PlayerType>();
			Leader.playerType.put(1, PlayerType.C);
			Leader.playerType.put(-1, PlayerType.C);
			Leader.newGame();
			gamePanel.repaint();
		}
	}

	public void osveziGUI() {
		if (Leader.gameVisible == null) {
			status.setText("Igra ni v teku.");
		}
		// else {
		// double stanje = Game.getGameEnded(Leader.igra.board, Leader.igra.igralec);
		// if (stanje == 0) {
		// String ime = "bel";
		// if (Leader.igra.igralec == 1) ime = "črn";
		// status.setText("Na potezi je " + ime +
		// " - " + Leader.playerType.get(Leader.igra.igralec));
		// }
		// else if (stanje == -1 || stanje == 1) {
		// if (Leader.igra.igralec == 1) {
		// status.setText("Zmagal je bel." +
		// Leader.playerType.get(-1));
		// }
		// else {
		// status.setText("Zmagal je črn." +
		// Leader.playerType.get(1));
		// }
		// }
		// else {
		// System.out.println(stanje);
		// status.setText("Neodločeno!");
		// }
		//
		// }
		gamePanel.repaint();
	}

}
