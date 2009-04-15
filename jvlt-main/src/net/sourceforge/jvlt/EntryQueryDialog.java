package net.sourceforge.jvlt;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import net.sourceforge.jvlt.event.ComponentReplacementListener;
import net.sourceforge.jvlt.event.ComponentReplacementListener.
	ComponentReplacementEvent;
import net.sourceforge.jvlt.event.DictUpdateListener;

public class EntryQueryDialog extends AbstractDialog
	implements ActionListener {
	private class ComponentReplacementHandler
		implements ComponentReplacementListener {
		public void componentReplaced(ComponentReplacementEvent e) {
			JComponent o = e.getOldComponent();
			JComponent n = e.getNewComponent();
			int i=0;
			for (Iterator<EntryQueryRow> it=_query_rows.iterator();
				it.hasNext(); i++) {
				EntryQueryRow row = it.next();
				if (row.getValueField() == n) {
					_query_panel.remove(o);
					CustomConstraints cc = new CustomConstraints();
					cc.update(2, i, 1.0, 0.0);
					_query_panel.add(n, cc);
					_query_panel.revalidate();
					_query_panel.repaint(_query_panel.getVisibleRect());
					break;
				}
			}
		}
	}
	
	private class PropertyChangeHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent ev) {
			if (ev.getPropertyName().equals("filters"))
				loadFilters();
		}
	}
	
	private class FilterMapEditorPanel
			extends ObjectMapEditorPanel<ObjectQuery> {
		private static final long serialVersionUID = 1L;
		
		protected ObjectQuery getCurrentObject() {
			return EntryQueryDialog.this.getObjectQuery();
		}
		
		protected void removeSelectedItem() {
			super.removeSelectedItem();
			EntryQueryDialog.this.clear();
		}
		
		protected void selectionChanged() {
			Object item = getSelectedItem();
			if (_item_map.containsKey(item))
				EntryQueryDialog.this.setObjectQuery(
					(ObjectQuery) _item_map.get(item));
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	private JVLTModel _model;
	private LinkedList<EntryQueryRow> _query_rows;
	
	private FilterMapEditorPanel _filter_map_panel;
	private JPanel _query_panel;
	private JRadioButton _match_all_button;
	private JRadioButton _match_one_button;
	
	public EntryQueryDialog (Frame owner, String title, JVLTModel model) {
		this(owner, title, false, model);
	}
	
	public EntryQueryDialog (Frame owner, String title, boolean modal,
		JVLTModel model) {
		super(owner, title, modal);
		_model = model;
		_query_rows = new LinkedList<EntryQueryRow>();
		init();
	}
	
	public ObjectQuery getObjectQuery() {
		ObjectQuery oq = new ObjectQuery(Entry.class);
		Object selected = _filter_map_panel.getSelectedItem();
		if (selected != null)
			oq.setName(selected.toString());
		if (_match_all_button.isSelected())
			oq.setType(ObjectQuery.MATCH_ALL);
		else if (_match_one_button.isSelected())
			oq.setType(ObjectQuery.MATCH_ONE);
		
		Iterator<EntryQueryRow> it = _query_rows.iterator();
		while (it.hasNext())
			oq.addItem(it.next().getQueryItem());
		
		return oq;
	}

	public void setObjectQuery(ObjectQuery query) {
		if (query.getType() == ObjectQuery.MATCH_ALL)
			_match_all_button.setSelected(true);
		else
			_match_one_button.setSelected(true);

		Iterator<EntryQueryRow> it = _query_rows.iterator();
		while (it.hasNext()) {
			EntryQueryRow row = it.next();
			_query_panel.remove(row.getNameBox());
			_query_panel.remove(row.getTypeBox());
			_query_panel.remove(row.getValueField());
		}
		_query_rows.clear();

		ObjectQueryItem[] items = query.getItems();
		for (int i=0; i<items.length; i++) {
			EntryQueryRow row = new EntryQueryRow(_model);
			row.setQueryItem(items[i]);
			addRow(row);
		}
		_query_panel.revalidate();
		_query_panel.repaint(_query_panel.getVisibleRect());
	}
	
	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("more"))
			addRow();
		else if (ev.getActionCommand().equals("less"))
			removeRow();
		else if (ev.getActionCommand().equals("reset"))
			clear();
		else { // Either "Close" or "Apply" button has been pressed
			saveFilters();
			super.actionPerformed(ev);
		}
	}
	
	private void init() {
		Action more_action = GUIUtils.createTextAction(this, "more");
		Action less_action = GUIUtils.createTextAction(this, "less");
		Action reset_action = GUIUtils.createTextAction(this, "reset");
		
		_filter_map_panel = new FilterMapEditorPanel();
		
		JPanel type_panel = new JPanel();
		type_panel.setLayout(new GridLayout(2,1,5,5));
		ButtonGroup bg = new ButtonGroup();
		_match_all_button = new JRadioButton(
			GUIUtils.getString("Labels", "match_all"));
		type_panel.add(_match_all_button);
		bg.add(_match_all_button);
		_match_one_button = new JRadioButton(
			GUIUtils.getString("Labels", "match_one"));
		type_panel.add(_match_one_button);
		bg.add(_match_one_button);
		_match_all_button.setSelected(true);
		
		_query_panel = new JPanel();
		_query_panel.setLayout(new GridBagLayout());
		JPanel query_panel =  new JPanel();
		query_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		query_panel.add(_query_panel, cc);
		cc.update(0, 1, 1.0, 1.0);
		query_panel.add(Box.createVerticalGlue(), cc);
		JScrollPane query_scrpane = new JScrollPane(query_panel);
		query_scrpane.setPreferredSize(new Dimension(400, 100));
		
		JPanel more_less_panel = new JPanel();
		more_less_panel.setLayout(new GridBagLayout());
		cc.update(0, 0, 1.0, 0.0);
		more_less_panel.add(Box.createHorizontalGlue(), cc);
		cc.update(1, 0, 0.0, 0.0);
		more_less_panel.add(new JButton(more_action), cc);
		cc.update(2, 0, 0.0, 0.0);
		more_less_panel.add(new JButton(less_action), cc);
		cc.update(3, 0, 0.0, 0.0);
		more_less_panel.add(new JButton(reset_action), cc);
		
		JPanel main_panel = new JPanel();
		main_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		main_panel.setLayout(new GridBagLayout());
		cc.update(0, 0, 1.0, 0.0);
		main_panel.add(_filter_map_panel, cc);
		cc.update(0, 1, 1.0, 0.0);
		main_panel.add(type_panel, cc);
		cc.update(0, 2, 1.0, 1.0);
		main_panel.add(query_scrpane, cc);
		cc.update(0, 3, 1.0, 0.0);
		main_panel.add(more_less_panel, cc);
		
		setContent(main_panel);
		setButtons(new int[] {AbstractDialog.APPLY_OPTION,
			AbstractDialog.CLOSE_OPTION});
		loadFilters();
		JVLT.getRuntimeProperties().addPropertyChangeListener(
			new PropertyChangeHandler());
		
		// Start with a query that has one row.
		clear();
	}
	
	private void clear() {
		_filter_map_panel.setSelectedItem("");
		setObjectQuery(new ObjectQuery(Entry.class));
		addRow();
	}
	
	private void loadFilters() {
		ObjectQuery[] oqs = (ObjectQuery[])
			JVLT.getRuntimeProperties().get("filters");
		HashMap<Object, ObjectQuery> map = new HashMap<Object, ObjectQuery>();
		if (oqs != null)
			for (int i=0; i<oqs.length; i++)
				map.put(oqs[i].getName(), oqs[i]);
		
		_filter_map_panel.setItems(map);
	}
	
	private void saveFilters() {
		JVLT.getRuntimeProperties().put("filters",
			_filter_map_panel.getItems().values().toArray(new ObjectQuery[0]));
	}

	/** Add a single row. Do not repaint afterwards. */
	private void addRow(EntryQueryRow row) {
		row.addComponentReplacementListener(new ComponentReplacementHandler());
		_query_rows.addLast(row);
		int numrows = _query_rows.size();
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, numrows-1, 1.0, 0.0);
		_query_panel.add(row.getNameBox(), cc);
		cc.update(1, numrows-1, 1.0, 0.0);
		_query_panel.add(row.getTypeBox(), cc);
		cc.update(2, numrows-1, 1.0, 0.0);
		_query_panel.add(row.getValueField(), cc);
	}
	
	private void addRow() {
		EntryQueryRow row = new EntryQueryRow(_model);
		addRow(row);
		_query_panel.revalidate();
		_query_panel.repaint(_query_panel.getVisibleRect());
	}
	
	private void removeRow() {
		if (_query_rows.size() == 0)
			return;
		
		EntryQueryRow row = (EntryQueryRow) _query_rows.getLast();
		_query_rows.removeLast();
		_query_panel.remove(row.getNameBox());
		_query_panel.remove(row.getTypeBox());
		_query_panel.remove(row.getValueField());
		_query_panel.revalidate();
		_query_panel.repaint(_query_panel.getVisibleRect());
	}
}

