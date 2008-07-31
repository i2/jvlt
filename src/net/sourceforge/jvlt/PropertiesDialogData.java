package net.sourceforge.jvlt;

import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.JPanel;

public class PropertiesDialogData extends CustomDialogData {
	private LanguageComboBox _language_box;
	
	public PropertiesDialogData(String lang) {
		_language_box = new LanguageComboBox();
		_language_box.setSelectedLanguage(lang);
		
		_content_pane = new JPanel();
		_content_pane.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 0.0, 0.0);
		_content_pane.add(_language_box.getLabel(), cc);
		cc.update(1, 0, 1.0, 0.0);
		_content_pane.add(_language_box, cc);
		cc.update(0, 1, 0.0, 1.0);
		_content_pane.add(Box.createVerticalGlue(), cc);
	}
	
	public String getLanguage() { return _language_box.getSelectedLanguage(); }

	public void updateData() throws InvalidDataException {}
}

