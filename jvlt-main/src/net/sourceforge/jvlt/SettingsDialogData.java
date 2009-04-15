package net.sourceforge.jvlt;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class SettingsDialogData extends CustomDialogData
	implements ActionListener {
	private Font _old_print_font;
	private Font _old_html_font;
	private Font _old_ui_font;
	private Font _old_orth_font;
	private Font _old_pron_font;
	private Locale _old_locale;
	private boolean _old_restore_previously_open;
	private boolean _old_input_answer;
	private boolean _old_match_case;
	private boolean _old_play_immediately;
	private LookAndFeel _old_laf;
	private String _old_default_answer;
	private HashMap<String, Locale> _string_locale_map;
	private HashMap<String, String> _string_laf_map;
	private int _old_num_batches;
	private float _old_expiration_factor;
	private String _old_expiration_unit;
	private Attribute[] _old_displayed_attrs;

	private JVLTModel _model;
	
	private JCheckBox _restore_chbox;
	private JCheckBox _input_answer_chbox;
	private JCheckBox _match_case_chbox;
	private JCheckBox _play_immediately_chbox;
	private JCheckBox _default_answer_chbox;
	private LabeledComboBox _locale_cobox;
	private LabeledComboBox _laf_cobox;
	private LabeledComboBox _default_answer_cobox;
	private FileTypePanel _file_type_panel;
	private FontChooserButton _print_font_button;
	private FontChooserButton _html_font_button;
	private FontChooserButton _ui_font_button;
	private FontChooserButton _orth_font_button;
	private FontChooserButton _pron_font_button;
	private ExpirationTimePanel _expiration_panel;
	private AttributeSelectionPanel _displayed_attrs_panel;

	public SettingsDialogData(JVLTModel model) {
		_model = model;
		Config config = JVLT.getConfig();
		
		_old_print_font = config.getFontProperty("print_font",
			new Font("Dialog", Font.PLAIN, 12));
		_old_html_font = config.getFontProperty("html_font",
			new Font("Dialog", Font.PLAIN, 12));
		_old_ui_font = config.getFontProperty("ui_font",
			new Font("Dialog", Font.PLAIN, 12));
		_old_orth_font = config.getFontProperty("orth_font",
			new Font("Dialog", Font.PLAIN, 24));
		_old_pron_font = config.getFontProperty("pron_font",
			new Font("Dialog", Font.PLAIN, 12));
		_old_restore_previously_open = config.getBooleanProperty(
			"restore_previously_open_file", false);
		_old_input_answer = config.getBooleanProperty(
			"input_answer", false);
		_old_match_case = config.getBooleanProperty(
			"match_case", true);
		_old_play_immediately = config.getBooleanProperty(
			"play_audio_immediately", false);
		_old_default_answer = config.getProperty(
			"default_answer", "");
		_old_num_batches = config.getIntProperty(
			"num_batches", 7);
		_old_expiration_factor = config.getFloatProperty(
			"expiration_factor", 3.0f);
		_old_expiration_unit = config.getProperty(
			"expiration_unit", Entry.UNIT_DAYS);

		MetaData data = model.getDictModel().getMetaData(Entry.class);
		String[] attr_names;
		Object[] attrs = (Object[]) JVLT.getRuntimeProperties().get(
				"displayed_attributes");
		if (attrs != null)
			attr_names = Utils.objectArrayToStringArray(attrs);
		else
			attr_names = new String[0];
		_old_displayed_attrs = new Attribute[attr_names.length];
		for (int i=0; i<attr_names.length; i++)
			_old_displayed_attrs[i] = data.getAttribute(attr_names[i]);
			
		_old_locale = null;
		Locale locale = config.getLocaleProperty("locale",Locale.getDefault());
		Locale[] locales = JVLT.getSupportedLocales();
		_string_locale_map = new HashMap<String, Locale>();
		for (int i=0; i<locales.length; i++) {
			_string_locale_map.put(locales[i].getDisplayLanguage(), locales[i]);
			if (locales[i].equals(locale))
				_old_locale = locale;
		}
		if (_old_locale == null)
			_old_locale = Locale.US;
		
		_old_laf = UIManager.getLookAndFeel();
		_string_laf_map = new HashMap<String, String>();
		String class_name;
		String id;
		try {
			class_name = _old_laf.getClass().getName();
			id = _old_laf.getID();
			_string_laf_map.put(id, class_name);
			
			class_name = UIManager.getSystemLookAndFeelClassName();
			id = ((LookAndFeel)Class.forName(class_name).newInstance()).getID();
			_string_laf_map.put(id, class_name);

			class_name = "javax.swing.plaf.metal.MetalLookAndFeel";
			id = ((LookAndFeel)Class.forName(class_name).newInstance()).getID();
			_string_laf_map.put(id, class_name);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		init();
	}
	
	public void updateData() throws InvalidDataException {
		Locale new_locale = (Locale) _string_locale_map.get(
			_locale_cobox.getSelectedItem());
		String new_laf_string = (String) _string_laf_map.get(
			_laf_cobox.getSelectedItem());
		Font new_print_font=_print_font_button.getFontInfo().getFont();
		Font new_html_font=_html_font_button.getFontInfo().getFont();
		Font new_ui_font=_ui_font_button.getFontInfo().getFont();
		Font new_orth_font=_orth_font_button.getFontInfo().getFont();
		Font new_pron_font=_pron_font_button.getFontInfo().getFont();
		boolean new_restore_previously_open = _restore_chbox.isSelected();
		boolean new_input_answer = _input_answer_chbox.isSelected();
		boolean new_match_case = ! _match_case_chbox.isSelected();
		boolean new_play_immediately = _play_immediately_chbox.isSelected();
		String new_default_answer = "";
		if (_default_answer_chbox.isSelected()) {
			String item = _default_answer_cobox.getSelectedItem().toString();
			if (item.equals(GUIUtils.getString("Labels", "yes")))
				new_default_answer = "yes";
			else
				new_default_answer = "no";
		}
		int new_num_batches = _expiration_panel.getNumBatches();
		float new_expiration_factor = _expiration_panel.getExpirationFactor();
		String new_expiration_unit = _expiration_panel.getExpirationUnit();
		Object[] new_displayed_attrs = 
			_displayed_attrs_panel.getSelectedObjects();
		
		Config config = JVLT.getConfig();
		config.setProperty("print_font", new_print_font);
		config.setProperty("html_font", new_html_font);
		config.setProperty("ui_font", new_ui_font);
		config.setProperty("orth_font", new_orth_font);
		config.setProperty("pron_font", new_pron_font);
		config.setProperty("locale", new_locale);
		config.setProperty("restore_previously_open_file",
			String.valueOf(new_restore_previously_open));
		config.setProperty("input_answer",
			String.valueOf(new_input_answer));
		config.setProperty("match_case",
			String.valueOf(new_match_case));
		config.setProperty("look_and_feel", new_laf_string);
		config.setProperty("play_audio_immediately", new_play_immediately);
		config.setProperty("default_answer", new_default_answer);
		config.setProperty("num_batches", new_num_batches);
		config.setProperty("expiration_factor", new_expiration_factor);
		config.setProperty("expiration_unit", new_expiration_unit);
		JVLT.getRuntimeProperties().put("displayed_attributes",
			new_displayed_attrs);
		
		_file_type_panel.save();
			
		if ((! new_html_font.equals(_old_html_font))
			|| (! new_ui_font.equals(_old_ui_font))
			|| (! new_orth_font.equals(_old_orth_font))
			|| (! new_pron_font.equals(_old_pron_font))
			|| (! _old_locale.equals(new_locale))
			|| (! _old_laf.getClass().getName().equals(new_laf_string))
			|| (_old_input_answer != new_input_answer))
				MessageDialog.showDialog(_content_pane,
					MessageDialog.WARNING_MESSAGE,
					GUIUtils.getString("Messages", "restart"));
	}
	
	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("look_and_feel")) {
			Object item = _laf_cobox.getSelectedItem();
			boolean metal_theme = _string_laf_map.get(item).equals(
					"javax.swing.plaf.metal.MetalLookAndFeel");
			_ui_font_button.setEnabled(metal_theme);
		}
		else if (ev.getActionCommand().equals("input_answer")) {
			boolean input_answer = _input_answer_chbox.isSelected();
			_match_case_chbox.setEnabled(input_answer);
			_default_answer_chbox.setEnabled(! input_answer);
			_default_answer_cobox.setEnabled(! input_answer &&
				_default_answer_chbox.isSelected());
		}
		else if (ev.getActionCommand().equals("default_answer")) {
			_default_answer_cobox.setEnabled(
				_default_answer_chbox.isSelected());
		}
	}
	
	private void init() {
		_content_pane = new JPanel();
		
		_print_font_button = new FontChooserButton();
		_print_font_button.setFontInfo(new FontInfo(_old_print_font));
		_print_font_button.setActionCommand("print_font");
		_html_font_button = new FontChooserButton();
		_html_font_button.setFontInfo(new FontInfo(_old_html_font));
		_html_font_button.setActionCommand("html_font");
		_ui_font_button = new FontChooserButton();
		_ui_font_button.setFontInfo(new FontInfo(_old_ui_font));
		_ui_font_button.setActionCommand("ui_font");
		_orth_font_button = new FontChooserButton();
		_orth_font_button.setFontInfo(new FontInfo(_old_orth_font));
		_orth_font_button.setActionCommand("orth_font");
		_pron_font_button = new FontChooserButton();
		_pron_font_button.setFontInfo(new FontInfo(_old_pron_font));
		_pron_font_button.setActionCommand("pron_font");
		
		_laf_cobox = new LabeledComboBox();
		_laf_cobox.setLabel("look_and_feel");
		Iterator<String> it = _string_laf_map.keySet().iterator();
		while (it.hasNext())
			_laf_cobox.addItem(it.next());
		_laf_cobox.addActionListener(this);
		_laf_cobox.setSelectedItem(_old_laf.getID());

		_locale_cobox = new LabeledComboBox();
		_locale_cobox.setLabel("locale");
		Locale[] locales = JVLT.getSupportedLocales();
		for (int i=0; i<locales.length; i++)
			_locale_cobox.addItem(locales[i].getDisplayLanguage());
		_locale_cobox.setSelectedItem (_old_locale.getDisplayLanguage());
	
		_displayed_attrs_panel = new AttributeSelectionPanel();
		_displayed_attrs_panel.setAllowReordering(true);
		_displayed_attrs_panel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			GUIUtils.getString("Labels", "displayed_attributes")));
		MetaData data = _model.getDictModel().getMetaData(Entry.class);
		_displayed_attrs_panel.setAvailableObjects(data.getAttributes());
		_displayed_attrs_panel.setSelectedObjects(_old_displayed_attrs);

		_restore_chbox = new JCheckBox(GUIUtils.createTextAction(this,
			"restore_previously_open"));
		_restore_chbox.setSelected(_old_restore_previously_open);
		
		_input_answer_chbox = new JCheckBox(GUIUtils.createTextAction(this,
			"input_answer"));
		_input_answer_chbox.setSelected(_old_input_answer);
		_match_case_chbox = new JCheckBox(GUIUtils.createTextAction(this,
			"ignore_case"));
		_match_case_chbox.setEnabled(_old_input_answer);
		_match_case_chbox.setSelected(! _old_match_case);
		_default_answer_chbox = new JCheckBox(GUIUtils.createTextAction(this,
			"default_answer"));
		_default_answer_chbox.setEnabled(! _old_input_answer);
		_default_answer_chbox.setSelected(! _old_default_answer.equals(""));
		_default_answer_cobox = new LabeledComboBox();
		_default_answer_cobox.setLabel("default_answer_choice");
		_default_answer_cobox.setEnabled(! _old_input_answer &&
			! _old_default_answer.equals(""));
		_default_answer_cobox.addItem(GUIUtils.getString("Labels", "yes"));
		_default_answer_cobox.addItem(GUIUtils.getString("Labels", "no"));
		if (_old_default_answer.equals("no"))
			_default_answer_cobox.setSelectedItem(
				GUIUtils.getString("Labels", "no"));
		else
			_default_answer_cobox.setSelectedItem(
				GUIUtils.getString("Labels", "yes"));
		
		_file_type_panel = new FileTypePanel();
		_file_type_panel.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), GUIUtils.getString("Labels", "file_types")));
		_play_immediately_chbox = new JCheckBox(GUIUtils.createTextAction(
			this, "play_audio_immediately"));
		_play_immediately_chbox.setSelected(_old_play_immediately);
		
		JPanel appearance_panel = new JPanel();
		appearance_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0,0,1.0,0.0);
		appearance_panel.add(_laf_cobox.getLabel(), cc);
		cc.update(1,0,1.0,0.0);
		appearance_panel.add(_laf_cobox, cc);
		cc.update(0,1,1.0,0.0);
		appearance_panel.add(_ui_font_button.getJLabel(), cc);
		cc.update(1,1,1.0,0.0);
		appearance_panel.add(_ui_font_button, cc);
		cc.update(0,2,1.0,0.0);
		appearance_panel.add(_html_font_button.getJLabel(), cc);
		cc.update(1,2,1.0,0.0);
		appearance_panel.add(_html_font_button, cc);
		cc.update(0,3,1.0,0.0);
		appearance_panel.add(_orth_font_button.getJLabel(), cc);
		cc.update(1,3,1.0,0.0);
		appearance_panel.add(_orth_font_button, cc);
		cc.update(0,4,1.0,0.0);
		appearance_panel.add(_pron_font_button.getJLabel(), cc);
		cc.update(1,4,1.0,0.0);
		appearance_panel.add(_pron_font_button, cc);
		cc.update(0,5,1.0,0.0);
		appearance_panel.add(_locale_cobox.getLabel(), cc);
		cc.update(1,5,1.0,0.0);
		appearance_panel.add(_locale_cobox, cc);
		cc.update(0,6,1.0,0.0,2,1);
		appearance_panel.add(_displayed_attrs_panel, cc);
		cc.update(0,7,0.0,1.0);
		appearance_panel.add(Box.createVerticalGlue(), cc);
		
		JPanel printing_panel = new JPanel();
		printing_panel.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), GUIUtils.getString("Labels", "printing")));
		printing_panel.setLayout(new GridBagLayout());
		cc.reset();
		cc.update(0, 0, 1.0, 0.0);
		printing_panel.add(_print_font_button.getJLabel(), cc);
		cc.update(1, 0, 1.0, 0.0);
		printing_panel.add(_print_font_button, cc);
		
		JPanel quizzes_panel = new JPanel();
		quizzes_panel.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), GUIUtils.getString("Labels", "quizzes")));
		quizzes_panel.setLayout(new GridBagLayout());
		JPanel default_answer_panel = new JPanel();
		default_answer_panel.setLayout(new GridLayout());
		default_answer_panel.add(_default_answer_cobox.getLabel());
		default_answer_panel.add(_default_answer_cobox);
		cc.update(0, 0, 1.0, 0.0);
		quizzes_panel.add(_input_answer_chbox, cc);
		cc.update(0, 1, 1.0, 0.0);
		cc.insets.left = 15;
		quizzes_panel.add(_match_case_chbox, cc);
		cc.update(0, 2, 1.0, 0.0);
		quizzes_panel.add(_default_answer_chbox, cc);
		cc.update(0, 3, 1.0, 0.0);
		cc.insets.left = 2;
		quizzes_panel.add(default_answer_panel, cc);
		
		_expiration_panel = new ExpirationTimePanel();
		_expiration_panel.setNumBatches(_old_num_batches);
		_expiration_panel.setExpirationFactor(_old_expiration_factor);
		_expiration_panel.setExpirationUnit(_old_expiration_unit);
		_expiration_panel.setBorder(new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED),
			GUIUtils.getString("Labels", "expiration_time")));
		
		JPanel general_panel = new JPanel();
		general_panel.setLayout(new GridBagLayout());
		cc.update(0, 0, 1.0, 0.0);
		general_panel.add(_restore_chbox, cc);
		cc.update(0, 1, 1.0, 0.0);
		general_panel.add(printing_panel, cc);
		cc.update(0, 2, 1.0, 0.0);
		general_panel.add(quizzes_panel, cc);
		cc.update(0, 3, 1.0, 1.0);
		general_panel.add(_expiration_panel, cc);
		cc.update(0, 4, 0.0, 1.0);
		general_panel.add(Box.createVerticalGlue(), cc);
		
		JPanel multimedia_panel = new JPanel();
		multimedia_panel.setLayout(new GridBagLayout());
		cc.update(0,0,1.0,0.0);
		multimedia_panel.add(_play_immediately_chbox, cc);
		cc.update(0,1,1.0,1.0);
		multimedia_panel.add(_file_type_panel, cc);
		
		CustomTabbedPane tab_pane = new CustomTabbedPane();
		tab_pane.addTab("appearance", appearance_panel);
		tab_pane.addTab("general", general_panel);
		tab_pane.addTab("multimedia_files", multimedia_panel);
		
		_content_pane.setLayout(new GridLayout());
		_content_pane.add(tab_pane);
	}
}

