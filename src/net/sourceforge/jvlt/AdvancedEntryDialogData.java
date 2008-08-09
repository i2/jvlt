package net.sourceforge.jvlt;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AdvancedEntryDialogData extends CustomDialogData {
	private JVLTModel _model;
	private List<Entry> _entries;
	
	private EntryClass _orig_class;
	private String[] _orig_categories;
	private String[] _orig_mmfiles;
	
	private ChoiceListPanel _category_selection_panel;
	private EntryAttributeSchemaPanel _schema_panel;
	private FileSelectionPanel _file_selection_panel;
	
	public AdvancedEntryDialogData(List<Entry> entries, JVLTModel model) {
		_entries = entries;
		_model = model;
		init();
	}
	
	public void updateData() throws InvalidDataException {
		if (_schema_panel != null) {
			EntryClass ec = _schema_panel.getValue();
			if (_entries.size() == 1) {
				_entries.get(0).setEntryClass(ec);
			} else {
				if (ec == null) {
					if (_orig_class != null) {
						Iterator<Entry> it = _entries.iterator();
						for (; it.hasNext(); )
							it.next().setEntryClass(null);
					}
				} else if (! ec.equals(_orig_class))
					/*
					 * Currently, only when the new entry class has another
					 * name as the old one, the entry class is updated.
					 * TODO: Update also if only attributes have changed   
					 */ 
					for (Iterator<Entry> it=_entries.iterator(); it.hasNext(); )
						it.next().setEntryClass(ec);
			}
		}

		String[] categories = Utils.objectArrayToStringArray(
				_category_selection_panel.getSelectedObjects());
		if (! arraysEqual(categories, _orig_categories))
			for (Iterator<Entry> it=_entries.iterator(); it.hasNext(); )
				it.next().setCategories(categories);

		String[] files = _file_selection_panel.getFiles();
		if (! arraysEqual(files, _orig_mmfiles))
			for (Iterator<Entry> it=_entries.iterator(); it.hasNext(); )
				it.next().setMultimediaFiles(files);
		
		if (files.length > 0)
			if (FileUtils.isPathRelative(files[files.length-1]))
				JVLT.getConfig().setProperty("use_relative_path", true);
			else
				JVLT.getConfig().setProperty("use_relative_path", false);
	}
	
	private void init()	{
		_category_selection_panel = new ChoiceListPanel();
		_category_selection_panel.setAllowCustomChoices(true);
		
		_file_selection_panel = new FileSelectionPanel();
		String dict_file_name = _model.getDictFileName();
		if (dict_file_name != null && ! dict_file_name.equals("")) {
			File file = new File(dict_file_name);
			File parent = file.getParentFile();
			if (parent != null)
				_file_selection_panel.setPath(parent.getAbsolutePath());
		}
		_file_selection_panel.setUseRelativePath(
			JVLT.getConfig().getBooleanProperty("use_relative_path", false));
		SimpleFileFilter filter = new SimpleFileFilter(
			GUIUtils.getString("Labels", "audio_files"));
		filter.setExtensions(MultimediaUtils.AUDIO_FILE_EXTENSIONS);
		_file_selection_panel.addFileFilter(filter);
		filter = new SimpleFileFilter(
			GUIUtils.getString("Labels", "image_files"));
		filter.setExtensions(MultimediaUtils.IMAGE_FILE_EXTENSIONS);
		_file_selection_panel.addFileFilter(filter);
		
		EntryAttributeSchema schema =
			_model.getDict().getEntryAttributeSchema();
		if (schema == null)
			_schema_panel = null;
		else
			_schema_panel = new EntryAttributeSchemaPanel(schema);
		
		CustomTabbedPane tpane = new CustomTabbedPane();
		if (_schema_panel != null)
			tpane.addTab("details", _schema_panel);
		tpane.addTab("categories", _category_selection_panel);
		tpane.addTab("multimedia_files", _file_selection_panel);
		
		_content_pane = new JPanel();
		_content_pane.setLayout(new GridLayout());
		_content_pane.add(tpane);
			
		//-----
		// Init data
		//-----
		MetaData data=_model.getDictModel().getMetaData(Entry.class);
		ChoiceAttribute attr=(ChoiceAttribute) data.getAttribute("Categories");
		_category_selection_panel.setAvailableObjects(attr.getValues());
		_orig_categories = _entries.get(0).getCategories();
		_orig_mmfiles = _entries.get(0).getMultimediaFiles();
		_orig_class = _entries.get(0).getEntryClass();
		for (int i=1; i<_entries.size(); i++) {
			if (_orig_categories.length > 0 && ! arraysEqual(_orig_categories,
						_entries.get(i).getCategories()))
				_orig_categories = new String[0];
			if (_orig_mmfiles.length > 0 &&	! arraysEqual(_orig_mmfiles,
						_entries.get(i).getMultimediaFiles()))
				_orig_mmfiles = new String[0];
			if (_orig_class != null &&
				! _orig_class.equals(_entries.get(i).getEntryClass()))
				_orig_class = null;
		}
		_category_selection_panel.setSelectedObjects(_orig_categories);
		_file_selection_panel.setFiles(_orig_mmfiles);
		if (schema != null)
			_schema_panel.setValue(_orig_class);
	}
	
	private boolean arraysEqual(Object[] array1, Object[] array2) {
		if (array1 == null)
			return array2 == null;
		if (array1.length != array2.length)
			return false;
		
		for (int i=0; i<array1.length; i++)
			if (! array1[i].equals(array2[i]))
				return false;
		
		return true;
	}
}


class FileSelectionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private class ChangeHandler implements ChangeListener {
		public void stateChanged(ChangeEvent e) { update(); }
	}
	
	private class ListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent ev) {
			if (ev.getValueIsAdjusting() == false)
				update();
		}
	}
	
	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if (ev.getActionCommand().equals("add")) {
				String value = _field.getText();
				if (! value.equals("") && ! _list_model.contains(value)) {
					_list_model.addElement(value);
					_list.setSelectedIndex(_list_model.size()-1);
				}
			} else if (ev.getActionCommand().equals("remove")) {
				int index = _list.getSelectedIndex();
				if (index >= 0)
					_list_model.remove(index);
			} else if (ev.getActionCommand().equals("browse")) {
				JFileChooser chooser = new JFileChooser();
				Iterator<SimpleFileFilter> it = _file_filters.iterator();
				while (it.hasNext())
					chooser.addChoosableFileFilter(it.next());
				
				File file = new File(_path);
				if (file != null)
					chooser.setCurrentDirectory(file);
				
				int val = chooser.showOpenDialog(FileSelectionPanel.this);
				if (val == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					if (_relative_path_box.isSelected())
						_list_model.addElement(
							FileUtils.getRelativePath(new File(_path), f));
					else
						_list_model.addElement(f.getAbsolutePath());
				}
			} else if (ev.getActionCommand().equals("relative_path")) {
				int index = _list.getSelectedIndex();
				if (index < 0)
					return;
				
				String path = _list_model.get(index).toString();
				File f;
				if (FileUtils.isPathRelative(path))
					f = new File(_path + File.separator + path);
				else
					f = new File(path);
				
				if (_relative_path_box.isSelected())
					_list_model.set(index,
						FileUtils.getRelativePath(new File(_path), f));
				else
					_list_model.set(index, f.getAbsolutePath());
			}
		}
	}
	
	private ArrayList<SimpleFileFilter> _file_filters;
	private String _path;
	private boolean _use_relative_path;
	
	private Action _add_action;
	private Action _browse_action;
	private Action _remove_action;
	private CustomTextField _field;
	private DefaultListModel _list_model;
	private JCheckBox _relative_path_box;
	private JList _list;

	public FileSelectionPanel()	{
		_path = System.getProperty("user.home");
		_use_relative_path = false;
		_file_filters = new ArrayList<SimpleFileFilter>();
		
		init();
	}
	
	public String getPath() { return _path; }

	public String[] getFiles() {
		return Utils.objectArrayToStringArray(_list_model.toArray());
	}
	
	public void addFileFilter(SimpleFileFilter filter) {
		_file_filters.add(filter);
	}
	
	public void setPath(String path) { _path = path; }
	
	public void setFiles(String[] files) {
		_list_model.clear();
		for (int i=0; i<files.length; i++)
			_list_model.addElement(files[i]);
	}

	public void setUseRelativePath(boolean relative) {
		_use_relative_path = relative;
	}
	
	private void init() {
		ActionHandler handler = new ActionHandler();
		_add_action = GUIUtils.createTextAction(handler, "add");
		_browse_action = GUIUtils.createTextAction(handler, "browse");
		_remove_action = GUIUtils.createTextAction(handler, "remove");
		Action relative_path_action = GUIUtils.createTextAction(
			handler, "relative_path");
		
		_list_model = new DefaultListModel();
		_list = new JList(_list_model);
		_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_list.addListSelectionListener(new ListSelectionHandler());
		
		_field = new CustomTextField(20);
		_field.addChangeListener(new ChangeHandler());

		_relative_path_box = new JCheckBox(relative_path_action);
		_relative_path_box.setSelected(_use_relative_path);
		
		JScrollPane list_scrpane = new JScrollPane();
		list_scrpane.setPreferredSize(new Dimension(100, 50));
		list_scrpane.getViewport().setView(_list);

		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		add(_field, cc);
		cc.update(1, 0, 0.0, 0.0);
		add(new JButton(_browse_action), cc);
		cc.update(0, 1, 1.0, 1.0, 1, 3);
		add(list_scrpane, cc);
		cc.update(1, 1, 0.0, 0.0, 1, 1);
		add(new JButton(_add_action), cc);
		cc.update(1, 2, 0.0, 0.0);
		add(new JButton(_remove_action), cc);
		cc.update(1, 3, 0.0, 1.0);
		add(Box.createVerticalGlue(), cc);
		cc.update(0, 4, 1.0, 0.0, 2, 1);
		add(_relative_path_box, cc);
	}
	
	protected void update()	{
		String path = getCurrentPath();
		
		_add_action.setEnabled(_field.getText().length() > 0);
		_remove_action.setEnabled(path != null);
		_relative_path_box.setEnabled(path != null);
		if (path != null)
			_relative_path_box.setSelected(FileUtils.isPathRelative(path));
	}
	
	private String getCurrentPath() {
		String path = null;
		int index = _list.getSelectedIndex();
		if (index >= 0)
			path = _list_model.get(index).toString();
		
		return path;
	}
}

class StringListInputComponent implements InputComponent {
	ObjectListPanel _input_component = new ObjectListPanel();

	public JComponent getComponent() { return _input_component; }
	
	public Object getInput() {
		return _input_component.getSelectedObjects();
	}

	public void setInput(Object input) {
		_input_component.setSelectedObjects((Object[]) input);
	}

	public void reset() {
		_input_component.setSelectedObjects(new Object[0]);
	}
}

class StringListEditor extends ObjectListEditor {
	public StringListEditor(String label) { super(label); }

	protected InputComponent createSingleInputComponent() {
		return new StringInputComponent();
	}

	protected InputComponent createMultiInputComponent() {
		return new StringListInputComponent();
	}
}
