//package CaroteneClassifier;
//package CaroteneClassifier.CaroteneClassifier;

public class Job {
	private final String title;
	private final String description;
	private final String title_expected;
	private ArrayList<Integer> onet_socs;
	private int soc; // SOC number without "soc" prefix
	
	public Job(String row) {
	
	}

	public Job(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String setSoc(int soc) {
		this.soc = soc;
	}

	public String getSocString() {
		return "soc" + soc;
	}
}