class FileTypePanel extends JPanel
	implements ListSelectionListener, ActionListener {
	private static final long serialVersionUID = 1L;
		
	private TreeMap<String, MultimediaFile> _extensions;
	private TreeSet<String> _default_extensions;
	
	private Action _add_action;
	private Action _edit_action;
	private Action _remove_action;
	private SortableTable<MultimediaFile> _table;
	private SortableTableModel<MultimediaFile> _table_model;
	
	public FileTypePanel() {
		_extensions = new TreeMap<String, MultimediaFile>();
		_default_extensions = new TreeSet<String>();
		
		load();
		init();
		update();
	}
	
	public void load() {
		_extensions.clear();
		_default_extensions.clear();
		String[] exts = MultimediaUtils.AUDIO_FILE_EXTENSIONS;
		for (int i=0; i<exts.length; i++) {
			_extensions.put(exts[i], new AudioFile("."+exts[i]));
			_default_extensions.add(exts[i]);
		}
		
		exts = MultimediaUtils.IMAGE_FILE_EXTENSIONS;
		for (int i=0; i<exts.length; i++) {
			_extensions.put(exts[i], new ImageFile("."+exts[i]));
			_default_extensions.add(exts[i]);
		}
		
		exts = JVLT.getConfig().getStringListProperty("custom_extensions",
			new String[0]);
		for (int i=0; i<exts.length; i++) {
			String[] ext_prop = JVLT.getConfig().getStringListProperty(
				"extension_"+exts[i], new String[0]);
			CustomMultimediaFile f = new CustomMultimediaFile(
				"."+exts[i], Integer.parseInt(ext_prop[0]));
			f.setCommand(ext_prop[1]);
			_extensions.put(exts[i], f);
		}
	}
	
	public void save() {
		// Remove all extension_* keys from the config file
		Iterator<Object> it = JVLT.getConfig().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			if (key.startsWith("extension_"))
				it.remove();
		}
		
		// Insert all custom file types
		Iterator<String>it2 = _extensions.keySet().iterator();
		TreeSet<String> custom_extensions = new TreeSet<String>();
		while (it2.hasNext()) {
			String extension = it2.next();
			MultimediaFile file = (MultimediaFile) _extensions.get(extension);
			if (file instanceof CustomMultimediaFile) {
				CustomMultimediaFile cmf = (CustomMultimediaFile) file;
				JVLT.getConfig().setProperty("extension_"+extension,
					new Object[]{new Integer(cmf.getType()),cmf.getCommand()});
				custom_extensions.add(extension);
			}
		}
		JVLT.getConfig().setProperty("custom_extensions",
			custom_extensions.toArray());
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if (! e.getValueIsAdjusting())
			update();
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("add")) {
			FileTypeDialogData data = new FileTypeDialogData(
				new CustomMultimediaFile(MultimediaFile.OTHER_FILE),
				_default_extensions);
			int result = CustomDialog.showDialog(data, this,
				GUIUtils.getString("Labels", "edit_file_type"));
			if (result == CustomDialog.OK_OPTION) {
				MultimediaFile file = data.getFile();
				_table_model.addObject(file);
				_extensions.put(
					FileUtils.getFileExtension(file.getFileName()), file);
			}
		}
		else if (ev.getActionCommand().equals("edit")) {
			List<MultimediaFile> objs = _table.getSelectedObjects();
			MultimediaFile file = objs.get(0);
			FileTypeDialogData data = new FileTypeDialogData(
				file, _default_extensions);
			int result = CustomDialog.showDialog(data, this,
				GUIUtils.getString("Labels", "edit_file_type"));
			if (result == CustomDialog.OK_OPTION) {
				_table_model.removeObject(file);
				file = data.getFile();
				_table_model.addObject(file);
				_extensions.put(
					FileUtils.getFileExtension(file.getFileName()), file);
			}
		}
		else if (ev.getActionCommand().equals("remove")) {
			List<MultimediaFile> objs = _table.getSelectedObjects();
			MultimediaFile file = objs.get(0);
			_table_model.removeObject(file);
			_extensions.remove(FileUtils.getFileExtension(file.getFileName()));
		}
	}
	
	private void init() {
		_add_action = GUIUtils.createTextAction(this, "add");
		_edit_action = GUIUtils.createTextAction(this, "edit");
		_remove_action = GUIUtils.createTextAction(this, "remove");
		
		MultimediaFileMetaData data = new MultimediaFileMetaData();
		_table_model = new SortableTableModel<MultimediaFile>(data);
		_table_model.setColumnNames(data.getAttributeNames());
		_table_model.setObjects(_extensions.values());
		_table_model.setSortingDirective(
			new SortableTableModel.Directive(0, SortableTableModel.ASCENDING));
		_table = new SortableTable<MultimediaFile>(_table_model);
		_table.getSelectionModel().addListSelectionListener(this);
		_table.getSelectionModel().setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrpane = new JScrollPane();
		scrpane.getViewport().setView(_table);
		scrpane.setPreferredSize(new Dimension(250,150));
		
		ButtonPanel button_panel = new ButtonPanel(
			SwingConstants.VERTICAL, SwingConstants.TOP);
		button_panel.addButtons(new JButton[] {new JButton(_add_action),
			new JButton(_edit_action), new JButton(_remove_action)});
		
		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0,0,1.0,1.0);
		add(scrpane, cc);
		cc.update(1,0,0.0,1.0);
		add(button_panel, cc);
	}
	
	private void update() {
		List<MultimediaFile> objs = _table.getSelectedObjects();
		_edit_action.setEnabled(objs.size() > 0);
		_remove_action.setEnabled(objs.size() > 0);
		if (objs.size() > 0) {
			MultimediaFile file = objs.get(0);
			_remove_action.setEnabled(! _default_extensions.contains(
				FileUtils.getFileExtension(file.getFileName())));
		}
	}
}

