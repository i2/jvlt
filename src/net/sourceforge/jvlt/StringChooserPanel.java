package net.sourceforge.jvlt;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

public class StringChooserPanel extends JPanel {
	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if (ev.getActionCommand().equals("up"))
				swap(_table.getSelectedRow(), _table.getSelectedRow()-1);
			else if (ev.getActionCommand().equals("down"))
				swap(_table.getSelectedRow(), _table.getSelectedRow()+1);
		}
	}
	
	private class ListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent ev) {
			if (! ev.getValueIsAdjusting())
				update();
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	private Action _up_action;
	private Action _down_action;
	private StringChooserModel _table_model;
	private JTable _table;
	
	public StringChooserPanel() {
		_table_model = new StringChooserModel();
		_table = new JTable(_table_model);
		int height = _table.getFontMetrics(_table.getFont()).getHeight();
		_table.setRowHeight(height);
		_table.setShowGrid(false);
		_table.getColumnModel().getColumn(0).setMaxWidth(0);
		_table.getColumnModel().getColumn(0).setResizable(false);
		_table.setTableHeader(null);
		_table.getSelectionModel().addListSelectionListener(
			new ListSelectionHandler());
		JScrollPane sp = new JScrollPane(_table);
		sp.setPreferredSize(new Dimension(100,100));
		
		ActionHandler handler = new ActionHandler();
		_up_action = GUIUtils.createIconAction(handler, "up");
		_down_action = GUIUtils.createIconAction(handler, "down");
		ButtonPanel button_panel = new ButtonPanel(
			SwingConstants.VERTICAL, SwingConstants.TOP);
		button_panel.addButton(new JButton(_up_action));
		button_panel.addButton(new JButton(_down_action));
		
		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0,0,1.0,1.0);
		add(sp, cc);
		cc.update(1,0,0.0,1.0);
		add(button_panel, cc);
		
		update();
	}
	
	public void setStrings(String[] strings) {
		_table_model.setStrings(strings); }
		
	public boolean isStringSelected(String str) {
		return _table_model.isStringSelected(str); }
	
	public String[] getSelectedStrings() {
		return _table_model.getSelectedStrings(); }
		
	public void setSelectedStrings(String[] strings) {
		_table_model.setSelectedStrings(strings); }
		
	public void setEnabled(boolean enable) {
		super.setEnabled(enable);
		_table.setEnabled(enable);
		if (enable)
			update();
		else {
			_up_action.setEnabled(false);
			_down_action.setEnabled(false);
		}
	}
		
	private void update() {
		int selected_row = _table.getSelectedRow();
		int num_selected_strings = _table_model.getSelectedStrings().length;
		_up_action.setEnabled(
			selected_row>0 && selected_row<num_selected_strings);
		_down_action.setEnabled(selected_row<num_selected_strings-1);
	}

	private void swap(int i1, int i2) {
		String[] selected = _table_model.getSelectedStrings();
		String tmp = selected[i1];
		selected[i1] = selected[i2];
		selected[i2] = tmp;
		_table_model.setSelectedStrings(selected);
		_table.getSelectionModel().setSelectionInterval(i2,i2);
	}
}

class StringChooserModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private int _first_deselected_index = 0;
	private LinkedList<Object> _string_list = new LinkedList<Object>();
	
	public int getRowCount() { return _string_list.size(); }
	
	public int getColumnCount() { return 2; }
	
	public Class<? extends Object> getColumnClass(int column) {
		if (column == 0)
			return Boolean.class;
		else if (column == 1)
			return String.class;
		else
			return null;
	}
	
	public Object getValueAt(int row, int column) {
		if (column == 0)
			return new Boolean(row < _first_deselected_index);
		else if (column == 1)
			return _string_list.get(row);
		else
			return null;
	}
	
	public boolean isCellEditable(int row, int column) {
		return column == 0; }
	
	/**
	 * Set a value at a specific position in the table.  Only the values in the
	 * first column can be modified. To set the values in the second column,
	 * use the {@link #setStrings(String[]) setStrings} method. By calling the
	 * {@link #setValueAt(Object,int,int) setValutAt} method, the order of the
	 * list entries changes: Entries that are selected move to the top while
	 * entries that are deselected move down.*/
	public void setValueAt(Object value, int row, int column) {
		if (column != 0 || row < 0 || row >= _string_list.size())
			return;
		
		boolean selected = ((Boolean) value).booleanValue();
		Object obj = _string_list.remove(row);
		if (selected) {
			_string_list.add(_first_deselected_index, obj);
			fireTableRowsUpdated(_first_deselected_index, row);
			_first_deselected_index++;
		}
		else {
			_string_list.add(_first_deselected_index-1, obj);
			fireTableRowsUpdated(row, _first_deselected_index);
			_first_deselected_index--;
		}
	}

	public String[] getStrings() { return _string_list.toArray(new String[0]); }

	/**
	 * Set the list of available strings. */
	public void setStrings(String[] strings) {
		String[] selected = getSelectedStrings();
		int old_size = _string_list.size();
		_string_list.clear();
		_string_list.addAll(Arrays.asList(strings));
		setSelectedStrings(selected);
		int new_size = _string_list.size();
		fireTableRowsUpdated(0, new_size);
		if (old_size > new_size)
			fireTableRowsDeleted(new_size, old_size-1);
		else if (old_size < new_size)
			fireTableRowsInserted(old_size, new_size-1);
	}
	
	public String[] getSelectedStrings() {
		String[] strings = new String[_first_deselected_index];
		Iterator<Object> it = _string_list.iterator();
		for (int i=0; i<_first_deselected_index && it.hasNext(); i++)
			strings[i] = (String) it.next();
		
		return strings;
	}
	
	public boolean isStringSelected(String str) {
		Iterator<Object> it = _string_list.iterator();
		for (int i=0; i<_first_deselected_index && it.hasNext(); i++)
			if (it.next().equals(str))
				return true;
			
		return false;
	}
	
	/**
	 * Mark a set of strings as selected.
	 * All other strings in the list are deselected. The selected strings
	 * are moved to the top of the list. */
	public void setSelectedStrings(String[] strings) {
		int num_selected_strings = 0;
		for (int i=strings.length-1; i>=0; i--) {
			int index = _string_list.indexOf(strings[i]);
			if (index >= 0) {
				_string_list.remove(index);
				_string_list.addFirst(strings[i]);
				num_selected_strings++;
			}
		}
		_first_deselected_index = num_selected_strings;
		fireTableRowsUpdated(0, getRowCount()-1);
	}
}
