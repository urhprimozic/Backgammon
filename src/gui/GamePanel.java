package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;

import leader.Leader;
import rules.GameVisible;
import utils.Pair;
//import rules.Board;

/**
 * Rectangular area with the game field and pieces.
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements MouseListener, MouseMotionListener {
	// Constants for gui
	// sizes
	private final static double CHIP_SIZE = 75. / 1024.;
	private final static double WOOD_WIDTH = 42. / 1024.;// 36. / 1024.;
	private final static double WOOD_HEIGHT = 42. / 1024.;// 36. / 878.;
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
	private final static Color COLOR_OUTLINE_HIGHLITED = Color.YELLOW;
	private final static Color COLOR_TRIANGLE_WHITE = new Color(204, 30, 9);
	private final static Color COLOR_TRIANGLE_BLACK = new Color(221, 222, 171);
	private final static Color COLOR_TEXT = Color.WHITE;
	private final static Color TEXT_B = Color.BLACK;
	private final static Color TEXT_W = Color.WHITE;

	// MARGINS TODO - REMOVE because margins look ugly
	private final static double MARGIN_TRIANGLES = 0.; /// 8. / 1024.;// Margin between triangles and the wooden box
	private final static int CLICK_MARGIN = 0;

	/**
	 * True, if one chip is being moved around
	 */
	public boolean activeChip;
	/**
	 * Index of the triagnle, from which we took an active chip
	 */
	private int activeChipIndex;
	private int activeChipX;
	private int activeChipY;
	private int activeChipDx;
	private int activeChipDy;
	private int activeChipColor;

	private PopUp noGame;

	public GamePanel() {
		setBackground(COLOR_BACKGROUND);

		// all the chips are inactive in the beginning
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		// this.addMouseMotionListener(this);
		activeChip = false;

		// popups:
		noGame = new PopUp("Dobrodošli v igri Backgammon", "Izberite igro v meniju", this);
	}

	/**
	 * 
	 * @return Chip diameter on a current widnow
	 */
	private int chipSize() {
		return (int) (Math.round(Math.min(getWidth(), getHeight()) * CHIP_SIZE));
	}

	/**
	 * 
	 * @return {width, height} size of the wooden border in pixels
	 */
	private int[] woodSize() {
		return new int[] { (int) (getWidth() * (1 - 2 * GREEN_WIDTH)) / 4,
				(int) (getHeight() * (1 - GREEN_HEIGHT)) / 2 };
	}

	/**
	 * 
	 * @return font size in pixels
	 */
	protected int fontSize() {
		return (int) (chipSize() / 1.5);
	}

	private int[] rollPosition() {
		return new int[] { (getWidth() / 4) - chipSize(), (getHeight() / 2) - (int) (chipSize() / 3) };
	}

	private int[] rollSize() {
		return new int[] { chipSize() * 2, fontSize() };
	}

	private int[] stringRollPosition() {
		return new int[] { (getWidth() / 4) - (int) (chipSize() / 1.03), (getHeight() / 2) + chipSize() / 4 };
	}

	private int[] stringRemovedPosition() {
		return new int[] { (3 * getWidth() / 4) - (int) (chipSize() / 1.02), (getHeight() / 2) + chipSize() / 4 };
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
			y = woodSize()[1];
		else
			y = getHeight() - woodSize()[1];

		int triangle_w = (int) Math.round(Math.round(GREEN_WIDTH * getWidth()) / 6.);
		int j = i + 1;
		if (i + 1 >= 13) {
			j -= 12;
			// top lone of triangles - from 13 to 24
			if (j <= 6)
				x = (int) (MARGIN_TRIANGLES * getWidth() + woodSize()[0] + (j - 1) * triangle_w);
			else
				x = (int) (MARGIN_TRIANGLES * getWidth() + 3 * woodSize()[0]
						+ (int) Math.round(GREEN_WIDTH * getWidth()) + (j - 7) * triangle_w);

		} else {// bottom line of trangles
			if (j <= 6)
				x = (int) (MARGIN_TRIANGLES * getWidth() + 3 * woodSize()[0] + Math.round(GREEN_WIDTH * getWidth())
						+ (6 - j) * triangle_w);
			// x = (int) (MARGIN_TRIANGLES * getWidth() + woodSize() + (k - 1) *
			// triangle_w);
			else
				x = (int) (MARGIN_TRIANGLES * getWidth() + woodSize()[0] + (12 - j) * triangle_w);
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

		return new int[] { x, y };
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1024, 878);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

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
			g2.fillRect(woodSize()[0], woodSize()[1], (int) Math.round(GREEN_WIDTH * getWidth()),
					(int) Math.round(GREEN_HEIGHT * getHeight()));
			g2.drawRect(woodSize()[0], woodSize()[1], (int) Math.round(GREEN_WIDTH * getWidth()),
					(int) Math.round(GREEN_HEIGHT * getHeight()));
			g2.fillRect(woodSize()[0] * 3 + (int) Math.round(GREEN_WIDTH * getWidth()), woodSize()[1],
					(int) Math.round(GREEN_WIDTH * getWidth()), (int) Math.round(GREEN_HEIGHT * getHeight()));
			g2.drawRect(woodSize()[0] * 3 + (int) Math.round(GREEN_WIDTH * getWidth()), woodSize()[1],
					(int) Math.round(GREEN_WIDTH * getWidth()), (int) Math.round(GREEN_HEIGHT * getHeight()));

			// --------------------------------------------------------------
			// TRIANGLES
			// ---------------------------------------------------------------
			// int triangle_w = (int) ((Math.round(GREEN_WIDTH - MARGIN_TRIANGLES) *
			// getWidth()) / 6);
			// int triangle_w = (int) Math.round( TRIANGLE_WIDTH * getWidth());
			int triangle_w = (int) Math.round(Math.round(GREEN_WIDTH * getWidth()) / 6.);

			for (int i = 12; i >= 1; i--) {

				int x0 = (int) MARGIN_TRIANGLES * getWidth() + woodSize()[0] + (12 - i) * triangle_w;
				if (i <= 6)
					x0 = (int) MARGIN_TRIANGLES * getWidth() + woodSize()[0] * 3
							+ (int) Math.round(GREEN_WIDTH * getWidth())
							+ (int) (Math.round(GREEN_WIDTH - MARGIN_TRIANGLES) * getWidth()) + (6 - i) * triangle_w;

				// upper triangles
				if (i % 2 == 0)
					g2.setColor(COLOR_TRIANGLE_WHITE);
				else
					g2.setColor(COLOR_TRIANGLE_BLACK);
				// TODO - should the border be of a different color?
				g2.fillPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) }, new int[] {
						woodSize()[1], woodSize()[1], woodSize()[1] + (int) (TRIANGLE_HEIGHT * getHeight()) }, 3);
				g2.drawPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) }, new int[] {
						woodSize()[1], woodSize()[1], woodSize()[1] + (int) (TRIANGLE_HEIGHT * getHeight()) }, 3);

				// lower triangles
				if (i % 2 == 1)
					g2.setColor(COLOR_TRIANGLE_WHITE);
				else
					g2.setColor(COLOR_TRIANGLE_BLACK);
				// TODO - should the border be of a different color?
				g2.fillPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) },
						new int[] { getHeight() - woodSize()[1], getHeight() - woodSize()[1],
								getHeight() - woodSize()[1] - (int) (TRIANGLE_HEIGHT * getHeight()) },
						3);
				g2.drawPolygon(new int[] { x0, x0 + triangle_w, (int) ((2 * x0 + triangle_w) / 2) },
						new int[] { getHeight() - woodSize()[1], getHeight() - woodSize()[1],
								getHeight() - woodSize()[1] - (int) (TRIANGLE_HEIGHT * getHeight()) },
						3);
			}
			// ----------------------------------------------------------
			// BEARING OFF
			// ----------------------------------------------------------
			g2.setColor(COLOR_TEXT);
			g2.setFont(g2.getFont().deriveFont((float) fontSize() * (float) 0.5));
			g2.drawString("CHIPS", (int) (stringRemovedPosition()[0] + fontSize() / 1.42),
					(int) (stringRemovedPosition()[1] - fontSize() * 0.5));
			g2.drawString("OFF BOARD", stringRemovedPosition()[0], stringRemovedPosition()[1]);

			g2.setFont(g2.getFont().deriveFont((float) fontSize()));
			// white removed chips
			g2.setColor(COLOR_W);
			g2.fillOval((int) (3 * woodSize()[0] + GREEN_WIDTH * getWidth() * (1. + 1. / 4.) - chipSize() / 2),
					(int) (woodSize()[1] + GREEN_HEIGHT * getHeight() / 2. - chipSize() / 2), chipSize(), chipSize());
			g2.setColor(COLOR_OUTLINE);
			g2.drawOval((int) (3 * woodSize()[0] + GREEN_WIDTH * getWidth() * (1. + 1. / 4.) - chipSize() / 2),
					(int) (woodSize()[1] + GREEN_HEIGHT * getHeight() / 2. - chipSize() / 2), chipSize(), chipSize());

			g2.setColor(TEXT_B);
			if (gameVisible.board.offboard.getFirst() < 10) {
				g2.drawString(String.valueOf(gameVisible.board.offboard.getFirst()),
						(int) (3 * woodSize()[0] + GREEN_WIDTH * getWidth() * (1. + 1. / 4.) - fontSize() / 3),
						(int) (woodSize()[1] + GREEN_HEIGHT * getHeight() / 2. + chipSize() / 4));
			} else {
				g2.drawString(String.valueOf(gameVisible.board.offboard.getFirst()),
						(int) (3 * woodSize()[0] + GREEN_WIDTH * getWidth() * (1. + 1. / 4.) - fontSize() / 1.6),
						(int) (woodSize()[1] + GREEN_HEIGHT * getHeight() / 2. + chipSize() / 4));
			}
			// black removed chips
			g2.setColor(COLOR_B);
			g2.fillOval((int) (3 * woodSize()[0] + GREEN_WIDTH * getWidth() * (1. + 3. / 4.) - chipSize() / 2),
					(int) (woodSize()[1] + GREEN_HEIGHT * getHeight() / 2. - chipSize() / 2), chipSize(), chipSize());
			g2.setColor(COLOR_OUTLINE);
			g2.drawOval((int) (3 * woodSize()[0] + GREEN_WIDTH * getWidth() * (1. + 3. / 4.) - chipSize() / 2),
					(int) (woodSize()[1] + GREEN_HEIGHT * getHeight() / 2. - chipSize() / 2), chipSize(), chipSize());

			g2.setColor(TEXT_W);
			if (gameVisible.board.offboard.getLast() < 10) {
				g2.drawString(String.valueOf(gameVisible.board.offboard.getLast()),
						(int) (3 * woodSize()[0] + GREEN_WIDTH * getWidth() * (1. + 3. / 4.) - fontSize() / 3),
						(int) (woodSize()[1] + GREEN_HEIGHT * getHeight() / 2. + chipSize() / 4));
			} else {
				g2.drawString(String.valueOf(gameVisible.board.offboard.getLast()),
						(int) (3 * woodSize()[0] + GREEN_WIDTH * getWidth() * (1. + 3. / 4.) - fontSize() / 1.6),
						(int) (woodSize()[1] + GREEN_HEIGHT * getHeight() / 2. + chipSize() / 4));
			}

			// ----------------------------------------------------------
			// BOARD TEXT
			// ----------------------------------------------------------

			// Dice rolling
			// TODO: only if active player is human
			if (!Leader.diceRolled) {
				g2.setColor(COLOR_W);
				g2.fillRect(rollPosition()[0], rollPosition()[1], rollSize()[0], rollSize()[1]);

				g2.setColor(COLOR_OUTLINE);
				g2.drawRect(rollPosition()[0], rollPosition()[1], rollSize()[0], rollSize()[1]);

				g2.setFont(g2.getFont().deriveFont((float) fontSize()));
				g2.setColor(TEXT_B);
				g2.drawString("ROLL!", stringRollPosition()[0], stringRollPosition()[1]);
			}
			else {
				int spacing = ((int) (GREEN_WIDTH * getWidth()) - 2 * chipSize()) / 3;

				g2.setColor(COLOR_W);
				g2.fillRoundRect(woodSize()[0] + spacing, getHeight() / 2 - chipSize() / 2, chipSize(), chipSize(), chipSize() / 4, chipSize() / 4);
				g2.fillRoundRect(woodSize()[0] + 2 * spacing + chipSize(), getHeight() / 2 - chipSize() / 2, chipSize(), chipSize(), chipSize() / 4, chipSize() / 4);
				
				g2.setColor(COLOR_OUTLINE);
				g2.drawRoundRect(woodSize()[0] + spacing, getHeight() / 2 - chipSize() / 2, chipSize(), chipSize(), chipSize() / 4, chipSize() / 4);
				g2.drawRoundRect(woodSize()[0] + 2 * spacing + chipSize(), getHeight() / 2 - chipSize() / 2, chipSize(), chipSize(), chipSize() / 4, chipSize() / 4);
				
				g2.setFont(g2.getFont().deriveFont((float) fontSize()));
				g2.setColor(TEXT_B);
				g2.drawString(String.valueOf(gameVisible.board.dice.getFirst()),
						woodSize()[0] + spacing + chipSize() * 7 / 24, getHeight() / 2 + chipSize() / 4);
				g2.drawString(String.valueOf(gameVisible.board.dice.getLast()),
						woodSize()[0] + 2 * spacing + chipSize() + chipSize() * 7 / 24,
						getHeight() / 2 + chipSize() / 4);

			}
			// ----------------------------------------------------------
			// CHIPS
			// ----------------------------------------------------------

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
				for (int j = 1; j <= Math.min(num, 5); j++) {
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

					// we highlight the chips that can be moved
					// TODO: only legal moves for the given dice roll
					if (Leader.diceRolled && color == Leader.gameVisible.player && j == Math.min(num, 5)
							&& (!activeChip || i != activeChipIndex)) {
						g2.setColor(COLOR_OUTLINE_HIGHLITED);
					} else
						g2.setColor(COLOR_OUTLINE);
					g2.drawOval(x, y, chipSize(), chipSize());
				}
				// when there are too many chips in one spot
				if (num >= 6) {
					if (color == 1) {
						g2.setColor(TEXT_B);
					}
					else {
						g2.setColor(TEXT_W);
					}
					g2.setFont(g2.getFont().deriveFont((float) fontSize()));
					// top row
					if (i >= 12) {
						if (num - 4 < 10) {
							g2.drawString(String.valueOf(num - 4), triangleCoordinates(i)[0] + (chipSize() * 7 / 24) + ((int) (getWidth() * TRIANGLE_WIDTH) - chipSize()), triangleCoordinates(i)[1] + chipSize() * 3 / 4);
						}
						else {
							g2.drawString(String.valueOf(num - 4), triangleCoordinates(i)[0] + (chipSize() / 12) + ((int) (getWidth() * TRIANGLE_WIDTH) - chipSize()), triangleCoordinates(i)[1] + chipSize() * 3 / 4);
						}
					}
					// bottom row
					else {
						if (num - 4 < 10) {
							g2.drawString(String.valueOf(num - 4), triangleCoordinates(i)[0] + (chipSize() * 7 / 24) + ((int) (getWidth() * TRIANGLE_WIDTH) - chipSize()), triangleCoordinates(i)[1] - chipSize() / 4);
						}
						else {
							g2.drawString(String.valueOf(num - 4), triangleCoordinates(i)[0] + (chipSize() / 12) + ((int) (getWidth() * TRIANGLE_WIDTH) - chipSize()), triangleCoordinates(i)[1] - chipSize() / 4);
						}
					}
				}
			}

			// captured chips
			g2.setFont(g2.getFont().deriveFont((float) fontSize()));

			g2.setColor(COLOR_W);
			g2.fillOval(getWidth() / 2 - (chipSize() / 2), getHeight() / 2 - chipSize(), chipSize(), chipSize());
			// TODO: only legal moves for the given dice roll
			if (Leader.gameVisible.player == 1 && gameVisible.board.whiteChipsCaptured > 0 && Leader.diceRolled)
				g2.setColor(COLOR_OUTLINE_HIGHLITED);
			else
				g2.setColor(COLOR_OUTLINE);
			g2.drawOval(getWidth() / 2 - (chipSize() / 2), getHeight() / 2 - chipSize(), chipSize(), chipSize());

			g2.setColor(TEXT_B);
			if (gameVisible.board.whiteChipsCaptured < 10) {
				g2.drawString(String.valueOf(gameVisible.board.whiteChipsCaptured),
						(int) (getWidth() / 2 - (chipSize() / 4.8)), getHeight() / 2 - (chipSize() / 4));
			} else {
				g2.drawString(String.valueOf(gameVisible.board.whiteChipsCaptured),
						(int) (getWidth() / 2 - (chipSize() / 2.4)), getHeight() / 2 - (chipSize() / 4));
			}

			g2.setColor(COLOR_B);
			g2.fillOval(getWidth() / 2 - (chipSize() / 2), getHeight() / 2, chipSize(), chipSize());
			// TODO: only legal moves for the given dice roll
			if (Leader.gameVisible.player == -1 && gameVisible.board.blackChipsCaptured > 0 && Leader.diceRolled)
				g2.setColor(COLOR_OUTLINE_HIGHLITED);
			else
				g2.setColor(COLOR_OUTLINE);
			g2.drawOval(getWidth() / 2 - (chipSize() / 2), getHeight() / 2, chipSize(), chipSize());

			g2.setColor(TEXT_W);
			if (gameVisible.board.blackChipsCaptured < 10) {
				g2.drawString(String.valueOf(gameVisible.board.blackChipsCaptured),
						(int) (getWidth() / 2 - (chipSize() / 4.8)), getHeight() / 2 - (chipSize() / 4) + chipSize());
			} else {
				g2.drawString(String.valueOf(gameVisible.board.blackChipsCaptured),
						(int) (getWidth() / 2 - (chipSize() / 2.4)), getHeight() / 2 - (chipSize() / 4) + chipSize());
			}

			// active chip
			if (activeChip) {
				if (activeChipColor == 1)
					g2.setColor(COLOR_W);
				else
					g2.setColor(COLOR_B);
				g2.fillOval(activeChipX, activeChipY, chipSize(), chipSize());
				g2.setColor(COLOR_OUTLINE_HIGHLITED);
				g2.drawOval(activeChipX, activeChipY, chipSize(), chipSize());
			}
			this.repaint();
		} else
			noGame.draw(g, COLOR_BACKGROUND, COLOR_B, Color.WHITE, getWidth() / 7, getHeight() / 5, 5 * getWidth() / 7,
					3 * getHeight() / 5);
		
		// TODO ne tega u log pisat k lah da kod ni tosd kul
		// System.out.println("GUI: Nč nam risu k nč ni za risat bučko");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("GUI: mouse pressed");
		GameVisible gameVisible = Leader.gameVisible;
		if (gameVisible != null) {
			if (Leader.humanRound && Leader.diceRolled) {
				int x = e.getX();
				int y = e.getY();
				// checks all the top chips for the click
				System.out.println("GUI: \tNa vrsti: " + Leader.gameVisible.player);
				for (int i = 0; i < 24; i++) {// cheks all the triangels
					// num of chips and colors in the triangle
					int num = gameVisible.board.board[i][0];
					if (num <= 0)
						continue;
					int color = gameVisible.board.board[i][1];
					if (color == 0)
						continue;

					// calculate the position of the outside chip
					// (x0, y0) ....... upper left corner coordinates of the chip
					int triangle_w = (int) Math.round(Math.round(GREEN_WIDTH * getWidth()) / 6.);
					int x0 = triangleCoordinates(i)[0] + (int) Math.round((triangle_w - chipSize()) / 2.);
					int y0 = triangleCoordinates(i)[1];
					if (i + 1 >= 13)
						y0 += (Math.min(num, 5) - 1) * chipSize();
					else
						y0 -= Math.min(num, 5) * chipSize();

					// if the chip was clicked:
					int dw = chipSize() / 2;
					int r = dw + CLICK_MARGIN;// click margin is a bad idea with the current setup
					if ((x0 + dw - x) * (x0 + dw - x) + (y0 + dw - y) * (y0 + dw - y) <= r * r) {
						if (color != Leader.gameVisible.player) {
							System.out.println("GUI: Misclick - tried to move a different chip\n\tTODO ukren neki");
						} else {
							System.out.println("GUI: Top chip selected!");
							activeChip = true;
							activeChipColor = color;
							activeChipIndex = i;
							activeChipDx = x0 - x;
							activeChipDy = y0 - y;
							activeChipX = x0;// = x + dx
							activeChipY = y0;// = y + dy

							return;
						}
					}
				}
				int dw = chipSize() / 2;
				int r = dw + CLICK_MARGIN;

				// white captured chips
				int whitex0 = getWidth() / 2 - chipSize() / 2;
				int whitey0 = getHeight() / 2 - chipSize();
				if ((whitex0 + dw - x) * (whitex0 + dw - x) + (whitey0 + dw - y) * (whitey0 + dw - y) <= r * r) {
					if (Leader.gameVisible.player != 1) {
						System.out.println("GUI: Misclick - tried to move a different chip\n\tTODO ukren neki");
					} else {
						System.out.println("GUI: White captured chip selected!");
						activeChip = true;
						activeChipColor = 1;
						activeChipIndex = -1;
						activeChipDx = whitex0 - x;
						activeChipDy = whitey0 - y;
						activeChipX = whitex0;// = x + dx
						activeChipY = whitey0;// = y + dy

						return;
					}
				}

				// black captured chips
				int blackx0 = getWidth() / 2 - chipSize() / 2;
				int blacky0 = getHeight() / 2;
				if ((blackx0 + dw - x) * (blackx0 + dw - x) + (blacky0 + dw - y) * (blacky0 + dw - y) <= r * r) {
					if (Leader.gameVisible.player != -1) {
						System.out.println("GUI: Misclick - tried to move a different chip\n\tTODO ukren neki");
					} else {
						System.out.println("GUI: Black captured chip selected!");
						activeChip = true;
						activeChipColor = -1;
						activeChipIndex = 24;
						activeChipDx = blackx0 - x;
						activeChipDy = blacky0 - y;
						activeChipX = blackx0;// = x + dx
						activeChipY = blacky0;// = y + dy

						return;
					}
				}

				// This kind of click does nothing.
				System.out.println("GUI: Misclick on (" + x + ", " + y + ")");
				// System.out.println("");
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		GameVisible gameVisible = Leader.gameVisible;
		if (gameVisible != null && !Leader.diceRolled && Leader.humanRound) {
			if (x >= rollPosition()[0] && x <= rollPosition()[0] + rollSize()[0] &&
				y >= rollPosition()[1] && y <= rollPosition()[1] + rollSize()[1])
			{
				Leader.rollDice();
				
				System.out.println("GUI: Legal moves for player " + gameVisible.player);
				List<List<Pair<Integer, Integer>>> legal = gameVisible.board.getLegalMoves(gameVisible.player, gameVisible.board.dice);
				for (int i = 0; i < legal.size(); ++i) {
					Pair<Integer, Integer> test1 = legal.get(i).get(0);
					Pair<Integer, Integer> test2 = new Pair<Integer, Integer>(null, null);
					if (legal.get(i).size() != 1) {
						test2 = legal.get(i).get(1);
					}
					System.out.println("GUI: legal move: " + test1.getFirst() + " -> " + test1.getLast() + ", " + test2.getFirst() + " -> " + test2.getLast());
				}
				System.out.println();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		GameVisible gameVisible = Leader.gameVisible;
		if (gameVisible != null && activeChip) {
			activeChip = false;
			// Released on the wooden part
			if ((x < woodSize()[0] || x > getWidth() - woodSize()[0]
					|| (woodSize()[0] + GREEN_WIDTH * getWidth() < x
							&& x < 3 * woodSize()[0] + GREEN_WIDTH * getWidth()))
					|| (y < woodSize()[1] || y > getHeight() - woodSize()[1])) {
				// wood - try to drop one there
				System.out.println("Bearing off..");
				boolean success = false;
				if (activeChipColor == 1) {
					success = Leader.playMove(new Pair<Integer, Integer>(activeChipIndex, 24));
				}
				else {
					success = Leader.playMove(new Pair<Integer, Integer>(activeChipIndex, -1));
				}
				if (success) {
					System.out.println("");
					if (gameVisible.movesMade == 0) {
						Leader.diceRolled = false;
					}
				}

			} else if ((x < triangleCoordinates(5)[0] && x > triangleCoordinates(6)[0] + (TRIANGLE_WIDTH * getWidth()))
					|| // wooden middle column
					(x < triangleCoordinates(11)[0]) || // wooden left column
					(x > triangleCoordinates(0)[0] + (TRIANGLE_WIDTH * getWidth())) || // wooden right column
					(y < triangleCoordinates(23)[1]) || // wooden top row
					(y > triangleCoordinates(0)[1])) // wooden bottom row
			{
				System.out.println("GUI: Not released on a triangle");
			}
			// Indicies 0 to 11
			else if (y > (1 - TRIANGLE_HEIGHT) * getHeight() - woodSize()[1]) {
				int i = 11;
				while (x > triangleCoordinates(i - 1)[0] && i >= 1) {
					--i;
				}
				if (activeChipIndex == i) {
					return;
				}
				boolean success = Leader.playMove(new Pair<Integer, Integer>(activeChipIndex, i));
				if (success) {
					System.out.println("GUI: Moved chip from triangle " + activeChipIndex + " to triangle " + i);
					if (gameVisible.movesMade == 0) {
						Leader.diceRolled = false;
					}
				}
			}
			// Indicies 12 to 23
			else if (y < TRIANGLE_HEIGHT * getHeight() + woodSize()[1]) {
				int i = 12;
				while (x > triangleCoordinates(i + 1)[0] && i <= 22) {
					++i;
				}
				if (activeChipIndex == i) {
					return;
				}
				boolean success = Leader.playMove(new Pair<Integer, Integer>(activeChipIndex, i));
				if (success) {
					System.out.println("GUI: Moved chip from triangle " + activeChipIndex + " to triangle " + i);
					if (gameVisible.movesMade == 0) {
						Leader.diceRolled = false;
					}
				}
			}
			// TODO: Moving chips offboard
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
