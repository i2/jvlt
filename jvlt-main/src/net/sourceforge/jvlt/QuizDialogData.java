package net.sourceforge.jvlt;

import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
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
	
	private JVLTModel _model = null;
	
	private QuizListPanel _quiz_list_panel;
	private AttributeSelectionPanel _quizzed_attribute_box;
	private AttributeSelectionPanel _shown_attributes_panel;
	
	public QuizDialogData(JVLTModel model) {
		_model = model;
		
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

		_quizzed_attribute_box = new AttributeSelectionPanel();
		_quizzed_attribute_box.setAllowReordering(false);
		_quizzed_attribute_box.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED),
				GUIUtils.getString("Labels", "quizzed_attributes")));

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
		cc.update(1, 1, 0.5, 0.0, 1, 1);
		_content_pane.add(_quizzed_attribute_box, cc);
		cc.update(0, 2, 1.0, 1.0, 2, 1);
		_content_pane.add(_shown_attributes_panel, cc);
		
		MetaData data = _model.getDictModel().getMetaData(Entry.class);
		Attribute[] attrs = data.getAttributes();
		_shown_attributes_panel.setAvailableObjects(attrs);
		_quizzed_attribute_box.setAvailableObjects(attrs);
	}
	
	private QuizInfo getCurrentQuizInfo() {
		String name = (String) _quiz_list_panel.getSelectedItem();
		if (name == null || name.equals(""))
			return null;
		
		Object[] shown_selected = _shown_attributes_panel.getSelectedObjects();
		String[] shown_attr_list = new String[shown_selected.length];
		for (int i=0; i<shown_selected.length; i++)
			shown_attr_list[i] = ((Attribute) shown_selected[i]).getName();

		Object[] quizzed_selected = _quizzed_attribute_box.getSelectedObjects();
		String[] quizzed_attr_list = new String[quizzed_selected.length];
		for (int i=0; i<quizzed_selected.length; i++)
			quizzed_attr_list[i] = ((Attribute) quizzed_selected[i]).getName();
		
		QuizInfo info = new QuizInfo();
		info.setName(name);
		info.setLanguage(_model.getDict().getLanguage());
		info.setQuizzedAttributes(quizzed_attr_list);
		info.setShownAttributes(shown_attr_list);
		
		return info;
	}

	private void setCurrentQuizInfo(QuizInfo info) {
		/*
		 * The attributes are stored as strings in the QuizInfo object.
		 * Obtain the real attributes from the entry meta data.
		 */
		MetaData data = _model.getDictModel().getMetaData(Entry.class);
		String[] shown_names = info.getShownAttributes();
		Attribute[] shown_attrs = new Attribute[shown_names.length];
		for (int i=0; i<shown_attrs.length; i++)
			shown_attrs[i] = data.getAttribute(shown_names[i]);
		_shown_attributes_panel.setSelectedObjects(shown_attrs);

		String[] quizzed_names = info.getQuizzedAttributes();
		Attribute[] quizzed_attrs = new Attribute[quizzed_names.length];
		for (int i=0; i<quizzed_attrs.length; i++)
			quizzed_attrs[i] = data.getAttribute(quizzed_names[i]);
		_quizzed_attribute_box.setSelectedObjects(quizzed_attrs);
	}
}

