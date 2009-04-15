package net.sourceforge.jvlt;

import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Dialog for managing (adding, editing, removing) quiz types.
 * For each quiz type, it is possible to select the attribute to quiz on and
 * the displayed attributes.
 */
public class QuizDialogData extends CustomDialogData {
	private class QuizListPanel extends ObjectMapEditorPanel<QuizInfo> {
		private static final long serialVersionUID = 1L;
		
		private boolean _quiz_info_list_modified = false;
		
		protected QuizInfo getCurrentObject() {
			return QuizDialogData.this.getCurrentQuizInfo();
		}
		
		protected void selectionChanged() {
			Object item = getSelectedItem();
			if (_item_map.containsKey(item))
				QuizDialogData.this.setCurrentQuizInfo(
					(QuizInfo) _item_map.get(item));
		}
		
		protected void createOrUpdateItem() {
			super.createOrUpdateItem();
			_quiz_info_list_modified = true;
		}
		
		protected void removeSelectedItem() {
			super.removeSelectedItem();
			_quiz_info_list_modified = true;
		}
	}
	
	private AttributeResources _resources = new AttributeResources();
	private HashMap<String, Attribute> _name_attribute_map;
	private JVLTModel _model = null;
	
	private QuizListPanel _quiz_list_panel;
	private LabeledComboBox _quizzed_attribute_box;
	private AttributeSelectionPanel _shown_attributes_panel;
	
	public QuizDialogData(JVLTModel model) {
		_model = model;
		_name_attribute_map = new HashMap<String, Attribute>();
		
		init();
	}

	public void updateData() throws InvalidDataException {
		if (! _quiz_list_panel._quiz_info_list_modified)
			throw new InvalidDataException(GUIUtils.getString(
				"Messages", "quiz_info_list_unmodified"));
	}
	
	public QuizInfo[] getQuizInfoList() {
		Map<Object, QuizInfo> items = _quiz_list_panel.getItems();
		return items.values().toArray(new QuizInfo[0]);
	}

	public void setQuizInfoList(QuizInfo[] info_list) {
		HashMap<Object, QuizInfo> info_map = new HashMap<Object, QuizInfo>();
		for (int i=0; i<info_list.length; i++)
			info_map.put(info_list[i].getName(), info_list[i]);

		_quiz_list_panel.setItems(info_map);
	}
	
	private void init() {
		_quiz_list_panel = new QuizListPanel();

		_quizzed_attribute_box = new LabeledComboBox();
		_quizzed_attribute_box.setLabel("quizzed_attribute");

		_shown_attributes_panel = new AttributeSelectionPanel();
		_shown_attributes_panel.setAllowReordering(false);
		_shown_attributes_panel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			GUIUtils.getString("Labels", "shown_attributes")));
		
		_content_pane = new JPanel();
		_content_pane.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0, 2, 1);
		_content_pane.add(_quiz_list_panel, cc);
		cc.update(0, 1, 0.5, 0.0, 1, 1);
		_content_pane.add(_quizzed_attribute_box.getLabel(), cc);
		cc.update(1, 1, 0.5, 0.0, 1, 1);
		_content_pane.add(_quizzed_attribute_box, cc);
		cc.update(0, 2, 1.0, 1.0, 2, 1);
		_content_pane.add(_shown_attributes_panel, cc);
		
		MetaData data = _model.getDictModel().getMetaData(Entry.class);
		Attribute[] attrs = data.getAttributes();
		_shown_attributes_panel.setAvailableObjects(attrs);

		TreeSet<String> standard_attr_set = new TreeSet<String>();
		TreeSet<String> custom_attr_set = new TreeSet<String>();
		for (int i=0; i<attrs.length; i++) {
			String s = _resources.getString(attrs[i].getName());
			_name_attribute_map.put(s, attrs[i]);
			if (! (attrs[i] instanceof CustomAttribute))
				standard_attr_set.add(s);
			else
				custom_attr_set.add(s);
		}

		for (Iterator<String> it=standard_attr_set.iterator(); it.hasNext(); )
			_quizzed_attribute_box.addItem(it.next());
		for (Iterator<String> it=custom_attr_set.iterator(); it.hasNext(); )
			_quizzed_attribute_box.addItem(it.next());
	}
	
	private QuizInfo getCurrentQuizInfo() {
		String name = (String) _quiz_list_panel.getSelectedItem();
		if (name == null || name.equals(""))
			return null;
		
		Object[] selected = _shown_attributes_panel.getSelectedObjects();
		String[] attr_list = new String[selected.length];
		for (int i=0; i<selected.length; i++)
			attr_list[i] = ((Attribute) selected[i]).getName();
		
		QuizInfo info = new QuizInfo();
		info.setName(name);
		info.setLanguage(_model.getDict().getLanguage());
		Attribute attr = (Attribute)
			_name_attribute_map.get(_quizzed_attribute_box.getSelectedItem());
		info.setQuizzedAttribute(attr.getName());
		info.setShownAttributes(attr_list);
		
		return info;
	}

	private void setCurrentQuizInfo(QuizInfo info) {
		/*
		 * The attributes are stored as strings in the QuizInfo object.
		 * Obtain the real attributes from the entry meta data.
		 */
		MetaData data = _model.getDictModel().getMetaData(Entry.class);
		String[] names = info.getShownAttributes();
		Attribute[] attrs = new Attribute[names.length];
		for (int i=0; i<attrs.length; i++)
			attrs[i] = data.getAttribute(names[i]);
		_shown_attributes_panel.setSelectedObjects(attrs);
		
		/* The currently selected item is given as a string */
		_quizzed_attribute_box.setSelectedItem(
			_resources.getString(info.getQuizzedAttribute()));
	}
}

