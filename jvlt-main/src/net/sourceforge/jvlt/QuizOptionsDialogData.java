package net.sourceforge.jvlt;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class QuizOptionsDialogData extends CustomDialogData {
	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			if (ev.getActionCommand().equals("input_answer")) {
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
	}
	
	private boolean _old_input_answer;
	private boolean _old_match_case;
	private String _old_default_answer;
	private boolean _old_ignore_batches;

	private JCheckBox _input_answer_chbox;
	private JCheckBox _match_case_chbox;
	private JCheckBox _default_answer_chbox;
	private LabeledComboBox _default_answer_cobox;
	private JCheckBox _ignore_batches_chbox;

	public QuizOptionsDialogData() {
		Config config = JVLT.getConfig();
		
		_old_input_answer = config.getBooleanProperty(
				"input_answer", false);
		_old_match_case = config.getBooleanProperty(
				"match_case", true);
		_old_default_answer = config.getProperty(
				"default_answer", "");
		_old_ignore_batches = config.getBooleanProperty(
				"ignore_batches", false);

		initUi();
	}
	
	@Override
	public void updateData() throws InvalidDataException {
		Config config = JVLT.getConfig();
		
		boolean new_input_answer = _input_answer_chbox.isSelected();
		boolean new_match_case = ! _match_case_chbox.isSelected();
		String new_default_answer = "";
		if (_default_answer_chbox.isSelected()) {
			String item = _default_answer_cobox.getSelectedItem().toString();
			if (item.equals(GUIUtils.getString("Labels", "yes")))
				new_default_answer = "yes";
			else
				new_default_answer = "no";
		}
		boolean new_ignore_batches = _ignore_batches_chbox.isSelected();

		config.setProperty("input_answer", String.valueOf(new_input_answer));
		config.setProperty("match_case", String.valueOf(new_match_case));
		config.setProperty("default_answer", new_default_answer);
		config.setProperty("ignore_batches", new_ignore_batches);
	}
	
	private void initUi() {
		ActionHandler handler = new ActionHandler();
		CustomConstraints cc = new CustomConstraints();
		
		_input_answer_chbox = new JCheckBox(GUIUtils.createTextAction(handler,
				"input_answer"));
		_match_case_chbox = new JCheckBox(GUIUtils.createTextAction(handler,
				"ignore_case"));
		_default_answer_chbox = new JCheckBox(GUIUtils.createTextAction(handler,
				"default_answer"));
		_default_answer_cobox = new LabeledComboBox();
		_default_answer_cobox.setLabel("default_answer_choice");
		_default_answer_cobox.addItem(GUIUtils.getString("Labels", "yes"));
		_default_answer_cobox.addItem(GUIUtils.getString("Labels", "no"));
		_ignore_batches_chbox = new JCheckBox(GUIUtils.createTextAction(handler,
				"ignore_batches"));
		
		JPanel general_panel = new JPanel();
		general_panel.setLayout(new GridBagLayout());
		JPanel default_answer_panel = new JPanel();
		default_answer_panel.setLayout(new GridLayout());
		default_answer_panel.add(_default_answer_cobox.getLabel());
		default_answer_panel.add(_default_answer_cobox);
		cc.update(0, 0, 1.0, 0.0);
		general_panel.add(_input_answer_chbox, cc);
		cc.update(0, 1, 1.0, 0.0);
		cc.insets.left = 15;
		general_panel.add(_match_case_chbox, cc);
		cc.update(0, 2, 1.0, 0.0);
		general_panel.add(_default_answer_chbox, cc);
		cc.update(0, 3, 1.0, 0.0);
		cc.insets.left = 2;
		general_panel.add(default_answer_panel, cc);
		cc.update(0, 4, 1.0, 0.0);
		general_panel.add(_ignore_batches_chbox, cc);
		cc.update(0, 5, 1.0, 1.0);
		general_panel.add(Box.createVerticalGlue(), cc);
		
		_content_pane = general_panel;
		
		/*
		 * Initialize data
		 */
		_input_answer_chbox.setSelected(_old_input_answer);
		_match_case_chbox.setEnabled(_old_input_answer);
		_match_case_chbox.setSelected(! _old_match_case);
		_default_answer_chbox.setEnabled(! _old_input_answer);
		_default_answer_chbox.setSelected(! _old_default_answer.equals(""));
		_default_answer_cobox.setEnabled(! _old_input_answer &&
				! _old_default_answer.equals(""));
		_default_answer_cobox.setSelectedItem(_old_default_answer.equals("no")
				? GUIUtils.getString("Labels", "no")
				: GUIUtils.getString("Labels", "yes"));
		_ignore_batches_chbox.setSelected(_old_ignore_batches);
	}
}

