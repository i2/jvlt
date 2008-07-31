package net.sourceforge.jvlt.event;

public interface SelectionListener {
	public static class SelectionEvent {
		private Object _element;
		/** The component inside which '_element' was selected. */
		private Object _source;

		public SelectionEvent(Object element, Object source) {
			_element = element;
			_source = source;
		}
		
		public SelectionEvent(Object element) { this (element, null); }
		
		public Object getElement() { return _element; }
		
		public Object getSource() { return _source; }
	}

	public void objectSelected(SelectionEvent ev);
}

