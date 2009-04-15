package net.sourceforge.jvlt;

public class MultimediaUtils {
	public static final String[] AUDIO_FILE_EXTENSIONS
		= { "wav", "wave", "aif", "aifc", "aiff", "au", "snd" };
	public static final String[] IMAGE_FILE_EXTENSIONS
		= { "gif", "jpg", "jpeg", "png", "tif", "tiff" };

	public static MultimediaFile getMultimediaFileForName(String name) {
		Config conf = JVLT.getConfig();
		String[] custom_extensions = conf.getStringListProperty(
			"custom_extensions", new String[0]);
		for (int i=0; i<custom_extensions.length; i++)
			if (name.toLowerCase().endsWith("."+custom_extensions[i])) {
				CustomMultimediaFile file = new CustomMultimediaFile(name);
				String[] ext_props = conf.getStringListProperty(
					"extension_"+custom_extensions[i], new String[0]);
				if (ext_props.length < 2) {
					file.setCommand("");
					file.setType(MultimediaFile.OTHER_FILE);
				}
				else {
					file.setCommand(ext_props[1]);
					file.setType(Integer.parseInt(ext_props[0]));
				}
				
				return file;
			}
		
		for (int i=0; i<AUDIO_FILE_EXTENSIONS.length; i++)
			if (name.toLowerCase().endsWith("."+AUDIO_FILE_EXTENSIONS[i]))
				return new AudioFile(name);
		
		for (int i=0; i<IMAGE_FILE_EXTENSIONS.length; i++)
			if (name.toLowerCase().endsWith("."+IMAGE_FILE_EXTENSIONS[i]))
				return new ImageFile(name);
		
		return new CustomMultimediaFile(name, MultimediaFile.OTHER_FILE);
	}
}

