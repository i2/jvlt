package net.sourceforge.jvlt.multimedia;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.reflectionassert.ReflectionAssert;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class CustomMultimediaFileTest {
	@Test
	public void testGetCommandArray() {
		String fileName = "test 01.mp3";
		int type = MultimediaFile.AUDIO_FILE;
		String command = "\"play mp3\" %f";
		String path = "/test/";
		CustomMultimediaFile file = new CustomMultimediaFile(
				fileName, type);
		file.setCommand(command);
		
		String[] commandArray = file.getCommandArray(path + fileName);
		ReflectionAssert.assertReflectionEquals(
				new String[] { "\"play mp3\"", "/test/test 01.mp3" },
				commandArray);
	}
}
