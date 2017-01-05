package tiledleveleditor.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import tiledleveleditor.core.Tile;

/**
 * renders a given tile
 */
public class TileRenderer {

    public void render(Graphics2D g, Tile tile, Point loc, float scale, Color markColor) {

        String serial = tile.serialize();
        Color bg = null;
        Color border = null;
        int borderWidth = Math.max((int)(scale)/19, 1);
        String text = null;

        if (serial.equals("empty")) {
            bg = Color.LIGHT_GRAY;
            border = Color.GRAY;
        } else if (serial.equals("solid")) {
            bg = Color.GRAY;
            border = null;
        } else if (serial.equals("ice")) {
            bg = new Color(100, 200, 255);
            border = null;
        } else if (serial.startsWith("breaking")) {
            bg = Color.GREEN.darker();
            border = bg.darker();
            text = "  " + serial.split(":")[1] + "  ";
        } else if (serial.startsWith("teleporter")) {
            bg = Color.ORANGE.darker();
            border = bg.darker();
            text = "TP:" + serial.split(":")[1];
        } else if (serial.startsWith("switch")) {
            bg = Color.DARK_GRAY;
            border = bg.darker();
            text = "Sw:" + serial.split(":")[1];
        } else if (serial.startsWith("bridge")) {
            bg = Color.YELLOW;
            border = bg.darker();
            text = "Br:" + serial.split(":")[1];
        } else if (serial.startsWith("newLevel")) {
            bg = Color.GREEN;
            border = new Color(154, 255, 51);
            text = "nL:" + serial.split(":")[1];
        } else if (serial.startsWith("goal")) {
            bg = Color.MAGENTA.darker();
            border = bg.darker();
            text = "  !  ";
        } else {
            System.err.println("unknown tileType: " + serial);
            bg = Color.RED;
        }

        int s = (int) Math.ceil(scale);
        if (bg != null) {
            g.setColor(bg);
            g.fillRect(loc.x, loc.y, s, s);
        }
        if (border != null) {
            g.setColor(border);
            g.setStroke(new BasicStroke(borderWidth));
            g.drawRect(loc.x + (borderWidth-2)/2, loc.y + (borderWidth-2)/2, s - borderWidth + 1, s - borderWidth + 1);
        }
        if (markColor != null) {
            g.setColor(markColor);
            g.fillOval(loc.x + (s / 4), loc.y + (s / 4), s / 2, s / 2);
        }
        if (text != null) {
            g.setColor(Color.BLACK);
            int o = scaleFontAndGetOffset(text, s - borderWidth - borderWidth, g);
            g.drawString(text, loc.x + borderWidth, loc.y + s - borderWidth - o);
        }
    }

    public static int scaleFontAndGetOffset(String text, float textWidth, Graphics g) {
        float fontSize = 20.0f;
        

        Font font = g.getFont().deriveFont(g.getFont().getStyle(), fontSize);
        Rectangle2D bounds = g.getFontMetrics(font).getStringBounds(text, g);
        int width = (int)Math.max(bounds.getWidth(), bounds.getHeight());
        fontSize = (textWidth / width) * fontSize;
        Font res = g.getFont().deriveFont(g.getFont().getStyle(), fontSize);
        g.setFont(res);
        double ratio = bounds.getHeight()/ bounds.getWidth();
        return (int)((1 - ratio) * textWidth/2);
    }
}
