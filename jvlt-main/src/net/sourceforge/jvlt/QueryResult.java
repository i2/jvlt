package net.sourceforge.jvlt;

public class QueryResult {
	private Entry _entry;
	private boolean _known;
	private String _answer;
	
	public QueryResult(Entry entry, boolean known) {
		this(entry, known, null); }
	
	public QueryResult(Entry entry, boolean known, String answer) {
		_entry = entry;
		_known = known;
		_answer = answer;
	}
	
	public Entry getEntry() { return _entry; }
	
	public String getAnswer() { return _answer; }
	
	public boolean isKnown() { return _known; }
	
	public void setAnswer(String answer) { _answer = answer; }
}

