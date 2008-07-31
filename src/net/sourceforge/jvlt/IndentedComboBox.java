package net.sourceforge.jvlt;

import java.awt.Component;
import java.util.TreeMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class IndentedComboBox extends LabeledComboBox {
	private class IndentedComboBoxModel extends DefaultComboBoxModel {
		private static final long serialVersionUID = 1L;

		private TreeMap<Object, Integer> _indentation_levels;
		private ItemContainer _container = new ItemContainer();
		
		public IndentedComboBoxModel() {
			_indentation_levels = new TreeMap<Object, Integer>();
		}
		
		public void addElement(Object item) { addElement(item, 0); }
		
		public void addElement(Object item, int indentation_level) {
			insertElementAt(item, indentation_level, getSize());
		}
		
		public Object getElementAt(int index) {
			return _container.getItem((String) super.getElementAt(index));
		}
			
		public int getIndexOf(Object obj) {
			return super.getIndexOf(_container.getTranslation(obj));
		}
			
		public Object getSelectedItem() {
			Object o = super.getSelectedItem();
			return o==null ? null : _container.getItem((String) o);
		}
		
		public void insertElementAt(Object obj, int index) {
			insertElementAt(obj, 0, index);
		}
		
		public void insertElementAt(
			Object item, int indentation_level, int index) {
			_container.addItem(item);
			_indentation_levels.put(item, new Integer(indentation_level));
			super.insertElementAt(_container.getTranslation(item), index);
		}
		
		public void removeElement(Object obj) {
			super.removeElement(_container.getTranslation(obj));
		}
			
		public void setSelectedItem(Object obj) {
			super.setSelectedItem(_container.getTranslation(obj));
		}
		
		public void setTranslateItems(boolean translate) {
			_container.setTranslateItems(translate);
		}
			
		public int getIndentationLevel(Object obj) {
			Integer level = obj==null ? null :
				(Integer) _indentation_levels.get(obj);
			return level==null ? 0 : level.intValue();
		}
			
		public String getTranslation(Object obj) {
			return _container.getTranslation(obj);
		}
	}
	
	private class IndentedComboBoxRenderer extends BasicComboBoxRenderer {
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean is_selected, boolean cell_has_focus) {
			IndentedComboBoxModel model = IndentedComboBox.this._model;
			int indent = model.getIndentationLevel(value);
			StringBuffer buf = new StringBuffer();
			for (int i=0; i<indent; i++)
				buf.append("  ");
			
			JLabel lbl = (JLabel) super.getListCellRendererComponent(
				list, value, index,	is_selected, cell_has_focus);
			lbl.setText(buf.toString()+model.getTranslation(value));
			
			return lbl;
		}
	}
	
	private static final long serialVersionUID = 1L;

	private IndentedComboBoxModel _model = new IndentedComboBoxModel();
	private IndentedComboBoxRenderer _renderer = new IndentedComboBoxRenderer();

	public IndentedComboBox() {
		setModel(_model);
		setRenderer(_renderer);
	}
	
	public void addItem(Object item) { this.addItem(item, 0); }
	
	public void addItem(Object item, int indentation_level) {
		_model.addElement(item, indentation_level);
	}
	
	public void setTranslateItems(boolean translate) {
		_model.setTranslateItems(translate);
	}
}

