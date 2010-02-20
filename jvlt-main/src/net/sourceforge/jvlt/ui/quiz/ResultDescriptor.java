package net.sourceforge.jvlt.ui.quiz;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.sourceforge.jvlt.core.Entry;
import net.sourceforge.jvlt.core.Sense;
import net.sourceforge.jvlt.ui.utils.CustomAction;
import net.sourceforge.jvlt.ui.utils.CustomConstraints;
import net.sourceforge.jvlt.ui.utils.GUIUtils;
import net.sourceforge.jvlt.ui.vocabulary.DictEntryTree;
import net.sourceforge.jvlt.ui.wizard.WizardModel;
import net.sourceforge.jvlt.utils.ChoiceFormatter;

class ResultDescriptor extends YesNoDescriptor implements
		TreeSelectionListener, ActionListener {
	private CustomAction _up_action;
	private CustomAction _down_action;
	private DictEntryTree _known_entries_tree;
	private DictEntryTree _notknown_entries_tree;
	private JPanel _comp;
	private TitledBorder _known_entries_border;
	private TitledBorder _notknown_entries_border;

	public ResultDescriptor(WizardModel model) {
		super(model, GUIUtils.getString("Messages", "save_changes"));
		init();
		updateActions();
		updateLabels();
	}

	@Override
	public String getID() {
		return "result";
	}

	public Entry[] getKnownEntries() {
		return _known_entries_tree.getEntries();
	}

	public void setKnownEntries(Entry[] entries) {
		_known_entries_tree.setEntries(entries);
		if (entries.length > 0) {
			_known_entries_tree.scrollRowToVisible(0);
		}
		updateLabels();
	}

	public Entry[] getNotKnownEntries() {
		return _notknown_entries_tree.getEntries();
	}

	public void setNotKnownEntries(Entry[] entries) {
		_notknown_entries_tree.setEntries(entries);
		if (entries.length > 0) {
			_notknown_entries_tree.scrollRowToVisible(0);
		}
		updateLabels();
	}

	public void valueChanged(TreeSelectionEvent e) {
		updateActions();
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("up")) {
			Entry entry = null;
			Object obj = _notknown_entries_tree.getSelectedObject();
			if (obj instanceof Entry) {
				entry = (Entry) obj;
			} else {
				// obj instanceof Sense
				entry = ((Sense) obj).getParent();
			}
			_notknown_entries_tree.removeEntry(entry);
			_known_entries_tree.addEntry(entry);
			updateLabels();
		} else if (ev.getActionCommand().equals("down")) {
			Entry entry = null;
			Object obj = _known_entries_tree.getSelectedObject();
			if (obj instanceof Entry) {
				entry = (Entry) obj;
			} else {
				// obj instanceof Sense
				entry = ((Sense) obj).getParent();
			}
			_known_entries_tree.removeEntry(entry);
			_notknown_entries_tree.addEntry(entry);
			updateLabels();
		}
	}

	private void init() {
		_up_action = GUIUtils.createIconAction(this, "up");
		_down_action = GUIUtils.createIconAction(this, "down");

		_known_entries_tree = new DictEntryTree();
		_known_entries_tree.addTreeSelectionListener(this);
		_known_entries_border = new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED));
		JScrollPane known_entries_scrpane = new JScrollPane();
		known_entries_scrpane.getViewport().setView(_known_entries_tree);
		known_entries_scrpane.setBorder(_known_entries_border);
		_notknown_entries_tree = new DictEntryTree();
		_notknown_entries_tree.addTreeSelectionListener(this);
		_notknown_entries_border = new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED));
		JScrollPane notknown_entries_scrpane = new JScrollPane();
		notknown_entries_scrpane.getViewport().setView(_notknown_entries_tree);
		notknown_entries_scrpane.setBorder(_notknown_entries_border);

		JPanel up_down_panel = new JPanel();
		up_down_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		up_down_panel.add(Box.createHorizontalGlue(), cc);
		cc.update(1, 0, 0.0, 0.0);
		up_down_panel.add(new JButton(_down_action), cc);
		cc.update(2, 0);
		up_down_panel.add(new JButton(_up_action), cc);

		_comp = new JPanel();
		_comp.setLayout(new GridBagLayout());
		cc.update(0, 0, 1.0, 1.0);
		_comp.add(known_entries_scrpane, cc);
		cc.update(0, 1, 1.0, 0.0);
		_comp.add(up_down_panel, cc);
		cc.update(0, 2, 1.0, 1.0);
		_comp.add(notknown_entries_scrpane, cc);

		setContentPanel(_comp);
	}

	private void updateActions() {
		_down_action
				.setEnabled(_known_entries_tree.getSelectedObject() != null);
		_up_action
				.setEnabled(_notknown_entries_tree.getSelectedObject() != null);
	}

	private void updateLabels() {
		ChoiceFormatter formatter = new ChoiceFormatter(GUIUtils.getString(
				"Labels", "num_words"));
		String value = formatter
				.format(_known_entries_tree.getEntries().length);
		_known_entries_border.setTitle(GUIUtils.getString("Labels",
				"known_words", new Object[] { value }));
		value = formatter.format(_notknown_entries_tree.getEntries().length);
		_notknown_entries_border.setTitle(GUIUtils.getString("Labels",
				"not_known_words", new Object[] { value }));
		_comp.revalidate();
		_comp.repaint(_comp.getVisibleRect());
	}
}
