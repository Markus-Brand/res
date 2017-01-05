package tiledleveleditor.core;

import java.util.Map.Entry;

/**
 * One instance per possible type of tile, e.g. solid, breaking, portal, lever,
 * ...
 */
public abstract class TileType {

    protected String name;

    public TileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Tile generateNew() {
        return generateNew(null);
    }

    @Override
    public String toString() {
        return "TileType: " + getName();
    }

    public abstract Tile generateNew(String[] split);

    public static class Simple extends TileType {

        private final String optionString;

        public Simple(String name, String options) {
            super(name);
            this.optionString = options;
        }

        @Override
        public Tile generateNew() {
            return new Tile(this, optionString) {

                @Override
                public String serialize() {
                    StringBuilder s = new StringBuilder(name.substring(0, 1).toLowerCase());
					s.append(name.substring(1, name.length()));
                    for (Entry<String, String> e : super.options.entrySet()) {
                        s.append(":").append(e.getValue());
                    }
                    return s.toString();
                }
            };
        }

        @Override
        public Tile generateNew(String[] split) {
            Tile t = generateNew();

            int i = 1;
            for (Entry<String, String> e : t.options.entrySet()) {
                e.setValue(split[i]);
                i++;
            }

            return t;
        }

    }

    public static class NoParams extends TileType {

        public NoParams(String name) {
            super(name);
        }

        @Override
        public Tile generateNew(String[] split) {
            return new Tile(this) {
                @Override
                public String serialize() {
                    return name.toLowerCase();
                }
            };
        }


    }
}
