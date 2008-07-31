package net.sourceforge.jvlt;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.sourceforge.jvlt.event.DialogListener;
import net.sourceforge.jvlt.event.DialogListener.DialogEvent;

public abstract class AbstractDialog extends JDialog implements ActionListener {
	public static final int OK_OPTION = 1;
	public static final int APPLY_OPTION = 2;
	public static final int CANCEL_OPTION = 3;
	public static final int CLOSE_OPTION = 4;
	
	private int[] _buttons = new int[] { OK_OPTION, CANCEL_OPTION };
	private int _default_button = OK_OPTION;
	private LinkedList<DialogListener> _listeners;
	
	private Container _content = null;
	private JPanel _button_panel = null;
	
	public AbstractDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		
		_listeners = new LinkedList<DialogListener>();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				fireDialogEvent(new DialogEvent(AbstractDialog.this,
					CLOSE_OPTION));
			}
		});
		
		init();
	}
	
	public void addDialogListener(DialogListener l)	{ _listeners.addLast(l); }
	
	public void setContent(Container container) {
		_content = container;
		init();
	}
	
	public void setButtons(int[] buttons) {
		_buttons = buttons;
		init();
	}
	
	public void actionPerformed(ActionEvent ev) {
		String command = ev.getActionCommand();
		int value = getValueForFieldName(command);
		fireDialogEvent(new DialogEvent(this, value));
	}

	protected void fireDialogEvent(DialogEvent ev) {
		Iterator<DialogListener> it = _listeners.iterator();
		while (it.hasNext())
			it.next().dialogStateChanged(ev);
	}
	
	public void setDefaultButton(int button) {
		_default_button = button;
		Component comp = getComponent(getFieldNameForValue(button));
		if (comp != null)
			getRootPane().setDefaultButton((JButton) comp);
	}
	
	protected String getFieldNameForValue(int value) {
		Field[] fields = AbstractDialog.class.getFields();
		for (int i=0; i<fields.length; i++) {
			Field field = fields[i];
			if (field.getType().getName().equals("int"))
				try {
					int val = field.getInt(null);
					if (val == value)
						return field.getName();
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
		}
		
		return null;
	}
	
	protected int getValueForFieldName(String name) {
		try {
			Field field = AbstractDialog.class.getField(name);
			
			return field.getInt(null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return -1;
	}

	private Component getComponent(String name)	{
		Component[] comps = _button_panel.getComponents();
		for (int i=0; i<comps.length; i++) {
			String comp_name = comps[i].getName();
			if (comp_name == null)
				continue;
			else if (comp_name.equals(name))
				return comps[i];
		}
		
		return null;
	}
	
	private void init()	{
		JPanel content_pane = new JPanel();

		_button_panel = new JPanel();
		_button_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		_button_panel.add(Box.createHorizontalGlue(), cc);
		for (int i=0; i<_buttons.length; i++) {
			String field_name = getFieldNameForValue(_buttons[i]);
			Action action = GUIUtils.createTextAction(this, field_name);
			JButton button = new JButton(action);
			button.setName(field_name);
			content_pane.getActionMap().put(field_name, action);
			cc.update(i+1, 0, 0.0, 0.0);
			_button_panel.add(button, cc);
		}
		
		content_pane.setLayout(new GridBagLayout());
		cc.update(0, 0, 1.0, 1.0);
		if (_content == null)
			content_pane.add(new JPanel(), cc);
		else
			content_pane.add(_content, cc);
		cc.update(0, 1, 1.0, 0.0);
		content_pane.add(_button_panel, cc);
		content_pane.setBorder(new EmptyBorder(5,5,5,5));
		
		setDefaultButton(_default_button);
		setContentPane(content_pane);
	}
}

