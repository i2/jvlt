package net.sourceforge.jvlt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.sourceforge.jvlt.event.ComponentReplacementListener;
import net.sourceforge.jvlt.event.DialogListener;

public class CustomDialog extends AbstractDialog implements DialogListener {
	private class ComponentReplacementHandler
		implements ComponentReplacementListener {
		public void componentReplaced(ComponentReplacementEvent ev) {
			CustomDialog.this.pack();
		}
	}

	private static final long serialVersionUID = 1L;
	
	private int _value;
	private CustomDialogData _data;
	private ComponentReplacementHandler _handler
		= new ComponentReplacementHandler();
	
	private static CustomDialog _dialog = null;
	
	public static class TooManyInstancesException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	public static int showDialog (CustomDialogData data,
		Component parent, String title) {
		if (_dialog != null)
			throw new TooManyInstancesException();
		
		Frame frame = JOptionPane.getFrameForComponent(parent);
		_dialog = new CustomDialog (data, frame, title);
		GUIUtils.showDialog(parent, _dialog);
		int value = _dialog._value;
		_dialog = null;
		
		return value;
	}

	public CustomDialog (CustomDialogData data, Frame parent, String title) {
		super(parent, title, true);
		
		_value = CANCEL_OPTION;
		_data = data;
		setButtons(_data.getButtons());
		setDefaultButton(OK_OPTION);
		setContent(_data.getContentPane());
		addDialogListener(this);
		data.addComponentReplacementListener(_handler);
		// If the cancel or close button exists, close the window when
		// the Escape keyboard button is pressed.
		InputMap map = ((JComponent) getContentPane()).getInputMap(
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			getFieldNameForValue(CANCEL_OPTION));
	}

	public int getStatus() { return _value; }
	
	public void dialogStateChanged(DialogEvent ev) {
		if (ev.getType() == AbstractDialog.OK_OPTION) {
			_value = OK_OPTION;
			try {
				_data.updateData();
				setVisible(false);
			} catch (InvalidDataException e) {
				MessageDialog.showDialog(this, MessageDialog.WARNING_MESSAGE,
					e.getMessage());
			}
		} else if (ev.getType() == AbstractDialog.CANCEL_OPTION
			|| ev.getType() == AbstractDialog.CLOSE_OPTION) {
			_value = CANCEL_OPTION;
			setVisible(false);
		}
	}
	
	/**
	 * Show/hide the dialog.
	 * Before the dialog is shown, {@link CustomDialogData#prepareToShow()} is
	 * called.
	 */
	public void setVisible(boolean visible) {
		if (_data != null && visible)
			_data.prepareToShow();
		
		super.setVisible(visible);
	}
}

