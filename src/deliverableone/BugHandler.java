package deliverableone;

public class BugHandler {
	
	private String monthDate;
	private int fixedBugs;
	private int commitNumber;

	public  BugHandler(String month, int bugs) {
		this.monthDate = month ;
		this.fixedBugs = bugs ;
	}
	
	public String getDate() {
		return this.monthDate;
	}
	
	public int getBugsNumber() {
		return this.fixedBugs;
	}
	
	public void setDate(String month) {
		this.monthDate = month ;
	}
	
	public void setFixedBugs(int bugs) {
		this.fixedBugs = bugs;
	}

	public int getCommitNumber() {
		return commitNumber;
	}

	public void setCommitNumber(int commitNumber) {
		this.commitNumber = commitNumber;
	}

}
