package net.sourceforge.jvlt;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class ListeningInputComponent implements InputComponent {
	protected ArrayList<ChangeListener> _listeners;

	public ListeningInputComponent() {
		_listeners = new ArrayList<ChangeListener>();
	}
	
	public void addChangeListener(ChangeListener l) { _listeners.add(l); }
	
	public void removeChangeListener(ChangeListener l) { _listeners.remove(l); }

	protected void fireChangeEvent(ChangeEvent e) {
		for (Iterator<ChangeListener> it=_listeners.iterator(); it.hasNext(); )
			it.next().stateChanged(e);
	}

	protected void fireChangeEvent() { fireChangeEvent(new ChangeEvent(this)); }
}

