package net.sourceforge.jvlt;

import java.awt.*;
import javax.swing.*;

public class CustomTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	public void addTab(String title, Component comp) {
		String str = GUIUtils.getString("Actions", title);
		Integer mnemonic = GUIUtils.getMnemonicKey(str);
		str = str.replaceAll("\\$", "");
		super.addTab(str, comp);
		if (mnemonic != null)
			setMnemonicAt(getTabCount()-1, mnemonic.intValue());
	}
}

