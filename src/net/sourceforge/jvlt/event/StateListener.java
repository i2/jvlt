package net.sourceforge.jvlt.event;

import java.util.EventObject;

public interface StateListener {
	public static class StateEvent extends EventObject {
		private static final long serialVersionUID = 1L;

		private int _state;
		
		public StateEvent(Object src, int state) {
			super(src);
			_state = state;
		}
		
		public int getState() { return _state; }
	}

	public void stateChanged(StateEvent ev);
}

