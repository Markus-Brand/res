package tiledleveleditor.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import tiledleveleditor.core.TileType;
import tiledleveleditor.core.TileTypeContainer;
import tiledleveleditor.editor.CursorTool.TileTool;

/**
 * What tool is currently selected?
 */
public class CursorStatus {
	
	private CursorTool currentTool;

	public CursorStatus() {
		currentTool = CursorTool.NOP;
	}

	public void setCurrentTool(CursorTool currentTool) {
		this.currentTool = currentTool;
	}

	public CursorTool getCurrentTool() {
		return currentTool;
	}
	
	public void cycleTools() {
		if (currentTool ==CursorTool.NOP) {
			currentTool = CursorTool.CHANGE_OPTIONS;
		} else if (currentTool == CursorTool.CHANGE_OPTIONS) {
			currentTool = CursorTool.createTileTool(TileTypeContainer.getTypes().get(0));
		} else if (currentTool instanceof TileTool){
			TileType oldType = ((TileTool)currentTool).getType();
			int index = TileTypeContainer.getTypes().indexOf(oldType) + 1;
			if (index >= TileTypeContainer.getTypes().size()) {
				currentTool = CursorTool.NOP;
			} else {
				currentTool = CursorTool.createTileTool(TileTypeContainer.getTypes().get(index));
			}
		}
	}
	
	public void render(Graphics2D g) {
		if (currentTool == null) {
			currentTool = CursorTool.NOP;
		}
		//render cursor status
		String msg = "Current Tool: " + currentTool.getName();
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(msg, g);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, (int)(bounds.getWidth() + 10), (int)(bounds.getHeight() + 4));
		g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(2));
		g.drawRect(0, 0, (int)(bounds.getWidth() + 10), (int)(bounds.getHeight() + 4));
		g.setColor(Color.BLACK);
		g.drawString(msg, 4, (int)(bounds.getHeight() - 2));
	}
}
