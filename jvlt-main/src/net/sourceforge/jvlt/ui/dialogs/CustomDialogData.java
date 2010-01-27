package net.sourceforge.jvlt.ui.dialogs;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.jvlt.event.ComponentReplacementListener;
import net.sourceforge.jvlt.event.ComponentReplacementListener.ComponentReplacementEvent;
import net.sourceforge.jvlt.utils.Config;

/** Content of {@link CustomDialog} */
public abstract class CustomDialogData {
	protected ArrayList<ComponentReplacementListener> _listeners;

	protected JPanel _content_pane;

	public CustomDialogData() {
		_listeners = new ArrayList<ComponentReplacementListener>();
	}

	public int[] getButtons() {
		return new int[] { AbstractDialog.OK_OPTION,
				AbstractDialog.CANCEL_OPTION };
	}

	public JPanel getContentPane() {
		return _content_pane;
	}

	public void addComponentReplacementListener(ComponentReplacementListener l) {
		_listeners.add(l);
	}

	public void removeComponentReplacementListener(
			ComponentReplacementListener l) {
		_listeners.remove(l);
	}

	public abstract void updateData() throws InvalidDataException;

	/**
	 * This method is called right before the dialog is shown.
	 * 
	 * @see CustomDialog#setVisible(boolean)
	 */
	public void prepareToShow() {
		// do nothing by default
	}

	protected void fireComponentReplacementEvent(JComponent old_comp,
			JComponent new_comp) {
		ComponentReplacementEvent ev = new ComponentReplacementEvent(old_comp,
				new_comp);
		for (Iterator<ComponentReplacementListener> it = _listeners.iterator(); it
				.hasNext();)
			it.next().componentReplaced(ev);
	}

	protected void loadState(Config config) {
		// do nothing by default
	}

	protected void saveState(Config config) {
		// do nothing by default
	}
}