class MultimediaFileMetaData extends MetaData {
	public MultimediaFileMetaData() {
		super(MultimediaFile.class);
	}
	
	protected void init() {
		addAttribute(new DefaultAttribute("Extension", String.class) {
			public Object getValue(Object o) {
				MultimediaFile m = (MultimediaFile) o;
				return FileUtils.getFileExtension(m.getFileName());
			}
		});
		addAttribute(new DefaultAttribute("Type", String.class) {
			public Object getValue(Object o) {
				return getValue(o, "TypeString"); }
		});
		addAttribute(new DefaultAttribute("Command", String.class) {
			public Object getValue(Object o) {
				if (o instanceof CustomMultimediaFile)
					return getValue(o, "Command");
				else
					return "-";
			}
		});
	}
}

class FileTypeDialogData extends CustomDialogData implements ActionListener {
	private Set<String> _default_extensions;
	private MultimediaFile _file;
	
	private Action _browse_action;
	private CustomTextField _extension_field;
	private JCheckBox _jvlt_plays_box;
	private LabeledComboBox _type_box;
	private CustomTextField _command_field;
	
	public FileTypeDialogData(
			MultimediaFile file, Set<String> default_extensions) {
		_file = file;
		_default_extensions = default_extensions;
		
		init();
		update();
	}
	
