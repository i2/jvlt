package net.sourceforge.jvlt;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.sourceforge.jvlt.event.DictUpdateListener;
import net.sourceforge.jvlt.event.SelectionNotifier;
import net.sourceforge.jvlt.event.StateListener;
import net.sourceforge.jvlt.event.DictUpdateListener.DictUpdateEvent;
import net.sourceforge.jvlt.event.DictUpdateListener.EntryDictUpdateEvent;
import net.sourceforge.jvlt.event.DictUpdateListener.LanguageDictUpdateEvent;
import net.sourceforge.jvlt.event.DictUpdateListener.NewDictDictUpdateEvent;

public class QuizModel extends WizardModel {
	private class DictUpdateHandler implements DictUpdateListener {
		public void dictUpdated(DictUpdateEvent event) {
			/*
			 * Notify panel descriptors
			 */
			for (WizardPanelDescriptor d: _descriptor_map.values()) {
				if (d instanceof EntryDescriptor) {
					((EntryDescriptor) d).dictUpdated(event);
				} else if (d instanceof StatsDescriptor) {
					((StatsDescriptor) d).dictUpdated(event);
				}
			}
			
			/*
			 * Notify the wizard after all panel descriptors have been updated.
			 */
			if (event instanceof EntryDictUpdateEvent
				&& event.getType() == EntryDictUpdateEvent.ENTRIES_REMOVED) {
				if (_current_descriptor instanceof EntryDescriptor
					&& ((EntryDescriptor) _current_descriptor).getEntry()
						== null) {
					/*
					 * If the currently displayed entry has been removed, the
					 * EntryDescriptor's dictUpdated() method already has set
					 * its current entry to null. In this case the
					 * EntryNullDescriptor is shown.
					 */
					WizardPanelDescriptor old_descriptor = _current_descriptor;
					_current_descriptor = getPanelDescriptor("entry_null");
					_wizard.newPanelDescriptorSelected(old_descriptor,
							_current_descriptor);
				}
				else
					/* Update buttons */
					_wizard.panelDescriptorUpdated(_current_descriptor);
			}
		}
	}
	
	private JVLTModel _model;
	private QuizDict _qdict;
	private boolean _repeat_mode;
	private Entry[] _known_entries;
	private Entry[] _notknown_entries;
	private HashMap<Entry, Integer> _flag_map;

	public QuizModel(JVLTModel model, SelectionNotifier notifier) {
		_model = model;
		_qdict = null;
		_repeat_mode = false;
		_known_entries = new Entry[0];
		_notknown_entries = new Entry[0];
		_flag_map = new HashMap<Entry, Integer>();
		
		WizardPanelDescriptor d = new StatsDescriptor(this);
		registerPanelDescriptor(d);
		_current_descriptor = d;
		d = new EntryInputDescriptor(this, notifier);
		registerPanelDescriptor(d);
		d = new EntryQuestionDescriptor(this, notifier);
		registerPanelDescriptor(d);
		d = new EntryInputAnswerDescriptor(this, notifier);
		registerPanelDescriptor(d);
		d = new EntryAnswerDescriptor(this, notifier);
		registerPanelDescriptor(d);
		d = new RepeatDescriptor(this);
		registerPanelDescriptor(d);
		d = new ResultDescriptor(this);
		registerPanelDescriptor(d);
		d = new EntryNullDescriptor(this);
		registerPanelDescriptor(d);
		
		DictUpdateHandler handler = new DictUpdateHandler();
		model.getDictModel().addDictUpdateListener(handler);
		model.getQueryModel().addDictUpdateListener(handler);
	}

	public String getButtonText(String button_command) {
		if (_current_descriptor.getID().equals("stats")) {
			if (button_command.equals(Wizard.NEXT_COMMAND))
				return GUIUtils.getString("Actions", "start");
		} else  {
			if (button_command.equals(Wizard.CANCEL_COMMAND))
				return GUIUtils.getString("Actions", "finish");
		}
		
		return super.getButtonText(button_command);
	}
	
	public boolean isButtonEnabled(String button_command) {
		if (_current_descriptor.getID().equals("stats")) {
			StatsDescriptor sd = (StatsDescriptor) _current_descriptor;
			if (button_command.equals(Wizard.NEXT_COMMAND)
				&& sd.getSelectedEntries() > 0)
				return true;
			else
				return false;
		} else if (_current_descriptor.getID().equals("entry_question")) {
			if (button_command.equals(Wizard.BACK_COMMAND))
				return _qdict.hasPreviousEntry();
			else
				return true;
		} else if (_current_descriptor.getID().equals("entry_input")) {
			if (button_command.equals(Wizard.BACK_COMMAND))
				return _qdict.hasPreviousEntry();
			else if (button_command.equals(Wizard.NEXT_COMMAND)) {
				EntryInputDescriptor eid
					= (EntryInputDescriptor) _current_descriptor;
				if (! eid.isAnswerKnown())
					return true;
				else
					return (eid.getAnswer().length() > 0);
			}
		} else if (_current_descriptor.getID().equals("entry_answer")) {
			EntryAnswerDescriptor ead
				= (EntryAnswerDescriptor) _current_descriptor;
			
			if (button_command.equals(Wizard.BACK_COMMAND))
				return _qdict.hasPreviousEntry();
			else if (button_command.equals(Wizard.NEXT_COMMAND)) {
				if (! _qdict.hasNextEntry())
					return false;
				else
					return (ead.getState() != YesNoPanel.UNKNOWN_OPTION);
			}
		} else if (_current_descriptor.getID().equals("entry_input_answer")) {
			if (button_command.equals(Wizard.BACK_COMMAND))
				return _qdict.hasPreviousEntry();
			else if (button_command.equals(Wizard.NEXT_COMMAND))
				return _qdict.hasNextEntry();
		} else if (_current_descriptor.getID().equals("repeat")) {
			if (button_command.equals(Wizard.CANCEL_COMMAND))
				return false;
			else if (button_command.equals(Wizard.BACK_COMMAND))
				return true;
			else if (button_command.equals(Wizard.NEXT_COMMAND)) {
				RepeatDescriptor rd = (RepeatDescriptor) _current_descriptor;
				return (rd.getState() != YesNoPanel.UNKNOWN_OPTION);
			}
		} else if (_current_descriptor.getID().equals("result")) {
			if (button_command.equals(Wizard.NEXT_COMMAND))
				return false;
			else if (button_command.equals(Wizard.CANCEL_COMMAND)) {
				ResultDescriptor rd = (ResultDescriptor) _current_descriptor;
				return (rd.getState() != YesNoPanel.UNKNOWN_OPTION);
			}
		} else if (_current_descriptor.getID().equals("entry_null")) {
			return button_command.equals(Wizard.NEXT_COMMAND);
		}
		
		return super.isButtonEnabled(button_command);
	}

