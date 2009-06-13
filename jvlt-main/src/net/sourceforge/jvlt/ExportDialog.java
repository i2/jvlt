package net.sourceforge.jvlt;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.jvlt.event.DialogListener;
import net.sourceforge.jvlt.event.StateListener;
import net.sourceforge.jvlt.event.StateListener.StateEvent;

public class ExportDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public ExportDialog(Frame parent, JVLTModel model) {
		super(parent, GUIUtils.getString("Labels", "export"), true);
		
		final ExportWizardModel ewmodel = new ExportWizardModel(model);
		ewmodel.loadState();
		ewmodel.addStateListener(new StateListener() {
			public void stateChanged(StateEvent ev) {
				ewmodel.saveState();
				dispose();
			}
		});
		Wizard wizard = new Wizard(ewmodel);
		JPanel content = wizard.getContent();
		content.setPreferredSize(new Dimension(600,400));
		setContentPane(content);
	}
}

class ExportWizardModel extends DialogWizardModel {
	public ExportWizardModel(JVLTModel model) {
		super(model);

		registerPanelDescriptor(new StartExportDescriptor(this));
		registerPanelDescriptor(new FinishExportDescriptor(this));
		
		_current_descriptor = getPanelDescriptor("start");
	}
	
	public String getButtonText(String button_command) {
		if (_current_descriptor instanceof FinishExportDescriptor) {
			if (button_command.equals(Wizard.NEXT_COMMAND))
				return GUIUtils.getString("Actions", "finish");
		}
		return super.getButtonText(button_command);
	}
	
	public boolean isButtonEnabled(String button_command) {
		if (_current_descriptor instanceof StartExportDescriptor) {
			if (button_command.equals(Wizard.BACK_COMMAND))
				return false;
		}
		return super.isButtonEnabled(button_command);
	}

	public WizardPanelDescriptor nextPanelDescriptor(String command)
		throws InvalidInputException {
		WizardPanelDescriptor next = _current_descriptor;
		if (_current_descriptor instanceof StartExportDescriptor) {
			if (command.equals(Wizard.NEXT_COMMAND))
				next = getPanelDescriptor("finish");
		}
		else if (_current_descriptor instanceof FinishExportDescriptor) {
			StartExportDescriptor sed =
				(StartExportDescriptor) getPanelDescriptor("start");
			FinishExportDescriptor fed =
				(FinishExportDescriptor) _current_descriptor;
			if (command.equals(Wizard.NEXT_COMMAND)) {
				Dict dict = sed.getDict();
				fed.setDict(dict);
				fed.setClearStats(sed.getClearStats());
				try { fed.export(); }
				catch (IOException e) {
					throw new InvalidInputException(
						GUIUtils.getString("Messages", "exporting_failed"),
						e.getMessage());
				}
				fireStateEvent(new StateEvent(this, FINISH_STATE));
			}
			else if (command.equals(Wizard.BACK_COMMAND))
				next = sed;
		}
		
		_current_descriptor = next;
		if (command.equals(Wizard.CANCEL_COMMAND))
			fireStateEvent(new StateEvent(this, CANCEL_STATE));

		return next;
	}
	
	public void loadState() {
		StartExportDescriptor sed =
			(StartExportDescriptor) getPanelDescriptor("start");
		sed.loadState();
	}

	public void saveState() {
		StartExportDescriptor sed =
			(StartExportDescriptor) getPanelDescriptor("start");
		sed.saveState();
	}
}