class EntryQueryRow implements ActionListener {
	private class DictUpdateHandler implements DictUpdateListener {
		public void dictUpdated(DictUpdateEvent event) {
			if (event instanceof NewDictDictUpdateEvent
				|| event instanceof LanguageDictUpdateEvent) {
				updateAttributeBox();
			}
		}
	}

	private ItemContainer _container;
	private JVLTModel _model;
	private ArrayList<ComponentReplacementListener> _listeners;
	private HashMap<Class<? extends Attribute>, ObjectQueryItem> _query_items;
	private HashMap<String, Integer> _translation_type_map;
	private HashMap<Integer, String> _type_translation_map;
	private MetaData _data;
	private InputComponent _input_component;
	private JComboBox _name_box = null;
	private JComboBox _type_box = null;
	
	public EntryQueryRow(JVLTModel model) {
		_container = new ItemContainer();
		_container.setTranslateItems(true);
		_model = model;
		_model.getDictModel().addDictUpdateListener(new DictUpdateHandler());
		_listeners = new ArrayList<ComponentReplacementListener>();
		_translation_type_map = new HashMap<String, Integer>();
		_type_translation_map = new HashMap<Integer, String>();
		_query_items =
			new HashMap<Class<? extends Attribute>, ObjectQueryItem>();
		_query_items.put(DefaultChoiceAttribute.class,
			new ChoiceQueryItem());
		_query_items.put(ArrayAttribute.class,
			new ObjectArrayQueryItem());
		_query_items.put(ArrayChoiceAttribute.class,
			new ChoiceObjectArrayQueryItem());
		_query_items.put(CalendarAttribute.class,
			new CalendarQueryItem());
		_query_items.put(NumberAttribute.class,
			new NumberQueryItem());
		_query_items.put(DefaultAttribute.class,
			new StringQueryItem());
		_query_items.put(CustomAttribute.class,
			new StringQueryItem());
		_query_items.put(CustomChoiceAttribute.class,
			new ChoiceQueryItem());
		_query_items.put(CustomArrayAttribute.class,
			new ObjectArrayQueryItem());
		_query_items.put(EntryMetaData.SensesAttribute.class,
			new SenseArrayQueryItem());
		_query_items.put(EntryMetaData.EntryClassAttribute.class,
			new EntryClassQueryItem());
		_data = _model.getDictModel().getMetaData(Entry.class);

		_name_box = new JComboBox();
		_name_box.addActionListener(this);
		_type_box = new JComboBox();
		_type_box.addActionListener(this);
		_input_component = null;
				
		updateAttributeBox();
		setAttribute(_data.getAttributes()[0]);
	}
	
