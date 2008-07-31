package net.sourceforge.jvlt.event;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

import net.sourceforge.jvlt.GUIUtils;

class PopupListener extends MouseAdapter {
	protected JPopupMenu _menu = null;
	
	public void setPopupMenu(JPopupMenu m) { _menu = m; }
	
	public void mousePressed(MouseEvent e) { handlePopupEvent(e); }

	public void mouseClicked(MouseEvent e) { handlePopupEvent(e); }

	public void mouseReleased(MouseEvent e) { handlePopupEvent(e); }
	
	protected void handlePopupEvent(MouseEvent e) {
		if (e.isPopupTrigger() && _menu != null)
			_menu.show((Component) e.getSource(), e.getX(), e.getY());
	}
}

public class CopyPastePopupListener extends PopupListener {
	private Action _copy_action;
	private Action _paste_action;
	private JTextComponent _component;

	public CopyPastePopupListener(JTextComponent comp) {
		_component = comp;
		
		_copy_action = new DefaultEditorKit.CopyAction();
		String str = GUIUtils.getString("Actions", "copy");
		Integer mnemonic = GUIUtils.getMnemonicKey(str);
		_copy_action.putValue(Action.NAME, str.replaceAll("\\$", ""));
		if (mnemonic != null)
			_copy_action.putValue(Action.MNEMONIC_KEY, mnemonic);
		
		_paste_action = new DefaultEditorKit.PasteAction();
		str = GUIUtils.getString("Actions", "paste");
		mnemonic = GUIUtils.getMnemonicKey(str);
		_paste_action.putValue(Action.NAME, str.replaceAll("\\$", ""));
		if (mnemonic != null)
			_paste_action.putValue(Action.MNEMONIC_KEY, mnemonic);
		
		_menu = new JPopupMenu();
		_menu.add(_copy_action);
	}
	
	protected void handlePopupEvent(MouseEvent e) {
		String selected = _component.getSelectedText();
		_copy_action.setEnabled(selected != null && ! selected.equals(""));
		if (_component.isEditable() && _menu.getSubElements().length<2)
			_menu.add(_paste_action);
		else if (! _component.isEditable() && _menu.getSubElements().length>1)
			_menu.remove(1);
		
		super.handlePopupEvent(e);
	}
}

