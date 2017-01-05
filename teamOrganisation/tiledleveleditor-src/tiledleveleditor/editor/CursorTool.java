package tiledleveleditor.editor;

import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import tiledleveleditor.core.Tile;
import tiledleveleditor.core.TileType;

/**
 * A possible state of the cursor
 */
public abstract class CursorTool {

	public static final CursorTool NOP = new CursorTool("none") {

		@Override
		public Tile handleClick(Tile t) {
			return null;
		}

		@Override
		public Tile handleSecondClick(Tile t) {
			return null;
		}
	};

	public static final CursorTool CHANGE_OPTIONS = new CursorTool("Change Tile options") {

		@Override
		public Tile handleClick(Tile t) {
			new TileOptionsDialog(null, t, null).setVisible(true);
			return null;
		}

		@Override
		public Tile handleSecondClick(Tile t) {
			return handleClick(t);
		}

	};

	public static final CursorTool createTileTool(TileType type) {
		return new TileTool(type, type.getName());
	}
	;
	
	protected String name;

	public CursorTool(String name) {
		this.name = name;
	}

	public abstract Tile handleClick(Tile t);

	public abstract Tile handleSecondClick(Tile t);

	public String getName() {
		return name;
	}

	public static class TileTool extends CursorTool {

		private final TileType type;
		private final Tile paramPrototypeTile;

		public TileTool(TileType type, String name) {
			super(name);
			this.type = type;
			paramPrototypeTile = this.type.generateNew();
		}

		@Override
		public Tile handleClick(Tile t) {
			Tile newTile = this.type.generateNew();
			if (paramPrototypeTile != null) {
				newTile.getOptions().putAll(paramPrototypeTile.getOptions());
			}
			return newTile;
		}

		public TileType getType() {
			return type;
		}

		@Override
		public Tile handleSecondClick(Tile t) {
			return CHANGE_OPTIONS.handleClick(t);
		}

		public Tile getParamPrototypeTile() {
			return paramPrototypeTile;
		}

		@Override
		public String getName() {
			StringBuilder b = new StringBuilder(super.getName());
			
			if (getParamPrototypeTile().getOptions().size() > 0) {
				b.append(" {");
				b.append(getParamPrototypeTile().getOptions().entrySet().stream()
						.map((Entry<String, String> e) -> e.getKey() + "=" + e.getValue())
						.reduce((String t, String u) -> t + ", " + u)
						.get());
				b.append("}");
			}
			return b.toString();
		}
	}
}
