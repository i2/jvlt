package net.sourceforge.jvlt;

public class AttributeChoiceListPanel extends ChoiceListPanel {
	private static final long serialVersionUID = 1L;
	
	public AttributeChoiceListPanel() {
		super(new AttributeChoiceInputComponent());
		setTranslateItems(true);
	}
	
	public AttributeChoice[] getSelectedAttributeChoices() {
		Object[] vals = super.getSelectedObjects();
		AttributeChoice[] choices = new AttributeChoice[vals.length];
		for (int i=0; i<vals.length; i++)
			choices[i] = (AttributeChoice) _container.getItem((String) vals[i]);
		
		return choices;
	}
}

