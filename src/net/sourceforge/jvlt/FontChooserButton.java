package net.sourceforge.jvlt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class FontChooserButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private FontInfo _info = null;
	private JLabel _label = null;
	
	public FontChooserButton() { addActionListener(this); }
	
	public FontInfo getFontInfo() { return _info; }
	
	public void setFontInfo(FontInfo info) {
		_info = info;
		setText(info.getFamily() + " " + info.getSize());
		Font font = getFont().deriveFont(info.getStyle());
		setFont(font);
	}
	
	public JLabel getJLabel() { return _label; }
	
	public void setActionCommand(String command) {
		super.setActionCommand(command);
		_label = GUIUtils.getLabel(command, this);
	}
	
	public void actionPerformed(ActionEvent ev)	{
		FontChooser chooser = new FontChooser();
		chooser.setFontInfo(_info);
		int val = chooser.showDialog(this);
		if (val == JOptionPane.OK_OPTION)
			setFontInfo(chooser.getFontInfo());
	}
}

class FontChooser implements ActionListener, ListSelectionListener {
	private int _option;
	private InputList _font_list;
	private InputList _size_list;
	private JCheckBox _bold_box;
	private JCheckBox _italic_box;
	private JDialog _dlg;
	private JPanel _content_pane;
	private CustomTextField _preview_field;
	
	public FontChooser() {
		GraphicsEnvironment ge =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		_font_list = new InputList(ge.getAvailableFontFamilyNames(),
			"font_family");
		_font_list.addListSelectionListener(this);
		String[] font_sizes = new String[] { "8", "9", "10", "11", "12", "14",
			"16", "18", "20", "22", "24", "26", "28", "36", "48", "72" };
		_size_list = new InputList(font_sizes, "font_size");
		_size_list.addListSelectionListener(this);
		_bold_box = new JCheckBox(GUIUtils.getString("Labels", "font_bold"));
		_bold_box.setActionCommand("bold");
		_bold_box.addActionListener(this);
		_italic_box = new JCheckBox(GUIUtils.getString("Labels", "font_italic"));
		_italic_box.setActionCommand("italic");
		_italic_box.addActionListener(this);
			
		JPanel preview_panel = new JPanel();
		preview_panel.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), GUIUtils.getString("Labels", "preview")));
		_preview_field = new CustomTextField(20);
		_preview_field.setText("abcdefghijklmNOPQRSTUVWXYZ");
		JScrollPane preview_spane = new JScrollPane(_preview_field);
		preview_spane.setPreferredSize(new Dimension(100,50));
		preview_panel.setLayout(new GridLayout());
		preview_panel.add(preview_spane);
			
		Action ok_action = GUIUtils.createTextAction(this, "ok");
		Action cancel_action = GUIUtils.createTextAction(this, "cancel");
		
		JPanel selection_panel = new JPanel();
		selection_panel.setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0,0,1.0,1.0);
		selection_panel.add(_font_list, cc);
		cc.update(1,0,1.0,1.0);
		selection_panel.add(_size_list, cc);
		
		JPanel style_panel = new JPanel();
		style_panel.setLayout(new GridBagLayout());
		style_panel.setBorder(new TitledBorder(new EtchedBorder(
			EtchedBorder.LOWERED), GUIUtils.getString("Labels", "font_style")));
		cc.update(0, 0, 1.0, 0.0);
		style_panel.add(_bold_box, cc);
		cc.update(1, 0, 1.0, 0.0);
		style_panel.add(_italic_box, cc);
		
		JPanel button_panel = new JPanel();
		button_panel.setLayout(new GridBagLayout());
		cc.update(0, 0, 1.0, 0.0);
		button_panel.add(Box.createHorizontalGlue(), cc);
		cc.update(1, 0, 0.0, 0.0);
		button_panel.add(new JButton(ok_action), cc);
		cc.update(2, 0, 0.0, 0.0);
		button_panel.add(new JButton(cancel_action), cc);
			
		_content_pane = new JPanel();
		_content_pane.setLayout(new GridBagLayout());
		cc.update(0, 0, 1.0, 0.75);
		_content_pane.add(selection_panel, cc);
		cc.update(0, 1, 1.0, 0.0);
		_content_pane.add(style_panel, cc);
		cc.update(0, 2, 1.0, 0.25);
		_content_pane.add(preview_panel, cc);
		cc.update(0, 3, 1.0, 0.0);
		_content_pane.add(button_panel, cc);
	}
	
	public FontInfo getFontInfo() {
		int style;
		if (! _bold_box.isSelected() && ! _italic_box.isSelected())
			style = Font.PLAIN;
		else {
			style = 0;
			if (_bold_box.isSelected())
				style+=Font.BOLD;
			if (_italic_box.isSelected())
				style+=Font.ITALIC;
		}
		
		int size;
		try {
			size = Integer.parseInt(_size_list.getSelectedString());
		}
		catch (NumberFormatException ex) {
			size = 12;
		}
		
		return new FontInfo(_font_list.getSelectedString(),
			style, size);
	}
	
	public void setFontInfo(FontInfo info) {
		_font_list.setSelectedString(info.getFamily());
		_size_list.setSelectedString(String.valueOf(info.getSize()));
		_bold_box.setSelected((info.getStyle() & Font.BOLD)!=0);
		_italic_box.setSelected((info.getStyle() & Font.ITALIC)!=0);
		updatePreview();
	}
	
	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("ok")) {
			_option = JOptionPane.OK_OPTION;
			_dlg.setVisible(false);
		}
		else if (ev.getActionCommand().equals("cancel")) {
			_option = JOptionPane.CANCEL_OPTION;
			_dlg.setVisible(false);
		}
		else if (ev.getActionCommand().equals("bold")
			|| ev.getActionCommand().equals("italic"))
			updatePreview();
	}
	
	public void valueChanged(ListSelectionEvent ev) {
		if (! ev.getValueIsAdjusting())
			updatePreview();
	}
	
	public int showDialog(Component parent) {
		Frame frame = JOptionPane.getFrameForComponent(parent);
		_dlg = new JDialog(frame,
			GUIUtils.getString("Labels", "select_font"), true);
		_dlg.setContentPane(_content_pane);
		_option = JOptionPane.CANCEL_OPTION;
		GUIUtils.showDialog(frame, _dlg);
		
		return _option;
	}
	
	private void updatePreview() {
		Font font = getFontInfo().getFont();
		_preview_field.setFont(font);
		_content_pane.revalidate();
	}
}

