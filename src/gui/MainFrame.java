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

import rules.Game;
import leader.Vodja;
import leader.VrstaIgralca;


/**
 *TODO
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	/**
	 * JPanel, v katerega igramo
	 */
	private GamePanel polje;

	
	//Statusna vrstica v spodnjem delu okna
	private JLabel status;
	
	// Izbire v menujih
	private JMenuItem igraClovekRacunalnik;
	private JMenuItem igraRacunalnikClovek;
	private JMenuItem igraClovekClovek;
	private JMenuItem igraRacunalnikRacunalnik;

	/**
	 * Ustvari novo glavno okno in prični igrati igro.
	 */
	public GlavnoOkno() {
		
		this.setTitle("Backgammon");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new GridBagLayout());
	
		// menu
		JMenuBar menu_bar = new JMenuBar();
		this.setJMenuBar(menu_bar);
		// Nova igra
		JMenu igra_menu = new JMenu("Nova igra");
		menu_bar.add(igra_menu);


		igraClovekRacunalnik = new JMenuItem("Človek – računalnik");
		igra_menu.add(igraClovekRacunalnik);
		igraClovekRacunalnik.addActionListener(this);
		
		igraRacunalnikClovek = new JMenuItem("Računalnik – človek");
		igra_menu.add(igraRacunalnikClovek);
		igraRacunalnikClovek.addActionListener(this);
		
		igraClovekClovek = new JMenuItem("Človek – človek");
		igra_menu.add(igraClovekClovek);
		igraClovekClovek.addActionListener(this);
		
		igraRacunalnikRacunalnik = new JMenuItem("Računalnik – računalnik");
		igra_menu.add(igraRacunalnikRacunalnik);
		igraRacunalnikRacunalnik.addActionListener(this);

		//nastavitve
		JMenu nastavitve_menu = new JMenu("Nastavitve");
		menu_bar.add(nastavitve_menu);

		

		// igralno polje
		polje = new GamePanel();

		GridBagConstraints polje_layout = new GridBagConstraints();
		polje_layout.gridx = 0;
		polje_layout.gridy = 0;
		polje_layout.fill = GridBagConstraints.BOTH;
		polje_layout.weightx = 1.0;
		polje_layout.weighty = 1.0;
		getContentPane().add(polje, polje_layout);
		
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
		if (e.getSource() == igraClovekRacunalnik) {
			Vodja.vrstaIgralca = new HashMap<Integer,VrstaIgralca>();
			Vodja.vrstaIgralca.put(1, VrstaIgralca.C); 
			Vodja.vrstaIgralca.put(-1, VrstaIgralca.R);
			Vodja.igramoNovoIgro();
		} else if (e.getSource() == igraRacunalnikClovek) {
			Vodja.vrstaIgralca = new HashMap<Integer,VrstaIgralca>();
			Vodja.vrstaIgralca.put(1, VrstaIgralca.R); 
			Vodja.vrstaIgralca.put(-1, VrstaIgralca.C);
			Vodja.igramoNovoIgro();
		} else if (e.getSource() == igraClovekClovek) {
			Vodja.vrstaIgralca = new HashMap<Integer,VrstaIgralca>();
			Vodja.vrstaIgralca.put(1, VrstaIgralca.C); 
			Vodja.vrstaIgralca.put(-1, VrstaIgralca.C);
			Vodja.igramoNovoIgro();
		} else if (e.getSource() == igraRacunalnikRacunalnik) {
			Vodja.vrstaIgralca = new HashMap<Integer,VrstaIgralca>();
			Vodja.vrstaIgralca.put(1, VrstaIgralca.R); 
			Vodja.vrstaIgralca.put(-1, VrstaIgralca.R);
			Vodja.igramoNovoIgro();
		}
	}

	public void osveziGUI() {
		if (Vodja.igra == null) {
			status.setText("Igra ni v teku.");
		}
		else {
			double stanje = Game.getGameEnded(Vodja.igra.board, Vodja.igra.igralec);
			if (stanje == 0) {
				String ime = "bel";
				if (Vodja.igra.igralec == 1) ime = "črn";
		 		status.setText("Na potezi je " + ime + 
		 				" - " + Vodja.vrstaIgralca.get(Vodja.igra.igralec));
			}
			else if (stanje == -1 || stanje == 1) {
				if (Vodja.igra.igralec == 1) {
					status.setText("Zmagal je bel." + 
					 		Vodja.vrstaIgralca.get(-1));
				}
				else {
					status.setText("Zmagal je črn." + 
							Vodja.vrstaIgralca.get(1));
				}
			}
			else {
				System.out.println(stanje);
				status.setText("Neodločeno!");
			}
			
		}
		polje.repaint();
	}
	



}