	public void addComponentReplacementListener(
		ComponentReplacementListener l) {
		_listeners.add(l);
	}
	
	public JComboBox getNameBox() { return _name_box; }

	public JComboBox getTypeBox() { return _type_box; }

	public JComponent getValueField() {
		return _input_component.getComponent();
	}
	
	public ObjectQueryItem getQueryItem() {
		Attribute attr = (Attribute) _container.getItem(
				(String) _name_box.getSelectedItem());
		ObjectQueryItem item = _query_items.get(attr.getClass());
		item.setName(attr.getName());
		Object type_obj = _type_box.getSelectedItem().toString();
		int type = _translation_type_map.get(type_obj).intValue();
		item.setType(type);
		Object value = _input_component.getInput();
		item.setValue(value);
		
		return item;
	}

	public void setQueryItem(ObjectQueryItem item) {
		String name = item.getName();
		setAttribute(_data.getAttribute(name));
		Integer type = new Integer(item.getType());
		if (_type_translation_map.containsKey(type))
			_type_box.setSelectedItem(_type_translation_map.get(type));

		_input_component.setInput(item.getValue());
	}
	
	public void actionPerformed(ActionEvent ev) {
		Object selected = _name_box.getSelectedItem();
		Integer type = _translation_type_map.get(_type_box.getSelectedItem());
		if (ev.getSource() == _name_box) {
			if (selected != null)
				setAttribute((Attribute) _container.getItem(selected));
		} else if (ev.getSource() == _type_box) { 
			if (selected != null && type != null)
				setType((Attribute) _container.getItem(selected),
						type.intValue());
		}
	}
	
