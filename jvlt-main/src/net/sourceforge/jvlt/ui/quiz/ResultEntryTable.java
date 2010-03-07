package net.sourceforge.jvlt.ui.quiz;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.jvlt.JVLT;
import net.sourceforge.jvlt.core.Entry;
import net.sourceforge.jvlt.core.Entry.Stats.UserFlag;
import net.sourceforge.jvlt.metadata.EntryMetaData.SensesAttribute;
import net.sourceforge.jvlt.ui.table.CustomFontCellRenderer;
import net.sourceforge.jvlt.ui.utils.GUIUtils;

public class ResultEntryTable extends JTable {
	private static class Model extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		private static SensesAttribute sensesAttribute = new SensesAttribute();

		private List<Entry> entries = new ArrayList<Entry>();
		private Map<Entry, Boolean> alwaysQuizFlagMap =
				new HashMap<Entry, Boolean>();

		@Override
		public Class<?> getColumnClass(int column) {
			if (column == 0 || column == 1)
				return String.class;
			else if (column == 2)
				return Boolean.class;
			else
				return null;
		}
		
		@Override
		public int getColumnCount() { return 3; }

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return GUIUtils.getString("Labels", "original");
			case 1:
				return GUIUtils.getString("Labels", "meanings");
			case 2:
				return GUIUtils.getString("Labels", "flag_always_quiz_short");
			default:
				return null;
			}
		}
		
		@Override
		public int getRowCount() {
			return entries.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (row < 0 || row >= entries.size())
				return null;
			
			Entry e = entries.get(row);
			switch (column) {
			case 0:
				return e.getOrthography();
			case 1:
				return sensesAttribute.getFormattedValue(e);
			case 2:
				if (alwaysQuizFlagMap.containsKey(e))
					return alwaysQuizFlagMap.get(e);
				else
					return (e.getUserFlags() & UserFlag.ALWAYS_QUIZ.getValue())
							!= 0;
			default:
				return null;
			}
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 2;
		}
		
		@Override
		public void setValueAt(Object object, int row, int column) {
			if (column == 2) {
				Entry e = entries.get(row);
				alwaysQuizFlagMap.put(e, (Boolean) object);
			}
		}
		
		public void addEntry(Entry entry) {
			entries.add(entry);
			fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
		}
		
		public void removeEntry(Entry entry) {
			int index = entries.indexOf(entry);
			if (index >= 0) {
				entries.remove(index);
				fireTableRowsDeleted(index, index);
			}
		}
		
		public void clear() {
			int entriesSize = entries.size();
			entries.clear();
			if (entriesSize > 0)
				fireTableRowsDeleted(0, entriesSize - 1);
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	private static final CustomFontCellRenderer originalRenderer;
	private static final CustomFontCellRenderer pronunciationRenderer;
	static {
		Font font;
		originalRenderer = new CustomFontCellRenderer();
		font = JVLT.getConfig().getFontProperty("ui_orth_font");
		if (font != null) {
			originalRenderer.setCustomFont(font);
		}
		pronunciationRenderer = new CustomFontCellRenderer();
		font = JVLT.getConfig().getFontProperty("ui_pron_font");
		if (font != null) {
			pronunciationRenderer.setCustomFont(font);
		}
	}
	
	private Model model;
	
	public ResultEntryTable() {
		model = new Model();
		setModel(model);
		setFillsViewportHeight(true);
	}
	
	public void addEntry(Entry e) { model.addEntry(e); }
	
	public void setEntries(List<Entry> entries) {
		model.clear();
		for (Entry e: entries)
			addEntry(e);
	}
	
	public void removeEntry(Entry e) { model.removeEntry(e); }
	
	public List<Entry> getEntries() { return model.entries; }
	
	public Entry getSelectedEntry() {
		int selected = getSelectedRow();
		if (selected != -1)
			return model.entries.get(selected);
		else
			return null;
	}
	
	public Map<Entry, Boolean> getAlwaysQuizFlagMap() {
		return model.alwaysQuizFlagMap;
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 0)
			return originalRenderer;
		else if (column == 1)
			return pronunciationRenderer;
		else
			return super.getCellRenderer(row, column);
	}
}
