package net.sourceforge.jvlt.utils; // NOPMD static imports for mocking/asserting

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.File;

import net.sourceforge.jvlt.utils.UIConfig;

import org.testng.annotations.Test;

public class JVLTTester {

	private static final String REGULAR_CONFIG = UIConfig.getConfigPath();
	private static final String CONFIG_OVERRIDE = "config";

	/**
	 * Ensures that existing config folders are used to override the default.
	 */
	@Test
	public void testDebugFolderSet() {
		String folder = System.getProperty("user.home");
		System.setProperty(CONFIG_OVERRIDE, folder);
		assertEquals(UIConfig.getConfigPath(), folder + File.separator + "jvlt",
				"Did not set config folder to specified existing path");
	}

	/**
	 * Ensures that config settings for nonexistent paths are ignored.
	 */
	@Test
	public void testNonExistingDebugFolderSet() {
		String folder = "this doesn't exist - hopefully";
		System.setProperty(CONFIG_OVERRIDE, folder);
		assertEquals(UIConfig.getConfigPath(), REGULAR_CONFIG,
				"Did not ignore nonexistent path for config");
	}

	/**
	 * Ensures that config settings for invalid paths are ignored.
	 */
	@Test
	public void testInvalidDebugFolderSet() {
		String folder = "?/\\'";
		System.setProperty(CONFIG_OVERRIDE, folder);
		assertEquals(UIConfig.getConfigPath(), REGULAR_CONFIG,
				"Did not ignore invalid path for config");
	}

	/**
	 * Ensures that config settings for invalid paths are ignored.
	 */
	@Test
	public void testNoDebugFolderSet() {
		System.clearProperty(CONFIG_OVERRIDE);
		assertEquals(UIConfig.getConfigPath(), REGULAR_CONFIG,
				"Did not use default path for config with nothing specified");
	}

	/**
	 * If the new configuration directory exists, any old configuration must be
	 * ignored.
	 */
	@Test
	public void testNewConfigExists() {
		File config = mock(File.class);
		File oldConfig = mock(File.class);

		when(config.exists()).thenReturn(true);
		when(config.isDirectory()).thenReturn(false);

		UIConfig.getOrBuildConfigDirectory(config, oldConfig);

		verifyNoMoreInteractions(oldConfig);
	}

	/**
	 * Ensures that if no new configuration exists, the system attempts to
	 * rename (move) the old one.
	 */
	@Test
	public void testRenameOldConfig() {
		File config = mock(File.class);
		File oldConfig = mock(File.class);

		when(config.exists()).thenReturn(false);
		when(oldConfig.exists()).thenReturn(true);
		when(oldConfig.renameTo(config)).thenReturn(true);

		UIConfig.getOrBuildConfigDirectory(config, oldConfig);

		verify(oldConfig).renameTo(config);
	}

	/**
	 * Ensures that if no new configuration exists and the existing old
	 * configuration cannot be renamed, the system attempts to create the new
	 * folder.
	 */
	@Test
	public void testRenameOldConfigFailed() {
		File config = mock(File.class);
		File oldConfig = mock(File.class);

		when(config.exists()).thenReturn(false);
		when(oldConfig.exists()).thenReturn(true);
		when(oldConfig.renameTo(config)).thenReturn(false);

		UIConfig.getOrBuildConfigDirectory(config, oldConfig);

		verify(oldConfig).renameTo(config);
		verify(config).mkdirs();
	}

	/**
	 * Ensures that if neither new nor old configuration exist, the system
	 * attempts to create the new folder.
	 */
	@Test
	public void testCreationIfNoDirExists() {
		File config = mock(File.class);
		File oldConfig = mock(File.class);

		when(config.exists()).thenReturn(false);
		when(oldConfig.exists()).thenReturn(false);

		UIConfig.getOrBuildConfigDirectory(config, oldConfig);

		verify(config).mkdirs();
	}
}