	public MultimediaFile getFile() { return _file; }
	
	public void updateData() throws InvalidDataException {
		String extension = FileUtils.getFileExtension(_file.getFileName());
		String new_ext = _extension_field.getText();
		boolean default_type = _default_extensions.contains(extension);
		if (! default_type && _default_extensions.contains(new_ext))
			throw new InvalidDataException(GUIUtils.getString("Messages",
				"overwrite_extension", new String[]{new_ext}));
		
		int type;
		if (_type_box.getSelectedItem().toString().equals(
			GUIUtils.getString("Labels", "audio_file")))
			type = MultimediaFile.AUDIO_FILE;
		else if (_type_box.getSelectedItem().toString().equals(
			GUIUtils.getString("Labels", "image_file")))
			type = MultimediaFile.IMAGE_FILE;
		else
			type = MultimediaFile.OTHER_FILE;
		
		if (_jvlt_plays_box.isSelected()) {
			if (type == MultimediaFile.AUDIO_FILE)
				_file = new AudioFile("."+new_ext);
			else // if (type == MultimediaFile.IMAGE_FILE)
				_file = new ImageFile("."+new_ext);
		}
		else {
			CustomMultimediaFile file =
				new CustomMultimediaFile("."+new_ext, type);
			file.setCommand(_command_field.getText());
			_file = file;
		}
	}
	