	private void updateAttributeBox() {
		/*
		 * Remove action listener. Otherwise ActionEvents are generated
		 * while updating the name combo box which results in
		 * NullPointerExceptions.
		 */
		_name_box.removeActionListener(this);
		
		/*
		 * Save attribute in order to restore it after the name box has been
		 * updated
		 */
		Attribute attr = (Attribute) _container.getItem(
				_name_box.getSelectedItem());
		
		_name_box.removeAllItems();
		Attribute[] attrs = _data.getAttributes();
		boolean found = false;
		if (attrs != null && attrs.length > 0) {
			_container.setItems(attrs);
			TreeSet<Object> set = new TreeSet<Object>(
					new AttributeComparator(_container));
			set.addAll(Arrays.asList(attrs));
			for (Iterator<Object> it=set.iterator(); it.hasNext(); )
				_name_box.addItem(_container.getTranslation(it.next()));
		
			/*
			 * If the name box does not contain the original name, use the
			 * default name (the first one in the array). Otherwise, restore the
			 * original name. 
			 */
			if (attr != null)
				for (int i=0; i<attrs.length; i++)
					if (attrs[i].getName().equals(attr.getName())) {
						found = true;
						break;
					}
			
			if (found)
				_name_box.setSelectedItem(_container.getTranslation(attr));
		}
			
		_name_box.addActionListener(this);
		
		if (! found)
			/*
			 * - Do not call _name_box.setSelectedItem() as the other values
			 *   in the row probably also have to be updated.
			 * - Call setAttribute() after the action listener has been added so
			 *   the action listener will not be added twice 
			 */
			setAttribute(attrs[0]);
	}
	
	private void setAttribute(Attribute attr) {
		/*
		 * Remove action listeners from combo boxes so actionPerformed() will
		 * not called during the update
		 */
		_name_box.removeActionListener(this);
		_type_box.removeActionListener(this);
		
		_name_box.setSelectedItem(_container.getTranslation(attr));
		
		ObjectQueryItem item = (ObjectQueryItem)
			_query_items.get(attr.getClass());
		String[] types = item.getTypeNames();
		_type_box.removeAllItems();
		_translation_type_map.clear();
		_type_translation_map.clear();
		for (int i=0; i<types.length; i++) {
			String translation = GUIUtils.getString("Labels", types[i]);
			_type_box.addItem(translation);
			try {
				Field field = item.getClass().getField(types[i]);
				int type_value = field.getInt(item);
				_translation_type_map.put(translation, new Integer(type_value));
				_type_translation_map.put(new Integer(type_value), translation);
			}
			catch (Exception ex) { ex.printStackTrace(); }
		}
		
		/* Re-add action listeners */
		_name_box.addActionListener(this);
		_type_box.addActionListener(this);

		setType(attr, item.getTypes()[0]);
	}
	
