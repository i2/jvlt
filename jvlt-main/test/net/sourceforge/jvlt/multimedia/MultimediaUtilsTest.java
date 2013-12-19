package net.sourceforge.jvlt.multimedia;

import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class MultimediaUtilsTest {
	@Mock
	private CustomMultimediaFile customFile;
	
	/**
	 * Test whether MultimediaUtils.playAudioFile runs without throwing
	 * an exception.
	 */
	@Test
	public void testPlayAudioFiles() throws IOException {
		EasyMock.expect(customFile.getType())
			.andReturn(MultimediaFile.AUDIO_FILE);
		customFile.play();
		EasyMockUnitils.replay();
		
		MultimediaUtils.playAudioFile(customFile);
	}
}
