package net.sourceforge.jvlt.ui.components;

import java.awt.Component;

import javax.swing.JTabbedPane;

import net.sourceforge.jvlt.ui.utils.GUIUtils;

public class CustomTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	@Override
	public void addTab(String title, Component comp) {
		String str = GUIUtils.getString("Actions", title);
		Integer mnemonic = GUIUtils.getMnemonicKey(str);
		str = str.replaceAll("\\$", "");
		super.addTab(str, comp);
		if (mnemonic != null)
			setMnemonicAt(getTabCount() - 1, mnemonic.intValue());
	}
}
