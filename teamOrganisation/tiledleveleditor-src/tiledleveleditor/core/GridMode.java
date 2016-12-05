package tiledleveleditor.core;

/**
 * The different modes of level
 */
public enum GridMode {

	@Deprecated
	Triangular(3), 
	Grid4(4), 
    @Deprecated
	Hex(6);

	private int neighbourCount;

	private GridMode(int neighbourCount) {
		this.neighbourCount = neighbourCount;
	}

	public int getNeighbourCount() {
		return neighbourCount;
	}

}