	public String getStatusString() {
		if (_current_descriptor instanceof EntryQuestionDescriptor
			|| _current_descriptor instanceof EntryInputDescriptor
			|| _current_descriptor instanceof EntryAnswerDescriptor
			|| _current_descriptor instanceof EntryInputAnswerDescriptor
			|| _current_descriptor instanceof RepeatDescriptor) {
			int words = _qdict.getResultCount();
			int known = _qdict.getKnownEntries().length;
			int not_known = words - known;
			ChoiceFormatter formatter = new ChoiceFormatter(
				GUIUtils.getString("Labels", "num_words"));
			String s = formatter.format(words);
			
			return GUIUtils.getString("Messages", "quizzed_words",
				new Object[] {s, new Integer(known), new Integer(not_known)});
		} else
			return super.getStatusString();
	}
	
	public WizardPanelDescriptor nextPanelDescriptor(String command) {
		WizardPanelDescriptor next = null;
		
		if (_current_descriptor instanceof StatsDescriptor) {
			StatsDescriptor sd = (StatsDescriptor) _current_descriptor;
			_qdict = sd.getQuizDict();
			_qdict.start(); /* Start a new quiz */
			_repeat_mode = false;
			_known_entries = new Entry[0];
			_notknown_entries = new Entry[0];

			if (JVLT.getConfig().getBooleanProperty("input_answer", false))
				next = getPanelDescriptor("entry_input");
			else
				next = getPanelDescriptor("entry_question");
		} else if (_current_descriptor instanceof EntryAnswerDescriptor
			|| _current_descriptor instanceof EntryInputAnswerDescriptor) {
			boolean input = true;
			if (_current_descriptor instanceof EntryAnswerDescriptor)
				input = false;
			
			/* Save current entry's flags */
			saveEntry((EntryDescriptor) _current_descriptor);
			
			if (command.equals(Wizard.NEXT_COMMAND)) {
				if (! input)
					saveResult(_current_descriptor);
				
				/* Switch to next entry */
				Entry entry = _qdict.nextEntry();
				
				if (_qdict.getResult(entry) == null) {
					next = getPanelDescriptor(input ? "entry_input"
							: "entry_question");
				} else {
					next = getPanelDescriptor(input ? "entry_input_answer"
							: "entry_answer");
				}
			} else if (command.equals(Wizard.BACK_COMMAND)) {
				if (input)
					next = getPanelDescriptor("entry_input_answer");
				else
					next = getPanelDescriptor("entry_answer");
				
				/* Switch to previous entry */
				_qdict.previousEntry();
			} else if (command.equals(Wizard.CANCEL_COMMAND)) {
				if (! input)
					saveResult(_current_descriptor);
				if (_qdict.getNotKnownEntries().length > 0)
					next = getPanelDescriptor("repeat");
				else if (! _repeat_mode && _qdict.getResultCount()==0)
					next = getPanelDescriptor("stats");
				else
					next = getPanelDescriptor("result");
			}
		} else if (_current_descriptor instanceof EntryQuestionDescriptor
			|| _current_descriptor instanceof EntryInputDescriptor) {
			boolean input = false;
			if (_current_descriptor instanceof EntryInputDescriptor)
				input = true;
				
			if (command.equals(Wizard.NEXT_COMMAND)) {
				if (input) {
					saveResult(_current_descriptor);
					next = getPanelDescriptor("entry_input_answer");
				} else
					next = getPanelDescriptor("entry_answer");
			} else if (command.equals(Wizard.BACK_COMMAND)) {
				if (input)
					next = getPanelDescriptor("entry_input_answer");
				else
					next = getPanelDescriptor("entry_answer");
				
				/* Switch to previous entry */
				_qdict.previousEntry();
			} else if (command.equals(Wizard.CANCEL_COMMAND)) {
				if (_qdict.getNotKnownEntries().length > 0)
					next = getPanelDescriptor("repeat");
				else if (! _repeat_mode && _qdict.getResultCount() == 0)
					next = getPanelDescriptor("stats");
				else
					next = getPanelDescriptor("result");
			}
		} else if (_current_descriptor instanceof RepeatDescriptor) {
			if (command.equals(Wizard.NEXT_COMMAND)) {
				RepeatDescriptor rd = (RepeatDescriptor) _current_descriptor;
				if (rd.getState() == YesNoPanel.NO_OPTION)
					next = getPanelDescriptor("result");
				else {
					if (JVLT.getConfig().getBooleanProperty(
							"input_answer", false))
						next = getPanelDescriptor("entry_input");
					else
						next = getPanelDescriptor("entry_question");
					
					if (! _repeat_mode) {
						_known_entries = _qdict.getKnownEntries();
						_notknown_entries = _qdict.getNotKnownEntries();
					}
					_repeat_mode = true;
					_qdict.reset();
				}
			} else if (command.equals(Wizard.BACK_COMMAND)) {
				if (JVLT.getConfig().getBooleanProperty(
						"input_answer", false))
					next = getPanelDescriptor("entry_input_answer");
				else
					next = getPanelDescriptor("entry_answer");
			}
		} else if (_current_descriptor instanceof ResultDescriptor) {
			if (command.equals(Wizard.CANCEL_COMMAND)) {
				next = getPanelDescriptor("stats");
				
				ResultDescriptor rd = (ResultDescriptor) _current_descriptor;
				if (rd.getState() == YesNoPanel.YES_OPTION) {
					StatsUpdateAction sua = createStatsUpdateAction(
						rd.getKnownEntries(), rd.getNotKnownEntries());
					_model.getQueryModel().executeAction(sua);
				}
			} else if (command.equals(Wizard.BACK_COMMAND)) {
				if (_qdict.getNotKnownEntries().length > 0)
					next = getPanelDescriptor("repeat");
				else {
					if (JVLT.getConfig().getBooleanProperty(
							"input_answer", false))
						next = getPanelDescriptor("entry_input_answer");
					else
						next = getPanelDescriptor("entry_answer");
				}
			}
			// NEXT_COMMAND is disabled.
		} else if (_current_descriptor instanceof EntryNullDescriptor) {
			boolean input = JVLT.getConfig().getBooleanProperty(
					"input_answer", false);
			Entry entry = _qdict.getCurrentEntry();
			
			if (entry == null) { /* No entry left */
				if (_qdict.getNotKnownEntries().length > 0)
					next = getPanelDescriptor("repeat");
				else if (! _repeat_mode && _qdict.getResultCount() == 0)
					next = getPanelDescriptor("stats");
				else
					next = getPanelDescriptor("result");
			} else {
				if (_qdict.getResult(entry) != null)
					next = getPanelDescriptor(input ? "entry_input_answer"
							: "entry_answer");
				else
					next = getPanelDescriptor(input ? "entry_input"
							: "entry_question");
			}
		}
		
		if (next instanceof StatsDescriptor) {
			_qdict = null;
			((StatsDescriptor) next).update();
		} else if (next instanceof EntryQuestionDescriptor) {
			EntryQuestionDescriptor eqd = (EntryQuestionDescriptor) next;
			Entry entry = _qdict.getCurrentEntry();
			loadEntry(eqd, entry);
			eqd.setQuizInfo(_qdict.getQuizInfo());
		} else if (next instanceof EntryInputDescriptor) {
			EntryInputDescriptor eid = (EntryInputDescriptor) next;
			Entry entry = _qdict.getCurrentEntry();
			loadEntry(eid, entry);
			eid.setQuizInfo(_qdict.getQuizInfo());
		} else if (next instanceof EntryAnswerDescriptor) {
			loadResult(_current_descriptor, next);
			EntryAnswerDescriptor ead = (EntryAnswerDescriptor) next;
			Entry entry = _qdict.getCurrentEntry();
			loadEntry(ead, entry);
			ead.setQuizInfo(_qdict.getQuizInfo());
		} else if (next instanceof EntryInputAnswerDescriptor) {
			loadResult(_current_descriptor, next);
			EntryInputAnswerDescriptor d = (EntryInputAnswerDescriptor) next;
			Entry entry = _qdict.getCurrentEntry();
			loadEntry(d, entry);
			d.setQuizInfo(_qdict.getQuizInfo());
		} else if (next instanceof RepeatDescriptor) {
			if (! (_current_descriptor instanceof ResultDescriptor)) {
				RepeatDescriptor rd = (RepeatDescriptor) next;
				rd.setState(YesNoPanel.UNKNOWN_OPTION);
			}
		} else if (next instanceof ResultDescriptor) {
			ResultDescriptor rd = (ResultDescriptor) next;
			rd.setState(YesNoPanel.UNKNOWN_OPTION);
			if (! _repeat_mode) {
				_known_entries = _qdict.getKnownEntries();
				_notknown_entries = _qdict.getNotKnownEntries();
			}
			rd.setKnownEntries(_known_entries);
			rd.setNotKnownEntries(_notknown_entries);
		}
		
		_current_descriptor = next;
		return _current_descriptor;
	}

