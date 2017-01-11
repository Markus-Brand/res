package tiledleveleditor.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Part of a Level: The structure this can resize dynamically
 */
public class Grid implements Iterable<Tile> {

	private TileType defaultTileType;
	private Tile[][] tiles;
	private Point tileOffset;

	public Grid(TileType defaultTileType) {
		this(defaultTileType, new Point(1, 1));
	}

	public Grid(TileType defaultTileType, Point startSize) {
		this.tileOffset = new Point(0, 0);
		this.defaultTileType = defaultTileType;
		tiles = new Tile[startSize.x][startSize.y];
	}

	public void setTile(Point coords, Tile t) {
		resizeToFit(coords);
		if (getDimensions().equals(new Point(1, 1)) && t.getType() == TileTypeContainer.get("empty")) {
			return;
		}
		tiles[tileOffset.x + coords.x][tileOffset.y + coords.y] = t;
	}

	public Tile getTile(Point coords) {
		return getTile(coords, false);
	}

	public Tile getTile(Point coords, boolean noNull) {
		try {
			Tile t = tiles[tileOffset.x + coords.x][tileOffset.y + coords.y];
			if (noNull && t == null) {
				t = defaultTileType.generateNew();
				setTile(coords, t);
			}
			return t;
		} catch (IndexOutOfBoundsException ex) {
			if (noNull) {
				setTile(coords, defaultTileType.generateNew());
				return tiles[tileOffset.x + coords.x][tileOffset.y + coords.y];
			} else {
				return null;
			}
		}
	}

	public List<Point> getNeighbours(Point coords) {
		List<Point> l = new ArrayList<>(4);
		l.add(new Point(coords.x - 1, coords.y));
		l.add(new Point(coords.x + 1, coords.y));
		l.add(new Point(coords.x, coords.y - 1));
		l.add(new Point(coords.x, coords.y + 1));
		return l;
	}

	public Point getDimensions() {
		return new Point(tiles.length, tiles[0].length);
	}

	public Point getTileOffset() {
		return tileOffset;
	}

	/**
	 * remove borders of empty tiles. Call Level.recoordinate first!
	 */
	public void collapse() {
		//todo this
		Point d = getDimensions();
		Point offset = new Point(0, 0);

		//outer x
		boolean removeLine = true;
		do {
			int checkX = d.x - 1;
			for (int y = 0; y < d.y; y++) {
				Tile t = getTile(new Point(checkX, y));

				if (t != null && !defaultTileType.equals(t.getType())) {
					removeLine = false;
					break;
				}
			}
			if (removeLine) {
				d.x--;
			}
		} while (removeLine);

		//outer y
		removeLine = true;
		do {
			int checkY = d.y - 1;
			for (int x = 0; x < d.x; x++) {
				Tile t = getTile(new Point(x, checkY));

				if (t != null && !defaultTileType.equals(t.getType())) {
					removeLine = false;
					break;
				}
			}
			if (removeLine) {
				d.y--;
			}
		} while (removeLine);

		//inner x
		removeLine = true;
		do {
			for (int y = 0; y < d.y; y++) {
				Tile t = getTile(new Point(offset.x, y));

				if (t != null && !defaultTileType.equals(t.getType())) {
					removeLine = false;
					break;
				}
			}
			if (removeLine) {
				offset.translate(1, 0);
			}
		} while (removeLine);

		//inner y
		removeLine = true;
		do {
			for (int x = offset.x; x < d.x; x++) {
				Tile t = getTile(new Point(x, offset.y));

				if (t != null && !defaultTileType.equals(t.getType())) {
					removeLine = false;
					break;
				}
			}
			if (removeLine) {
				offset.translate(0, 1);
			}
		} while (removeLine);

		//apply changes
		if (offset.equals(new Point()) && d.equals(getDimensions())) {
			return;
		}

		Point finalDimensions = new Point(d.x - offset.x, d.y - offset.y);
		Tile[][] newTiles = new Tile[finalDimensions.x][finalDimensions.y];
		for (int x = 0; x < finalDimensions.x; x++) {
			for (int y = 0; y < finalDimensions.y; y++) {
				newTiles[x][y] = getTile(new Point(x + offset.x, y + offset.y));
			}
		}
		this.tiles = newTiles;
		this.tileOffset = new Point(this.tileOffset.x - offset.x, this.tileOffset.y - offset.y);

	}

    //
	//
	//
	private void resizeToFit(Point p) {
		if (containsCoord(p)) {
			return;
		}

		p = toAbs(p);
		Point d = getDimensions();
		int nx = d.x;
		int ny = d.y;
		int mx = 0;
		int my = 0;

		if (p.x < 0) {
			nx -= p.x;
			mx = p.x;
		} else if (p.x >= d.x) {
			nx += (p.x - d.x + 1);
		}
		if (p.y < 0) {
			ny -= p.y;
			my = p.y;
		} else if (p.y >= d.y) {
			ny += (p.y - d.y + 1);
		}

		tileOffset.x -= mx;
		tileOffset.y -= my;
		Tile[][] newTiles = new Tile[nx][ny];

		for (int x = 0; x < d.x; x++) {
			for (int y = 0; y < d.y; y++) {
				newTiles[x - mx][y - my] = tiles[x][y];
			}
		}

		tiles = newTiles;
	}

	private Point toAbs(Point coord) {
		return new Point(coord.x + tileOffset.x, coord.y + tileOffset.y);
	}

	private boolean containsCoord(Point p) {
		Point absolute = toAbs(p);

		if (absolute.x < 0 || absolute.y < 0) {
			return false;
		}

		Point dim = getDimensions();

		return absolute.x < dim.x && absolute.y < dim.y;
	}

	@Override
	public Iterator<Tile> iterator() {

		return new CachedIterator<>(new CachedIterator.ItemGetter<Tile>() {
			int x = 0;
			int y = 0;

			@Override
			public Tile tryNext() {
				Tile t = getTile(new Point(x, y), false);

				x++;
				if (x >= getDimensions().x) {
					x = 0;
					y++;
				}
				if (y >= getDimensions().y) {
					return null;
				}
				return t == null ? tryNext() : t;
			}
		});
	}

}
