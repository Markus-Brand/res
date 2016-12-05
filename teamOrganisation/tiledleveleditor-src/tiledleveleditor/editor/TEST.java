/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiledleveleditor.editor;

import java.awt.Point;
import tiledleveleditor.core.Grid;
import tiledleveleditor.core.GridMode;
import tiledleveleditor.core.Level;
import tiledleveleditor.core.TileType;
import tiledleveleditor.core.TileTypeContainer;

/**
 *
 * @author Erik
 */
public class TEST {
	public static void main(String[] args) {
		TileType empty = TileTypeContainer.get("empty");
		TileType solid = TileTypeContainer.get("solid");
		Grid g = new Grid(GridMode.Grid4, empty, new Point(5, 5));
		Level l = new Level(g, new Point(3, 2));
		g.setTile(new Point(2, 2), solid.generateNew());
		g.setTile(new Point(3, 2), solid.generateNew());
		//LevelIO.save(l, null);
		
		MainFrame mf = new MainFrame();
		mf.setVisible(true);
		mf.getEditPanel().setLevel(l);
		mf.repaint();
	}
}
