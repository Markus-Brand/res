package tiledleveleditor.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tiledleveleditor.editor.LevelIO;

/**
 *
 * @author Erik
 */
public interface LevelOverlay {
	public static LevelOverlay loadFrom(Element xmlNode) {
		if (xmlNode.getTagName().equals(TextOverlay.TAG)) {
			return new TextOverlay(
					LevelIO.getFloatPoint(xmlNode, "position"), 
					LevelIO.getText(xmlNode, "text"));
		}
		System.err.println("unknown overlay type: " + xmlNode.getTagName());
		return null;
	}
	public void saveTo(Document doc, Element overlaysTag);
	
	public static class TextOverlay implements LevelOverlay {
		public static final String TAG = "textOverlay";
		
		private float[] gridPosition;
		private String text;

		public TextOverlay(float[] gridPosition, String text) {
			this.gridPosition = gridPosition;
			this.text = text;
		}
		
		@Override
		public void saveTo(Document doc, Element overlaysTag) {
			Element myTag = doc.createElement(TAG);
			LevelIO.addFloatPoint(doc, myTag, "position", getGridPosition());
			LevelIO.addTextNode(doc, myTag, "text", getText());
			overlaysTag.appendChild(myTag);
		}

		public float[] getGridPosition() {
			return gridPosition;
		}

		public String getText() {
			return text;
		}
		
	}
}
