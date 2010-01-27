package net.sourceforge.jvlt.io;

import java.io.File;
import java.io.IOException;

import net.sourceforge.jvlt.JVLT;
import net.sourceforge.jvlt.core.Dict;

public abstract class DictReader {
	protected Dict _dict;
	protected String _version;

	public DictReader(String version) {
		_dict = null;
		_version = version;
	}

	public DictReader() {
		this(JVLT.getDataVersion());
	}

	public Dict getDict() {
		return _dict;
	}

	public abstract void read(File file) throws DictReaderException,
			IOException;
}
