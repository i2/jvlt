package net.sourceforge.jvlt;

public class AttributeSelectionPanel extends ObjectSelectionPanel {
	private static final long serialVersionUID = 1L;
	
	public AttributeSelectionPanel() {
		super();
		setTranslateItems(true);
		setComparator(new AttributeComparator(_container));
	}
}

