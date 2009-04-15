package net.sourceforge.jvlt;

import java.awt.Font;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class XSLUtils {
	private static GregorianCalendar _now = new GregorianCalendar();

	public static String expired(String date_string) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date date = sdf.parse(date_string);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			if (cal.before(_now))
				return "true";
			else
				return "false";
		}
		catch (ParseException ex) { return "false"; }
	}
	
	public static String fontFamily(String key, String default_value) {
		Font font = new Font(default_value, Font.PLAIN, 12);
		return JVLT.getConfig().getFontProperty(key, font).getFamily();
	}

	public static String fontFamily(String key) {
		return fontFamily(key, "Dialog");
	}
	
	public static String fontSize(String key, float default_value) {
		Font font = new Font("Dialog", Font.PLAIN, (int) default_value);
		return String.valueOf(
			JVLT.getConfig().getFontProperty(key, font).getSize());
	}
	
	public static String fontSize(String key) { return fontSize(key, 12); }
	
	public static String formatDate(String date_string)	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date date = sdf.parse(date_string);
			DateFormat format = DateFormat.getDateTimeInstance(
				DateFormat.MEDIUM, DateFormat.MEDIUM);
			
			return format.format(date);
		}
		catch (ParseException ex) {
			return date_string;
		}
	}
	
	public static String i18nString(String key) {
		return GUIUtils.getString("Labels", key);
	}
		
	public static String translate(String key) {
		AttributeResources resources = new AttributeResources();
		return resources.getString(key);
	}
	
	public static String mimeTypeImage(String file_name) {
		MultimediaFile file =
			MultimediaUtils.getMultimediaFileForName(file_name);
		if (file.getType() == MultimediaFile.AUDIO_FILE)
			return "/images/audio.png";
		else if (file.getType() == MultimediaFile.IMAGE_FILE)
			return "/images/image.png";
		else // if (file.getType() == MultimediaFile.OTHER_FILE)
			return "/images/unknown.png";
	}
}