	private void setType(Attribute attr, int type) {
		/*
		 * Update the type combo box. Remove action listener before updating
		 * in order to prevent actionPerformed() from being called.
		 */
		_type_box.removeActionListener(this);
		_type_box.setSelectedItem(_type_translation_map.get(new Integer(type)));
		_type_box.addActionListener(this);
		
		JComponent old_component = null;
		if (_input_component != null)
			old_component = _input_component.getComponent();

		ObjectQueryItem item = (ObjectQueryItem)
			_query_items.get(attr.getClass());
		if (item instanceof ChoiceQueryItem) {
			if (type == ChoiceQueryItem.CONTAINS) {
				_input_component = new StringInputComponent();
			} else {
				ChoiceInputComponent cic = new ChoiceInputComponent();
				if (attr.getClass().equals(CustomChoiceAttribute.class)) {
					CustomChoiceAttribute cca = (CustomChoiceAttribute) attr;
					cic.setTranslateItems(true);
					cic.setChoices(cca.getValues());
				} else if (attr.getClass().equals(DefaultChoiceAttribute.class)) {
					DefaultChoiceAttribute dca = (DefaultChoiceAttribute) attr;
					cic.setChoices(dca.getValues());
				}
				_input_component = cic;
			}
		} else if (item instanceof ObjectArrayQueryItem) {
			if (type == ObjectArrayQueryItem.EMPTY
				|| type == ObjectArrayQueryItem.NOT_EMPTY)
				_input_component = new EmptyInputComponent();
			else if (type == ObjectArrayQueryItem.ITEM_CONTAINS)
				_input_component = new StringInputComponent();
			else { // item is a ChoiceObjectArrayQueryItem
				ChoiceListInputComponent clic = new ChoiceListInputComponent();
				_input_component = clic;
				if (attr instanceof ChoiceAttribute)
					clic.setChoices(((ChoiceAttribute) attr).getValues());
				if (attr instanceof CustomArrayAttribute)
					clic.setTranslateItems(true);
			}
		} else if (item instanceof CalendarQueryItem) {
			_input_component = new DateInputComponent();
		} else if (item instanceof NumberQueryItem) {
			_input_component = new NumberInputComponent();
		} else if (item instanceof StringQueryItem) {
			_input_component = new StringInputComponent();
		} else if (item instanceof SenseArrayQueryItem) {
			_input_component = new StringInputComponent();
		} else if (item instanceof EntryClassQueryItem) {
			EntryClassInputComponent ecic = new EntryClassInputComponent();
			ecic.setSchema(_model.getDict().getEntryAttributeSchema());
			_input_component = ecic;
		} else {
			_input_component = null;
		}
		
		// Notify listeners
		for (Iterator<ComponentReplacementListener> it=_listeners.iterator();
			it.hasNext(); )
			it.next().componentReplaced(new ComponentReplacementEvent(
				old_component, _input_component.getComponent()));
	}
}

class EmptyInputComponent implements InputComponent {
	private JPanel _input_panel = new JPanel();
	
	public JComponent getComponent() { return _input_panel; }
	
	public void reset() {}

	public  Object getInput() { return null; }

	public void setInput(Object input) {}
}

class DateInputComponent implements InputComponent {
	private DateChooserButton _input_panel = new DateChooserButton();
	
	public JComponent getComponent() { return _input_panel; }
	
