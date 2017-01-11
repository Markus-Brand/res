package tiledleveleditor.core;

import java.awt.Point;
import java.util.Map;
import java.util.TreeMap;

/**
 * One instance of a specific Tile
 */
public abstract class Tile {

    public static Tile deserialize(String serial) {
        
        TileType type = TileTypeContainer.getBySerial(serial);
        
        Tile t = type.generateNew(serial.split(":"));
        
        return t;
    }

	protected Map<String, String> options;
	protected final TileType type;

	public TileType getType() {
		return type;
	}
	
	

	public Tile(TileType type) {
		this.type = type;
		options = new TreeMap<>();
	}

	public Tile(TileType type, String defaultOptions) {
		this(type);
		for (String o : defaultOptions.split(";")) {
			String[] data = o.split("=");
			options.put(data[0].trim(), data[1].trim());
		}
	}

	protected String getOption(String name) {
		String result = options.get(name);
		return result == null ? "0" : result;
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public abstract String serialize();

	public void setOption(String string, String s) {
		options.put(string, s);
	}
	
	public static Point fromSmalltalk(String data) {
		String[] d = data.split("@");
		return new Point(Integer.valueOf(d[0]), Integer.valueOf(d[1]));
	}
	
	public static String toSmalltalk(Point data) {
		return data.x + "@" + data.y;
	}
}
