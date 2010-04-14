package net.sourceforge.jvlt.ui.vocabulary.entrydialog;

import net.sourceforge.jvlt.core.Sense;
import net.sourceforge.jvlt.model.JVLTModel;
import net.sourceforge.jvlt.ui.dialogs.InvalidDataException;
import net.sourceforge.jvlt.ui.utils.GUIUtils;

public class EditSenseDialogData extends SenseDialogData {
	protected Sense _orig_sense;
	
	public EditSenseDialogData(JVLTModel model, Sense sense) {
		super(model, (Sense) sense.clone());
		
		_orig_sense = sense;
	}
	
	@Override
	protected void check(Sense sense) throws InvalidDataException {
		if (_orig_sense.getParent().getSense(sense) != _orig_sense) {
			throw new InvalidDataException(GUIUtils.getString("Messages",
					"duplicate_sense"));
		}
	}
}