	public JVLTModel getJVLTModel() { return _model; }
	
	public boolean existsUnfinishedQuiz() {
		return ! (_current_descriptor instanceof StatsDescriptor);
	}
	
	public void saveQuizResults() {
		Entry[] known;
		Entry[] notknown;
		if (_current_descriptor instanceof ResultDescriptor) {
			ResultDescriptor rd = (ResultDescriptor) _current_descriptor;
			known = rd.getKnownEntries();
			notknown = rd.getNotKnownEntries();
		} else if (! _repeat_mode) {
			known = _qdict.getKnownEntries();
			notknown = _qdict.getNotKnownEntries();
		} else {
			known = _known_entries;
			notknown = _notknown_entries;
		}

		StatsUpdateAction sua = createStatsUpdateAction(known, notknown);
		_model.getQueryModel().executeAction(sua);
	}
	
	public void panelDescriptorUpdated(WizardPanelDescriptor descriptor) {
		_qdict = ((StatsDescriptor) getPanelDescriptor("stats")).getQuizDict(); 
		
		super.panelDescriptorUpdated(descriptor);
	}
	
	private void saveResult(WizardPanelDescriptor d) {
		QueryResult result = null;
		Entry entry = _qdict.getCurrentEntry();
		
		if (entry == null)
			return;
		
		if (d instanceof EntryAnswerDescriptor) {
			EntryAnswerDescriptor ead = (EntryAnswerDescriptor) d;
			if (ead.getState() == YesNoPanel.YES_OPTION)
				result = new QueryResult(entry, true);
			else if (ead.getState() == YesNoPanel.NO_OPTION)
				result = new QueryResult(entry, false);
		} else if (d instanceof EntryInputDescriptor) {
			String attr_name = _qdict.getQuizInfo().getQuizzedAttribute();
			Attribute attr = _model.getDictModel().getMetaData(
					Entry.class).getAttribute(attr_name);
			EntryInputDescriptor eid = (EntryInputDescriptor) d;
			String answer = eid.getAnswer();
			String solution = attr.getFormattedValue(entry);
			// Strip leading and trailing blank spaces
			solution = solution.replaceAll("^\\s+", "");
			solution = solution.replaceAll("\\s+$", "");
			boolean match_case = JVLT.getConfig().getBooleanProperty(
				"match_case", true);
			if (! eid.isAnswerKnown())
				result = new QueryResult(entry, false);
			else if (match_case && answer.equals(solution))
				result = new QueryResult(entry, true, answer);
			else if (! match_case && answer.toLowerCase().equals(
				solution.toLowerCase()))
				result = new QueryResult(entry, true, answer);
			else
				result = new QueryResult(entry, false, answer);
		}
		
		if (result != null)
			_qdict.setResult(entry, result);
	}
	
	/**
	 * @param c Current descriptor
	 * @param n Next descriptor
	 */
	private void loadResult(WizardPanelDescriptor c, WizardPanelDescriptor n) {
		QueryResult result = _qdict.getResult(_qdict.getCurrentEntry());
		if (n instanceof EntryAnswerDescriptor) {
			EntryAnswerDescriptor ead = (EntryAnswerDescriptor) n;
			if (result == null) {
				String default_answer =
					JVLT.getConfig().getProperty("default_answer", "");
				if (default_answer.equals("yes"))	
					ead.setState(YesNoPanel.YES_OPTION);
				else if (default_answer.equals("no"))
					ead.setState(YesNoPanel.NO_OPTION);
				else
					ead.setState(YesNoPanel.UNKNOWN_OPTION);
			} else if (result.isKnown())
				ead.setState(YesNoPanel.YES_OPTION);
			else
				ead.setState(YesNoPanel.NO_OPTION);
		} else if (n instanceof EntryInputAnswerDescriptor) {
			EntryInputAnswerDescriptor eiad = (EntryInputAnswerDescriptor) n;
			eiad.setResult(result);
		}
	}
	
