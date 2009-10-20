package net.sourceforge.jvlt;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.Application;

public class MacOSController extends Application
  implements OSController
{
	public MacOSController() {
		setEnabledPreferencesMenu(true);
		addApplicationListener(new MacAdapter());
	}

	JVLTUI _mainUI;

	public void setMainView(JVLTUI ui) {
		_mainUI = ui;
	}
  
	public class MacAdapter extends ApplicationAdapter {
		public void handleAbout(ApplicationEvent e) {
			_mainUI.showAbout();
			e.setHandled(true);
		}
    
		public void handleQuit(ApplicationEvent e) {
			//Check to see if user has unsaved changes, if not set e.setHandled(true)
			//If user has unsaved changes set e.setHandled(false) and move into code
			//that handles saving files.
			if (_mainUI.requestQuit())
				e.setHandled(true);
			else
				e.setHandled(false);
		}
    
		public void handlePreferences(ApplicationEvent e) {
			_mainUI.showSettings();
			e.setHandled(true);
		}
	}
}


