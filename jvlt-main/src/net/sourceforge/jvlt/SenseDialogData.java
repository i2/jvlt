package net.sourceforge.jvlt;

import java.awt.*;
import javax.swing.*;

public class SenseDialogData extends CustomDialogData {
	private Entry _entry;
	private Sense _sense;
	
	private CustomTextField _translation_field;
	private CustomTextField _definition_field;
	
	public SenseDialogData(Entry entry, Sense sense) {
		_entry = entry;
		_sense = sense;
	
		init();
	}

	public void updateData() throws InvalidDataException {
		if (_translation_field.getText().equals("") &&
			_definition_field.getText().equals(""))
			throw new InvalidDataException(GUIUtils.getString(
				"Messages", "no_translation_definition"));
		
		Sense sense = new Sense(_translation_field.getText(),
			_definition_field.getText());
		if (_entry.getSense(sense) != null)
			throw new InvalidDataException(GUIUtils.getString(
				"Messages", "duplicate_sense"));
		
		_sense.setTranslation(_translation_field.getText());
		_sense.setDefinition(_definition_field.getText());
	}
	
	private void init() {
		_translation_field = new CustomTextField (20);
		_translation_field.setActionCommand("translation");
		_definition_field = new CustomTextField (20);
		_definition_field.setActionCommand("definition");
		
		_content_pane = new JPanel();
		_content_pane.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		_content_pane.add(_translation_field.getLabel(), cc);
		cc.update(0, 1);
		_content_pane.add(_definition_field.getLabel(), cc);
		cc.update(1, 0);
		_content_pane.add(_translation_field, cc);
		cc.update(1, 1);
		_content_pane.add(_definition_field, cc);
		
		//----------
		// Init data.
		//----------
		_translation_field.setText (_sense.getTranslation());
		_definition_field.setText (_sense.getDefinition());
	}
}

