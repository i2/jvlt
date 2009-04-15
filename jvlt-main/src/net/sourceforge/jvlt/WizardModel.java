package net.sourceforge.jvlt;

import java.util.HashMap;

public abstract class WizardModel {
	public static class InvalidInputException extends DetailedException {
		private static final long serialVersionUID = 1L;
		
		public InvalidInputException(String short_msg, String long_msg) {
			super(short_msg, long_msg);
		}
	}
	
	protected WizardPanelDescriptor _current_descriptor;
	protected HashMap<String, WizardPanelDescriptor> _descriptor_map;
	protected Wizard _wizard;
	
	public WizardModel() {
		_current_descriptor = null;
		_descriptor_map = new HashMap<String, WizardPanelDescriptor>();
		_wizard = null;
	}
	
	public WizardPanelDescriptor getCurrentPanelDescriptor() {
		return _current_descriptor;
	}
	
	public void setWizard(Wizard wizard) { _wizard = wizard; }
	
	public String getButtonText(String button_command) {
		return GUIUtils.getString("Actions", button_command);
	}
	
	public boolean isButtonEnabled(String button_command) { return true; }

	public String getDefaultButton() { return Wizard.NEXT_COMMAND; }
	
	public String getStatusString() { return ""; }
	
	public abstract WizardPanelDescriptor
		nextPanelDescriptor(String command) throws InvalidInputException;
	
	public void panelDescriptorUpdated(WizardPanelDescriptor descriptor) {
		_wizard.panelDescriptorUpdated(descriptor);
	}
	
	protected WizardPanelDescriptor getPanelDescriptor(String name) {
		return _descriptor_map.get(name);
	}
	
	protected void registerPanelDescriptor(WizardPanelDescriptor d) {
		_descriptor_map.put(d.getID(), d);
	}
}

