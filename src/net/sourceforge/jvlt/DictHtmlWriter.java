package net.sourceforge.jvlt;

import java.io.*;

public class DictHtmlWriter extends DictWriter {
	public DictHtmlWriter(Dict dict, OutputStream stream) {
		super(dict, stream);
	}
	
	public void write() throws IOException {
		final PipedOutputStream pos = new PipedOutputStream();
		final PipedInputStream pis = new PipedInputStream(pos);
		final DictXMLWriter xmlwriter = new DictXMLWriter(_dict, pos,
				DictXMLWriter.FORMAT_XML);
		final XSLTransformer xsltransformer = new XSLTransformer(
				DictHtmlWriter.class.getResourceAsStream("/export_html.xsl"));

		Thread xmlthread = new Thread() {
			public void run() {
				try {
					xmlwriter.write();
					pos.close();
				}
				catch (IOException ex) { ex.printStackTrace(); }
			}
		};

		Thread xslthread = new Thread() {
			public void run() {
				try { xsltransformer.transform(pis, _stream); }
				catch (IOException ex) { ex.printStackTrace(); }
			}
		};

		xmlthread.start();
		xslthread.start();
	}
}
