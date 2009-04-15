package net.sourceforge.jvlt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class EditEntriesAction extends DictAction {
	private ArrayList<EditEntryAction> _actions;

	public EditEntriesAction(EditEntryAction[] actions) {
		_actions = new ArrayList<EditEntryAction>();
		_actions.addAll(Arrays.asList(actions));
	}

	public void executeAction() throws DictException {
		for (Iterator<EditEntryAction> it=_actions.iterator(); it.hasNext(); )
			it.next().executeAction();
	}

	public void undoAction() throws DictException {
		for (Iterator<EditEntryAction> it=_actions.iterator(); it.hasNext(); )
			it.next().undoAction();
	}
	
	public Collection<Entry> getEntries() {
		ArrayList<Entry> entries = new ArrayList<Entry>();
		for (Iterator<EditEntryAction> it = _actions.iterator();
			it.hasNext(); ) {
			entries.add((Entry) it.next().getNewData());
		}
		
		return entries;
	}
}
