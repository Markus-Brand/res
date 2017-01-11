package tiledleveleditor.editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import tiledleveleditor.core.Level;
import tiledleveleditor.core.LevelOverlay;
import tiledleveleditor.core.Tile;
import tiledleveleditor.core.TileTypeContainer;
import static tiledleveleditor.editor.TileRenderer.scaleFontAndGetOffset;

/**
 * A wrapped level, able to draw. Contains view status information. can
 * translate between the global pixel coordinate system, the local
 * view-independent system and the tile-coordinates.
 */
public class LevelRenderer {

	private Level level;
	private float[] viewPos; //(0, 0) is center
	private float scale; //pixel per tile in grid4
	private Point size;

	private TileRenderer tileRenderer = new TileRenderer();

	public LevelRenderer(Level l) {
		this.level = l;
		this.viewPos = new float[]{0, 0};
		this.scale = 32;
		this.size = new Point(64, 64);
	}

	public void setSize(Point size) {
		this.size = size;
	}

	/**
	 * render the level with the current size
	 *
	 * @param g
	 */
	public void render(Graphics2D g) {
		if (level == null) {
			return;
		}

		Point dim = level.getGrid().getDimensions();
		for (int x = 0; x < dim.x; x++) {
			for (int y = 0; y < dim.y; y++) {
				Point relative = new Point(x - level.getGrid().getTileOffset().x, y - level.getGrid().getTileOffset().y);
				Point drawLoc = localToGlobal(new float[]{relative.x, relative.y});
				Color mark = null;
				if (level.getStartPosition().equals(relative)) {
					mark = Color.red;
				}
				tileRenderer.render(g, level.getGrid().getTile(relative, true), drawLoc, scale, mark);
			}
		}
		
		for (LevelOverlay o: level.getOverlays()) {
			if (o instanceof LevelOverlay.TextOverlay) {
				LevelOverlay.TextOverlay to = (LevelOverlay.TextOverlay)o;
				Point p = localToGlobal(to.getGridPosition());
				
				g.setColor(Color.BLACK);
				scaleFontAndGetOffset("test", scale, g);
				g.drawString(to.getText(), p.x, p.y);
			}
		}
	}

	/**
	 * wich tile is a current cursor hovering at?
	 *
	 * @param cursor
	 * @return
	 */
	public Point getTile(Point cursor) {
		float[] local = globalToLocal(cursor, true);
		return localToTile(local);
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	//
	//
	public float[] globalToLocal(Point global, boolean center) {
		float x = global.x;
		float y = global.y;

		if (center) {
			x -= size.x / 2;
			y -= size.y / 2;
		}

		x /= scale;
		y /= scale;

		if (center) {
			x -= viewPos[0];
			y -= viewPos[1];
		}

		return new float[]{x, y};
	}

	private Point localToGlobal(float[] local) {
		float x = local[0];
		float y = local[1];

		x += viewPos[0];
		y += viewPos[1];

		x *= scale;
		y *= scale;

		x += size.x / 2;
		y += size.y / 2;

		return new Point((int) Math.round(x), (int) Math.round(y));
	}

	private Point localToTile(float[] local) {
		return new Point((int) Math.floor(local[0]), (int) Math.floor(local[1]));
	}

	void moveView(Point delta) {
		float[] deltaLocal = globalToLocal(delta, false);
		viewPos[0] += deltaLocal[0];// / scale;
		viewPos[1] += deltaLocal[1];// / scale;
	}

	void zoom(double d) {
		scale *= d;
        scale = Math.min(Math.max(scale, 10), 50);
	}

	public void reCoordinate() {
		viewPos[0] -= level.getGrid().getTileOffset().x;
		viewPos[1] -= level.getGrid().getTileOffset().x;
		level.reCoordinate();
	}

	void setLevelTile(Point tcoord, Tile newTile) {
		getLevel().getGrid().setTile(tcoord, newTile);
		
		if (newTile.getType() == TileTypeContainer.get("empty")) {
			return;
		}
		Point move = new Point(Math.min(tcoord.x, 0), Math.min(tcoord.y, 0));
		viewPos[0] += move.x;
		viewPos[1] += move.y;
	}
}
