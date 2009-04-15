package net.sourceforge.jvlt;

import java.io.IOException;
import java.io.OutputStream;

public abstract class DictWriter {
	protected Dict _dict;
	protected OutputStream _stream;
	
	public DictWriter(Dict dict, OutputStream stream) {
		_dict = dict;
		_stream = stream;
	}
	
	public abstract void write() throws IOException;
}