class StartExportDescriptor extends WizardPanelDescriptor {
	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("select_words"))
				GUIUtils.showDialog(
					JOptionPane.getFrameForComponent(_panel), _query_dialog);
		}
	}
	
	private class DialogHandler implements DialogListener {
		public void dialogStateChanged(DialogEvent ev) {
			if (ev.getSource() == _query_dialog) {
				if (ev.getType() == AbstractDialog.APPLY_OPTION) {
					_query_modified = true;
					updateUI();
				}
				else
					_query_dialog.setVisible(false);
			}
		}
	}
	
	private boolean _query_modified;
	private EntryList _entry_list;
	private ExampleList _example_list;
	private JCheckBox _clear_stats_box;
	private JLabel _entry_label;
	private JLabel _example_label;
	private EntryQueryDialog _query_dialog;
	
	public StartExportDescriptor(ExportWizardModel model) {
		super(model);
		_query_modified = false;
		initUI();
		updateUI();
	}

	public String getID() { return "start"; }
	
	public Dict getDict() {
		ExportWizardModel model = (ExportWizardModel) _model;
		Dict dict = new Dict();
		try {
			dict.setLanguage(model.getJVLTModel().getDict().getLanguage());
			for (Iterator<Entry> it=_entry_list.getEntries().iterator();
					it.hasNext(); )
				dict.addEntry(it.next());
			for (Iterator<Example> it=_example_list.getExamples().iterator();
					it.hasNext(); )
				dict.addExample(it.next());
		}
		catch (DictException e) {} // Should not happen

		return dict;
	}
	
	public boolean getClearStats() { return _clear_stats_box.isSelected(); }
	
	public void loadState() {
		_clear_stats_box.setSelected(
			JVLT.getConfig().getBooleanProperty("export_clear_stats", false));
	}
	
	public void saveState() {
		JVLT.getConfig().setProperty(
				"export_clear_stats", _clear_stats_box.isSelected());
	}
	
	private void setEntries(Collection<Entry> entries) {
		ExportWizardModel model = (ExportWizardModel) _model;
		int n = model.getJVLTModel().getDict().getEntryCount();
		_entry_label.setText(getLabel(entries.size(), n, "num_words"));
		_entry_list.setEntries(entries);
	}
	
	private void setExamples(Collection<Example> examples) {
		ExportWizardModel model = (ExportWizardModel) _model;
		int n = model.getJVLTModel().getDict().getExampleCount();
		_example_label.setText(getLabel(examples.size(), n, "num_examples"));
		_example_list.setExamples(examples);
	}
	
	private void initUI() {
		JVLTModel model = ((ExportWizardModel) _model).getJVLTModel();
		_query_dialog = new EntryQueryDialog(
			JOptionPane.getFrameForComponent(_panel),
			GUIUtils.getString("Labels", "advanced_filter"), true, model);
		_query_dialog.addDialogListener(new DialogHandler());
		
		_entry_list = new EntryList();
		_example_list = new ExampleList();
		_entry_label = new JLabel();
		_example_label = new JLabel();
		
		_clear_stats_box = new JCheckBox(
				GUIUtils.createTextAction("clear_stats"));
		
		Action select_words_action = GUIUtils.createTextAction(
			new ActionHandler(), "select_words");
		JPanel selection_panel = new JPanel();
		selection_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0,0,1.0,0.0);
		selection_panel.add(new JLabel(
			GUIUtils.getString("Labels", "select_exported_words")+":"), cc);
		cc.update(1,0,0.0,0.0);
		selection_panel.add(new JButton(select_words_action), cc);

		_panel = new JPanel();
		_panel.setLayout(new GridBagLayout());
		cc.update(0,0,1.0,0.0);
		_panel.add(_clear_stats_box, cc);
		cc.update(0,1,1.0,0.0);
		_panel.add(selection_panel, cc);
		cc.update(0,2,1.0,0.0);
		_panel.add(_entry_label, cc);
		cc.update(0,3,1.0,0.5);
		_panel.add(new JScrollPane(_entry_list), cc);
		cc.update(0,4,1.0,0.0);
		_panel.add(_example_label, cc);
		cc.update(0,5,1.0,0.5);
		_panel.add(new JScrollPane(_example_list), cc);
	}

	private String getLabel(int num, int total, String i18n) {
		ChoiceFormatter formatter = new ChoiceFormatter(
			GUIUtils.getString("Labels", i18n));
		String str = formatter.format(total);
		return GUIUtils.getString("Labels", "exported",
			new Object[]{new Integer(num), str});
	}
	
	private void updateUI() {
		ExportWizardModel model = (ExportWizardModel) _model;
		Dict dict = model.getJVLTModel().getDict();
		ObjectQuery query;
		if (_query_modified)
			query = _query_dialog.getObjectQuery();
		else
			query = new ObjectQuery(Entry.class);
		EntryFilter filter = new EntryFilter(query);
		Collection<Entry> entries
			= filter.getMatchingEntries(dict.getEntries());
		TreeSet<Example> examples = new TreeSet<Example>();
		for (Iterator<Entry> it=entries.iterator(); it.hasNext(); ) {
			examples.addAll(dict.getExamples(it.next()));
		}
		setEntries(entries);
		setExamples(examples);
	}
}

