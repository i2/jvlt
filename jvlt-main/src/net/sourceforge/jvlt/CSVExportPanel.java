package net.sourceforge.jvlt;

import java.awt.GridBagLayout;
import javax.swing.JLabel;

public class CSVExportPanel extends CSVPanel {
	private static final long serialVersionUID = 1L;

	protected void initLayout() {
		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0,0,1.0,0.0,2,1);
		add(new JLabel(GUIUtils.getString("Messages", "csv_export")), cc);
		cc.update(0,1,0.5,0.0,1,1);
		add(_charset_box.getLabel(), cc);
		cc.update(1,1,0.5,0.0);
		add(_charset_box, cc);
		cc.update(0,2,0.5,0.0);
		add(_field_delim_box.getLabel(), cc);
		cc.update(1,2,0.5,0.0);
		add(_field_delim_box, cc);
		cc.update(0,3,0.5,0.0);
		add(_text_delim_box.getLabel(), cc);
		cc.update(1,3,0.5,0.0);
		add(_text_delim_box, cc);
	}
}