	private void loadEntry(EntryDescriptor ed, Entry entry) {
		ed.setEntry(entry);
		ed.setUserFlags(
				_flag_map.containsKey(entry) ? _flag_map.get(entry) : 0);
	}
	
	private void saveEntry(EntryDescriptor ed) {
		if (ed.getEntry() != null)
			_flag_map.put(ed.getEntry(), ed.getUserFlags());
	}
	
	private StatsUpdateAction createStatsUpdateAction(
			Entry[] known, Entry[] unknown) {
		StatsUpdateAction sua = new StatsUpdateAction(known, unknown);
		sua.setUpdateBatches(!_qdict.isIgnoreBatches()
			|| JVLT.getConfig().getBooleanProperty("update_batches", false));
		sua.setMessage(GUIUtils.getString(
			"Actions", "save_quiz_results"));
		
		/* Store flags */
		for (Entry e: _flag_map.keySet())
			sua.setUserFlag(e, _flag_map.get(e));
		
		return sua;
	}
}

class EntryAnswerDescriptor extends EntryDescriptor implements StateListener {
	private YesNoPanel _yes_no_panel;
	
	public EntryAnswerDescriptor(QuizModel m, SelectionNotifier n) {
		super(m,n);
	}
	
	public String getID() { return "entry_answer"; }

	public int getState() {
		return _yes_no_panel.getState();
	}
	
	public void setState(int state) {
		_yes_no_panel.setState(state);
	}
	
	public void stateChanged(StateEvent ev) {
		_model.panelDescriptorUpdated(this);
	}
	
	protected void init() {
		String msg = GUIUtils.getString("Messages", "entry_known");
		_yes_no_panel = new YesNoPanel(msg);
		_yes_no_panel.addStateListener(this);

		_panel = new JPanel();
		_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 1.0);
		_panel.add(_info_panel, cc);
		cc.update(0, 1, 1.0, 0.0);
		_panel.add(_flag_panel, cc);
		cc.update(0, 2, 1.0, 0.0);
		_panel.add(_yes_no_panel, cc);
	}
}

class EntryInputAnswerDescriptor extends EntryDescriptor {
	private JLabel _answer_label;
	
	public EntryInputAnswerDescriptor(QuizModel m, SelectionNotifier n) {
		super(m,n);
	}
	
	public String getID() { return "entry_input_answer"; }
	
	public void setResult(QueryResult result) {
		String text;
		String answer = result.getAnswer();
		if (result == null)
			text = "";
		else if (result.isKnown())
			text = GUIUtils.getString("Messages", "answer_correct",
				new String[]{answer});
		else { // ! result.isKnown()
			if (answer == null)
				text = GUIUtils.getString("Messages", "no_answer");
			else
				text = GUIUtils.getString("Messages", "answer_wrong",
					new String[]{answer});
		}
		
		_answer_label.setText(text);
	}
	
	protected void init() {
		_answer_label = new JLabel();
		
		_panel = new JPanel();
		_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 1.0);
		_panel.add(_info_panel, cc);
		cc.update(0, 1, 1.0, 0.0);
		_panel.add(_flag_panel, cc);
		cc.update(0, 2, 1.0, 0.0);
		_panel.add(_answer_label, cc);
	}
}

abstract class EntryDescriptor extends WizardPanelDescriptor {
	protected EntryInfoPanel _info_panel;
	protected QuizInfo _quiz_info;
	protected FlagPanel _flag_panel;
	
	public EntryDescriptor(QuizModel model, SelectionNotifier notifier) {
		super(model);
		_info_panel = new EntryInfoPanel(model.getJVLTModel(), notifier);
		_flag_panel = new FlagPanel();
		 _quiz_info = null;
		init();
	}
	
	protected abstract void init();

	public Entry getEntry() { return _info_panel.getEntry(); }
	
	public void setEntry(Entry entry) { _info_panel.setEntry(entry); }
	
	public int getUserFlags() {
		return _flag_panel.getSelectedItem().getValue();
	}
	
	public void setUserFlags(int flags) {
		Entry.Stats.UserFlag flag = Entry.Stats.UserFlag.NONE;
		
		/*
		 * Set user flag. Though there may be more than one flag set,
		 * only one is displayed.
		 */
		for (Entry.Stats.UserFlag f: Entry.Stats.UserFlag.values()) {
			if (f.getValue() != 0)
				if ((flags & f.getValue()) != 0)
				{
					flag = f;
					break;
				}
		}
		
		_flag_panel.setSelectedItem(flag);
	}
	
	public void setQuizInfo(QuizInfo info) {
		_quiz_info = info;
		entryAttributesUpdated();
	}

	public void dictUpdated(DictUpdateEvent event) {
		if (event instanceof NewDictDictUpdateEvent
				|| event instanceof LanguageDictUpdateEvent) {
			entryAttributesUpdated();
		} else if (event instanceof EntryDictUpdateEvent) {
			if (event.getType() == EntryDictUpdateEvent.ENTRIES_REMOVED) {
				EntryDictUpdateEvent edue = (EntryDictUpdateEvent) event;
				if (edue.getEntries().contains(getEntry()))
					/*
					 * Set null entry so wizard knows that panels have to be
					 * switched
					 */
					setEntry(null);
			}
		}
		/*
		 * Modifying entries or examples are all handled
		 * by the info panel.
		 */
	}
	
	protected void entryAttributesUpdated() {
		QuizModel model = (QuizModel) _model;
		String[] entryattrs = model.getJVLTModel().getDictModel().getMetaData(
			Entry.class).getAttributeNames();
		String[] exampleattrs = model.getJVLTModel().getDictModel().getMetaData(
				Example.class).getAttributeNames();
		_info_panel.setDisplayedEntryAttributes(entryattrs);
		_info_panel.setDisplayedExampleAttributes(exampleattrs);
	}
}

class EntryQuestionDescriptor extends EntryDescriptor {
	private JLabel _lbl;
	
	public EntryQuestionDescriptor(QuizModel m, SelectionNotifier n) {
		super(m,n);
	}
	
	public String getID() { return "entry_question"; }

