package tiledleveleditor.core;

import java.awt.Point;

/**
 * One stage containing everything needed: The grid structure, the starting
 * position, metadata etc
 */
public class Level {

	private final Grid grid;
	private Point startPosition;
	private String title;
	private String description;
	private boolean breakAllTilesToWin = false;

	public Level(Grid grid, Point startPosition) {
		this.grid = grid;
		this.startPosition = startPosition;
		this.title = "Unnamed Level";
		this.description = "";
	}
	
	public void reCoordinate() {
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

	public GridMode getGridMode() {
		return getGrid().getMode();
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

	public void setBreakAllTilesToWin(boolean breakAllTilesToWin) {
		this.breakAllTilesToWin = breakAllTilesToWin;
	}

	public boolean isBreakAllTilesToWin() {
		return breakAllTilesToWin;
	}
}
