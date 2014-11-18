//package CaroteneClassifier;
//package CaroteneClassifier.CaroteneClassifier;

import java.util.Arrays;
import java.util.ArrayList;

public class Job {
	private String jobid;
	private final String title;
	private final String description;
	private ArrayList<String> expected_titles;
	private ArrayList<Integer> expected_socs;
	private int soc; // top expected SOC number without "soc" prefix
	private ArrayList<Integer> onet_socs;
	private final String onetcode; // original onet code like 11-2022.00

	public static enum Mode{
		CREATE, EXPECTED
	}

	// File Name, Original Title, Expected V2.1 title, Comments, ONet SOCs, Description
	// v2, DIRECTOR OF SUSTAINABILITY, Director of Strategy OR "Director of Sustainability", We don't have ..., [11, 13], pstrongemspan style ...
	public Job(String row, Mode mode) {
		String[] fields = row.split("\t");
		switch(mode) {
			case CREATE:
				jobid = fields[0];
				title = fields[1];
				description = fields[2];
				onetcode = fields[3];
				break;
			case EXPECTED:
				title = fields[1];
				expected_titles = toExpectedTitles(fields[2]);
				expected_socs = toExpectedSocs(fields[4]);
				description = fields[5];
				break;
			default:
				break;
		}
	}

	public Job(String title, String description) {
		this.title = title;
		this.description = description;
	}

	/**
	*  jobid example: J03CB1MJYSGKCW9F6K 
	*  onetcode example: 11-2022.00
	*/
	public Job(String jobid, String title, String description, String onetcode) {
		this.jobid = jobid;
		this.title = title;
		this.description = description;
		this.onetcode = onetcode;
	}

	/**
	*  convert to multiple expected titles
	*  Director of Strategy OR "Director of Sustainability" 
	*/
	public ArrayList<String> toExpectedTitles(String str) {
		String[] titles = str.toLowerCase().split("or");	
		return new ArrayList<String>(Arrays.asList(titles));
	}

	public ArrayList<String> toExpectedSocs(String str) {
		String[] titles = str.toLowerCase().split(",");	
		// trim [, ], "
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

	public String getONetCode() {
		return onetcode;	
	}

	public ArrayList<Integer> getExpectedSocs() {
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
