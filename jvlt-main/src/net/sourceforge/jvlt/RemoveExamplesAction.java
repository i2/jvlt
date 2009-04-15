package net.sourceforge.jvlt;

import java.util.Collection;

public class RemoveExamplesAction extends DictAction {
	private Collection<Example> _examples;
	
	public RemoveExamplesAction(Collection<Example> examples) {
		_examples = examples;
	}
	
	public Collection<Example> getExamples() { return _examples; }
}