	protected void init() {
		_lbl = new JLabel();
		
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 1.0);
		p.add(_info_panel, cc);
		cc.update(0, 1, 1.0, 0.0);
		p.add(_lbl, cc);
		
		_panel = p;
	}
	
	protected void entryAttributesUpdated() {
		if (_quiz_info == null) {
			_info_panel.setDisplayedEntryAttributes(new String[0]);
			_info_panel.setDisplayedExampleAttributes(new String[0]);
		} else {
			String[] attrs = _quiz_info.getShownAttributes();
			_info_panel.setDisplayedEntryAttributes(attrs);
			_info_panel.setDisplayedExampleAttributes(new String[0]);
			
			AttributeResources ar = new AttributeResources();
			String attr = _quiz_info.getQuizzedAttribute();
			if (attr != null)
				_lbl.setText(GUIUtils.getString("Messages", "entry_known_question",
					new Object[]{ar.getString(attr)}));
		}
	}
}

class EntryInputDescriptor extends EntryDescriptor
	implements ActionListener, ChangeListener {
	private CustomTextField _input_field;
	private JCheckBox _box;
	private JLabel _lbl;
	
	public EntryInputDescriptor(QuizModel m, SelectionNotifier n) {
		super(m,n);
	}
	
	public String getID() { return "entry_input"; }

	public boolean isAnswerKnown() { return _box.isSelected(); }
	
	public String getAnswer() {
		String answer = _input_field.getText();
		// Strip leading and trailing blank spaces
		answer = answer.replaceAll("^\\s+", "");
		answer = answer.replaceAll("\\s+$", "");
		return answer;
	}
	
	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("answer_known")) {
			_input_field.setEnabled(_box.isSelected());
			_model.panelDescriptorUpdated(this);
		}
	}
	
	public void stateChanged(ChangeEvent ev) {
		_model.panelDescriptorUpdated(this);
	}

	public void prepareToShow() {
		_input_field.setText("");
		_input_field.setEnabled(true);
		_input_field.requestFocusInWindow();
		_box.setSelected(true);
	}
	
	protected void init() {
		_lbl = new JLabel();
		_input_field = new CustomTextField(20);
		_input_field.addChangeListener(this);
		_box = new JCheckBox(GUIUtils.createTextAction(this, "answer_known"));
		JPanel input_panel = new JPanel();
		input_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		input_panel.add(_lbl, cc);
		cc.update(1, 0, 0.0, 0.0);
		input_panel.add(_box, cc);
		cc.update(2, 0, 0.0, 0.0);
		input_panel.add(_input_field, cc);
		
		_panel = new JPanel();
		_panel.setLayout(new GridBagLayout());
		cc.update(0, 0, 1.0, 1.0);
		_panel.add(_info_panel, cc);
		cc.update(0, 1, 1.0, 0.0);
		_panel.add(input_panel, cc);
	}

	protected void entryAttributesUpdated() {
		if (_quiz_info == null) {
			_info_panel.setDisplayedEntryAttributes(new String[0]);
			_info_panel.setDisplayedExampleAttributes(new String[0]);
		} else {
			String[] attrs = _quiz_info.getShownAttributes();
			_info_panel.setDisplayedEntryAttributes(attrs);
			_info_panel.setDisplayedExampleAttributes(new String[0]);
			
			AttributeResources ar = new AttributeResources();
			String attr = _quiz_info.getQuizzedAttribute();
			if (attr != null)
				_lbl.setText(GUIUtils.getString("Messages", "entry_known_question",
					new Object[]{ar.getString(attr)}));
		}
	}
}

class StatsDescriptor extends WizardPanelDescriptor implements ActionListener {
	private HashMap<String, QuizInfo> _quiz_info_map;
	private HashMap<String, QuizInfo> _visible_quiz_info_map;
	private HashMap<String, QuizInfo> _invisible_quiz_info_map;
	private Dict _dict;
	private QuizDict _qdict;
	
	private EntrySelectionDialogData _entry_selection_data;
	private JEditorPane _html_panel;
	private JLabel _select_words_label;
	private LabeledComboBox _quiz_info_box;
	private ActionHandler _quiz_info_box_listener = new ActionHandler();

