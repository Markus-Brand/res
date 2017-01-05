package tiledleveleditor.editor;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tiledleveleditor.core.Grid;
import tiledleveleditor.core.Level;
import tiledleveleditor.core.LevelOverlay;
import tiledleveleditor.core.Tile;
import tiledleveleditor.core.TileTypeContainer;

/**
 * static methods to load / save levels
 */
public class LevelIO {

	public static Level load(File f) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);
			doc.getDocumentElement().normalize();
			Element map = doc.getDocumentElement();
			Element meta = (Element) map.getElementsByTagName("meta").item(0);

			Grid levelGrid = new Grid(TileTypeContainer.get("empty"));

			Element grid = (Element) map.getElementsByTagName("grid").item(0);

			NodeList tilesList = grid.getElementsByTagName("tiles");
			for (int _y = 0; _y < tilesList.getLength(); _y++) {
				Element tiles = (Element) tilesList.item(_y);
				int y = Integer.valueOf(tiles.getAttribute("id"));

				NodeList tileList = tiles.getElementsByTagName("tile");
				for (int _x = 0; _x < tileList.getLength(); _x++) {
					Element tile = (Element) tileList.item(_x);
					int x = Integer.valueOf(tile.getAttribute("id"));

					String serial = tile.getTextContent();

					Tile t = Tile.deserialize(serial);
					levelGrid.setTile(new Point(x, y), t);
				}
			}

			Point startPos = getPoint(meta, "startPosition");
			Level level = new Level(levelGrid, startPos);
			level.setTitle(getText(meta, "title"));
			level.setDescription(getText(meta, "description"));
			level.setEscapeHandler(getText(meta, "escapeAction"));

			Element objects = (Element) map.getElementsByTagName("objects").item(0);
			if (objects != null) {
				NodeList overlays = objects.getChildNodes();
				for (int o = 0; o < overlays.getLength(); o++) {
					if (overlays.item(o) instanceof Element) {
						level.getOverlays().add(LevelOverlay.loadFrom((Element) overlays.item(o)));
					}
				}
			}

			return level;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static boolean save(Level l, File f) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element rootElement = doc.createElement("map");
			doc.appendChild(rootElement);

			//meta inf
			Element meta = doc.createElement("meta");
			rootElement.appendChild(meta);
			addTextNode(doc, meta, "title", l.getTitle());
			addTextNode(doc, meta, "description", l.getDescription());
			addTextNode(doc, meta, "escapeAction", l.getEscapeHandler());
			addPoint(doc, meta, "startPosition", l.getStartPosition());
			addPoint(doc, meta, "size", l.getGrid().getDimensions());

			Element objects = doc.createElement("objects");
			rootElement.appendChild(objects);
			for (LevelOverlay o : l.getOverlays()) {
				o.saveTo(doc, objects);
			}

			Element grid = doc.createElement("grid");
			rootElement.appendChild(grid);

			Point dim = l.getGrid().getDimensions();
			for (int y = 0; y < dim.y; y++) {
				Element row = doc.createElement("tiles");
				row.setAttribute("id", y + "");
				grid.appendChild(row);
				for (int x = 0; x < dim.x; x++) {
					Element tileElem = doc.createElement("tile");
					tileElem.setAttribute("id", x + "");
					row.appendChild(tileElem);

					Tile t = l.getGrid().getTile(new Point(x, y), true);
					String serial = t.serialize();
					tileElem.appendChild(doc.createTextNode(serial));
				}
			}

			OutputFormat format = new OutputFormat(doc);
			format.setIndent(2);

			//TransformerFactory transformerFactory = TransformerFactory.newInstance();
			//Transformer transformer = transformerFactory.newTransformer();
			//DOMSource source = new DOMSource(doc);
			//StreamResult res = new StreamResult(f);
			//transformer.transform(source, res);
			XMLSerializer ser = new XMLSerializer(new FileOutputStream(f), format);
			ser.serialize(doc);

			return true;
		} catch (Exception ex) {
			System.err.println("ERROR!");
			ex.printStackTrace();
			return false;
		}
	}

	public static final void addTextNode(Document doc, Node node, String name, String value) {
		Element textNode = doc.createElement(name);
		textNode.appendChild(doc.createTextNode(value));
		node.appendChild(textNode);
	}

	private static final void addBoolean(Document doc, Node node, String name, boolean value) {
		Element boolNode = doc.createElement(name);
		boolNode.appendChild(doc.createTextNode(value ? "true" : "false"));
		node.appendChild(boolNode);
	}

	public static final void addFloatPoint(Document doc, Node node, String pointName, float[] p) {
		Element pE = doc.createElement(pointName);
		addTextNode(doc, pE, "x", p[0] + "");
		addTextNode(doc, pE, "y", p[1] + "");
		node.appendChild(pE);
	}

	public static final void addPoint(Document doc, Node node, String pointName, Point p) {
		Element pE = doc.createElement(pointName);
		addTextNode(doc, pE, "x", p.x + "");
		addTextNode(doc, pE, "y", p.y + "");
		node.appendChild(pE);
	}

	public static final String getText(Element node, String textName) {
		try {
			return node.getElementsByTagName(textName).item(0).getTextContent();
		} catch (Exception exception) {
			return "";
		}
	}

	private static final Point getPoint(Element node, String pointName) {
		Element pnode = (Element) node.getElementsByTagName(pointName).item(0);
		int x = Integer.valueOf(getText(pnode, "x"));
		int y = Integer.valueOf(getText(pnode, "y"));
		return new Point(x, y);
	}

	public static final float[] getFloatPoint(Element node, String pointName) {
		Element pnode = (Element) node.getElementsByTagName(pointName).item(0);
		float x = Float.valueOf(getText(pnode, "x"));
		float y = Float.valueOf(getText(pnode, "y"));
		return new float[]{x, y};
	}

	private static final boolean getBoolean(Element node, String boolName) {
		Element bNode = (Element) node.getElementsByTagName(boolName).item(0);
		if (bNode == null) {
			return false;
		}
		return Boolean.valueOf(bNode.getTextContent());
	}
}
