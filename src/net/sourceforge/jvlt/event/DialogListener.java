package net.sourceforge.jvlt.event;

import java.util.EventObject;

public interface DialogListener {
	public static class DialogEvent extends EventObject {
		private static final long serialVersionUID = 1L;

		private int _type;
		
		public DialogEvent (Object source, int type) {
			super(source);
			_type = type;
		}
		
		public int getType() { return _type; }
	}

	public void dialogStateChanged(DialogEvent ev);
}