	private class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JVLT.getRuntimeProperties().put("selected_quiz_type",
				_quiz_info_box.getSelectedItem());
			update();
		}
	}

	public StatsDescriptor(QuizModel model) {
		super(model);
		
		_quiz_info_map = new HashMap<String, QuizInfo>();
		_visible_quiz_info_map = new HashMap<String, QuizInfo>();
		_invisible_quiz_info_map = new HashMap<String, QuizInfo>();
		
		JVLTModel jm = model.getJVLTModel();
		_entry_selection_data = new EntrySelectionDialogData(jm);
		
		_dict = null;
		_qdict = new QuizDict(jm);
		_qdict.setIgnoreBatches(JVLT.getConfig().getBooleanProperty(
				"ignore_batches", false));
		
		init();
		loadQuizInfoList();
	}
	
	public String getID() { return "stats"; }
	
	public QuizDict getQuizDict() { return _qdict; }

	public int getSelectedEntries() { return _qdict.getAvailableEntryCount(); }

	public QuizInfo getQuizInfo() {
		Object name = _quiz_info_box.getSelectedItem();
		QuizInfo[] default_quiz_infos = QuizInfo.getDefaultQuizInfos();
		for (int i = 0; i < default_quiz_infos.length; i++)
			if (name.equals(default_quiz_infos[i].getName()))
				return default_quiz_infos[i];

		return _quiz_info_map.get(name);
	}

	/**
	 * Updates the list of available entries and refreshes the view.
	 * This function must not be called during a quiz.
	 */
	public void update() {
		updateQuizDict();
		updateView();
	}
	
	public void dictUpdated(DictUpdateEvent event) {
		if (event instanceof NewDictDictUpdateEvent) {
			_dict = ((NewDictDictUpdateEvent) event).getDict();
			loadQuizInfoList();
			updateEntrySelectionDialog();
			update();
		} else if (event instanceof LanguageDictUpdateEvent) {
			loadQuizInfoList();
			updateEntrySelectionDialog();
			update();
		} else if (event instanceof EntryDictUpdateEvent) {
			/* Update quiz dictionary */
			EntryDictUpdateEvent entry_event = (EntryDictUpdateEvent) event;
			int type = entry_event.getType();
			switch (type) {
			case EntryDictUpdateEvent.ENTRIES_ADDED:
				_qdict.update(entry_event.getEntries(), null, null);
				break;
			case EntryDictUpdateEvent.ENTRIES_CHANGED:
				_qdict.update(null, entry_event.getEntries(), null);
				break;
			case EntryDictUpdateEvent.ENTRIES_REMOVED:
				_qdict.update(null, null, entry_event.getEntries());
				break;
			}
			
			/* Update view */
			updateView();
			
			/* Enable/disable "Next" button */
			_model.panelDescriptorUpdated(this);
		}
	}
		
	public void actionPerformed(ActionEvent ev) {
		Frame frame = JOptionPane.getFrameForComponent(_panel);
		if (ev.getActionCommand().equals("select_words")) {
			// Do not use CustomDialog.showDialog() as there will be a
			// subdialog.
			CustomDialog dlg = new CustomDialog(_entry_selection_data, frame,
				GUIUtils.getString("Labels", "select_words"));
			GUIUtils.showDialog(frame, dlg);
			EntrySelectionDialogData.State oldstate
				= _entry_selection_data.getState();
			if (dlg.getStatus() == CustomDialog.OK_OPTION) {
				update();
				EntrySelectionDialogData.State state
					= _entry_selection_data.getState();
				EntrySelectionDialogData.State[] states = 
					(EntrySelectionDialogData.State[])
						JVLT.getRuntimeProperties().get("quiz_entry_filters");
				ArrayList<EntrySelectionDialogData.State> statelist =
					new ArrayList<EntrySelectionDialogData.State>();
				if (states != null)
					statelist.addAll(Arrays.asList(states));
				
				Iterator<EntrySelectionDialogData.State> it =
					statelist.iterator();
				while (it.hasNext())
					if (it.next().getLanguage().equals(state.getLanguage())) {
						it.remove();
						break;
					}
				statelist.add(state);
				JVLT.getRuntimeProperties().put("quiz_entry_filters",
					statelist.toArray(new EntrySelectionDialogData.State[0]));
			} else {
				// Initialize according to previous state
				_entry_selection_data.initFromState(oldstate);
			}
		} else if (ev.getActionCommand().equals("manage_quiz_types")) {
			QuizDialogData data = new QuizDialogData(
				((QuizModel) _model).getJVLTModel());
			data.setQuizInfoList(
				_visible_quiz_info_map.values().toArray(new QuizInfo[0]));

			CustomDialog dlg = new CustomDialog(data, frame,
				GUIUtils.getString("Labels", "manage_quiz_types"));
			GUIUtils.showDialog(frame, dlg);
			if (dlg.getStatus() == CustomDialog.OK_OPTION) {
				QuizInfo[] quiz_info_list = data.getQuizInfoList();
				_quiz_info_map.clear();
				_quiz_info_map.putAll(_invisible_quiz_info_map);
				for (int i=0; i<quiz_info_list.length; i++) {
					String name = quiz_info_list[i].getName();
					_quiz_info_map.put(name, quiz_info_list[i]);
				}
					
				updateQuizInfoList();
				JVLT.getRuntimeProperties().put("quiz_types", quiz_info_list);
			}
		} else if (ev.getActionCommand().equals("options")) {
			boolean old_ignore_batches = _qdict.isIgnoreBatches();
			
			QuizOptionsDialogData data = new QuizOptionsDialogData();
			CustomDialog.showDialog(data, StatsDescriptor.this._panel,
					GUIUtils.getString("Labels", "quiz_options"));
			_qdict.setIgnoreBatches(JVLT.getConfig().getBooleanProperty(
					"ignore_batches", false));
			
			if (old_ignore_batches != _qdict.isIgnoreBatches())
				update();
		}
	}
	
	private void init() {
		_select_words_label = new JLabel();
		
		Action select_words_action =
			GUIUtils.createTextAction(this, "select_words");
		Action manage_quiz_types_action =
			GUIUtils.createTextAction(this, "manage_quiz_types");
		Action options_action =
			GUIUtils.createTextAction(this, "options");
		
		_quiz_info_box = new LabeledComboBox();
		_quiz_info_box.setLabel("select_quiz_type");
		_quiz_info_box.addActionListener(_quiz_info_box_listener);
		
		JPanel settings_panel = new JPanel();
		settings_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.anchor = CustomConstraints.WEST;

		/* First row */
		cc.update(0, 0, 0.0, 0.0);
		settings_panel.add(_quiz_info_box.getLabel(), cc);
		cc.update(1, 0, 0.0, 0.0);
		settings_panel.add(_quiz_info_box, cc);
		cc.update(2, 0, 0.0, 0.0);
		settings_panel.add(new JButton(manage_quiz_types_action), cc);
		
		/* Second row */
		cc.update(0, 1, 0.0, 0.0);
		settings_panel.add(
				new JLabel(GUIUtils.getString("Labels", "options")+":"), cc);
		cc.update(1, 1, 0.0, 0.0);
		settings_panel.add(Box.createHorizontalGlue(), cc);
		cc.update(2, 1, 0.0, 0.0);
		settings_panel.add(new JButton(options_action), cc);

		/* Third row */
		cc.update(0, 2, 0.0, 0.0);
		settings_panel.add(
			new JLabel(GUIUtils.getString("Labels", "select_filters")+":"), cc);
		cc.update(1, 2, 0.0, 0.0);
		settings_panel.add(Box.createHorizontalGlue(), cc);
		cc.update(2, 2, 0.0, 0.0);
		settings_panel.add(new JButton(select_words_action), cc);
		
		_html_panel = new JEditorPane();
		_html_panel.setEditable(false);
		_html_panel.setContentType("text/html");
		JScrollPane spane = new JScrollPane(_html_panel);
		spane.setPreferredSize(new Dimension(400,300));
		
		_panel = new JPanel();
		_panel.setLayout(new GridBagLayout());
		cc.reset();
		cc.update(0, 0, 1.0, 1.0, 2, 1);
		_panel.add(spane, cc);
		cc.update(0, 1, 1.0, 0.0, 1, 1);
		_panel.add(Box.createHorizontalGlue(), cc);
		cc.update(1, 1, 0.0, 0.0, 1, 1);
		_panel.add(settings_panel, cc);
		cc.update(0, 2, 1.0, 0.0, 2, 1);
		_panel.add(_select_words_label, cc);
	}

	private synchronized void updateEntrySelectionDialog() {
		EntrySelectionDialogData.State[] states =
			(EntrySelectionDialogData.State[])
				JVLT.getRuntimeProperties().get("quiz_entry_filters");
		if (states != null) {
			int i;
			for (i=0; i<states.length; i++)
				if (states[i].getLanguage().equals(_dict.getLanguage())) {
					_entry_selection_data.initFromState(states[i]);
					break;
				}
		
			if (i == states.length)
				_entry_selection_data.initFromState(
						new EntrySelectionDialogData.State());
		} else {
			_entry_selection_data.initFromState(
					new EntrySelectionDialogData.State());
		}
	}
	
	private synchronized void updateQuizDict() {
		QuizInfo info = getQuizInfo();
		ObjectQuery[] oqs = _entry_selection_data.getObjectQueries();
		EntryFilter[] filters = new EntryFilter[oqs.length];
		for (int i=0; i<oqs.length; i++)
			filters[i] = new EntryFilter(oqs[i]);
		
		_qdict.update(filters, info);
		_model.panelDescriptorUpdated(this);
	}
	
	private synchronized void updateView() {
		Font font = JVLT.getConfig().getFontProperty("html_font");
		GregorianCalendar now = new GregorianCalendar();
		Collection<Entry> entries = _dict.getEntries();
		int num_entries = entries.size();
		int num_never_quizzed = 0;
		int total_num_quizzed = 0;
		int total_num_mistakes = 0;
		int not_expired = 0;
		int max_batch = 0;
		Map<Integer, Integer> batches = new HashMap<Integer, Integer>();
		Map<Integer, Integer> expired = new HashMap<Integer, Integer>();
		for (Iterator<Entry> it=entries.iterator(); it.hasNext(); ) {
			Entry entry = it.next();
			int batch = entry.getBatch();
			int num;
			
			if (batch > max_batch)
				max_batch = batch;
			
			if (entry.getNumQueried() == 0)
				num_never_quizzed++;
			
			total_num_quizzed += entry.getNumQueried();
			total_num_mistakes += entry.getNumMistakes();
			
			num = batches.containsKey(batch) ? batches.get(batch) : 0;
			batches.put(batch, num+1);
			if (batch > 0) {
				Calendar expire_date = entry.getExpireDate();
				if (expire_date == null || expire_date.before(now)) {
					num = expired.containsKey(batch) ? expired.get(batch) : 0;
					expired.put(batch, num+1);
				} else {
					not_expired++;
				}
			}
		}
		int num_expired = num_entries - num_never_quizzed - not_expired;
		
		String num_entries_str = String.valueOf(num_entries);
		String num_never_quizzed_str = String.valueOf(num_never_quizzed);
		String num_expired_str = String.valueOf(num_expired) + "/"
			+ String.valueOf(not_expired);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		double avg_num_quizzed = 0.0;
		if (num_entries>0)
			avg_num_quizzed = ((double) total_num_quizzed)/num_entries;
		String avg_num_quizzed_str = df.format(avg_num_quizzed);
		float mistake_ratio = 0.0f;
		if (total_num_quizzed > 0)
			mistake_ratio = total_num_mistakes*100.0f/total_num_quizzed;
		df.setMaximumFractionDigits(1);
		String avg_mistake_ratio_str = df.format(mistake_ratio) + "%";
		
		StringBuffer buffer = new StringBuffer();
		String label;
		buffer.append("<html>\n");
		if (font == null) {
			buffer.append("<body>");
		} else {
			buffer.append("<body style=\"font-family:" + font.getFamily()
				+ "; font-size:" + font.getSize() + "pt;\">\n");
		}
		buffer.append("<table width=\"100%\">\n");
		label = GUIUtils.getString("Labels", "num_entries") + ":";
		buffer.append(getRowString(label, num_entries_str));
		label = GUIUtils.getString("Labels", "never_quizzed_entries") + ":";
		buffer.append(getRowString(label, num_never_quizzed_str));
		label = GUIUtils.getString("Labels", "expired_entries") + ":";
		buffer.append(getRowString(label, num_expired_str));
		label = GUIUtils.getString("Labels", "avg_num_quizzed") + ":";
		buffer.append(getRowString(label, avg_num_quizzed_str));
		label = GUIUtils.getString("Labels", "avg_mistake_ratio") + ":";
		buffer.append(getRowString(label, avg_mistake_ratio_str));
		for (int i=0; i<=max_batch; i++) {
			if (! batches.containsKey(i) || batches.get(i) == 0)
				continue;
			
			label = GUIUtils.getString("Labels", "batch_no",
				new Integer[]{new Integer(i)}) + ":";
			ChoiceFormatter formatter = new ChoiceFormatter(
				GUIUtils.getString("Labels", "num_words"));
			String value = formatter.format(batches.get(i));
			int num_exp = expired.containsKey(i) ? expired.get(i) : 0;
			if (i > 0)
				value =	GUIUtils.getString("Labels", "words_expired",
					new Object[] {value, new Integer(num_exp)});

			buffer.append(getRowString(label, value));
		}
		buffer.append("</table>\n");
		buffer.append("</body>\n");
		buffer.append("</html>\n");
		_html_panel.setText(buffer.toString());
		
		label = GUIUtils.getString("Messages", "selected_words",
			new Object[] { new Integer(getSelectedEntries()) });
		_select_words_label.setText(label);
	}

	private String getRowString(String label, String value) {
		String str = "<tr>";
		str += "<td width=\"50%\">" + label + "</td>";
		str += "<td width=\"50%\">" + value + "</td>";
		str += "</tr>\n";
		
		return str;
	}
	
	private void loadQuizInfoList() {
		/*
		 * At this state, no quiz should be running, since the quiz dictionary
		 * will be reset.
		 */
		_quiz_info_map.clear();
		QuizInfo[] qinfos =
			(QuizInfo[]) JVLT.getRuntimeProperties().get("quiz_types");
		if (qinfos != null)
			for (int i=0; i<qinfos.length; i++)
				_quiz_info_map.put(qinfos[i].getName(), qinfos[i]);

		updateQuizInfoList();
	}

	private void updateQuizInfoList() {
		JVLTModel jm = ((QuizModel) _model).getJVLTModel();
		Dict dict = jm.getDict();
		String dict_lang = dict==null ? null : dict.getLanguage();
		QuizInfo[] default_quiz_infos = QuizInfo.getDefaultQuizInfos();

		_visible_quiz_info_map.clear();
		_invisible_quiz_info_map.clear();
		_quiz_info_box.removeActionListener(_quiz_info_box_listener);
		_quiz_info_box.removeAllItems();
		
		for (int i = 0; i < default_quiz_infos.length; i++)
			_quiz_info_box.addItem(default_quiz_infos[i].getName());
		
		for (Iterator<QuizInfo> it=_quiz_info_map.values().iterator();
				it.hasNext(); ) {
			QuizInfo info = (QuizInfo) it.next();
			if (info.getLanguage() == null ||
				info.getLanguage().equals(dict_lang)) {
				_visible_quiz_info_map.put(info.getName(), info);
				_quiz_info_box.addItem(info.getName());
			} else
				_invisible_quiz_info_map.put(info.getName(), info);
		}
		
		Object selected_quiz_type = JVLT.getRuntimeProperties().get(
				"selected_quiz_type");
		if (selected_quiz_type == null ||
				! _visible_quiz_info_map.containsKey(selected_quiz_type))
			_quiz_info_box.setSelectedItem(default_quiz_infos[0].getName());
		else
			_quiz_info_box.setSelectedItem(selected_quiz_type);
		
		_quiz_info_box.addActionListener(_quiz_info_box_listener);
	}
}

