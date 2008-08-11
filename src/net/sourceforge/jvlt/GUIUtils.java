package net.sourceforge.jvlt;

import java.awt.*;
import java.awt.event.ActionListener;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class GUIUtils {
	public static CustomAction createAnonymousAction(
		ActionListener listener, String action_command) {
		CustomAction action = new CustomAction(action_command);
		action.addActionListener(listener);
		
		return action;
	}
	
	public static CustomAction createTextAction(
		ActionListener listener, String action_command) {
		CustomAction action;
		if (listener == null)
			action = new CustomAction(action_command);
		else
			action = createAnonymousAction(listener, action_command);
		
		String action_string = getString("Actions", action_command);
		Integer mnemonic = getMnemonicKey(action_string);
		if (mnemonic != null) {
			action.putValue(Action.MNEMONIC_KEY, mnemonic);
			action_string = action_string.replaceAll("\\$", "");
		}
		
		action.putValue(Action.NAME, action_string);
		
		return action;
	}
	
	public static CustomAction createTextAction(String action_command) {
		return createTextAction(null, action_command);
	}
	
	public static CustomAction createIconAction(
		ActionListener listener, String action_command) {
		CustomAction action = createAnonymousAction(listener, action_command);
		String image_path = "/images/"+action_command+".png";
		URL image_url = GUIUtils.class.getResource(image_path);
		action.putValue(Action.SMALL_ICON, new ImageIcon(image_url));
		// Set message for tooltip text
		action.putValue(Action.SHORT_DESCRIPTION,
			getString("Labels", action_command));
		
		return action;
	}
	
	public static JMenu createMenu(String str) {
		JMenu menu = new JMenu();
		String name = getString("Actions", str);
		Integer mnemonic = getMnemonicKey(name);
		if (mnemonic != null) {
			menu.setMnemonic((char) mnemonic.intValue());
			name = name.replaceAll("\\$", "");
		}
		menu.setText(name);
		
		return menu;
	}
	
	public static JScrollPane createScrollPane(JComponent comp, String name) {
		JScrollPane pane = new JScrollPane();
		pane.getViewport().setView(comp);
		if (name != null)
			pane.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED), name));
		
		return pane;
	}
	
	public static String getString (String resource_bundle, String name) {
		return getString(resource_bundle, name, null);
	}
	
	public static String getString (String resource_bundle, String name,
		Object[] args) {
		ResourceBundle messages = ResourceBundle.getBundle(
			resource_bundle, Locale.getDefault());
		String str = messages.getString(name);
		
		if (args == null)
			return str;
		else {
			MessageFormat formatter = new MessageFormat("");
			formatter.setLocale(Locale.getDefault());
			formatter.applyPattern(str);
			return formatter.format(args);
		}
	}
	
	/**
	 * If the string contains a "$", return the value
	 * of the character after "$". Otherwise, return <i>null</i>. */
	public static Integer getMnemonicKey(String str) {
		int index = str.indexOf('$');
		if (index >= 0) {
			char mnemonic = Character.toUpperCase(str.charAt(index+1));

			return new Integer(mnemonic);
		}
		else
			return null;
	}
	
	public static JLabel getLabel(String lbl, Component comp) {
		String str = getString("Actions", lbl);
		JLabel label = new JLabel();
		Integer mnemonic = getMnemonicKey(str);
		if (mnemonic != null) {
			label.setLabelFor(comp);
			label.setDisplayedMnemonic(mnemonic.intValue());
			str = str.replaceAll("\\$", "");
		}
		label.setText(str+":");
		
		return label;
	}
	
	public static Component getFrameOrDialogForComponent(Component parent)
		throws HeadlessException {
		if (parent == null)
			return null;
		else if ((parent instanceof Frame) || (parent instanceof Dialog))
			return parent;
		else
			return GUIUtils.getFrameOrDialogForComponent(parent.getParent());
	}
	
	/* Shows a dialog in the middle of a frame. */
	public static void showDialog(Component parent, Dialog dlg) {
		dlg.pack();
		Component comp = getFrameOrDialogForComponent(parent);
		Rectangle pbounds = comp.getBounds();
		Rectangle dbounds = dlg.getBounds();
		dlg.setLocation(
			(int) ((pbounds.getWidth()-dbounds.getWidth())/2+pbounds.getX()),
			(int) ((pbounds.getHeight()-dbounds.getHeight())/2+pbounds.getY()));
		dlg.setVisible(true);
	}

	public static int showSaveDiscardCancelDialog(Frame frame, String message) {
		String text = GUIUtils.getString("Messages", message);
		String title = GUIUtils.getString("Labels", "confirm");
		Object[] options = {
			GUIUtils.getString("Actions", "yes_save"),
			GUIUtils.getString("Actions", "no_discard"),
			// Do not use "Actions" as we want no mnemonic key.
			GUIUtils.getString("Labels", "cancel") };
		
		return JOptionPane.showOptionDialog(frame, text, title,
			JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
			null, options, options[2]);
	}
}

