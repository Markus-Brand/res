package tiledleveleditor.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import tiledleveleditor.core.TileType;
import tiledleveleditor.core.TileTypeContainer;

/**
 * What tool is currently selected?
 */
public class CursorStatus {

	private int toolHeight = 35;
	private int width = 315;
	private int margin = 10;

	private List<CursorTool> tools;
	private int currentTool;
	private Point position;

	public CursorStatus(Point pos) {
		this.position = pos;
		loadTools();
		currentTool = 2;
	}

	public void loadTools() {
		this.tools = new ArrayList<>();
		tools.add(CursorTool.CHANGE_OPTIONS);
		for (TileType type : TileTypeContainer.getTypes()) {
			tools.add(CursorTool.createTileTool(type));
		}
	}

	public void render(Graphics2D g) {
		//render cursor status

		int i = 0;
		g.setColor(Color.BLACK);
		g.fillRect(position.x, position.y, getSize().x, getSize().y);
		for (CursorTool tool : tools) {
			int y = position.y + margin + (i * (toolHeight + 2));
			int x = position.x + margin;
			if (i == currentTool) {
				g.setColor(Color.GRAY);
			} else {
				g.setColor(Color.LIGHT_GRAY);
			}
			g.fillRect(x, y, width, toolHeight);
			if (i == currentTool) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.BLACK);
			}
			g.setStroke(new BasicStroke(2));
			g.drawRect(x, y, width, toolHeight);
			String msg = tool.getName();
			if (i == 0) {
				msg = "[esc] " + msg;
			} else {
				msg = "[ " + i + " ]  " + msg;
			}
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(msg, g);
			g.setColor(Color.BLACK);
			g.drawString(msg, x + 5, y + (int) (bounds.getHeight() - 2) + 5);
			i++;
		}
	}

	public Point getSize() {
		return new Point(width + margin * 2, (toolHeight + 2) * tools.size() + margin * 2);
	}

	/**
	 *
	 * @param evt
	 * @return true if the event got handled
	 */
	public boolean onClick(MouseEvent evt) {
		if (!pointInRect(evt.getX(), evt.getY(), position, getSize())) {
			return false;
		}

		int i = 0;
		for (CursorTool tool : tools) {
			if (pointInRect(evt.getX(), evt.getY(),
					new Point(position.x + margin,
							position.y + margin + (i * (toolHeight + 2))),
					new Point(width, toolHeight))) {
				currentTool = i;
				if (evt.getClickCount() > 1) {
					if (tool instanceof CursorTool.TileTool) {
						CursorTool.TileTool tt = (CursorTool.TileTool) tool;
						new TileOptionsDialog(null, tt.getParamPrototypeTile(), "Change Current Brush's presets").setVisible(true);
					}
				}

			}
			i++;
		}

		return true;
	}

	private boolean pointInRect(int x, int y, Point start, Point size) {
		return (x >= start.x
				&& y >= start.y
				&& x <= start.x + size.x
				&& y <= start.y + size.y);
	}
	
	/**
	 * 
	 * @param p
	 * @return true whether the given point lies within this panel
	 */
	public boolean isPointOn(Point p) {
		return pointInRect(p.x, p.y, position, getSize());
	}

	public CursorTool getCurrentTool() {
		return tools.get(currentTool);
	}

	/**
	 * 
	 * @param evt
	 * @return true if handled event
	 */
	public boolean onKey(KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (currentTool == 0) {
				return false;
			} else {
				currentTool = 0;
				return true;
			}
		}
		
		int num = -1;
		if ((int)evt.getKeyChar() > (int)'0' &&
				(int)evt.getKeyChar() <= (int)'9') {
			num = (int)evt.getKeyChar() - (int)'0';
		}
		if (num > 0) {
			currentTool = num;
		}
		
		return false;
	}
}