abstract class YesNoDescriptor extends WizardPanelDescriptor
	implements StateListener {
	private YesNoPanel _yes_no_panel;
	private JComponent _content_panel;
	
	public YesNoDescriptor(WizardModel model, String message) {
		super(model);
		_content_panel = null;
		init();
		setMessage(message);
	}
	
	public void stateChanged(StateEvent ev) {
		_model.panelDescriptorUpdated(this);
	}
	
	public int getState() { return _yes_no_panel.getState(); }
	
	public void setState(int state) { _yes_no_panel.setState(state); }
	
	public void setMessage(String message) {
		_yes_no_panel.setMessage(message);
	}
	
	public void setContentPanel(JComponent content) {
		if (_content_panel != null)
			_panel.remove(_content_panel);
		
		_content_panel = content;
		
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 1.0);
		_panel.add(content, cc);
	}
	
	private void init() {
		String msg = GUIUtils.getString("Messages", "repeat_words");
		_yes_no_panel = new YesNoPanel(msg);
		_yes_no_panel.addStateListener(this);
		
		_panel = new JPanel();
		_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 1, 1.0, 0.0);
		_panel.add(_yes_no_panel, cc);
	}
}

class RepeatDescriptor extends YesNoDescriptor {
	public RepeatDescriptor(WizardModel model) {
		super(model, GUIUtils.getString("Messages", "repeat_words"));
		setContentPanel(new JPanel());
	}
	
