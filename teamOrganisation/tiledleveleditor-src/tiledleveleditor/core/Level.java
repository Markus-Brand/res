package tiledleveleditor.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

/**
 * One stage containing everything needed: The grid structure, the starting
 * position, metadata etc
 */
public class Level {

	private final Grid grid;
	private Point startPosition;
	private String title;
	private String description;
	private String escapeHandler;
	private Collection<LevelOverlay> overlays = new ArrayList<>();

	public Level(Grid grid, Point startPosition) {
		this.grid = grid;
		this.startPosition = startPosition;
		this.title = "Unnamed Level";
		this.description = "";
		this.escapeHandler = "menu/mainMenu";
	}
	
	public void reCoordinate() {
		grid.iterator().forEachRemaining((Tile t) -> {
			if (t.getType() == TileTypeContainer.get("teleporter")) {
				Point p = Tile.fromSmalltalk(t.getOption("target"));
				p.x += grid.getTileOffset().x;
				p.y += grid.getTileOffset().y;
				t.setOption("target", Tile.toSmalltalk(p));
			}
		});
		startPosition.x += grid.getTileOffset().x;
		startPosition.y += grid.getTileOffset().y;
		grid.getTileOffset().x = 0;
		grid.getTileOffset().y = 0;
	}

	public Grid getGrid() {
		return grid;
	}

	public Point getStartPosition() {
		return startPosition;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setStartPosition(Point startPosition) {
		this.startPosition = startPosition;
	}

	public Collection<LevelOverlay> getOverlays() {
		return overlays;
	}

	public String getEscapeHandler() {
		return escapeHandler;
	}

	public void setEscapeHandler(String escapeHandler) {
		this.escapeHandler = escapeHandler;
	}
}
