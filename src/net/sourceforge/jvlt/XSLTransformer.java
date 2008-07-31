package net.sourceforge.jvlt;

import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

public class XSLTransformer {
	private Transformer _transformer = null;
	
	public XSLTransformer(InputStream stream) {
		try {
			StreamSource stylesrc = new StreamSource(stream);
			TransformerFactory factory = TransformerFactory.newInstance();
			_transformer = factory.newTransformer(stylesrc);
			_transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		}
		catch (TransformerConfigurationException ex) { ex.printStackTrace(); }
	}
	
	public String transform(InputStream stream) {
		return transform(new StreamSource(stream)); }
	
	public String transform(Document doc) {
		return transform(new DOMSource(doc)); }
	
	private String transform(Source src) {
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			_transformer.transform(src, result);
			return writer.toString();
		}
		catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}

