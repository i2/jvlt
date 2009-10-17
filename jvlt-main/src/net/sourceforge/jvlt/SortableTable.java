package net.sourceforge.jvlt;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.*;

public class SortableTable<T extends Object> extends JTable
		implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private class HeaderRenderer implements TableCellRenderer {
		private TableCellRenderer _renderer;
		
		public HeaderRenderer(TableCellRenderer renderer) {
			_renderer = renderer;
		}
		
		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean selected, boolean focus, int row, int col) {
			Component c = _renderer.getTableCellRendererComponent(
					table, value, selected, focus, row, col);
			if (c instanceof JLabel) {
				JLabel l = (JLabel) c;
				l.setHorizontalTextPosition(JLabel.LEFT);
				l.setIcon(getHeaderRendererIcon(col));
			}
			
			return c;
		}
	}

	protected SortableTableModel<T> _model;
	
	private JPopupMenu _menu;
	private Map<String, CustomFontCellRenderer> _cell_renderers;
	private JMenuItem _sort_descending_item;
	private JMenuItem _no_sorting_item;
	private JMenuItem _sort_ascending_item;
	private JMenuItem _select_cols_item;
	private MouseHandler _mouse_handler;
	private ImageIcon _up_arrow;
	private ImageIcon _down_arrow;
	private boolean _arrow_direction_reversed;

	public SortableTable(SortableTableModel<T> model) {
		super(model);
		_model = model;
		
		_cell_renderers = new HashMap<String, CustomFontCellRenderer>();
		
		_mouse_handler = new MouseHandler();
		getTableHeader().addMouseListener(_mouse_handler);
		
		_up_arrow = new ImageIcon(SortableTable.class.getResource(
				"/images/arrow_up.png"));
		_down_arrow = new ImageIcon(SortableTable.class.getResource(
				"/images/arrow_down.png"));
		
		_arrow_direction_reversed = false;
		
		int height = getFontMetrics(getFont()).getHeight();
		setRowHeight(height);
		getTableHeader().setReorderingAllowed(false);
		getTableHeader().setDefaultRenderer(new HeaderRenderer(
				getTableHeader().getDefaultRenderer()));
		
		init();
	}
	
	public List<T> getSelectedObjects() {
		int[] indices = getSelectedRows();
		ArrayList<T> array = new ArrayList<T>();
		for (int i=0; i<indices.length; i++)
			array.add(_model.getObjectAt(indices[i]));
		
		return array;
	}
	
	public void setSelectedObject(T obj) {
		int index = _model.getObjectIndex(obj);
		if (index >= 0) {
			setRowSelectionInterval (index, index);
			
			// Scroll to selected row.
			Rectangle rect = getCellRect(index, 0, true);
			scrollRectToVisible(rect);
		}
	}
	
	public void setCellRenderer(String column_name, CustomFontCellRenderer r) {
		_cell_renderers.put(column_name, r);
		
		// Adjust row height
		if (r.getCustomFont() != null) {
			int old_height = getRowHeight();
			int new_height = getFontMetrics(r.getCustomFont()).getHeight();
			if (new_height > old_height)
				setRowHeight(new_height);
		}
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		String column_name = _model.getColumnName(column);
		if (_cell_renderers.containsKey(column_name))
			return _cell_renderers.get(column_name);
		else
			return super.getCellRenderer(row, column);
	}
	
	@Override
	public String getToolTipText(MouseEvent event) {
		int row = rowAtPoint(event.getPoint());
		int col = columnAtPoint(event.getPoint());
		Object o = _model.getValueAt(row, col);
		
		if (o != null)
			return o.toString();
		else
			return super.getToolTipText(event);
	}
	
	public boolean isArrowDirectionReversed() {
		return _arrow_direction_reversed;
	}
	
	public void setArrowDirectionReversed(boolean reversed) {
		_arrow_direction_reversed = reversed;
	}
	
	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("sort_ascending")) {
			_model.setSortingDirective(new SortableTableModel.Directive(
				_mouse_handler.getLastClickedColumn(),
				SortableTableModel.ASCENDING));
		}
		else if (ev.getActionCommand().equals("sort_descending")) {
			_model.setSortingDirective(new SortableTableModel.Directive(
				_mouse_handler.getLastClickedColumn(),
				SortableTableModel.DESCENDING));
		}
		else if (ev.getActionCommand().equals("no_sorting")) {
			_model.setSortingDirective(new SortableTableModel.Directive(
				_mouse_handler.getLastClickedColumn(),
				SortableTableModel.NOT_SORTED));
		}
		else if (ev.getActionCommand().equals("select_columns")) {
			ColumnSelectionDialogData dd = new ColumnSelectionDialogData();
			dd.setSelectedStrings(_model.getColumnNames());
			dd.setAvailableStrings(_model.getMetaData().getAttributeNames());
	
			int result = CustomDialog.showDialog(dd, SortableTable.this,
				GUIUtils.getString("Labels", "select_columns"));
			if (result == CustomDialog.OK_OPTION) {
				_model.setColumnNames(dd.getSelectedStrings());
			}
		}
	}
	
	private void init() {
		CustomAction sort_ascending_action = GUIUtils.createTextAction(
			this, "sort_ascending");
		CustomAction sort_descending_action = GUIUtils.createTextAction(
			this, "sort_descending");
		CustomAction no_sorting_action = GUIUtils.createTextAction(
			this, "no_sorting");
		CustomAction select_cols_action = GUIUtils.createTextAction(
			this, "select_columns");

		_menu = new JPopupMenu();
		ButtonGroup group = new ButtonGroup();
		_no_sorting_item = new JRadioButtonMenuItem(no_sorting_action);
		group.add(_no_sorting_item); 
		_menu.add(_no_sorting_item);
		_sort_ascending_item = new JRadioButtonMenuItem(sort_ascending_action);
		group.add(_sort_ascending_item);
		_menu.add(_sort_ascending_item);
		_sort_descending_item=new JRadioButtonMenuItem(sort_descending_action);
		group.add(_sort_descending_item);
		_menu.add(_sort_descending_item);
		_menu.addSeparator();
		_select_cols_item = new JMenuItem(select_cols_action);
		_menu.add(_select_cols_item);
	}
	
	private ImageIcon getHeaderRendererIcon(int col) {
		SortableTableModel.Directive dir = _model.getSortingDirective();
		if (col != dir.getColumn())
			return null;
		
		if (dir.getDirection() == SortableTableModel.DESCENDING)
			return _arrow_direction_reversed ? _down_arrow : _up_arrow;
		else if (dir.getDirection() == SortableTableModel.ASCENDING)
			return _arrow_direction_reversed ? _up_arrow : _down_arrow;
		else
			return null;
	}
	
	private class MouseHandler extends MouseAdapter {
		private int _last_clicked_col;
		
		public MouseHandler() { _last_clicked_col = -1;	}
		
		public void mouseClicked(MouseEvent ev) {
			if (ev.getButton() != MouseEvent.BUTTON1)
				return;

			int col = getColumn(ev);
			if (col < 0)
				return;
			
			SortableTableModel.Directive directive =
				_model.getSortingDirective();
			SortableTableModel.Directive new_directive;
			if (directive.getColumn() != col)
				new_directive = new SortableTableModel.Directive(
					col, SortableTableModel.ASCENDING);
			else {
				if (directive.getDirection()==SortableTableModel.ASCENDING)
					new_directive = new SortableTableModel.Directive(
						col, SortableTableModel.DESCENDING);
				else
					new_directive = new SortableTableModel.Directive(
						col, SortableTableModel.ASCENDING);
			}
			_model.setSortingDirective(new_directive);
		}
		
		public void mousePressed(MouseEvent ev) { maybeShowPopup(ev); }
		
		public void mouseReleased(MouseEvent ev) { maybeShowPopup(ev); }
		
		public int getLastClickedColumn() { return _last_clicked_col; }
		
		private void maybeShowPopup(MouseEvent ev) {
			if (ev.isPopupTrigger()) {
				_last_clicked_col = getColumn(ev);
				SortableTableModel.Directive d = _model.getSortingDirective();
				if (d.getColumn() != _last_clicked_col)
					_no_sorting_item.setSelected(true);
				else if (d.getDirection() == SortableTableModel.ASCENDING)
					_sort_ascending_item.setSelected(true);
				else if (d.getDirection() == SortableTableModel.DESCENDING)
					_sort_descending_item.setSelected(true);
				else
					_no_sorting_item.setSelected(true);

				_menu.show(ev.getComponent(),ev.getX(),ev.getY());
			}
		}
		
		private int getColumn(MouseEvent ev) {
			Object src = ev.getSource();
			if (! (src instanceof JTableHeader))
				return -1;
			
			JTableHeader header = (JTableHeader) src;
			TableColumnModel column_model = header.getColumnModel();
			int view_col = column_model.getColumnIndexAtX(ev.getX());
			return column_model.getColumn(view_col).getModelIndex();
		}
	}
}

class ColumnSelectionDialogData extends CustomDialogData {
	private AttributeSelectionPanel _selection_panel;
	
	public ColumnSelectionDialogData() {
		_selection_panel = new AttributeSelectionPanel();
		_selection_panel.setAllowReordering(true);
		_content_pane = _selection_panel;
	}
	
	public String[] getSelectedStrings() {
		Object[] selected = _selection_panel.getSelectedObjects();
		return Utils.objectArrayToStringArray(selected);
	}
	
	public void setAvailableStrings(String[] strings) {
		_selection_panel.setAvailableObjects(strings);
	}
	
	public void setSelectedStrings(String[] strings) {
		_selection_panel.setSelectedObjects(strings);
	}
	
	public void updateData() throws InvalidDataException {}
}

