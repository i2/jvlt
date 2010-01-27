package net.sourceforge.jvlt.ui.dialogs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.text.DateFormat;
import java.util.Date;

import net.sourceforge.jvlt.ui.JVLTUI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Verifies that the error log behaves correctly after initialization.
 * 
 * @author thrar
 */
@SuppressWarnings("PMD.SystemPrintln")
// needed for this test to make sure it's logged
public class ErrorLogDialogTester {

	private ErrorLogDialog dialog;
	
	private Logger logger;

	/**
	 * Initializes the global configuration and sets up a log dialog.
	 */
	@BeforeClass
	public void initLog() {
		dialog = new ErrorLogDialog(null);
		logger = Logger.getLogger(JVLTUI.class);
	}

	/**
	 * Clears the log area before each test.
	 */
	@BeforeMethod
	public void prepareLog() {
		dialog._text_area.setText("");
	}

	/**
	 * Ensures that messages written to System.err show up in the error log
	 * dialog.
	 */
	@Test()
	public void errorsInLog() {
		String message = "System.err message";
		logger.error(message);
		String timeStamp = DateFormat.getTimeInstance().format(new Date());

		assertLogContains(message);
		assertFormat(timeStamp, message);
	}

	/**
	 * Provides logger levels that need to show up in the log dialog.
	 * 
	 * @return logger levels that need to show up in the log dialog
	 */
	@DataProvider(name = "levelsToLog")
	public Object[][] provideLevelsToLog() {
		return new Object[][] { { Level.WARN }, { Level.ERROR },
				{ Level.FATAL } };
	}

	/**
	 * Ensures that messages written to the logger at high levels show up in the
	 * log.
	 */
	@Test(dataProvider = "levelsToLog")
	public void loggerMessagesInLog(Level level) {
		String message = "Logger message";

		// pretend to log from the UI
		logger.log(level, message);
		String timeStamp = DateFormat.getTimeInstance().format(new Date());

		assertLogContains(message);
		assertFormat(timeStamp, message);
	}

	/**
	 * Provides logger levels that mustn't show up in the log dialog.
	 * 
	 * @return logger levels that mustn't show up in the log dialog
	 */
	@DataProvider(name = "levelsNotToLog")
	public Object[][] provideLevelsToNotLog() {
		return new Object[][] { { Level.TRACE }, { Level.DEBUG },
				{ Level.INFO } };
	}

	/**
	 * Ensures that messages written to the logger at low levels don't show up
	 * in the log.
	 */
	@Test(dataProvider = "levelsNotToLog")
	public void loggerMessagesNotInLog(Level level) {
		String message = "Logger message";

		// pretend to log from the UI
		logger.log(level, message);

		assertFalse(dialog._text_area.getText().contains(message),
				"Info message showed up in text area, was '"
						+ dialog._text_area.getText() + "'");
	}

	/**
	 * Ensures that log messages followed by a System.err message are not
	 * overwritten.
	 */
	@Test
	public void loggerFollowedByError() {
		String logMessage = "log message";
		String errMessage = "err message";

		logger.error(logMessage);
		logger.error(errMessage);

		assertLogContains(logMessage);
		assertLogContains(errMessage);
	}

	/**
	 * Asserts that the given message is displayed in the error log window.
	 * 
	 * @param message the message that should be displayed
	 */
	private void assertLogContains(String message) {
		assertTrue(dialog._text_area.getText().contains(message),
				"Message didn't show up in text area, was '"
						+ dialog._text_area.getText() + "'");
	}

	/**
	 * Ensures that the given message and time stamp are correctly formatted in
	 * the log output.
	 * 
	 * @param timeStamp time stamp to be displayed
	 * @param message log message to be displayed
	 */
	private void assertFormat(String timeStamp, String message) {
		String expected = timeStamp + " - " + message + "\r\n";
		assertEquals(dialog._text_area.getText(), expected,
				"Logger message not formatted correctly");
	}
}
