package net.sourceforge.jvlt.metadata;

import java.text.ParseException;

import net.sourceforge.jvlt.core.Example;
import net.sourceforge.jvlt.core.Example.TextFragment;
import net.sourceforge.jvlt.utils.SimpleHTMLParser;
import net.sourceforge.jvlt.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ExampleMetaData extends MetaData {
	private static class TextFragmentsAttribute extends ArrayAttribute {
		private final SimpleHTMLParser _parser = new SimpleHTMLParser();

		public TextFragmentsAttribute() {
			super("TextFragments", Example.TextFragment[].class);
		}

		@Override
		public Element getXMLElement(Document doc, Object o) {
			Element elem = doc.createElement("TextFragments");
			Example.TextFragment[] fragments = (Example.TextFragment[]) getValue(o);
			for (TextFragment fragment : fragments) {
				Element e = doc.createElement("Fragment");
				elem.appendChild(e);
				Element text_elem = doc.createElement("Text");
				e.appendChild(text_elem);
				if (fragment.getSense() == null) {
					Node[] nodes = null;
					try {
						_parser.parse(fragment.getText(), doc);
						nodes = _parser.getNodes();
					} catch (ParseException ex) {
						ex.printStackTrace();
						nodes = new Node[0];
					}
					for (Node node : nodes) {
						text_elem.appendChild(node);
					}
				} else {
					text_elem.appendChild(doc
							.createTextNode(fragment.getText()));
					e.appendChild(XMLUtils.createTextElement(doc, "Link",
							fragment.getSense().getID()));
				}
			}
			return elem;
		}
	}

	private static class TranslationAttribute extends DefaultAttribute {
		private final SimpleHTMLParser _parser = new SimpleHTMLParser();

		public TranslationAttribute() {
			super("Translation", String.class);
		}

		@Override
		public Element getXMLElement(Document doc, Object o) {
			Element elem = doc.createElement("Translation");
			String val = (String) getValue(o);
			try {
				_parser.parse(val, doc);
				Node[] nodes = _parser.getNodes();
				for (Node node : nodes) {
					elem.appendChild(node);
				}
			} catch (ParseException ex) {
				ex.printStackTrace();
			}

			return elem;
		}
	}

	public ExampleMetaData() {
		super(Example.class);

		addAttribute(new TextFragmentsAttribute());
		addAttribute(new TranslationAttribute());
	}
}
