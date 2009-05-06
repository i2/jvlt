package net.sourceforge.jvlt;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTMLDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import net.sourceforge.jvlt.event.DictUpdateListener;
import net.sourceforge.jvlt.event.FilterListener;
import net.sourceforge.jvlt.event.ModelResetEventListener;
import net.sourceforge.jvlt.event.SelectionListener;
import net.sourceforge.jvlt.event.SelectionNotifier;
import net.sourceforge.jvlt.event.UndoableActionListener;

public class JVLTUI implements ActionListener, UndoableActionListener,
	DictUpdateListener, SelectionListener, ModelResetEventListener {

	private class ChangeHandler implements ChangeListener {
		public void stateChanged(ChangeEvent ev) {
			Component tab = _tab_pane.getSelectedComponent();
			if (tab == _quiz_wizard.getContent())
				JVLTUI.this._main_frame.getRootPane().setDefaultButton(
					_quiz_wizard.getDefaultButton());
			else
				JVLTUI.this._main_frame.getRootPane().setDefaultButton(null);
		}
	}
	
	
	private class EntryFilterListener implements FilterListener<Entry> {
		public void filterApplied(FilterEvent<Entry> event) {
			_matched_entries = event.getMatchedItems();
			updateStatusBar();
		}
	}

	private class ExampleFilterListener implements FilterListener<Example> {
		public void filterApplied(FilterEvent<Example> event) {
			_matched_examples = event.getMatchedItems();
			updateStatusBar();
		}
	}
	
	private Dict _dict;
	private JVLTModel _model;
	private Collection<Entry> _matched_entries;
	private Collection<Example> _matched_examples;
	private LinkedList<String> _recent_files;

	private EntryPanel _entry_tab;
	private ExamplePanel _example_tab;
	private Wizard _quiz_wizard;
	private JFrame _main_frame;
	private CustomAction _undo_action;
	private CustomAction _redo_action;
	private CustomAction _toolbar_redo_action;
	private CustomAction _toolbar_undo_action;
	private JLabel _left_status_label;
	private JMenu _recent_files_menu;
	private CustomTabbedPane _tab_pane;

	public JVLTUI(JVLTModel model) {
		_model = model;
		_matched_entries = null;
		_matched_examples = null;
		_recent_files = new LinkedList<String>();
		String[] file_names = JVLT.getConfig().getStringListProperty(
			"recent_files", new String[0]);
		for (int i=0; i<file_names.length; i++)
			_recent_files.add(file_names[i]);
		
		// Create empty dictionary. Will be replaced in method dictUpdated().
		_dict = new Dict();

		// Load runtime properties
		loadRuntimeProperties();
		
		// Listen to model events
		_model.getDictModel().addUndoableActionListener(this);
		_model.getDictModel().addModelResetEventListener(this);
		_model.getDictModel().addDictUpdateListener(this);
		_model.getQueryModel().addUndoableActionListener(this);
		_model.getQueryModel().addModelResetEventListener(this);
		_model.getQueryModel().addDictUpdateListener(this);
	}

	public void run() {
		_main_frame.pack();
		_main_frame.setVisible(true);

		Config conf = JVLT.getConfig();
		if (conf.getBooleanProperty("restore_previously_open_file", false)) {
			String dict_file_name = conf.getProperty("dict_file", "dict.jvlt");
			if (! dict_file_name.equals(""))
				load(dict_file_name);
		}
		
		// If no default dictionary was specified or if loading it failed,
		// start with an empty one.
		if (_model.getDict() == null)
			_model.newDict();
	}
	
	public void actionPerformed (ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("new") || command.equals("open")
			|| command.startsWith("open_")) {
			if (! finishQuiz())
				return;
			
			//----------
			// Ask whether changes should be saved.
			//----------
			if (_model.isDataModified()) {
				int result = GUIUtils.showSaveDiscardCancelDialog(
						_main_frame, "save_changes");
				if (result == JOptionPane.YES_OPTION) {
					if (! save())
						return;
				}
				else if (result == JOptionPane.CANCEL_OPTION)
					return;
				// Proceed if result is JOptionPane.NO_OPTION.
			}
		}

		if (command.equals("new")) {
			_model.newDict();
		} else if (command.equals("open")) {
			JFileChooser chooser = new DictFileChooser(
				_model.getDictFileName());
			int val = chooser.showOpenDialog(_main_frame);
			if (val == JFileChooser.APPROVE_OPTION) {
				String file_name = chooser.getSelectedFile().getPath();
				load(file_name);
			}
		} else if (command.startsWith("open_")) {
			int index = Integer.parseInt(command.substring(command.length()-1));
			load(_recent_files.get(index));
		} else if (command.equals("save")) {
			save();
		} else if (command.equals("save_as")) {
			saveAs();
		} else if (command.equals("print_file")) {
			TablePrinter printer = getTablePrinter();
			print(printer);
		} else if (command.equals("print_preview")) {
			TablePrinter printer = getTablePrinter();
			try {
				printer.renderPages((Graphics2D) _main_frame.getGraphics());
				PrintPreviewDialog dlg = new PrintPreviewDialog(_main_frame,
					GUIUtils.getString("Labels", "print_preview"), printer);
				dlg.pack();
				dlg.setVisible(true);
				if (dlg.getOption() == PrintPreviewDialog.PRINT_OPTION)
					print(printer);
			} catch (PrinterException ex) {
				MessageDialog.showDialog(_main_frame,
					MessageDialog.WARNING_MESSAGE, ex.getMessage());
			}
		} else if (command.equals("import")) {
			ImportDialog dialog = new ImportDialog(_main_frame, _model);
			GUIUtils.showDialog(_main_frame, dialog);
		} else if (command.equals("export")) {
			ExportDialog dialog = new ExportDialog(_main_frame, _model);
			GUIUtils.showDialog(_main_frame, dialog);
		} else if (command.equals("quit")) {
			tryToQuit();
		} else if (command.equals("undo")) {
			_model.undo();
		} else if (command.equals("redo")) {
			_model.redo();
		} else if (command.equals("dict_properties")) {
			if (! finishQuiz())
				return;
			
			PropertiesDialogData ddata = new PropertiesDialogData(
					_dict.getLanguage());
			CustomDialog dlg = new CustomDialog(ddata, _main_frame,
				GUIUtils.getString("Labels", "dict_properties"));
			GUIUtils.showDialog(_main_frame, dlg);
			if (dlg.getStatus() == CustomDialog.OK_OPTION) {
				LanguageChangeAction lca = new LanguageChangeAction(
					_dict.getLanguage(), ddata.getLanguage());
				lca.setMessage(
					GUIUtils.getString("Actions", "change_language"));
				_model.getDictModel().executeAction(lca);
			}
		} else if (command.equals("reset_stats")) {
			reset_stats();
		} else if (command.equals("settings")) {
			SettingsDialogData ddata = new SettingsDialogData(_model);
			CustomDialog dlg = new CustomDialog(ddata, _main_frame,
				GUIUtils.getString("Labels", "settings"));
			GUIUtils.showDialog(_main_frame, dlg);
		} else if (command.equals("help")) {
			Locale locale = Locale.getDefault();
			URL url = JVLTUI.class.getResource("/doc/" + locale.toString()
					+ "/doc.html");
			if (url == null)
				url = JVLTUI.class.getResource("/doc/default/doc.html");
			
			BrowserDialog dlg = new BrowserDialog(_main_frame, url);
			GUIUtils.showDialog(_main_frame, dlg);
		} else if (command.equals("about")) {
			AboutDialog dlg = new AboutDialog(_main_frame);
			GUIUtils.showDialog(_main_frame, dlg);
		}
	}
	
	public void actionPerformed(UndoableActionEvent event) {
		updateMenu();
		updateTitle();
	}
	
	public void modelResetted(ModelResetEvent event) {
		if (event.getType() == ModelResetEvent.RESET_ALL)
			updateMenu();
		
		updateTitle();
	}
	
	public synchronized void dictUpdated(DictUpdateEvent e) {
		if (e instanceof NewDictDictUpdateEvent) {
			_dict = ((NewDictDictUpdateEvent) e).getDict();
			updateMenu();
			updateTitle();
		}
	}

	public void objectSelected(SelectionEvent e) {
		Object obj = e.getElement();
		if (obj instanceof Example)
			_tab_pane.setSelectedComponent(_example_tab);
		else if (obj instanceof Entry || obj instanceof Sense)
			_tab_pane.setSelectedComponent(_entry_tab);
		else if (obj instanceof AudioFile) {
			AudioFile file = (AudioFile) obj;
			try { file.play(); }
			catch (IOException ex) {
				String msg = GUIUtils.getString("Messages", "loading_failed");
				MessageDialog.showDialog(_main_frame,
					MessageDialog.ERROR_MESSAGE, msg, ex.getMessage());
			}
		}
		else if (obj instanceof ImageFile) {
			ImageFile file = (ImageFile) obj;
			try { file.show(_main_frame); }
			catch (IOException ex) {
				String msg = GUIUtils.getString("Messages", "loading_failed");
				MessageDialog.showDialog(_main_frame,
					MessageDialog.ERROR_MESSAGE, msg, ex.getMessage());
			}
		}
		else if (obj instanceof CustomMultimediaFile) {
			CustomMultimediaFile cmf = (CustomMultimediaFile) obj;
			try { cmf.play(); }
			catch (IOException ex) {
				String msg = GUIUtils.getString("Messages", "loading_failed");				
				MessageDialog.showDialog(_main_frame,
					MessageDialog.ERROR_MESSAGE, msg, ex.getMessage());
			}
		}
	}
	
	private void initUI() {
		_main_frame = new JFrame ();
		_main_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_main_frame.addWindowListener (new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				tryToQuit();
			}
		});
		URL url = JVLTUI.class.getResource("/images/jvlt.png");
		_main_frame.setIconImage(Toolkit.getDefaultToolkit().getImage(url));
		
		// Toolbar actions
		_toolbar_undo_action = GUIUtils.createIconAction(this, "undo");
		_toolbar_redo_action = GUIUtils.createIconAction(this, "redo");
		CustomAction toolbar_new_action
			= GUIUtils.createIconAction(this, "new");
		CustomAction toolbar_open_action
			= GUIUtils.createIconAction(this, "open");
		CustomAction toolbar_save_action
			= GUIUtils.createIconAction(this, "save");
			
		// File menu
		CustomAction new_action=GUIUtils.createTextAction(this, "new");
		CustomAction open_action=GUIUtils.createTextAction(this, "open");
		CustomAction open_recent_action=GUIUtils.createTextAction(
			this, "open_recent");
		CustomAction save_action=GUIUtils.createTextAction(this, "save");
		CustomAction save_as_action=GUIUtils.createTextAction(this, "save_as");
		CustomAction print_preview_action=GUIUtils.createTextAction(
			this, "print_preview");
		CustomAction print_action=GUIUtils.createTextAction(this, "print_file");
		CustomAction import_action=GUIUtils.createTextAction(this, "import");
		CustomAction export_action=GUIUtils.createTextAction(this, "export");
		CustomAction quit_action=GUIUtils.createTextAction(this, "quit");
		
		// Edit menu
		_undo_action=GUIUtils.createTextAction(this, "undo");
		_undo_action.putValue(Action.NAME, GUIUtils.getString("Actions",
			"undo", new Object[]{""}).replaceAll("\\$", ""));
		_undo_action.setEnabled(false);
		_redo_action=GUIUtils.createTextAction(this, "redo");
		_redo_action.putValue(Action.NAME, GUIUtils.getString("Actions",
			"redo", new Object[]{""}).replaceAll("\\$", ""));
		_undo_action.setEnabled(false);
		CustomAction properties_action = GUIUtils.createTextAction(
			this, "dict_properties");
		
		// Tools menu
		CustomAction reset_stats_action = GUIUtils.createTextAction(
				this, "reset_stats");
		CustomAction settings_action = GUIUtils.createTextAction(
				this, "settings");
		
		// Help menu
		CustomAction help_action = GUIUtils.createTextAction(this, "help");
		CustomAction about_action = GUIUtils.createTextAction(this, "about");
		
		JMenuItem item;
		JMenuBar menu_bar = new JMenuBar();
		JMenu file_menu = GUIUtils.createMenu("menu_file");
		menu_bar.add(file_menu);
		item = new JMenuItem(new_action);
		item.setIcon(null);
		file_menu.add(item);
		item = new JMenuItem(open_action);
		item.setIcon(null);
		file_menu.add(item);
		_recent_files_menu = new JMenu(open_recent_action);
		file_menu.add(_recent_files_menu);
		file_menu.addSeparator();
		item = new JMenuItem(save_action);
		item.setIcon(null);
		file_menu.add(item);
		file_menu.add(save_as_action);
		file_menu.addSeparator();
		file_menu.add(print_action);
		file_menu.add(print_preview_action);
		file_menu.addSeparator();
		file_menu.add(import_action);
		file_menu.add(export_action);
		file_menu.addSeparator();
		file_menu.add(quit_action);
		JMenu edit_menu = GUIUtils.createMenu("menu_edit");
		menu_bar.add(edit_menu);
		edit_menu.add(_undo_action);
		edit_menu.add(_redo_action);
		edit_menu.addSeparator();
		edit_menu.add(properties_action);
		JMenu tools_menu = GUIUtils.createMenu("menu_tools");
		menu_bar.add(tools_menu);
		tools_menu.add(reset_stats_action);
		tools_menu.addSeparator();
		tools_menu.add(settings_action);
		JMenu help_menu = GUIUtils.createMenu("menu_help");
		menu_bar.add(help_menu);
		help_menu.add(help_action);
		help_menu.add(about_action);
		_main_frame.setJMenuBar(menu_bar);
	
		SelectionNotifier notifier = new SelectionNotifier();
		notifier.addSelectionListener(this);
		_example_tab = new ExamplePanel(_model, notifier);
		_example_tab.loadState(JVLT.getConfig());
		_example_tab.addFilterListener(new ExampleFilterListener());
		_entry_tab = new EntryPanel(_model, notifier);
		_entry_tab.loadState(JVLT.getConfig());
		_entry_tab.addFilterListener(new EntryFilterListener());
		_quiz_wizard = new Wizard(new QuizModel(_model, notifier));
		
		_tab_pane = new CustomTabbedPane();
		_tab_pane.addTab("vocabulary", _entry_tab);
		_tab_pane.addTab("examples", _example_tab);
		_tab_pane.addTab("quiz", _quiz_wizard.getContent());
		_tab_pane.addChangeListener(new ChangeHandler());
		
		//----------
		// Create status bar
		//----------
		JPanel status_bar = new JPanel();
		_left_status_label = new JLabel ("");
		status_bar.setLayout (new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 0.0, 0.0);
		status_bar.add(_left_status_label, cc);
		cc.update(1, 0, 1.0, 0.0);
		status_bar.add(Box.createHorizontalGlue(), cc);
		
		//----------
		// Create tool bar.
		//----------
		JToolBar tool_bar = new JToolBar();
		tool_bar.add(toolbar_new_action);
		tool_bar.add(toolbar_open_action);
		tool_bar.add(toolbar_save_action);
		tool_bar.add(_toolbar_undo_action);
		tool_bar.add(_toolbar_redo_action);
		tool_bar.setFloatable(false);
		tool_bar.add(Box.createHorizontalGlue());
		
		Container cpane = _main_frame.getContentPane();
		cpane.setLayout(new GridBagLayout());
		cc.update(0, 0, 1.0, 0.0);
		cpane.add(tool_bar, cc);
		cc.update(0, 1, 1.0, 1.0);
		cpane.add(_tab_pane, cc);
		cc.update(0, 2, 1.0, 0.0);
		cpane.add(status_bar, cc);
		
		//----------
		// Init data.
		//----------
		updateStatusBar();
		if (_recent_files.size() > 0)
			updateRecentFilesMenu(_recent_files.get(0));
	}
	
	private void showError(String short_message, String long_message) {
		MessageDialog.showDialog(_main_frame, MessageDialog.ERROR_MESSAGE,
			short_message, long_message);
	}
	
	private boolean saveAs() {
		JFileChooser chooser = new DictFileChooser(_model.getDictFileName());
		int val = chooser.showSaveDialog(_main_frame);
		if (val == JFileChooser.APPROVE_OPTION) {
			String file_name = chooser.getSelectedFile().getPath();
			boolean has_suffix = true;
			if (file_name.length() < 5)
				has_suffix = false;
			else if (! file_name.endsWith(".jvlt") &&
				! file_name.endsWith(".JVLT"))
				has_suffix = false;
			if (! has_suffix)
				file_name = file_name + ".jvlt";
			
			File file = new File (file_name);
			boolean write_file = true;
			if (file.exists()) {
				int result = JOptionPane.showConfirmDialog(_main_frame,
					GUIUtils.getString("Messages", "overwrite"),
					GUIUtils.getString("Labels", "confirm"),
					JOptionPane.YES_NO_OPTION);
				if (result != JOptionPane.YES_OPTION)
					write_file = false;
			}
			
			if (write_file) {
				try {
					_model.save(file_name);
					updateTitle();
					updateRecentFilesMenu(file_name);
					
					return true;
				}
				catch (DetailedException ex) {
					showError(ex.getShortMessage(), ex.getLongMessage());
				}
			}
		}
		
		return false;
	}

	private boolean save() {
		if (_model.getDictFileName() == null)
			return saveAs();

		try {
			_model.save();
			updateRecentFilesMenu(_model.getDictFileName());
			return true;
		}
		catch (DetailedException ex) {
			showError(ex.getShortMessage(), ex.getLongMessage());
			return false;
		}
	}

	private void print(TablePrinter printer) {
		final PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(printer);
		if (! job.printDialog())
			return;
		final MessageDialog dlg = new MessageDialog(_main_frame,
			MessageDialog.INFO_MESSAGE,
			GUIUtils.getString("Messages", "printing"), null);
		
		Runnable show_dlg = new Runnable() {
			public void run() {
				GUIUtils.showDialog(_main_frame, dlg);
			}
		};
		final Runnable hide_dlg = new Runnable() {
			public void run() {
				dlg.setVisible(false);
			}
		};
		Runnable print = new Runnable() {
			public void run() {
				try {
					job.print();
				}
				catch (PrinterException ex) {
					ex.printStackTrace();
				}
				SwingUtilities.invokeLater(hide_dlg);
			}
		};
		
		SwingUtilities.invokeLater(show_dlg);
		Thread thread = new Thread(print);
		thread.start();
	}
	
	private void load(String dict_file_name) {
		final String file_name = dict_file_name;
		Runnable load_data = new Runnable() {
			public void run() {
				try {		
					_model.load(file_name);
					updateRecentFilesMenu(file_name);
				}
				catch (DictReaderException e) {
					Exception ex = e.getException();
					if (ex != null && ex instanceof VersionException)
					{
						VersionException ve = (VersionException) ex;
						if (ve.getVersion().compareTo(JVLT.getDataVersion())>0)
							handleLoadException(ve, file_name);
						else {
							String text = GUIUtils.getString(
								"Messages", "convert_file");
							int result = MessageDialog.showDialog(_main_frame,
								MessageDialog.WARNING_MESSAGE,
								MessageDialog.OK_CANCEL_OPTION, text);
							if (result == MessageDialog.OK_OPTION)
								loadVersion(file_name, ve.getVersion());
						}
					}
					else
						handleLoadException(e, file_name);
				}
				catch (Exception ex) {
					handleLoadException(ex, file_name);
				}
			}
		};
		
		Thread thread = new Thread(load_data);
		thread.start();
	}
	
	private void loadVersion(String file, String version) {
		try {
			_model.load(file, version);
			DictUpdater updater = new DictUpdater(version);
			updater.updateDict(_model.getDict());
			updateRecentFilesMenu(file);
		}
		catch (Exception dre) {
			handleLoadException(dre, file);
		}
	}
	
	private void handleLoadException(Exception ex, String file_name) {
		if (ex instanceof DictReaderException) {
			DictReaderException dre = (DictReaderException) ex;
			showError(dre.getShortMessage(), dre.getLongMessage());
		}
		else if (ex instanceof IOException) {
			IOException ioe = (IOException) ex;
			String text = GUIUtils.getString("Messages", "loading_failed");
			showError(text, ioe.getMessage());
		}
		else if (ex instanceof VersionException) {
			VersionException ve = (VersionException) ex;
			String text = GUIUtils.getString("Messages", "version_too_large");
			showError(text, ve.getMessage());
		}
	}
	
	private void tryToQuit() {
		if (! finishQuiz())
			return;
		
		if (! _model.isDataModified())
			exit();
		else {
			int result = GUIUtils.showSaveDiscardCancelDialog(
					_main_frame, "save_changes");
			if (result == JOptionPane.YES_OPTION) {
				if (save())
					exit();
				// Else cancel.
			}
			else if (result == JOptionPane.NO_OPTION)
				exit();
			// Else cancel.
		}
	}
	
	/**
	 * Checks whether there is an unfinished quiz and - if yes - asks
	 * the user whether to save or discard the quiz results, or to cancel.
	 * @return false if the user selects "Cancel", and true otherwise.
	 */
	private boolean finishQuiz() {
		QuizModel model = (QuizModel) _quiz_wizard.getModel();
		if (model.existsUnfinishedQuiz()) {
			int result = GUIUtils.showSaveDiscardCancelDialog(
					_main_frame, "save_quiz");
			if (result == JOptionPane.YES_OPTION)
				model.saveQuizResults();
			
			if (result == JOptionPane.YES_OPTION
				|| result == JOptionPane.NO_OPTION)
				return true;
			else
				return false;
		}
		else
			return true;
	}
	
	private void exit() {
		Config conf = JVLT.getConfig();
		
		//-----
		// Save dictionary file name
		//-----
		String dict_file_name = _model.getDictFileName();
		if (dict_file_name == null)
			conf.put("dict_file", "");
		else
			conf.put("dict_file", dict_file_name);
		
		//-----
		// Save list of recent files
		//-----
		conf.setProperty("recent_files", _recent_files.toArray(new String[0]));
		
		//-----
		// Save table columns.
		//-----
		_entry_tab.saveState(conf);
		_example_tab.saveState(conf);
	
		// Save runtime properties
		saveRuntimeProperties();

		try {
			JVLT.saveConfig();
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		System.exit(0);
	}
	
	private void reset_stats() {
		ResetStatsDialogData data = new ResetStatsDialogData(
				_dict.getEntryCount(), _matched_entries.size());
		int result = CustomDialog.showDialog(data, _main_frame,
				GUIUtils.getString("Labels", "reset_stats")); 
		if (result == CustomDialog.OK_OPTION) {
			Collection<Entry> entries;
			if (data.resetAllEntries())
				entries = _dict.getEntries();
			else
				entries = _matched_entries;
			
			if (entries.size() == 0)
				return;
			
			ArrayList<EditEntryAction> actions
				= new ArrayList<EditEntryAction>();
			for (Iterator<Entry> it=entries.iterator(); it.hasNext(); ) {
				Entry orig = it.next();
				Entry modified = (Entry) orig.clone();
				modified.resetStats();
				actions.add(new EditEntryAction(orig, modified));
			}
			EditEntriesAction action = new EditEntriesAction(
					actions.toArray(new EditEntryAction[0]));
			action.setMessage(GUIUtils.getString("Actions", "edit_entries",
					new Object[] { new Integer(entries.size()) }));
			_model.getDictModel().executeAction(action);
		}
	}
	
	private TablePrinter getTablePrinter() {
		TablePrinter printer = new TablePrinter();
		printer.setDataModel(_entry_tab.getTableModel());
		
		// Set column widths
		Config conf = JVLT.getConfig();
		_entry_tab.saveState(conf);
		double[] col_widths = conf.getNumberListProperty(
			"column_widths", new double[0]);
		double total_width = 0.0;
		for (int i=0; i<col_widths.length; i++)
			total_width += col_widths[i];
		for (int i=0; i<col_widths.length; i++)
			printer.setColWidth(i, (int) (100*col_widths[i]/total_width));
		
		return printer;
	}
	
	private void updateStatusBar() {
		int total_entries = _dict.getEntryCount();
		int total_examples = _dict.getExampleCount();
		int shown_entries, shown_examples;
		if (_matched_entries == null)
			shown_entries = total_entries;
		else
			shown_entries = _matched_entries.size();
		if (_matched_examples == null)
			shown_examples = total_examples;
		else
			shown_examples = _matched_examples.size();
		
		ChoiceFormatter formatter = new ChoiceFormatter(
			GUIUtils.getString("Labels", "num_words"));
		String entries_string = formatter.format(shown_entries);
		formatter.applyPattern(GUIUtils.getString("Labels", "num_examples"));
		String examples_string = formatter.format(shown_examples);
		String text = GUIUtils.getString("Labels", "words_examples",
			new Object[]{entries_string, examples_string,
				new Integer(total_entries), new Integer(total_examples)});
		_left_status_label.setText(text);
	}
	
	private void updateMenu() {
		//---------
		// Change undo menu item.
		if (_model.getNumUndoableActions() == 0) {
			String str = GUIUtils.getString("Actions",
				"undo", new Object[]{""}).replaceAll("\\$", "");
			_undo_action.putValue(Action.NAME, str);
			_toolbar_undo_action.putValue(Action.SHORT_DESCRIPTION, str);
			_undo_action.setEnabled(false);
			_toolbar_undo_action.setEnabled(false);
		}
		else {
			String text = GUIUtils.getString("Actions", "undo", new Object[]{
				_model.getFirstUndoableAction().getMessage()});
			text = text.replaceAll("\\$", "");
			_undo_action.putValue(Action.NAME, text);
			_toolbar_undo_action.putValue(Action.SHORT_DESCRIPTION, text);
			_undo_action.setEnabled(true);
			_toolbar_undo_action.setEnabled(true);
		}
		
		//---------
		// Change redo menu item.
		if (_model.getNumRedoableActions() == 0) {
			String str = GUIUtils.getString("Actions",
				"redo", new Object[]{""}).replaceAll("\\$", "");
			_redo_action.putValue(Action.NAME, str);
			_toolbar_redo_action.putValue(Action.SHORT_DESCRIPTION, str);
			_redo_action.setEnabled(false);
			_toolbar_redo_action.setEnabled(false);
		}
		else {
			String text=GUIUtils.getString("Actions", "redo", new Object[]{
				_model.getFirstRedoableAction().getMessage()});
			text = text.replaceAll("\\$", "");
			_redo_action.putValue(Action.NAME, text);
			_toolbar_redo_action.putValue(Action.SHORT_DESCRIPTION, text);
			_redo_action.setEnabled(true);
			_toolbar_redo_action.setEnabled(true);
		}
	}
	
	private void updateTitle() {
		String title;
		String file_name = _model.getDictFileName();
		if (file_name == null)
			title = GUIUtils.getString("Labels", "untitled");
		else {
			int index = file_name.lastIndexOf(File.separatorChar);
			if (index > 0)
				title = file_name.substring(index+1, file_name.length());
			else	
				title = file_name;
		}
		
		if (_model.getDictModel().isDataModified()
			|| _model.getQueryModel().isDataModified())
			title += " (" + GUIUtils.getString("Labels", "modified") + ")";
			
		title += " - jVLT";
		_main_frame.setTitle(title);
	}
	
	private void updateRecentFilesMenu(String file_name) {
		_recent_files.remove(file_name);
		_recent_files.addFirst(file_name);
		while (_recent_files.size() > 5)
			_recent_files.removeLast();
		
		_recent_files_menu.removeAll();
		Iterator<String> it = _recent_files.iterator();
		int index = 0;
		while (it.hasNext()) {
			File f = new File(it.next());
			CustomAction action = new CustomAction("open_"+index);
			action.addActionListener(this);
			action.putValue(Action.NAME, f.getName());
			_recent_files_menu.add(new JMenuItem(action));
			index++;
		}
	}

	private void loadRuntimeProperties() {
		Config conf = JVLT.getConfig();
		String home = System.getProperty("user.home") + File.separator
			+ ".jvlt" + File.separator;
		XMLDecoder decoder;

		try {
			//-----
			// Load filters
			//-----
			decoder = new XMLDecoder(new BufferedInputStream(
					new FileInputStream(home + "filters.xml")));
			ObjectQuery[] oqs = (ObjectQuery[]) decoder.readObject();
			JVLT.getRuntimeProperties().put("filters", oqs);
		} catch (Exception e) {
			e.printStackTrace();
			JVLT.getRuntimeProperties().put("filter", new ObjectQuery[0]);
		}
			
		try {
			//-----
			// Load quiz entry filter
			//-----
			decoder = new XMLDecoder(new BufferedInputStream(
					new FileInputStream(home + "quizfilters.xml")));
			EntrySelectionDialogData.State[] states =
				(EntrySelectionDialogData.State[]) decoder.readObject();
			JVLT.getRuntimeProperties().put("quiz_entry_filters", states);
		} catch (Exception e) {
			e.printStackTrace();
			JVLT.getRuntimeProperties().put("quiz_entry_filters", null);
		}
			
		try {
			//-----
			// Load quiz types
			//-----
			decoder = new XMLDecoder(new BufferedInputStream(
					new FileInputStream(home + "quiztypes.xml")));
			QuizInfo[] qinfos = (QuizInfo[]) decoder.readObject();
			decoder.close();
			JVLT.getRuntimeProperties().put("quiz_types", qinfos);
			JVLT.getRuntimeProperties().put("selected_quiz_type",
				conf.getProperty("selected_quiz_type", ""));
		} catch (Exception e) {
			e.printStackTrace();
			JVLT.getRuntimeProperties().put("quiz_types", new QuizInfo[0]);
			JVLT.getRuntimeProperties().put("selected_quiz_type", "");
		}
	}
	
	private void saveRuntimeProperties() {
		Config conf = JVLT.getConfig();
		String home = System.getProperty("user.home") + File.separator
			+ ".jvlt" + File.separator;
		XMLEncoder encoder;

		try {
			//-----
			// Save filters
			//-----
			ObjectQuery[] oqs = (ObjectQuery[])
				JVLT.getRuntimeProperties().get("filters");
			encoder = new XMLEncoder(new BufferedOutputStream(
					new FileOutputStream(home + "filters.xml")));
			encoder.writeObject(oqs);
			encoder.close();
			
			//-----
			// Save quiz entry filter
			//-----
			EntrySelectionDialogData.State[] states =
				(EntrySelectionDialogData.State[])
				JVLT.getRuntimeProperties().get("quiz_entry_filters");
			encoder = new XMLEncoder(new BufferedOutputStream(
					new FileOutputStream(home + "quizfilters.xml")));
			encoder.writeObject(states);
			encoder.close();
			
			//-----
			// Save quiz types
			//-----
			QuizInfo[] qts = (QuizInfo[])
				JVLT.getRuntimeProperties().get("quiz_types");
			encoder = new XMLEncoder(new BufferedOutputStream(
					new FileOutputStream(home + "quiztypes.xml")));
			encoder.writeObject(qts);
			encoder.close();
			Object obj = JVLT.getRuntimeProperties().get("selected_quiz_type");
			if (obj == null)
				conf.setProperty("selected_quiz_type", "");
			else
				conf.setProperty("selected_quiz_type", obj.toString());
	
			//-----
			// Save language-specific settings
			//-----
			String lang = _dict.getLanguage();
			Object[] displayed_attributes = (Object[])
				JVLT.getRuntimeProperties().get("displayed_attributes");
			String key = (lang==null||lang.equals("")) ?
				"displayed_attributes" : ("displayed_attributes_"+lang);
			JVLT.getConfig().setProperty(key, displayed_attributes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String[] args) {
		JVLT jvlt = new JVLT();
		jvlt.init();
		Config config = JVLT.getConfig();
		
		// Set fonts.
		Font def = new Font("Dialog", Font.PLAIN, 12);
		Font font = JVLT.getConfig().getFontProperty("ui_font", def);
		String font_str = Utils.fontToString(font);
		System.getProperties().put("swing.plaf.metal.controlFont", font_str);
		System.getProperties().put("swing.plaf.metal.menuFont", font_str);
		System.getProperties().put("swing.plaf.metal.systemFont", font_str);
		System.getProperties().put("swing.plaf.metal.userFont", font_str);
		
		// Set look & feel.
		try {
			if (config.containsKey("look_and_feel"))
				UIManager.setLookAndFeel(config.getProperty("look_and_feel"));
			else
				UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		final JVLTUI ui = new JVLTUI(jvlt.getModel());
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				ui.initUI();
				ui.run();
			}
		});
	}
}