	public void actionPerformed(ActionEvent ev)	{
		if (ev.getActionCommand().equals("jvlt_plays"))
			update();
		else if (ev.getActionCommand().equals("browse")) {
			JFileChooser chooser = new JFileChooser();
			int val = chooser.showOpenDialog(_content_pane);
			if (val == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				_command_field.setText(f.getAbsolutePath());
			}
		}
	}
	
	private void init()	{
		_browse_action = GUIUtils.createTextAction(this, "browse");
		_extension_field = new CustomTextField(20);
		_extension_field.setActionCommand("extension");
		_command_field = new CustomTextField(20);
		_command_field.setActionCommand("command");
		_jvlt_plays_box = new JCheckBox(GUIUtils.createTextAction(
			this, "jvlt_plays"));
		_jvlt_plays_box.addActionListener(this);
		_type_box = new LabeledComboBox();
		_type_box.setLabel("type");
		
		_content_pane = new JPanel();
		_content_pane.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 0.0, 0.0);
		_content_pane.add(_extension_field.getLabel(), cc);
		cc.update(1, 0, 0.0, 0.0);
		_content_pane.add(_extension_field, cc);
		cc.update(2, 0, 1.0, 0.0);
		_content_pane.add(Box.createHorizontalGlue(), cc);
		cc.update(0, 1, 0.0, 0.0);
		_content_pane.add(_type_box.getLabel(), cc);
		cc.update(1, 1, 0.0, 0.0);
		_content_pane.add(_type_box, cc);
		cc.update(2, 1, 0.0, 0.0);
		_content_pane.add(Box.createHorizontalGlue(), cc);
		cc.update(0, 2, 0.0, 0.0, 3, 1);
		_content_pane.add(_jvlt_plays_box, cc);
		cc.update(0, 3, 0.0, 0.0, 1, 1);
		_content_pane.add(_command_field.getLabel(), cc);
		cc.update(1, 3, 0.0, 0.0);
		cc.fill = CustomConstraints.HORIZONTAL;
		_content_pane.add(_command_field, cc);
		cc.update(2, 3, 0.0, 0.0);
		_content_pane.add(new JButton(_browse_action), cc);
		cc.update(0, 4, 0.0, 0.0);
		_content_pane.add(new JLabel(GUIUtils.getString("Labels",
			"command_description")), cc);

