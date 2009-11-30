package net.sourceforge.jvlt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Panel for setting custom fields (used in the advanced entry dialog)
 */
public class CustomFieldPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static class CustomFieldCellRenderer
			extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		
		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			if (value == null) {
				if (column == 0) {
					if (value == null)
						label.setText(
								GUIUtils.getString("Labels", "enter_name"));
				} else if (column == 1) {
					if (value == null)
						label.setText(
								GUIUtils.getString("Labels", "enter_value"));
				}
			}
			
			return label;
		}
	}
	
	private static class CustomFieldTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		
		private List<String> keys = new ArrayList<String>();
		private List<String> values = new ArrayList<String>();
		
		public int getColumnCount() { return 2; }

		public int getRowCount() { return keys.size() + 1; }

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < 0 || rowIndex >= keys.size())
				return null;
			
			if (columnIndex == 0)
				return keys.get(rowIndex);
			else if (columnIndex == 1)
				return values.get(rowIndex);
			else
				return null;
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0 || columnIndex == 1)
				return true;
			else
				return false;
		}
		
		@Override
		public Class<? extends Object> getColumnClass(int column) {
			return String.class;
		}
		
		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				if (rowIndex == keys.size()) {
					/* Add new key-value pair */
					keys.add((String) value);
					values.add(null);
					fireTableRowsUpdated(rowIndex, rowIndex);
					fireTableRowsInserted(rowIndex + 1, rowIndex + 1);
				} else {
					/* Replace a key */
					keys.set(rowIndex, (String) value);
					fireTableRowsUpdated(rowIndex, rowIndex);
				}
			} else if (columnIndex == 1) {
				if (rowIndex < values.size()) {
					/* Set/replace a value */
					values.set(rowIndex, (String) value);
					fireTableRowsUpdated(rowIndex, rowIndex);
				}
			}
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0)
				return GUIUtils.getString("Labels", "field_name");
			else
				return GUIUtils.getString("Labels", "field_value");
		}
		
		public void removeRow(int row) {
			if (row < 0 || row >= keys.size())
				return;
			
			keys.remove(row);
			values.remove(row);
			fireTableRowsDeleted(row, row);
		}
	}
	
	private CustomFieldTableModel table_model;
	private int popupRow = -1; // Row on which popup menu was opened
	
	private JTable table;
	private JComboBox keyBox;
	private JTextField valueField;
	private JLabel messageLabel;
	private DefaultCellEditor keyCellEditor;
	private DefaultCellEditor valueCellEditor;
	private JPopupMenu menu;
	
	public CustomFieldPanel() {
		keyBox = new JComboBox();
		keyBox.setEditable(true);
		
		valueField = new JTextField();
		
		table_model = new CustomFieldTableModel();
		table_model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				updateMessageLabel();
			}
		});
		
		ActionListener remove_listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table_model.removeRow(popupRow);
			}
		};
		menu = new JPopupMenu();
		menu.add(GUIUtils.createTextAction(remove_listener, "remove"));
		
		keyCellEditor = new DefaultCellEditor(keyBox);
		valueCellEditor = new DefaultCellEditor(valueField);
		
		table = new JTable(table_model);
		table.getColumnModel().getColumn(0).setCellEditor(keyCellEditor);
		table.getColumnModel().getColumn(1).setCellEditor(valueCellEditor);
		table.getColumnModel().getColumn(0).setCellRenderer(
				new CustomFieldCellRenderer());
		table.getColumnModel().getColumn(1).setCellRenderer(
				new CustomFieldCellRenderer());
		table.setRowHeight(table.getFontMetrics(getFont()).getHeight() + 5);
		table.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				table_model.removeRow(table.getSelectedRow());
			}
		});
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) { maybeShowPopup(e); }
			
			public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 200));
		
		messageLabel = new JLabel();
		
		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 1.0);
		add(scrollPane, cc);
		cc.update(0, 1, 1.0, 0.0);
		add(messageLabel, cc);
	}
	
	public void setChoices(Object[] choices) {
		Arrays.sort(choices);
		for (Object o: choices)
			keyBox.addItem(o.toString());
	}
	
	public Map<String, String> getValueMap() {
		Map<String, String> valueMap = new HashMap<String, String>();
		for (int i=0; i<table_model.keys.size(); i++) {
			if (table_model.values.get(i) != null)
				valueMap.put(table_model.keys.get(i),
						table_model.values.get(i));
		}
		
		return valueMap;
	}
	
	public void setValueMap(Map<String, String> valueMap) {
		if (valueMap == null)
			return;
		
		for (Map.Entry<String, String> e: valueMap.entrySet()) {
			table_model.keys.add(e.getKey());
			table_model.values.add(e.getValue());
		}
	}
	
	public void updateData() {
		// Save data
		keyCellEditor.stopCellEditing();
		valueCellEditor.stopCellEditing();
	}
	
	private void updateMessageLabel() {
		/* Check whether there are duplicates in the key list */
		String duplicate = null;
		for (int i=0; i<table_model.keys.size(); i++)
			for (int j=i+1; j<table_model.keys.size(); j++)
				if (table_model.keys.get(i).equals(table_model.keys.get(j))) {
					duplicate = table_model.keys.get(i);
					break;
				}
		
		if (duplicate != null)
			messageLabel.setText(MessageFormat.format(
					GUIUtils.getString("Labels", "duplicate_name"),
					duplicate));
		else
			messageLabel.setText("");
	}
	
	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popupRow = table.rowAtPoint(e.getPoint());
			if (table_model.getValueAt(popupRow, 0) != null)
				menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
