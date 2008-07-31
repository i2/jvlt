package net.sourceforge.jvlt;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class DateChooserButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 1L;

	private Calendar _date;
	
	public DateChooserButton(Calendar date) {
		super();
		setDate(date);
		addActionListener(this);
	}
	
	public DateChooserButton() { this(new GregorianCalendar()); }
	
	public Calendar getDate() { return _date; }
	
	public void setDate(Calendar date) {
		_date = date;
		
		DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
		setText(format.format(date.getTime()));
	}
	
	public void actionPerformed(ActionEvent ev) {
		DateChooser chooser = new DateChooser();
		chooser.setDate(_date);
		int result = CustomDialog.showDialog(chooser, this,
			GUIUtils.getString("Labels", "select_date"));
		if (result == CustomDialog.OK_OPTION)
			setDate(chooser.getDate());
	}
}

class DateChooser extends CustomDialogData implements ActionListener {
	private boolean _adjusting;
	private LabeledComboBox _day_box;
	private LabeledComboBox _month_box;
	private LabeledComboBox _year_box;
	
	public DateChooser() {
		_adjusting = false;
		init();
	}
	
	public void updateData() throws InvalidDataException {}
	
	private void init() {
		Calendar today = new GregorianCalendar();
		int year = today.get(Calendar.YEAR);
		String [] years = new String[20];
		for (int i=0; i<20; i++)
			years[i] = String.valueOf(year-15+i);
		String [] months = new String[12];
		for (int i=1; i<=12; i++)
			months[i-1] = String.valueOf(i);
		
		_day_box = new LabeledComboBox();
		_day_box.setLabel("day");
		_day_box.addActionListener(this);
		_month_box = new LabeledComboBox(months);
		_month_box.setLabel("month");
		_month_box.addActionListener(this);
		_year_box = new LabeledComboBox(years);
		_year_box.setLabel("year");
		_year_box.addActionListener(this);
		
		_content_pane = new JPanel();
		_content_pane.setLayout(new GridBagLayout());
		_content_pane.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		CustomConstraints cc = new CustomConstraints();
		cc.update(0, 0, 1.0, 0.0);
		_content_pane.add(_month_box.getLabel(), cc);
		cc.update(1, 0, 0.0, 0.0);
		_content_pane.add(_month_box, cc);
		cc.update(0, 1, 1.0, 0.0);
		_content_pane.add(_day_box.getLabel(), cc);
		cc.update(1, 1, 0.0, 0.0);
		_content_pane.add(_day_box, cc);
		cc.update(0, 2, 1.0, 0.0);
		_content_pane.add(_year_box.getLabel(), cc);
		cc.update(1, 2, 0.0, 0.0);
		_content_pane.add(_year_box, cc);
		
		setDate(today);		
	}
	
	public void actionPerformed(ActionEvent ev) {
		if (_adjusting)
			return;
		
		int year = Integer.parseInt(_year_box.getSelectedItem().toString());
		int month = Integer.parseInt(_month_box.getSelectedItem().toString());
		int day = Integer.parseInt(_day_box.getSelectedItem().toString());
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		int min_day = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		int max_day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		day = day>max_day ? max_day : day;
		day = day<min_day ? min_day : day;
		cal.set(Calendar.DAY_OF_MONTH, day);
		
		setDate(cal);
	}
	
	public synchronized void setDate(Calendar date) {
		_adjusting = true;
		
		int month = date.get(Calendar.MONTH)+1;
		int day = date.get(Calendar.DAY_OF_MONTH);
		int min_day = date.getActualMinimum(Calendar.DAY_OF_MONTH);
		int max_day = date.getActualMaximum(Calendar.DAY_OF_MONTH);
		int year = date.get(Calendar.YEAR);
		
		_year_box.setSelectedItem(String.valueOf(year));
		_month_box.setSelectedItem(String.valueOf(month));
		_day_box.removeAllItems();
		for (int i=min_day; i<=max_day; i++)
			_day_box.addItem(String.valueOf(i));
		_day_box.setSelectedItem(String.valueOf(day));
		
		_adjusting = false;
	}
	
	public Calendar getDate() {
		return new GregorianCalendar(
			Integer.parseInt(_year_box.getSelectedItem().toString()),
			Integer.parseInt(_month_box.getSelectedItem().toString())-1,
			Integer.parseInt(_day_box.getSelectedItem().toString()));
	}
}

