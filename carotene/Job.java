//package CaroteneClassifier;
//package CaroteneClassifier.CaroteneClassifier;

public class Job {
	private final String jobid;
	private final String title;
	private final String description;
	private final ArrayList<String> titles_expected;
	private int soc; // SOC number without "soc" prefix
	private ArrayList<Integer> onet_socs;
	private String onet_id;

	// File Name, Original Title, Expected V2.1 title, Comments, ONet SOCs, Description
	// v2, DIRECTOR OF SUSTAINABILITY, Director of Strategy OR "Director of Sustainability", We don't have ..., [11, 13], pstrongemspan style ...
	public Job(String row) {
		String[] fields = row.split("\t");
		title = fields[1];
		title_expected = fields[2];
		//onet_socs = toSocs(fields[4]);
		description = fields[5];
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
