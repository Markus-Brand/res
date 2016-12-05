package tiledleveleditor.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import tiledleveleditor.core.Grid;
import tiledleveleditor.core.GridMode;
import tiledleveleditor.core.Level;
import tiledleveleditor.core.Tile;
import tiledleveleditor.core.TileTypeContainer;

/**
 * The content pane of the editor
 */
public class EditPanel extends JPanel {

	private LevelRenderer levelRenderer; //thte level wea re editing atm.
	private File levelFile; //where is this level from?
	private CursorStatus cursorStatus;

	public void setLevel(Level l) {
		this.levelRenderer = new LevelRenderer(l);
		this.cursorStatus = new CursorStatus();
	}

	@Override
	public void paint(Graphics _g) {
		Graphics2D g = (Graphics2D) _g;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if (levelRenderer != null) {
			levelRenderer.setSize(new Point(getWidth(), getHeight()));
			levelRenderer.render(g);
			//draw the current cursor location
			g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(g.getFont().getStyle(), 14));
            Point mouse = super.getMousePosition();
			if (mouse != null) {
				float[] loc = levelRenderer.globalToLocal(mouse, true);
				String msg = "(" + (int)Math.floor(loc[0]) + ", " + (int)Math.floor(loc[1]) + ")";
				g.drawString("Loc: " + msg, 10, 37);
			}/**/

		}
		if (cursorStatus != null) {
			cursorStatus.render(g);
		}
	}

	private Point lastDrag;

	public void onDrag(MouseEvent evt) {
		if (lastDrag == null) {
			lastDrag = evt.getPoint();
			return;
		}
		Point delta = new Point(evt.getPoint().x - lastDrag.x, evt.getPoint().y - lastDrag.y);
		lastDrag = evt.getPoint();

		levelRenderer.moveView(delta);
	}

	void onMouseUp(MouseEvent evt) {
		lastDrag = null;
	}

	void onScroll(MouseWheelEvent evt) {
		float zoomFactor;
		if (evt.getPreciseWheelRotation() > 0) {
			zoomFactor = 1 / 1.05f;
		} else {
			zoomFactor = 1.05f;
		}
		levelRenderer.zoom(zoomFactor);
	}

	public void saveLevel(boolean quick) {
		if (levelFile == null || !quick) {
			JFileChooser ch = new JFileChooser();
			ch.setCurrentDirectory(getOpenStartFolder());
			ch.setFileFilter(new FileNameExtensionFilter("Level files", "lvlxml"));
			int result = ch.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				levelFile = ch.getSelectedFile();
			} else {
				return;
			}
			if (!levelFile.getName().endsWith(".lvlxml")) {
				levelFile = new File(levelFile.getParentFile(), levelFile.getName() + ".lvlxml");
			}
		}
		if (!LevelIO.save(levelRenderer.getLevel(), levelFile)) {
			JOptionPane.showMessageDialog(this, "There was an error in saving your Level.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	public static File getOpenStartFolder() {
		File relative = new File(".\\xml");
		if (relative.exists()) {
			return relative;
		}
		
		return new File(".");
	}

	public void openLevel() {
		JFileChooser ch = new JFileChooser();
		ch.setCurrentDirectory(getOpenStartFolder());
		ch.setFileFilter(new FileNameExtensionFilter("Level files", "lvlxml"));
		int result = ch.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			levelFile = ch.getSelectedFile();
		} else {
			return;
		}
		levelRenderer.setLevel(LevelIO.load(levelFile));
	}

	void newLevel() {
		levelFile = null;
		
		Grid g = new Grid(GridMode.Grid4, TileTypeContainer.get("empty"), new Point(5, 5));
		Level l = new Level(g, new Point(2, 2));
		setLevel(l);
	}

	void onClick(MouseEvent evt) {
		if (levelRenderer == null || cursorStatus == null) {
			return;
		}
		if (cursorStatus.getCurrentTool() == CursorTool.NOP) {
			return;
		}
		Point tcoord = levelRenderer.getTile(evt.getPoint());
		Tile t = levelRenderer.getLevel().getGrid().getTile(tcoord);
		Tile newTile = null;
		if ((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
			newTile = cursorStatus.getCurrentTool().handleClick(t);
		} else if ((evt.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
			newTile = cursorStatus.getCurrentTool().handleSecondClick(t);
		}
		if (newTile != null) {
			levelRenderer.getLevel().getGrid().setTile(tcoord, newTile);
		}
        levelRenderer.getLevel().reCoordinate();
        levelRenderer.getLevel().getGrid().collapse();
        levelRenderer.getLevel().reCoordinate();
	}

	void onKeyTyped(KeyEvent evt) {
		if (evt.getKeyChar() == '\t') {
			cursorStatus.cycleTools();
		}
	}

	public Level getLevel() {
		return levelRenderer.getLevel();
	}

	public LevelRenderer getLevelRenderer() {
		return levelRenderer;
	}

	public CursorStatus getCursorStatus() {
		return cursorStatus;
	}
	

	
}
