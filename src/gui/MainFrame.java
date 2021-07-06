package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;




/**
 *TODO
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	/**
	 * JPanel, v katerega igramo
	 */
	private GamePanel gamePanel;

	
	//Status bar at the bottom of the screen
	private JLabel status;
	
	// Izbire v menujih
	private JMenuItem igraClovekRacunalnik;
	private JMenuItem igraRacunalnikClovek;
	private JMenuItem igraClovekClovek;
	private JMenuItem igraRacunalnikRacunalnik;

	/**
	 * Ustvari novo glavno okno in prični igrati igro.
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
		igraClovekRacunalnik = new JMenuItem("Človek – računalnik");
		new_game_menu.add(igraClovekRacunalnik);
		igraClovekRacunalnik.addActionListener(this);
		igraRacunalnikClovek = new JMenuItem("Računalnik – človek");
		new_game_menu.add(igraRacunalnikClovek);
		igraRacunalnikClovek.addActionListener(this);
		igraClovekClovek = new JMenuItem("Človek – človek");
		new_game_menu.add(igraClovekClovek);
		igraClovekClovek.addActionListener(this);
		igraRacunalnikRacunalnik = new JMenuItem("Računalnik – računalnik");
		new_game_menu.add(igraRacunalnikRacunalnik);
		igraRacunalnikRacunalnik.addActionListener(this);

		//settings
		JMenu settings_menu = new JMenu("Nastavitve");
		menu_bar.add(settings_menu);

		

		// igralno gamePanel
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
		status.setFont(new Font(status.getFont().getName(),
							    status.getFont().getStyle(),
							    20));
		GridBagConstraints status_layout = new GridBagConstraints();
		status_layout.gridx = 0;
		status_layout.gridy = 1;
		status_layout.anchor = GridBagConstraints.CENTER;
		getContentPane().add(status, status_layout);
		
		status.setText("Izberite igro!");
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
	//	if (e.getSource() == igraClovekRacunalnik) {
	//		Vodja.vrstaIgralca = new HashMap<Integer,VrstaIgralca>();
	//		Vodja.vrstaIgralca.put(1, VrstaIgralca.C); 
	//		Vodja.vrstaIgralca.put(-1, VrstaIgralca.R);
	//		Vodja.igramoNovoIgro();
	//	} else if (e.getSource() == igraRacunalnikClovek) {
	//		Vodja.vrstaIgralca = new HashMap<Integer,VrstaIgralca>();
	//		Vodja.vrstaIgralca.put(1, VrstaIgralca.R); 
	//		Vodja.vrstaIgralca.put(-1, VrstaIgralca.C);
	//		Vodja.igramoNovoIgro();
	//	} else if (e.getSource() == igraClovekClovek) {
	//		Vodja.vrstaIgralca = new HashMap<Integer,VrstaIgralca>();
	//		Vodja.vrstaIgralca.put(1, VrstaIgralca.C); 
	//		Vodja.vrstaIgralca.put(-1, VrstaIgralca.C);
	//		Vodja.igramoNovoIgro();
	//	} else if (e.getSource() == igraRacunalnikRacunalnik) {
	//		Vodja.vrstaIgralca = new HashMap<Integer,VrstaIgralca>();
	//		Vodja.vrstaIgralca.put(1, VrstaIgralca.R); 
	//		Vodja.vrstaIgralca.put(-1, VrstaIgralca.R);
	//		Vodja.igramoNovoIgro();
	//	}
	}

	public void osveziGUI() {
	//	if (Vodja.igra == null) {
	//		status.setText("Igra ni v teku.");
	//	}
	//	else {
	//		double stanje = Game.getGameEnded(Vodja.igra.board, Vodja.igra.igralec);
	//		if (stanje == 0) {
	//			String ime = "bel";
	//			if (Vodja.igra.igralec == 1) ime = "črn";
	//	 		status.setText("Na potezi je " + ime + 
	//	 				" - " + Vodja.vrstaIgralca.get(Vodja.igra.igralec));
	//		}
	//		else if (stanje == -1 || stanje == 1) {
	//			if (Vodja.igra.igralec == 1) {
	//				status.setText("Zmagal je bel." + 
	//				 		Vodja.vrstaIgralca.get(-1));
	//			}
	//			else {
	//				status.setText("Zmagal je črn." + 
	//						Vodja.vrstaIgralca.get(1));
	//			}
	//		}
	//		else {
	//			System.out.println(stanje);
	//			status.setText("Neodločeno!");
	//		}
	//		
	//	}
	//	gamePanel.repaint();
	}
	



}
