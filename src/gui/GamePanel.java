package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import leader.Leader;
import rules.GameVisible;
import utils.Pair;
//import rules.Board;

/**
 * Rectangular area with the game field and pieces.
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements MouseListener,MouseMotionListener {
	// Constants for gui
	// sizes
	private final static double CHIP_SIZE = 75. / 1024.;
	private final static double WOOD_WIDTH = 36. / 1024.;
	private final static double WOOD_HEIGHT = 36. / 878.;
	private final static double TRIANGLE_WIDTH = 70. / 1024.;
	
	// private final static double TRIANGLE_HEIGHT = 184. / 1024.;
	private final static double TRIANGLE_HEIGHT = CHIP_SIZE * 5;
	
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
	private final static Color COLOR_OUTLINE = Color.BLACK;
	private final static Color COLOR_TRIANGLE_WHITE = new Color(204, 30, 9);
	private final static Color COLOR_TRIANGLE_BLACK = new Color(221, 222, 171);
	
	// MARGINS TODO - REMOVE because margins look ugly
	private final static double MARGIN_TRIANGLES = 0.; /// 8. / 1024.;// Margin between triangles and the wooden box
	private final static int CLICK_MARGIN = 0;

	/**
	 * True, if one chip is being moved around
	 */
	private boolean activeChip;
	/**
	 * Index of the triagnle, from which we took an active chip
	 */
	private int activeChipIndex;
	private int activeChipX;
	private int activeChipY;
	private int activeChipDx;
	private int activeChipDy;
	private int activeChipColor;
	
	
	public GamePanel() {
		setBackground(COLOR_BACKGROUND);

		// all the chips are inactive in the beginning
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		//this.addMouseMotionListener(this);
		activeChip = false;
	}

	/**
	 * 
	 * @return Chip 2*radius on a current widnow
	 */
	private int chipSize() {
		return (int) (Math.round(Math.min(getWidth(), getHeight()) * CHIP_SIZE));
	}

	/**
	 * 
	 * @return Size in pixels of wooden box surrounding the board.
	 */
	private int woodSize() {
		return (int) Math.round(Math.min(((double) getWidth()) * WOOD_WIDTH, ((double) getHeight()) * WOOD_HEIGHT));
	}

	/**
	 * 
	 * @param i between 0 and 23
	 * @return {x, y} coordinates of i-th triangle board[i][]. Coordinates of left
	 *         corner (upper or lower)
	 */
	private int[] triangleCoordinates(int i) {
		int x = 0, y = 0;

		if (i + 1 >= 13)
			y = woodSize();
		else
			y = getHeight() - woodSize();

		int triangle_w = (int) Math.round(Math.round(GREEN_WIDTH * getWidth()) / 6.);
		int j = i + 1;
		if (i + 1 >= 13) {
			j -= 12;
			// top lone of triangles - from 13 to 24
			if (j <= 6)
				x = (int) (MARGIN_TRIANGLES * getWidth() + woodSize() + (j - 1) * triangle_w);
			else
				x = (int) (MARGIN_TRIANGLES * getWidth() + 3 * woodSize() + (int) Math.round(GREEN_WIDTH * getWidth())
						+ (j - 7) * triangle_w);

		} else {// bottom line of trangles
			if (j <= 6)
				x = (int) (MARGIN_TRIANGLES * getWidth() + 3 * woodSize() + Math.round(GREEN_WIDTH * getWidth())
						+ (6 - j) * triangle_w);
			// x = (int) (MARGIN_TRIANGLES * getWidth() + woodSize() + (k - 1) *
			// triangle_w);
			else
				x = (int) (MARGIN_TRIANGLES * getWidth() + woodSize() + (12 - j) * triangle_w);
			// x = (int) (MARGIN_TRIANGLES * getWidth() + 3 * woodSize() + (int)
			// Math.round(GREEN_WIDTH * getWidth())
			// + (k - 7) * triangle_w);
			// if (j <= 6)
			// x = (int) (MARGIN_TRIANGLES * getWidth() + woodSize() + (13 - j) *
			// triangle_w);
			// else
			// x = (int) (MARGIN_TRIANGLES * getWidth() + 3 * woodSize() + (int)
			// Math.round(GREEN_WIDTH * getWidth())
			// + (int) (Math.round(GREEN_WIDTH - MARGIN_TRIANGLES) * getWidth()) + (5 - j) *
			// triangle_w);
		}
		
		return new int[]{x,y};
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

		if (Leader.gameVisible != null) {

			GameVisible gameVisible = Leader.gameVisible;

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

			// --------------------------------------------------------------
			// TRIANGLES
			// ---------------------------------------------------------------
			// int triangle_w = (int) ((Math.round(GREEN_WIDTH - MARGIN_TRIANGLES) *
			// getWidth()) / 6);
			// int triangle_w = (int) Math.round( TRIANGLE_WIDTH * getWidth());
			int triangle_w = (int) Math.round(Math.round(GREEN_WIDTH * getWidth()) / 6.);

			for (int i = 12; i >= 1; i--) {

				int x0 = (int) MARGIN_TRIANGLES * getWidth() + woodSize() + (12 - i) * triangle_w;
				if (i <= 6)
					x0 = (int) MARGIN_TRIANGLES * getWidth() + woodSize() * 3
							+ (int) Math.round(GREEN_WIDTH * getWidth())
							+ (int) (Math.round(GREEN_WIDTH - MARGIN_TRIANGLES) * getWidth()) + (6 - i) * triangle_w;

				// upper triangles
				if (i % 2 == 0)
					g2.setColor(COLOR_TRIANGLE_WHITE);
				else
					g2.setColor(COLOR_TRIANGLE_BLACK);
				// TODO - should the border be of a different color?
				g2.fillPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) }, new int[] {
						(int) woodSize(), (int) woodSize(), (int) woodSize() + (int) (TRIANGLE_HEIGHT * getHeight()) },
						3);
				g2.drawPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) }, new int[] {
						(int) woodSize(), (int) woodSize(), (int) woodSize() + (int) (TRIANGLE_HEIGHT * getHeight()) },
						3);

				// lower triangles
				if (i % 2 == 1)
					g2.setColor(COLOR_TRIANGLE_WHITE);
				else
					g2.setColor(COLOR_TRIANGLE_BLACK);
				// TODO - should the border be of a different color?
				g2.fillPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) },
						new int[] { (int) getHeight() - woodSize(), (int) getHeight() - woodSize(),
								(int) getHeight() - woodSize() - (int) (TRIANGLE_HEIGHT * getHeight()) },
						3);
				g2.drawPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) },
						new int[] { (int) getHeight() - woodSize(), (int) getHeight() - woodSize(),
								(int) getHeight() - woodSize() - (int) (TRIANGLE_HEIGHT * getHeight()) },
						3);
			}

			// ----------------------------------------------------------
			// CHIPS
			// ----------------------------------------------------------
			// active chip
			if (activeChip) {
				if (activeChipColor == 1)
					g2.setColor(COLOR_W);
				else
					g2.setColor(COLOR_B);
				g2.fillOval(activeChipX, activeChipY, chipSize(), chipSize());
				g2.setColor(COLOR_OUTLINE);
				g2.drawOval(activeChipX, activeChipY, chipSize(), chipSize());
			}
			// other chips
			for (int i = 0; i < 24; i++) {
				int num = gameVisible.board.board[i][0];
				// we must draw one chip less if we moved one
				if (i == activeChipIndex && activeChip)
					num -= 1;
				if (num <= 0)
					continue;
				int color = gameVisible.board.board[i][1];
				if (color == 0)
					continue;
				if (color == 1)
					g2.setColor(COLOR_W);
				else
					g2.setColor(COLOR_B);

				// drawing chips
				for (int j = 1; j <= num; j++) {
					int x = triangleCoordinates(i)[0] + (int) Math.round((triangle_w - chipSize()) / 2.);
					int y = triangleCoordinates(i)[1];

					if (i + 1 >= 13)
						y += (j - 1) * chipSize();
					else
						y -= j * chipSize();

			
					// coloring
					if (color == 1)
						g2.setColor(COLOR_W);
					else
						g2.setColor(COLOR_B);

					g2.fillOval(x, y, chipSize(), chipSize());
					g2.setColor(COLOR_OUTLINE);
					g2.drawOval(x, y, chipSize(), chipSize());
				}

			}
			this.repaint();
		} else
			// TODO ne tega u log pisat k lah da kod ni tosd kul
			System.out.println("GUI: Nč nam risu k nč ni za risat bučko");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("GUI: mouse pressed");
		GameVisible gameVisible = Leader.gameVisible;
		if (gameVisible != null) {
			if (Leader.humanRound) {
				int x = e.getX();
				int y = e.getY();
				// checks all the top chekcers for the click
				boolean misclick = true;
				System.out.println("GUI: \tNa vrsti: " + Leader.gameVisible.player);
				for (int i = 0; i < 24; i++) {// cheks all the triangels
					// num of chips and colors in the triangle
					int num = gameVisible.board.board[i][0];
					if (num <= 0)
						continue;
					int color = gameVisible.board.board[i][1];
					if (color == 0)
						continue;

					// calculate the position oh the outside chip
					// (x0, y0) ....... upper left corner coordinates of the chip
					int triangle_w = (int) Math.round(Math.round(GREEN_WIDTH * getWidth()) / 6.);
					int x0 = triangleCoordinates(i)[0] + (int) Math.round((triangle_w - chipSize()) / 2.);
					int y0 = triangleCoordinates(i)[1];
					if (i + 1 >= 13)
						y0 += (num - 1) * chipSize();
					else
						y0 -= num * chipSize();

					// if the chip was clicked:
					int dw = chipSize() / 2;
					int r = dw + CLICK_MARGIN;// click margin is a bad idea with the current setup
					if ((x0 + dw - x) * (x0 + dw - x) + (y0 + dw - y) * (y0 + dw - y) <= r*r){
						if (color != Leader.gameVisible.player) {
							System.out.println("GUI: Misclick - tried to move a different chip\n\tTODO ukren neki");
						} else {
							System.out.println("GUI: Top chip selected!");
							misclick = false;
							activeChip = true;
							activeChipColor = color;
							activeChipIndex = i;
							activeChipDx = x0 - x;
							activeChipDy = y0 - y;
							activeChipX = x0;// = x + dx
							activeChipY = y0;// = y + dy
						}
					}

						
				}

				if (misclick) {
					// This kind of click does nothing.
					System.out.println("GUI: Misclick on (" + x + ", " + y + ")");
					// System.out.println("");
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		GameVisible gameVisible = Leader.gameVisible;
		if (gameVisible != null && activeChip) {
			activeChip = false;
			// Released on the wooden part
			if ((x < triangleCoordinates(5)[0] && x > triangleCoordinates(6)[0] + (TRIANGLE_WIDTH * getWidth())) || // wooden middle column
				(x < triangleCoordinates(11)[0]) || // wooden left column
				(x > triangleCoordinates(0)[0]) || // wooden right column
				(y < triangleCoordinates(23)[1]) || // wooden top row
				(y > triangleCoordinates(0)[1])) // wooden bottom row
			{
				System.out.println("GUI: Not released on a triangle");
			}
			// Indicies 0 to 11
			else if (y > (1 - TRIANGLE_HEIGHT) * getHeight() - woodSize()) {
				int i = 11;
				while (x > triangleCoordinates(i - 1)[0] && i >= 1) {
					--i;
				}
				boolean success = gameVisible.playMove(new Pair<Integer, Integer>(activeChipIndex, i));
				if (success) {
					System.out.println("GUI: Moved chip from triangle " + activeChipIndex + " to triangle " + i);
				}
			}
			// Indicies 12 to 23
			else if (y < TRIANGLE_HEIGHT * getHeight() + woodSize()) {
				int i = 12;
				while (x > triangleCoordinates(i + 1)[0] && i <= 22) {
					++i;
				}
				boolean success = gameVisible.playMove(new Pair<Integer, Integer>(activeChipIndex, i));
				if (success) {
					System.out.println("GUI: Moved chip from triangle " + activeChipIndex + " to triangle " + i);
				}
			}
			else {
				System.out.println("GUI: Not released on a triangle");
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		GameVisible gameVisible = Leader.gameVisible;
		if (gameVisible != null) {
			if (Leader.humanRound && activeChip) {
				activeChipX = x + activeChipDx;
				activeChipY = y + activeChipDy;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
