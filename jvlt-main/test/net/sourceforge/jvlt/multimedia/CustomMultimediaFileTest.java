package net.sourceforge.jvlt.multimedia;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class CustomMultimediaFileTest {
	@Test
	public void testGetCommandString() {
		String fileName = "test.mp3";
		int type = MultimediaFile.AUDIO_FILE;
		String command = "play %f";
		String path = "\\\\test\\";
		CustomMultimediaFile file = new CustomMultimediaFile(
				fileName, type);
		file.setCommand(command);
		
		String commandString = file.getCommandString(path + fileName);
		Assert.assertEquals("Wrong command string: " + commandString,
				"play \"\\\\test\\test.mp3\"", commandString);
	}
}