class InputList extends JPanel implements ListSelectionListener {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ListSelectionListener> _listeners;
	private CustomTextField _field;
	private JList _list;
	
	public InputList(String[] data, String name) {
		_listeners = new ArrayList<ListSelectionListener>();
		
		_field = new CustomTextField(20);
		_field.setActionCommand(name);
		_list = new JList(data);
		_list.setVisibleRowCount(5);
		_list.addListSelectionListener(this);
		JScrollPane spane = new JScrollPane(_list);
		
		setLayout(new GridBagLayout());
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		add(_field.getLabel(), cc);
		cc.update(0, 1, 1.0, 0.0);
		add(_field, cc);
		cc.update(0, 2, 1.0, 1.0);
		add(spane, cc);
	}
	
	public String getSelectedString() {	return _field.getText(); }
	
	public void setSelectedString(String str) {
		_list.setSelectedValue(str, true);
		_field.setText(str);
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if (! e.getValueIsAdjusting()) {
			Object obj = _list.getSelectedValue();
			if (obj != null)
				_field.setText(obj.toString());
		}
		
		Iterator<ListSelectionListener> it = _listeners.iterator();
		while (it.hasNext())
			it.next().valueChanged(e);
	}
	
	public void addListSelectionListener(ListSelectionListener listener) {
		_listeners.add(listener);
	}
}
