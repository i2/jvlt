package net.sourceforge.jvlt;

import java.text.Collator;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;

class SortableTableModel<T extends Object> implements TableModel {
	public static final int DESCENDING = -1;
	public static final int NOT_SORTED = 0;
	public static final int ASCENDING = 1;
	
	private List<Row> _view_to_model;
	private int[] _model_to_view;
	private AttributeResources _resources;
	private ArrayList<TableModelListener> _listeners;
	private ArrayList<String> _columns;
	private ArrayList<T> _values;
	private Directive _directive;
	private MetaData _data;
	private Map<Class<? extends Attribute>, Boolean> _format_value;
	
	public SortableTableModel(MetaData data) {
		_resources = new AttributeResources();
		_listeners = new ArrayList<TableModelListener>();
		_columns = new ArrayList<String>();
		_values = new ArrayList<T>();
		_directive = new Directive(-1, NOT_SORTED);
		_data = data;
		_view_to_model = null;
		_model_to_view = null;
		_format_value = new HashMap<Class<? extends Attribute>, Boolean>();
	}
	
	/** Implementation of TableModel.addTableModelListener(). */
	public void addTableModelListener(TableModelListener l) {
		_listeners.add(l);
	}
	
	/** Implementation of TableModel.getColumnClass(). */
	public Class<? extends Object> getColumnClass(int col) {
		Attribute attr = _data.getAttribute(_columns.get(col).toString());
		if (_format_value.containsKey(attr.getClass()))
			return String.class;
		else
			return _data.getAttribute(_columns.get(col).toString()).getType();
	}
	
	/** Implementation of TableModel.getColumnCount(). */
	public int getColumnCount() { return _columns.size(); }
	
	/** Implements TableModel.getColumnName(). */
	public String getColumnName(int col) {
		String name = _columns.get(col).toString();
		return _resources.getString(name);
	}
	
	/** Implementation of TableModel.getRowCount(). */
	public int getRowCount() { return _values.size(); }
	
	/** Implementation of TableModel.getValueAt(). */
	public Object getValueAt(int row, int col) {
		return getValue(getModelIndex(row), col);
	}
	
	/** Implementation of TableModel.isCellEditable(). */
	public boolean isCellEditable(int row, int col) { return false; }
	
	/** Implementation of TableModel.removeTableModelListener(). */
	public void removeTableModelListener(TableModelListener l) {
		_listeners.remove(l);
	}
	
	/** Implementation of TableModel.setValueAt() (does nothing). */
	public void setValueAt(Object val, int row, int col) {}

	public Directive getSortingDirective() { return _directive; }
	
	/**
	 * Sets the sorting directive.
	 * If the column of the directive is invalid, an empty directive
	 * (no sorting) is chosen. */
	public void setSortingDirective(Directive directive) {
		if (directive.getColumn() >= getColumnCount() ||
			directive.getColumn() < 0)
			_directive = new Directive();
		else
			_directive = directive;
		
		clearSortingState();
		fireTableModelEvent(new TableModelEvent(this));
		fireTableModelEvent(
				new TableModelEvent(this, TableModelEvent.HEADER_ROW));
	}
	
	/** Set column names (not translated version). */
	public void setColumnNames(String[] names) {
		_columns.clear();
		for (int i=0; i<names.length; i++)
			if (_data.getAttribute(names[i]) != null)
				_columns.add(names[i]);
			
		// Number of columns may change, so the current sorting directive
		// is possibly invalid. Correct this by calling setSortingDirective().
		setSortingDirective(_directive);
		fireTableModelEvent(new TableModelEvent(
			this, TableModelEvent.HEADER_ROW));
	}
	
	/** Returns the name of a column (either translated or not translated) */
	public String getColumnName(int column, boolean translate) {
		if (translate)
			return getColumnName(column);
		else
			return _columns.get(column).toString();
	}
	
	/** Return the column names (not translated version). */
	public String[] getColumnNames() {
		return _columns.toArray(new String[0]);
	}
	
	public MetaData getMetaData() { return _data; }
	
	public boolean containsObject(Object obj) {
		return _values.contains(obj);
	}
	
	public Collection<T> getObjects() { return _values; }
	
	public T getObjectAt(int row) {
		return _values.get(getModelIndex(row));
	}
	
	public int getObjectIndex (T obj) {
		int index = 0;
		Iterator<T> it = _values.iterator();
		while (it.hasNext()) {
			T o = it.next();
			if (o.equals(obj))
				return getViewIndex(index);
			else
				index++;
		}
		
		return -1;
	}
	
