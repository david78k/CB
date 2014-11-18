//package CaroteneClassifier;
//package CaroteneClassifier.CaroteneClassifier;

import java.util.Arrays;
import java.util.ArrayList;

public class Job {
	//private final String jobid;
	private final String title;
	private final String description;
	private final ArrayList<String> expected_titles;
	private final ArrayList<Integer> expected_socs;
	private int soc; // top expected SOC number without "soc" prefix
	private ArrayList<Integer> onet_socs;
	private String onet_id;

	// File Name, Original Title, Expected V2.1 title, Comments, ONet SOCs, Description
	// v2, DIRECTOR OF SUSTAINABILITY, Director of Strategy OR "Director of Sustainability", We don't have ..., [11, 13], pstrongemspan style ...
	public Job(String row) {
		String[] fields = row.split("\t");
		title = fields[1];
		expected_titles = toArrayList(fields[2]);
		expected_socs = toSocs(fields[4]);
		description = fields[5];
	}

	public Job(String title, String description) {
		this.title = title;
		this.description = description;
		expected_titles = null;
	}

	/**
	*  convert to multiple expected titles
	*  Director of Strategy OR "Director of Sustainability" 
	*/
	public ArrayList<String> toArrayList(String str) {
		String[] titles = str.toLowerCase().split("or");	
		return new ArrayList<String>(Arrays.asList(titles));
	}

	public ArrayList<String> getExpectedTitles() {
		return expected_titles;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public int getExpectedSocs() {
		return expected_socs;
	}
	
	public int getExpectedSoc() {
		return expected_socs.get(0);
	}

/*
	public void setSoc(int soc) {
		this.soc = soc;
	}
*/
	public String getExpectedSocString() {
		return "soc" + expected_socs.get(0);
		//return "soc" + soc;
	}
}
