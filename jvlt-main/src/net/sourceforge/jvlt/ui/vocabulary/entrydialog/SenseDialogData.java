package net.sourceforge.jvlt.ui.vocabulary.entrydialog;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.jvlt.core.Sense;
import net.sourceforge.jvlt.metadata.ChoiceAttribute;
import net.sourceforge.jvlt.metadata.MetaData;
import net.sourceforge.jvlt.model.JVLTModel;
import net.sourceforge.jvlt.ui.components.CustomTextField;
import net.sourceforge.jvlt.ui.dialogs.CustomDialogData;
import net.sourceforge.jvlt.ui.dialogs.InvalidDataException;
import net.sourceforge.jvlt.ui.utils.CustomConstraints;
import net.sourceforge.jvlt.ui.utils.GUIUtils;

public abstract class SenseDialogData extends CustomDialogData {
	protected final JVLTModel _model;
	protected Sense _sense;

	private CustomTextField _translation_field;
	private CustomTextField _definition_field;
	private CustomFieldPanel _custom_field_panel;

	public SenseDialogData(JVLTModel model, Sense sense) {
		_model = model;
		_sense = sense;

		init();
	}

	public Sense getSense() { return _sense; }

	@Override
	public void updateData() throws InvalidDataException {
		_sense.setTranslation(_translation_field.getText());
		_sense.setDefinition(_definition_field.getText());

		/* Custom fields */
		_custom_field_panel.updateData();
		_sense.setCustomFields(_custom_field_panel.getKeyValuePairs());

		if (_sense.getTranslation().equals("")
				&& _sense.getDefinition().equals(""))
			throw new InvalidDataException(GUIUtils.getString("Messages",
					"no_translation_definition"));
		
		check(_sense);
	}
	
	protected abstract void check(Sense sense) throws InvalidDataException;
	
	private void init() {
		_translation_field = new CustomTextField(20);
		_translation_field.setActionCommand("translation");
		_definition_field = new CustomTextField(20);
		_definition_field.setActionCommand("definition");
		
		_custom_field_panel = new CustomFieldPanel();
		_custom_field_panel.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED),
				GUIUtils.getLabelString("custom_fields")));
		
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
		cc.update(0, 2, 1.0, 1.0, 2, 1);
		_content_pane.add(_custom_field_panel, cc);
		
		// ----------
		// Init data.
		// ----------
		_translation_field.setText(_sense.getTranslation());
		_definition_field.setText(_sense.getDefinition());
		MetaData data = _model.getDictModel().getMetaData(Sense.class);
		ChoiceAttribute custom_field_attr =
			(ChoiceAttribute) data.getAttribute("CustomFields");
		_custom_field_panel.setChoices(custom_field_attr.getValues());
		_custom_field_panel.setKeyValuePairs(_sense.getCustomFields());
	}
}
