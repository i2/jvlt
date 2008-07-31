package net.sourceforge.jvlt;

public class RemoveSenseAction extends DictObjectAction {
	private int _position;
	
	public RemoveSenseAction(Sense sense, int position) {
		super(sense);
		_position = position;
	}
	
	public int getOldPosition() { return _position; }
}

