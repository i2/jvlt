package net.sourceforge.jvlt.event;

import java.util.Collection;
import java.util.EventObject;

public interface FilterListener<T> {
	public static class FilterEvent<T extends Object> extends EventObject {
		private static final long serialVersionUID = 1L;

		private Collection<T> _matched_items;
		
		public FilterEvent(Object src) { this(src, null); }
		
		public FilterEvent(Object src, Collection<T> matched_items) {
			super(src);
			_matched_items = matched_items;
		}
		
		public Collection<T> getMatchedItems() { return _matched_items; }
	}

	public void filterApplied(FilterEvent<T> ev);
}