class DictUpdater {
	private String _original_version;

	public DictUpdater(String original_version)
	{	_original_version = original_version; }
	
	public void updateDict(Dict dict) {
		if (_original_version.compareTo("1.0") < 0) {
			for (Iterator<Example> it=dict.getExamples().iterator();
					it.hasNext(); ) {
				Example ex = it.next();
				String id = ex.getID().replace('e','x');
				ex.setID(id);
			}
		}
	}
}

class AboutDialog extends JDialog {
	class ActionEventHandler implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if (ev.getActionCommand().equals("close"))
				setVisible(false);
		}
	}

	private static final long serialVersionUID = 1L;
	
	private JEditorPane _html_pane;
	
	public AboutDialog(Frame parent) {
		super(parent, GUIUtils.getString("Labels", "about"), true);
		init();
	}
	
	private void init() {
		Action close_action = GUIUtils.createTextAction(
			new ActionEventHandler(), "close");
		
		_html_pane = new JEditorPane();
		_html_pane.setEditable(false);
		_html_pane.setContentType("text/html");
		JScrollPane scrpane = new JScrollPane(_html_pane);
		scrpane.setPreferredSize(new Dimension(320,400));
		
		getContentPane().setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0,0,1.0,1.0);
		getContentPane().add(scrpane, cc);
		cc.update(0,1,0.0,0.0);
		cc.fill = CustomConstraints.NONE;
		getContentPane().add(new JButton(close_action), cc);
		
		InputStream xsl = AboutDialog.class.getResourceAsStream("/info.xsl");
		XSLTransformer transformer = new XSLTransformer(xsl);
		try {
			InputStream xml = AboutDialog.class.getResourceAsStream(
					"/info.xml");
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fac.newDocumentBuilder();
			Document doc = builder.parse(xml);
			String html = transformer.transform(doc);
			_html_pane.setText(html);
			// System.out.println(html);
			
			URL url = AboutDialog.class.getResource("/doc/default/jvlt.png");
			HTMLDocument htmldoc = (HTMLDocument) _html_pane.getDocument();
			htmldoc.setBase(Utils.getDirectory(url));

			_html_pane.setCaretPosition(0);
		}
		catch(Exception ex) { ex.printStackTrace(); }
	}
}
