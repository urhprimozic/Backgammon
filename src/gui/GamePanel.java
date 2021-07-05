package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import logika.Igra;
import splosno.Koordinati;
import vodja.Vodja;

/**
 * Rectangular area with the game field and pieces.
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements MouseListener {

	// Relativna širina črte
	private final static double SIRINA_CRTE = 0.08;
	// Velikost zetona, relativna na kvadratek
	private final static double VELIKOST_ZETONA = 0.9;
	// Barva črt v mreži
	private Color barvaCrte;
	private Color barvaC;
	private Color barvaB;

	public GamePanel() {
		setBackground(Color.LIGHT_GRAY);
		this.addMouseListener(this);
		barvaCrte = Color.BLACK;
		barvaC = Color.BLACK;
		barvaB = Color.WHITE;
	}

	// Širina enega kvadratka
	private double squareWidth() {
		Igra igra = Vodja.igra;
		if (igra == null) {
			System.out.println("WARNING: squareWidth() called on igra=null");
			return 0.0;
		}
		return Math.min(getWidth() / igra.N, getHeight() / igra.N);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 600);
	}

	// Relativni prostor okoli X in O
	// private final static double PADDING = 0.18;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		Igra igra = Vodja.igra;
		double w = squareWidth();
		if (igra != null) {
			g2.setStroke(new BasicStroke((float) (w * SIRINA_CRTE)));

			// polja
			for (int vrstica = 0; vrstica < igra.N; vrstica++) {
				for (int stolpec = 0; stolpec < igra.N; stolpec++) {
					int p = Vodja.igra.board.plosca[vrstica][stolpec];
					if (p == 0) {
						continue;
					}
					else if (p == 1) {
						g2.setColor(barvaC);
					}
					else {
						g2.setColor(barvaB);
					}
					g2.fillOval((int) (stolpec * w + w*(1-VELIKOST_ZETONA)/2 + (SIRINA_CRTE )),
							(int) (vrstica * w + w*(1-VELIKOST_ZETONA)/2 + (SIRINA_CRTE )),
							(int) (w * VELIKOST_ZETONA), (int) (w * VELIKOST_ZETONA));
				}
			}

			// mreža
			g2.setColor(barvaCrte);
			for (int vrstica = 1; vrstica < igra.N; vrstica++) {
				g2.drawLine(0, vrstica * (int) w, igra.N * (int) w, vrstica * (int) w);
			}
			for (int stolpec = 1; stolpec < igra.N; stolpec++) {
				g2.drawLine(stolpec * (int) w, 0, stolpec * (int) w, igra.N * (int) w);
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Igra igra = Vodja.igra; 
		if(igra != null){
			if (Vodja.clovekNaVrsti) {
				int x = e.getX();
				int y = e.getY();
				int w = (int)(squareWidth());
				int i = x / w ;
				double di = (x % w) / squareWidth() ;
				int j = y / w ;
				double dj = (y % w) / squareWidth() ;
				if (0 <= i && i < igra.N &&
						0.5 * SIRINA_CRTE < di && di < 1.0 - 0.5 * SIRINA_CRTE &&
						0 <= j && j < igra.N && 
						0.5 * SIRINA_CRTE < dj && dj < 1.0 - 0.5 * SIRINA_CRTE) {
					Vodja.igrajClovekovoPotezo (new Koordinati(j, i));
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
