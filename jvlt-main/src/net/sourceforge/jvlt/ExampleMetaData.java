package net.sourceforge.jvlt;

import java.text.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ExampleMetaData extends MetaData {
	private static class TextFragmentsAttribute extends ArrayAttribute {
		private SimpleHTMLParser _parser = new SimpleHTMLParser();
		
		public TextFragmentsAttribute() {
			super("TextFragments", Example.TextFragment[].class); }
			
		public Element getXMLElement(Document doc, Object o) {
			Element elem = doc.createElement("TextFragments");
			Example.TextFragment[] fragments =
				(Example.TextFragment[]) getValue(o);
			for (int i=0; i<fragments.length; i++) {
				Example.TextFragment fragment = fragments[i];
				Element e = doc.createElement("Fragment");
				elem.appendChild(e);
				Element text_elem = doc.createElement("Text");
				e.appendChild(text_elem);
				if (fragment.getSense() == null) {
					Node[] nodes = null;
					try {
						_parser.parse(fragment.getText(), doc);
						nodes = _parser.getNodes();
					}
					catch (ParseException ex) {
						ex.printStackTrace();
						nodes = new Node[0];
					}
					for (int j=0; j<nodes.length; j++)
						text_elem.appendChild(nodes[j]);
				}
				else {
					text_elem.appendChild(
						doc.createTextNode(fragment.getText()));
					e.appendChild(XMLUtils.createTextElement(
						doc, "Link", fragment.getSense().getID()));
				}
			}
			return elem;
		}
	}
	
	private static class TranslationAttribute extends DefaultAttribute {
		private SimpleHTMLParser _parser = new SimpleHTMLParser();
		
		public TranslationAttribute() { super("Translation", String.class); }
			
		public Element getXMLElement(Document doc, Object o) {
			Element elem = doc.createElement("Translation");
			String val = (String) getValue(o);
			try {
				_parser.parse(val, doc);
				Node[] nodes = _parser.getNodes();
				for (int i=0; i<nodes.length; i++)
					elem.appendChild(nodes[i]);
			}
			catch (ParseException ex) { ex.printStackTrace(); }
			
			return elem;
		}
	}

	public ExampleMetaData() {
		super(Example.class);
		
		addAttribute(new TextFragmentsAttribute());
		addAttribute(new TranslationAttribute());
	}
}

