package net.sourceforge.jvlt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;

public class ErrorLog {
	public static final int NUM_LINES = 1024;
	
	private static ErrorLog _log;
	
	public static void init() {
		_log = new ErrorLog();
		System.setErr(new PrintStream(_log._pos));
	}
	
	public static ErrorLog getInstance() { return _log; }
	
	private LinkedList<String> _lines = new LinkedList<String>();
	
	private final PipedOutputStream _pos = new PipedOutputStream();
	
	public ErrorLog() {
		try {
			final PipedInputStream pis = new PipedInputStream(_pos);
			
			Thread read_thread = new Thread() {
				public void run() {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(pis));
					try {
						while (true) {
							String line = reader.readLine();
							synchronized (_lines) {
								_lines.add(line);
								if (_lines.size() > NUM_LINES)
									_lines.remove();
							}
						}
					}
					catch (IOException e) {}
				}
			};
			
			read_thread.start();
		} catch (IOException e) {}
	}
	
	public String getLines() {
		StringBuffer buffer = new StringBuffer();
		synchronized (_lines) {
			for (Iterator<String> it = _lines.iterator(); it.hasNext(); )
				buffer.append(it.next() + "\n");
		}
		
		return buffer.toString();
	}
}
