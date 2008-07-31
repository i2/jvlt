package net.sourceforge.jvlt;

import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerModel;

public class LabeledSpinner extends JSpinner {
	private static final long serialVersionUID = 1L;

	private JLabel _label;
	
	public LabeledSpinner(SpinnerModel m) { super(m); }
	
	public void setLabel(String label) {
		_label = GUIUtils.getLabel(label, this); }

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		_label.setEnabled(enabled);
	}
	
	public JLabel getLabel() { return _label; }
}

