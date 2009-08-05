package net.sourceforge.jvlt;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorLogDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JTextArea _text_area;
	
	public ErrorLogDialog(Frame parent) {
		super(parent, GUIUtils.getString("Labels", "error_log"), true);
		
		_text_area = new JTextArea();
		_text_area.setEditable(false);
		_text_area.setText(ErrorLog.getInstance().getLines());
		JScrollPane scrpane = new JScrollPane(_text_area);
		scrpane.setPreferredSize(new Dimension(400, 320));
		
		Action close_action = GUIUtils.createTextAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) { setVisible(false); }
		}, "close");
		
		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0,0,1.0,1.0);
		getContentPane().add(scrpane, cc);
		cc.update(0,1,0.0,0.0);
		cc.fill = CustomConstraints.NONE;
		getContentPane().add(new JButton(close_action), cc);
	}
}
