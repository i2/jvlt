package net.sourceforge.jvlt.ui.quiz;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.jvlt.ui.utils.GUIUtils;
import net.sourceforge.jvlt.ui.wizard.WizardPanelDescriptor;

class EntryNullDescriptor extends WizardPanelDescriptor {
	public EntryNullDescriptor(QuizModel model) {
		super(model);

		init();
	}

	@Override
	public String getID() {
		return "entry_null";
	}

	private void init() {
		JLabel label = new JLabel(GUIUtils.getString("Messages",
				"current_entry_removed"));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);

		_panel = label;
	}
}
