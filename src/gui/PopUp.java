package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Popup window for GamePanel
 */
public class PopUp {
    public GamePanel panel;
    //protected int width;
   // protected int height;
    //protected int x;
    //protected int y;
    protected String text;
    protected String title;

    public PopUp(String title, String text, GamePanel panel) {
        this.title = title;
        this.text = text;
        this.panel = panel;
    }

    public void draw(Graphics g, Color cTitle, Color border, Color fill,int x,int y,int width,int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(fill);
        g2.fillRect(x, y, width, height);
        g2.setColor(border);
        g2.drawRect(x, y, width, height);
        // title
        g2.setColor(cTitle);
        g2.setFont(g2.getFont().deriveFont((float) (width / (0.6 * title.length()))));
        int h = (int) (g2.getFontMetrics().getStringBounds(title, g).getHeight());
        int w = (int) (g2.getFontMetrics().getStringBounds(title, g).getWidth());
        g2.drawString(title, x + width / 2 - w / 2, y + height / 3 - h / 2);
        // text
        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont((float) (width / (1 * title.length()))));
        h = (int) (g2.getFontMetrics().getStringBounds(text, g).getHeight());
        w = (int) (g2.getFontMetrics().getStringBounds(text, g).getWidth());
        g2.drawString(text, x + width / 2 - w / 2, y + 2 * height / 3 - h / 2);
    }
}
