package tiledleveleditor.core;

import java.util.ArrayList;
import java.util.List;

/**
 * all the defautlt TyleTypes in one handy container
 */
public class TileTypeContainer {

	private static final List<TileType> types;

	public static List<TileType> getTypes() {
		return types;
	}
	
	public static TileType get(String name) {
		for (TileType t: getTypes()) {
			if (t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return null;
	}
    
    public static TileType getBySerial(String serial) {
        int longest = 0;
        TileType type = null;
		for (TileType t: getTypes()) {
            String tileSerial = t.generateNew().serialize();
            for (int l = longest + 1; true; l++) {
                if (tileSerial.length() > l && 
                        tileSerial.substring(0, l).equals(serial.substring(0, l))) {
                    longest = l;
                    type = t;
                } else {
                    break;
                }
            }
        }
        return type;
    }

	static {
		types = new ArrayList<>();
		types.add(new TileType.NoParams("Empty"));

		types.add(new TileType.NoParams("Solid"));

		types.add(new TileType.NoParams("Goal"));

		types.add(new TileType.Simple("Breaking", "stepAmount=1"));

		types.add(new TileType.Simple("Teleporter", "target=1@1"));

		types.add(new TileType.Simple("Switch", "switch=1"));

		types.add(new TileType.Simple("Bridge", "condition=+1"));
	}
}