	public void reset() { _input_panel.setDate(new GregorianCalendar()); }
	
	public Object getInput() { return _input_panel.getDate(); }
	
	public void setInput(Object input) {
		_input_panel.setDate((Calendar) input);
	}
}

class NumberInputComponent implements InputComponent {
	private CustomTextField _input_panel = new CustomTextField(10);
	
	public JComponent getComponent() { return _input_panel; }
	
	public void reset() { _input_panel.setText(""); }
	
	public Object getInput() {
		String text = _input_panel.getText();
		if (text == null || "".equals(text)) {
			_input_panel.setText("0");
			return new Double(0.0);
		}
		else
			return new Double(_input_panel.getText());
	}
	
	public void setInput(Object input) { 
		_input_panel.setText(input.toString());
	}
}

class ChoiceListInputComponent extends ChoiceInputComponent {
	private StringListInput _input_component = new StringListInput();
	
	public JComponent getComponent() { return _input_component; }
	
	public void reset() { _input_component.setStrings(new String[0]); }
	
	public Object getInput() {
		return _container.getItems(_input_component.getStrings());
	}
	
	public void setInput(Object input) {
		_input_component.setStrings(
			_container.getTranslations((Object[]) input));
	}
	
	protected void updateInputComponent() {
		_input_component.setAvailableStrings(_container.getTranslations());
	}
}

class EntryClassInputComponent extends ChoiceInputComponent {
	public EntryClassInputComponent() {
		super();
		setTranslateItems(true);
	}
	
	public void setSchema(EntryAttributeSchema s) {
		if (s == null)
			setChoices(new Object[0]);
		else
			setChoices(s.getEntryClasses());
	}
}

class StringListInput extends JPanel {
	public static class StringListDialog extends JDialog {
		private static final long serialVersionUID = 1L;

		public ChoiceListPanel choicePanel;
		
		public StringListDialog(Frame owner, String title) {
			super(owner, title, true);
			choicePanel = new ChoiceListPanel();
			choicePanel.setAllowCustomChoices(false);
			
			ActionListener listener = new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					dispose();
				}
			};
			Action ok = GUIUtils.createTextAction(listener, "ok");
			ButtonPanel button_panel = new ButtonPanel(
				SwingConstants.HORIZONTAL, SwingConstants.RIGHT);
			button_panel.addButtons(new JButton[]{new JButton(ok)});
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			CustomConstraints cc = new CustomConstraints();
			cc.update(0, 0, 1.0, 1.0);
			panel.add(this.choicePanel, cc);
			cc.update(0, 1, 1.0, 0.0);
			panel.add(button_panel, cc);
			setContentPane(panel);
		}
	}
	
	private static final long serialVersionUID = 1L;

	private JTextField _display;
	private StringListDialog _dialog;
	
	public StringListInput() {
		_display = new JTextField(10);
		_display.setEditable(false);
		_dialog = new StringListDialog(JOptionPane.getFrameForComponent(this),
			GUIUtils.getString("Labels", "select_categories"));
		JButton button = new JButton("...");
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GUIUtils.showDialog(StringListInput.this, _dialog);
				setStrings(getStrings());
			}
		};
		button.addActionListener(listener);
		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.insets = new Insets(0,0,0,0);
		cc.update(0, 0, 1.0, 0.0);
		add(_display, cc);
		cc.update(1, 0, 0.0, 0.0);
		add(button, cc);
	}
	
	public void setAvailableStrings(String[] strings) {
		_dialog.choicePanel.setAvailableObjects(strings);
	}
	
	public void setStrings(String[] strings) {
		_dialog.choicePanel.setSelectedObjects(strings);
		String text = Utils.arrayToString(strings, ", ");
		_display.setText(text);
		_display.setCaretPosition(0);
		_display.setToolTipText(text);
	}
	
	public String[] getStrings() {
		Object[] values = _dialog.choicePanel.getSelectedObjects();
		return Utils.objectArrayToStringArray(values);
	}
}

