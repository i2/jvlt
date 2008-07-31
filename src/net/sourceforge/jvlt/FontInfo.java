package net.sourceforge.jvlt;

import java.awt.Font;

public class FontInfo {
	private String _family;
	private int _size;
	private int _style;
	
	public FontInfo(String family, int style, int size) {
		_family = family;
		_style = style;
		_size = size;
	}
	
	public FontInfo(Font font) {
		this(font.getFamily(), font.getStyle(), font.getSize());
	}
	
	public String getFamily() { return _family; }
	
	public int getSize() { return _size; }
	
	public int getStyle() { return _style; }
	
	public Font getFont() { return new Font(_family, _style, _size); }
}