class FinishExportDescriptor extends WizardPanelDescriptor {
	class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String csv_file = GUIUtils.getString("Labels", "csv_file");
			String html_file = GUIUtils.getString("Labels", "html_file");
			String jvlt_file = GUIUtils.getString("Labels", "jvlt_file");
			if (e.getActionCommand().equals("select_file")) {
                DictFileChooser chooser;
				ExportWizardModel ewm = (ExportWizardModel) _model;
				String file = ewm.getJVLTModel().getDictFileName();
				if (_type_box.getSelectedItem().equals(csv_file))
					chooser = new DictFileChooser(
						file, DictFileChooser.CSV_FILES);
				else if (_type_box.getSelectedItem().equals(jvlt_file))
					chooser = new DictFileChooser(
						file, DictFileChooser.JVLT_FILES);
				else
					chooser = new DictFileChooser(
						file, DictFileChooser.HTML_FILES);
				
				int val = chooser.showSaveDialog(_panel);
				if (val == JFileChooser.APPROVE_OPTION)
					_file_field.setText(chooser.getSelectedFile().getPath());
			}
			else if (e.getActionCommand().equals("file_type")) {
				_panel.remove(_csv_panel);
				_panel.remove(_html_panel);
				CustomConstraints cc = new CustomConstraints();
				cc.update(0, 3, 1.0, 0.0, 3, 1);
				if (_type_box.getSelectedItem().equals(csv_file)) {
					_panel.add(_csv_panel, cc);
				} else if (_type_box.getSelectedItem().equals(html_file)) {
					_panel.add(_html_panel, cc);
				}
				
				_panel.revalidate();
				_panel.repaint(_panel.getVisibleRect());
			}
		}
	}
	
	private LabeledComboBox _type_box;
	private CustomTextField _file_field;
	private CSVExportPanel _csv_panel;
	private HTMLExportPanel _html_panel;
	private Dict _dict;
	private boolean _clear_stats = false; 
	
	public FinishExportDescriptor(ExportWizardModel model) {
		super(model);
		initUI();
	}
	
	public String getID() { return "finish"; }
	
	public void export() throws IOException {
		File f = new File(_file_field.getText());
		FileOutputStream stream = new FileOutputStream(f);
		if (_type_box.getSelectedItem().toString().equals(
				GUIUtils.getString("Labels", "jvlt_file"))) {
			DictXMLWriter writer = new DictXMLWriter(_dict, stream);
			writer.setClearStats(_clear_stats);
			writer.write();
		} else if (_type_box.getSelectedItem().toString().equals(
				GUIUtils.getString("Labels", "csv_file"))) {
			DictCSVWriter writer = new DictCSVWriter(_dict, stream);
			writer = new DictCSVWriter(_dict, stream);
			writer.setCharset(_csv_panel.getCharset());
			writer.setTextDelimiter(_csv_panel.getTextDelimiter());
			writer.setFieldDelimiter(_csv_panel.getFieldDelimiter());
			/* No need to clear entry statistics, since the exporter currently
			 * ignores the statistics fields.
			 */
			writer.write();
		} else if (_type_box.getSelectedItem().equals(
				GUIUtils.getString("Labels", "html_file"))) {
			DictHtmlWriter writer = new DictHtmlWriter(_dict, stream);
			writer.setAddReverse(_html_panel.isBidirectional());
			writer.write();
		}
	}
	
	public void setDict(Dict dict) { _dict = dict; }
	
	public void setClearStats(boolean clear) { _clear_stats = clear; }
	
	private void initUI() {
		Action select_action =
			GUIUtils.createTextAction(new ActionHandler(), "select_file");
			
		_file_field = new CustomTextField(20);
		_file_field.setActionCommand("select_export_file");
		
		_type_box = new LabeledComboBox();
		_type_box.setLabel("file_type");
		_type_box.addItem(GUIUtils.getString("Labels", "jvlt_file"));
		_type_box.addItem(GUIUtils.getString("Labels", "csv_file"));
		_type_box.addItem(GUIUtils.getString("Labels", "html_file"));
		_type_box.addActionListener(new ActionHandler());
		
		_csv_panel = new CSVExportPanel();
		_html_panel = new HTMLExportPanel();
		
		_panel = new JPanel();
		_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0, 3, 1);
		_panel.add(
			new JLabel(GUIUtils.getString("Messages", "start_export")), cc);
		cc.update(0, 1, 0.5, 0, 1, 1);
		_panel.add(_type_box.getLabel(), cc);
		cc.update(1, 1, 0.5, 0);
		_panel.add(_type_box, cc);
		cc.update(0, 2, 0.5, 0);
		_panel.add(_file_field.getLabel(), cc);
		cc.update(1, 2, 0.5, 0);
		_panel.add(_file_field, cc);
		cc.update(2, 2, 0, 0);
		_panel.add(new JButton(select_action), cc);
		cc.update(0, 4, 0, 1.0);
		_panel.add(Box.createVerticalGlue(), cc);
	}
}

class HTMLExportPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JCheckBox _bidirectional_box;

	public HTMLExportPanel() {
		_bidirectional_box = new JCheckBox();
		_bidirectional_box.setText(
			GUIUtils.getString("Actions", "bidirectional"));

		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		add(_bidirectional_box, cc);
		cc.update(0, 1, 0.0, 1.0);
		add(Box.createVerticalGlue(), cc);
	}

	public boolean isBidirectional() { return _bidirectional_box.isSelected(); }
}

