package net.sourceforge.jvlt.event;

import net.sourceforge.jvlt.UndoableAction;

public interface UndoableActionListener {
	public static class UndoableActionEvent {
		public static int UNDO_TYPE=0; // Action is undone.
		public static int REDO_TYPE=1; // Action is redone.
		public static int EXEC_TYPE=2; // Action is executed for the first time.
		
		private UndoableAction _action;
		private int _type; 
		
		public UndoableActionEvent (UndoableAction action, int type) {
			_action = action;
			_type = type;
		}
		
		public UndoableAction getAction () { return _action; }
		
		public int getType() { return _type; }
	}

	public void actionPerformed (UndoableActionEvent event);
}

