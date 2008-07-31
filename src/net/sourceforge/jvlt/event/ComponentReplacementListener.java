package net.sourceforge.jvlt.event;

import java.util.EventListener;

import javax.swing.JComponent;

public interface ComponentReplacementListener extends EventListener {
	public static class ComponentReplacementEvent {
		private JComponent _old_component;
		private JComponent _new_component;
		
		public ComponentReplacementEvent(JComponent o, JComponent n) {
			_old_component = o;
			_new_component = n;
		}

		public JComponent getOldComponent() { return _old_component; }

		public JComponent getNewComponent() { return _new_component; }
	}
	
	public void componentReplaced(ComponentReplacementEvent e);
}
