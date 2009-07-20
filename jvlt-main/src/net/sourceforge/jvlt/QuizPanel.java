package net.sourceforge.jvlt;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.sourceforge.jvlt.event.SelectionNotifier;

public class QuizPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JVLTModel _model;
	private Wizard _wizard;
	
	public QuizPanel(JVLTModel model, SelectionNotifier notifier) {
		_model = model;
		_wizard = new Wizard(new QuizModel(_model, notifier));
		
		setLayout(new BorderLayout());
		add(_wizard.getContent());
	}
	
	public Wizard getWizard() { return _wizard; }
}
