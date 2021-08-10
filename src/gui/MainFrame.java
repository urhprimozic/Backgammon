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
import javax.swing.JRadioButtonMenuItem;

import leader.Leader;
import leader.PlayerType;

/**
 * The main window for the game, contains everything else.
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
	private JRadioButtonMenuItem rbEasy;
	private JRadioButtonMenuItem rbMedium;
	private JRadioButtonMenuItem rbHard;

	// Time limits for dificulty settings
	private final static int TIME_EASY = 1000;
	private final static int TIME_MEDIUM = 3000;
	private final static int TIME_HARD = 5500;
	public int mctsTimeLimit;

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

		// ai difficulty
		JMenu dificulty_menu = new JMenu("Težavnost");
		menu_bar.add(dificulty_menu);

		rbEasy = new JRadioButtonMenuItem("Easy");
		rbEasy.setSelected(true);
		dificulty_menu.add(rbEasy);
		rbEasy.addActionListener(this);

		rbMedium = new JRadioButtonMenuItem("Medium");
		rbMedium.setSelected(false);
		dificulty_menu.add(rbMedium);
		rbMedium.addActionListener(this);

		rbHard = new JRadioButtonMenuItem("Hard");
		rbHard.setSelected(false);
		dificulty_menu.add(rbHard);
		rbHard.addActionListener(this);

		//// settings
		//JMenu settings_menu = new JMenu("Nastavitve");
		//menu_bar.add(settings_menu);

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

		mctsTimeLimit = TIME_EASY;

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
		} else if (e.getSource() == rbEasy) {
			rbEasy.setSelected(true);
			rbMedium.setSelected(false);
			rbHard.setSelected(false);
			System.out.println("easy mode selected");
			if (Leader.comp1 != null)
				Leader.comp1.setTimeLimit(TIME_EASY);
			if (Leader.comp2 != null)
				Leader.comp2.setTimeLimit(TIME_EASY);
			mctsTimeLimit = TIME_EASY;
		} else if (e.getSource() == rbMedium) {
			rbEasy.setSelected(false);
			rbMedium.setSelected(true);
			rbHard.setSelected(false);
			System.out.println("medium mode selected");
			mctsTimeLimit = TIME_MEDIUM;
			if (Leader.comp1 != null)
				Leader.comp1.setTimeLimit(TIME_MEDIUM);
			if (Leader.comp2 != null)
				Leader.comp2.setTimeLimit(TIME_MEDIUM);
		} else if (e.getSource() == rbHard) {
			rbEasy.setSelected(false);
			rbMedium.setSelected(false);
			rbHard.setSelected(true);
			System.out.println("hard mode selected");
			mctsTimeLimit = TIME_HARD;
			if (Leader.comp1 != null)
				Leader.comp1.setTimeLimit(TIME_HARD);
			if (Leader.comp2 != null)
				Leader.comp2.setTimeLimit(TIME_HARD);
		}

	}

	public void refreshGUI() {
		if (Leader.board == null) {
			status.setText("Igra ni v teku.");
		} else {
			double stanje = Leader.board.getGameEnded(Leader.player);
			if (stanje == 0) {
				String ime = "črn";
				if (Leader.player == 1)
					ime = "bel";
				status.setText("Na potezi je " + ime + " - " + Leader.playerType.get(Leader.player));
			} else {
				if (Leader.player == 1) {
					status.setText("Zmagal je črn. " + Leader.playerType.get(-1));
				} else {
					status.setText("Zmagal je bel. " + Leader.playerType.get(1));
				}
			}
		}
		gamePanel.repaint();
	}

}
