package net.sourceforge.jvlt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ErrorLog {
	public static final int NUM_LINES = 1024;
	
	private static Date _date = new Date();
	private static ErrorLog _log = new ErrorLog();
	
	public static void init() {
		System.setErr(new PrintStream(_log._pos));
	}
	
	public static ErrorLog getInstance() { return _log; }
	
	private LinkedList<String> _lines = new LinkedList<String>();
	
	private List<ChangeListener> _listeners = new ArrayList<ChangeListener>();
	
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
							line = line.replaceAll("\t", "    ");
							_date.setTime(System.currentTimeMillis());
							String date_string =
								DateFormat.getTimeInstance().format(_date);
							System.out.println(date_string + " " + line);
							String line_with_date = date_string + " " + line + "\n";
							synchronized (_lines) {
								_lines.add(line_with_date);
								if (_lines.size() > NUM_LINES)
									_lines.remove();
							}
							
							// Notify listeners
							synchronized(_listeners) {
								for(ChangeListener l: _listeners)
									l.stateChanged(new ChangeEvent(ErrorLog.this));
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
				buffer.append(it.next());
		}
		
		return buffer.toString();
	}
	
	public void addChangeListener(ChangeListener listener) {
		synchronized(_listeners) {
			_listeners.add(listener);
		}
		
		listener.stateChanged(new ChangeEvent(this));
	}
}
