package net.sourceforge.jvlt.multimedia;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class CustomMultimediaFile extends MultimediaFile {
	private static final Logger logger = Logger.getLogger(MultimediaFile.class);

	private String _command;

	public CustomMultimediaFile(String file_name, int type) {
		super(file_name);
		_type = type;
	}

	public CustomMultimediaFile(String file_name) {
		super(file_name);
	}

	public CustomMultimediaFile(int type) {
		this("", type);
	}

	public void setType(int type) {
		_type = type;
	}

	public String getCommand() {
		return _command;
	}

	public void setCommand(String command) {
		_command = command;
	}

	public void play() throws IOException {
		logger.trace("Trying to play file '" + _file_name + "' with command '"
				+ _command + "'...");

		if (_command == null || _command.equals("")) {
			return;
		}

		File f = getFile();
		if (!f.exists() || !f.isFile()) {
			String msg = "File " + f.getAbsolutePath() + " does not exist"
					+ " or cannot be opened.";
			throw new IOException(msg);
		}

		String[] cmdArray = getCommandArray(f.getAbsolutePath());
		
		Runtime rt = Runtime.getRuntime();
		rt.exec(cmdArray);
	}
	
	protected String[] getCommandArray(String path) {
		// Split space-separated string but ignore spaces in quotes
		String[] cmdArray = _command.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		for (int i = 0; i < cmdArray.length; i++) {
			if (cmdArray[i].equals("%f")) {
				cmdArray[i] = path;
			}
		}
		
		return cmdArray;
	}
}