	public void addObject(T obj) {
		_values.add(obj);
		clearSortingState();
		fireTableModelEvent(new TableModelEvent(this));
		//int row = _values.size()-1;
		//fireTableModelEvent(new TableModelEvent(this, row, row,
		//	TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	public void addObjects(Collection<T> objs) {
		_values.addAll(objs);
		if (objs.size() > 0) {
			clearSortingState();
			fireTableModelEvent(new TableModelEvent(this));
		}
	}
	
	public void clear() {
		_values.clear();
		clearSortingState();
		fireTableModelEvent(new TableModelEvent(this)); 
	}
	
	public void setObjects(Collection<T> objects) {
		clear();
		addObjects(objects);
	}

	public void removeObject(T obj) {
		int row = getObjectIndex(obj);
		if (row < 0)
			return;

		_values.remove(obj);
		clearSortingState();
		fireTableModelEvent(new TableModelEvent(this));
		//fireTableModelEvent(new TableModelEvent(this, row, row,
		//	TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE)); 
	}

	public void updateObjects(T[] objects) {
		boolean exists_visible_object = false;
		for (int i=0; i<objects.length; i++)
			if (getObjectIndex(objects[i]) >= 0) {
				exists_visible_object = true;
				break;
			}
		
		if (! exists_visible_object)
			return;
		
		clearSortingState();
		fireTableModelEvent(new TableModelEvent(this));
		//fireTableModelEvent(new TableModelEvent(this, row, row,
		//	TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE)); 
	}
	
	public void setFormatValue(Class<? extends Attribute> cl, boolean format) {
		_format_value.put(cl, format);
	}
	
	private void clearSortingState() {
		_view_to_model = null;
		_model_to_view = null;
	}
	
	private Object getValue(int row, int col) {
		Object obj = _values.get(row);
		Attribute attr = _data.getAttribute(_columns.get(col).toString());
		if (_format_value.containsKey(attr.getClass()))
			return attr.getFormattedValue(obj);
		
		if (Number.class.isAssignableFrom(attr.getType())
				|| Boolean.class.isAssignableFrom(attr.getType()))
			/*
			 * For numbers and booleans use the native type, so the appropriate
			 * cell renderers can be used
			 */
			return attr.getValue(obj);
		
		return attr.getFormattedValue(obj);
	}

	private int getModelIndex(int row) {
		return getViewToModel().get(row).getIndex();
	}
	
	private List<Row> getViewToModel() {
        if (_view_to_model == null) {
			int row_count = getRowCount();
			_view_to_model = new ArrayList<Row>();
			for (int row=0; row<row_count; row++)
				_view_to_model.add(new Row(row));
			
			if (_directive.getDirection() != NOT_SORTED)
				Collections.sort(_view_to_model);
        }
		
        return _view_to_model;
	}
	
	private int getViewIndex(int row) { return getModelToView()[row]; }
	
	private int[] getModelToView() {
        if (_model_to_view == null) {
            int n = getViewToModel().size();
            _model_to_view = new int[n];
            for (int i=0; i<n; i++)
                _model_to_view[getModelIndex(i)]=i;
		}
		
		return _model_to_view;
	}
	
	private void fireTableModelEvent(TableModelEvent ev) {
		Iterator<TableModelListener> it = _listeners.iterator();
		while (it.hasNext())
			it.next().tableChanged(ev);
	}

	public static class Directive {
		private int _column;
		private int _direction;
		
		public Directive() { this(-1, NOT_SORTED); }
		
		public Directive(int column, int direction)	{
			_column = column;
			_direction = direction;
		}
		
		public int getColumn() { return _column; }
		
		public int getDirection() { return _direction; }
		
		public void setColumn(int col) { _column = col; }
		
		public void setDirection(int dir) { _direction = dir; }
	}

	private class Row implements Comparable<Row> {
		private int _index;
		private Collator _collator;
		
		public Row(int index) {
			_index = index;
			
			_collator = CustomCollator.getInstance();
		}
		
		public int getIndex() { return _index; }
		
		public int compareTo(Row r) {
			int row1 = _index;
			int row2 = r._index;
			int col = _directive.getColumn();
			if (col < 0)
				return 0;

			int direction = _directive.getDirection();
			Object val1 = getValue(row1, col);
			Object val2 = getValue(row2, col);
			
			int comparison = 0;
			if (val1 == null && val2 == null)
				comparison = 0;
			else if (val1 == null)
				comparison = -1;
			else if (val2 == null)
				comparison = 1;
			else {
				if (val1 instanceof String)
					comparison = _collator.compare(val1, val2);
				else if (val1 instanceof Boolean)
					comparison = ((Boolean) val1).compareTo((Boolean) val2);
				else if (val1 instanceof Number)
					comparison = ((Number) val1).intValue()
									- ((Number) val2).intValue();
			}
			
			if (comparison != 0)
				return direction == DESCENDING ? -comparison : comparison;
			else
				return 0;
		}
	}
}