	public String getID() { return "repeat"; }
}

class ResultDescriptor extends YesNoDescriptor
	implements TreeSelectionListener, ActionListener {
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
	
	public String getID() { return "result"; }
	
	public Entry[] getKnownEntries() {
		return _known_entries_tree.getEntries();
	}
	
	public void setKnownEntries(Entry[] entries) {
		_known_entries_tree.setEntries(entries);
		if (entries.length > 0)
			_known_entries_tree.scrollRowToVisible(0);
		updateLabels();
	}
	
	public Entry[] getNotKnownEntries() {
		return _notknown_entries_tree.getEntries();
	}
	
	public void setNotKnownEntries(Entry[] entries) {
		_notknown_entries_tree.setEntries(entries);
		if (entries.length > 0)
			_notknown_entries_tree.scrollRowToVisible(0);
		updateLabels();
	}
	
	public void valueChanged(TreeSelectionEvent e) { updateActions(); }
	
	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("up")) {
			Entry entry = null;
			Object obj = _notknown_entries_tree.getSelectedObject();
			if (obj instanceof Entry)
				entry = (Entry) obj;
			else // obj instanceof Sense
				entry = ((Sense) obj).getParent();
			_notknown_entries_tree.removeEntry(entry);
			_known_entries_tree.addEntry(entry);
			updateLabels();
		} else if (ev.getActionCommand().equals("down")) {
			Entry entry = null;
			Object obj = _known_entries_tree.getSelectedObject();
			if (obj instanceof Entry)
				entry = (Entry) obj;
			else // obj instanceof Sense
				entry = ((Sense) obj).getParent();
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
		_known_entries_border = new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED));
		JScrollPane known_entries_scrpane = new JScrollPane();
		known_entries_scrpane.getViewport().setView(_known_entries_tree);
		known_entries_scrpane.setBorder(_known_entries_border);
		_notknown_entries_tree = new DictEntryTree();
		_notknown_entries_tree.addTreeSelectionListener(this);
		_notknown_entries_border = new TitledBorder(
			new EtchedBorder(EtchedBorder.LOWERED));
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
		_down_action.setEnabled(
			_known_entries_tree.getSelectedObject()!=null);
		_up_action.setEnabled(
			_notknown_entries_tree.getSelectedObject()!=null);
	}

	private void updateLabels() {
		ChoiceFormatter formatter = new ChoiceFormatter(
			GUIUtils.getString("Labels", "num_words"));
		String value = formatter.format(
			_known_entries_tree.getEntries().length);
		_known_entries_border.setTitle(GUIUtils.getString("Labels",
			"known_words", new Object[]{value}));
		value = formatter.format(
			_notknown_entries_tree.getEntries().length);
		_notknown_entries_border.setTitle(GUIUtils.getString("Labels",
			"not_known_words", new Object[]{value}));
		_comp.revalidate();
		_comp.repaint(_comp.getVisibleRect());
	}
}

class EntryNullDescriptor extends WizardPanelDescriptor {
	public EntryNullDescriptor(QuizModel model) {
		super(model);
		
		init();
	}
	
	public String getID() { return "entry_null"; }
	
	private void init() {
		JLabel label = new JLabel(
				GUIUtils.getString("Messages", "current_entry_removed"));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);

		_panel = label;
	}
}

class FlagPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private LabeledComboBox _flag_box;
	
	public FlagPanel() {
		_flag_box = new LabeledComboBox();
		_flag_box.setLabel("set_flag");
		_flag_box.setModel(new DefaultComboBoxModel(
				Entry.Stats.UserFlag.values()));
		
		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		add(Box.createHorizontalGlue(), cc);
		cc.update(1, 0, 0.0, 0.0);
		add(_flag_box.getLabel(), cc);
		cc.update(2, 0, 0.0, 0.0);
		add(_flag_box, cc);
	}
	
	public Entry.Stats.UserFlag getSelectedItem() {
		return (Entry.Stats.UserFlag) _flag_box.getSelectedItem();
	}
	
	public void setSelectedItem(Entry.Stats.UserFlag flag) {
		_flag_box.setSelectedItem(flag);
	}
}