		String extension = FileUtils.getFileExtension(_file.getFileName());
		boolean default_type = _default_extensions.contains(extension);
		_type_box.addItem(GUIUtils.getString("Labels", "audio_file"));
		_type_box.addItem(GUIUtils.getString("Labels", "image_file"));
		_type_box.addItem(GUIUtils.getString("Labels", "other_file"));
		_type_box.setSelectedItem(_file.getTypeString());
		_extension_field.setText(extension);
		_extension_field.setEnabled(! default_type);
		_type_box.setEnabled(! default_type);
		if (! default_type) {
			_jvlt_plays_box.setSelected(false);
			_jvlt_plays_box.setEnabled(false);
		}
		else {
			_jvlt_plays_box.setSelected(
				!(_file instanceof CustomMultimediaFile));
		}
		if (_file instanceof CustomMultimediaFile) {
			CustomMultimediaFile f = (CustomMultimediaFile) _file;
			_command_field.setText(f.getCommand());
		}
	}
	
	private void update() {
		_browse_action.setEnabled(! _jvlt_plays_box.isSelected());
		_command_field.setEnabled(! _jvlt_plays_box.isSelected());
	}
}

class ExpirationTimePanel extends JPanel {
	private class ActionHandler implements ActionListener, ChangeListener {
		public void actionPerformed(ActionEvent e) { updateTable(); }
		public void stateChanged(ChangeEvent e) { updateTable(); }
	}

