package net.sourceforge.jvlt.event;

import net.sourceforge.jvlt.AbstractModel;

public interface ModelResetEventListener {
	public static class ModelResetEvent {
		public static final int RESET_ALL = 0;
		public static final int RESET_COUNTER = 1;
		
		private AbstractModel _source;
		private int _type;
		
		public ModelResetEvent (AbstractModel source, int type) {
			_source = source;
			_type = type;
		}

		public ModelResetEvent (AbstractModel source) { this(source, RESET_ALL); }
		
		public AbstractModel getSource() { return _source; }

		public int getType() { return _type; }
	}

	public void modelResetted(ModelResetEvent event);
}

