package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import leader.Leader;
import rules.Game;

/**
 * Rectangular area with the game field and pieces.
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements MouseListener {
	// Constants for gui
	// sizes
	private final static double CHIP_SIZE = 75. / 1024.;
	private final static double WOOD_WIDTH = 36. / 1024.;
	private final static double WOOD_HEIGHT = 36. / 878.;
	private final static double TRIANGLE_WIDTH = 70. / 1024.;
	private final static double TRIANGLE_HEIGHT = 184. / 1024.;
	private final static double LINE_SIZE = 2;
	private final static double GREEN_HEIGHT = 805. / 878.;
	private final static double GREEN_WIDTH = 439. / 1024.;

	/// colors
	private final static Color COLOR_B = Color.BLACK;
	private final static Color COLOR_W = Color.WHITE;
	private final static Color OUTLINE_B = Color.DARK_GRAY;
	private final static Color OUTLINE_W = Color.LIGHT_GRAY;
	private final static Color COLOR_BACKGROUND = new Color(27, 69, 24);// Color.GREEN;
	private final static Color COLOR_WOOD = new Color(138, 99, 55);
	private final static Color OUTLINE = Color.BLACK;
	private final static Color COLOR_TRIANGLE_WHITE = new Color(204, 30, 9);
	private final static Color COLOR_TRIANGLE_BLACK = new Color(221, 222, 171);

	// MARGINS TODO - REMOVE because margins look ugly
	private final static double MARGIN_TRIANGLES = 0.; /// 8. / 1024.;// Margin between triangles and the wooden box

	public GamePanel() {
		setBackground(COLOR_BACKGROUND);
		this.addMouseListener(this);
	}

	/**
	 * 
	 * @return Chip 2*radius on a current widnow
	 */
	private int chipSize() {
		return (int) (Math.round(Math.min(getWidth(), getHeight()) * CHIP_SIZE));
	}

	private int woodSize() {
		return (int) Math.round(Math.min(((double) getWidth()) * WOOD_WIDTH, ((double) getHeight()) * WOOD_HEIGHT));
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1024, 878);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// TODO remove if redundant
		Graphics2D g2 = (Graphics2D) g;

		Game game = Leader.game;

		g2.setStroke(new BasicStroke((float) LINE_SIZE));
		// wooden box
		g2.setColor(COLOR_WOOD);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.drawRect(0, 0, getWidth(), getHeight());
		// green background
		g2.setColor(COLOR_BACKGROUND);
		g2.fillRect(woodSize(), woodSize(), (int) Math.round(GREEN_WIDTH * getWidth()),
				(int) Math.round(GREEN_HEIGHT * getHeight()));
		g2.drawRect(woodSize(), woodSize(), (int) Math.round(GREEN_WIDTH * getWidth()),
				(int) Math.round(GREEN_HEIGHT * getHeight()));
		g2.fillRect(woodSize() * 3 + (int) Math.round(GREEN_WIDTH * getWidth()), woodSize(),
				(int) Math.round(GREEN_WIDTH * getWidth()), (int) Math.round(GREEN_HEIGHT * getHeight()));
		g2.drawRect(woodSize() * 3 + (int) Math.round(GREEN_WIDTH * getWidth()), woodSize(),
				(int) Math.round(GREEN_WIDTH * getWidth()), (int) Math.round(GREEN_HEIGHT * getHeight()));

		// triangles
		// int triangle_w = (int) ((Math.round(GREEN_WIDTH - MARGIN_TRIANGLES) *
		// getWidth()) / 6);
		// int triangle_w = (int) Math.round( TRIANGLE_WIDTH * getWidth());
		int triangle_w = (int) Math.round(Math.round(GREEN_WIDTH * getWidth()) / 6.);

		for (int i = 12; i >= 1; i--) {

			int x0 = (int) MARGIN_TRIANGLES * getWidth() + woodSize() + (12 - i) * triangle_w;
			if (i <= 6)
				x0 = (int) MARGIN_TRIANGLES * getWidth() + woodSize() * 3 + (int) Math.round(GREEN_WIDTH * getWidth())
						+ (int) (Math.round(GREEN_WIDTH - MARGIN_TRIANGLES) * getWidth()) + (6 - i) * triangle_w;

			// upper triangles
			if (i % 2 == 0)
				g2.setColor(COLOR_TRIANGLE_WHITE);
			else
				g2.setColor(COLOR_TRIANGLE_BLACK);
			// TODO - should the border be of a different color?
			g2.fillPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) }, new int[] {
					(int) woodSize(), (int) woodSize(), (int) woodSize() + (int) (TRIANGLE_HEIGHT * getHeight()) }, 3);
			g2.drawPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) }, new int[] {
					(int) woodSize(), (int) woodSize(), (int) woodSize() + (int) (TRIANGLE_HEIGHT * getHeight()) }, 3);

			// lower triangles
			if (i % 2 == 1)
				g2.setColor(COLOR_TRIANGLE_WHITE);
			else
				g2.setColor(COLOR_TRIANGLE_BLACK);
			// TODO - should the border be of a different color?
			g2.fillPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) }, new int[] {
					(int) getHeight() - woodSize(), (int) getHeight() - woodSize(), (int) getHeight() - woodSize() - (int) (TRIANGLE_HEIGHT * getHeight()) }, 3);
			g2.drawPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) }, new int[] {
					(int) getHeight() - woodSize(), (int) getHeight() - woodSize(), (int) getHeight() - woodSize() - (int) (TRIANGLE_HEIGHT * getHeight()) }, 3);
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Igra igra = Vodja.igra;
		// if(igra != null){
		// if (Vodja.clovekNaVrsti) {
		// int x = e.getX();
		// int y = e.getY();
		// int w = (int)(squareWidth());
		// int i = x / w ;
		// double di = (x % w) / squareWidth() ;
		// int j = y / w ;
		// double dj = (y % w) / squareWidth() ;
		// if (0 <= i && i < igra.N &&
		// 0.5 * SIRINA_CRTE < di && di < 1.0 - 0.5 * SIRINA_CRTE &&
		// 0 <= j && j < igra.N &&
		// 0.5 * SIRINA_CRTE < dj && dj < 1.0 - 0.5 * SIRINA_CRTE) {
		// Vodja.igrajClovekovoPotezo (new Koordinati(j, i));
		// }
		// }
		// }
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