	private static final long serialVersionUID = 1L;

	private DefaultTableModel _table_model;
	private SpinnerNumberModel _batches_spinner_model;
	private SpinnerNumberModel _factor_spinner_model;
	private LabeledComboBox _unit_box;
	
	public ExpirationTimePanel() {
		// Create table
		_table_model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int col) { return false; }
		};
		_table_model.addColumn(GUIUtils.getString("Labels", "batch"));
		_table_model.addColumn(GUIUtils.getString("Labels", "duration"));
		JTable table = new JTable(_table_model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setPreferredScrollableViewportSize(new Dimension(100,100));
		JScrollPane pane = new JScrollPane(table);
		
		_batches_spinner_model = new SpinnerNumberModel();
		_batches_spinner_model.setStepSize(new Integer(1));
		_batches_spinner_model.setMinimum(new Integer(1));
		_batches_spinner_model.setMaximum(new Integer(20));
		_batches_spinner_model.setValue(new Integer(7));
		LabeledSpinner batches_spinner = new LabeledSpinner(
			_batches_spinner_model);
		batches_spinner.addChangeListener(new ActionHandler());
		batches_spinner.setLabel("num_batches");

		_factor_spinner_model = new SpinnerNumberModel();
		_factor_spinner_model.setStepSize(new Double(0.5));
		_factor_spinner_model.setMinimum(new Double(1.0));
		_factor_spinner_model.setMaximum(new Double(10));
		_factor_spinner_model.setValue(new Double(3));
		LabeledSpinner factor_spinner = new LabeledSpinner(
			_factor_spinner_model);
		factor_spinner.addChangeListener(new ActionHandler());
		factor_spinner.setLabel("expiration_factor");
		
		_unit_box = new LabeledComboBox();
		_unit_box.setLabel("unit");
		_unit_box.addItem(GUIUtils.getString("Labels", "days"));
		_unit_box.addItem(GUIUtils.getString("Labels", "hours"));
		_unit_box.addActionListener(new ActionHandler());

		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		add(batches_spinner.getLabel(), cc);
		cc.update(1, 0, 0.0, 0.0);
		add(batches_spinner, cc);
		cc.update(0, 1, 1.0, 0.0);
		add(factor_spinner.getLabel(), cc);
		cc.update(1, 1, 0.0, 0.0);
		add(factor_spinner, cc);
		cc.update(0, 2, 1.0, 0.0);
		add(_unit_box.getLabel(), cc);
		cc.update(1, 2, 0.0, 0.0);
		add(_unit_box, cc);
		cc.update(0, 3, 1.0, 1.0, 2, 1);
		add(pane, cc);
		
		updateTable();
	}
	
	public int getNumBatches() {
		return _batches_spinner_model.getNumber().intValue(); }
	
	public void setNumBatches(int num) {
		_batches_spinner_model.setValue(new Integer(num)); }
	
	public float getExpirationFactor() {
		return _factor_spinner_model.getNumber().floatValue(); }
		
	public void setExpirationFactor(float factor) {
		_factor_spinner_model.setValue(new Double(factor)); }
		
	public String getExpirationUnit() {
		if (_unit_box.getSelectedItem().equals(
			GUIUtils.getString("Labels", "hours")))
			return Entry.UNIT_HOURS;
		else
			return Entry.UNIT_DAYS;
	}
	
	public void setExpirationUnit(String unit) {
		if (unit.equals(Entry.UNIT_HOURS))
			_unit_box.setSelectedItem(GUIUtils.getString("Labels", "hours"));
		else
			_unit_box.setSelectedItem(GUIUtils.getString("Labels", "days"));
	}
		
	private void updateTable() {
		int num_batches = _batches_spinner_model.getNumber().intValue();
		double factor = _factor_spinner_model.getNumber().doubleValue();
		Object[][] new_data = new Object[num_batches][2];
		for (int i=0; i<num_batches; i++) {
			new_data[i][0] = String.valueOf(i+1);
			if (_unit_box.getSelectedItem().equals(
				GUIUtils.getString("Labels", "hours")))
				new_data[i][1] = getFormattedDuration(Math.pow(factor, i));
			else
				new_data[i][1] = getFormattedDuration(Math.pow(factor, i)*24);
		}
		
		Object[] columns = {
			GUIUtils.getString("Labels", "batch"),
			GUIUtils.getString("Labels", "duration") };
			
		_table_model.setDataVector(new_data, columns);
	}
	
	private String getFormattedDuration(double hours) {
		ChoiceFormatter formatter = new ChoiceFormatter(
			GUIUtils.getString("Labels", "num_days"));
		int num_days = (int) hours/24;
		String days_str = formatter.format(num_days);
		formatter.applyPattern(GUIUtils.getString("Labels", "num_hours"));
		int num_hours = (int) (hours - 24*num_days);
		if (num_hours > 0)
			return days_str + " " + formatter.format(num_hours);
		else
			return days_str;
	}
}

