package net.sourceforge.jvlt.ui.quiz;

import javax.swing.JPanel;

import net.sourceforge.jvlt.ui.utils.GUIUtils;
import net.sourceforge.jvlt.ui.wizard.WizardModel;

class RepeatDescriptor extends YesNoDescriptor {
	public RepeatDescriptor(WizardModel model) {
		super(model, GUIUtils.getString("Messages", "repeat_words"));
		setContentPanel(new JPanel());
	}

	@Override
	public String getID() {
		return "repeat";
	}
}
