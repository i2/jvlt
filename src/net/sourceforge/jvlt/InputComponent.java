package net.sourceforge.jvlt;

import javax.swing.JComponent;

public interface InputComponent {
	public abstract JComponent getComponent();
	
	public abstract Object getInput();

	public abstract void setInput(Object input);

	public abstract void reset();
}

