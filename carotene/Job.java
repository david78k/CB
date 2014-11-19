//package CaroteneClassifier;
//package CaroteneClassifier.CaroteneClassifier;

import java.util.Arrays;
import java.util.ArrayList;

public class Job {
	private String jobid;
	private String title;
	private String description;
	private String original_expected_titles;
	private String original_expected_socs;
	private ArrayList<String> expected_titles;
	private ArrayList<Integer> expected_socs;
	private int soc; // top expected SOC number without "soc" prefix
	private ArrayList<Integer> onet_socs;
	private String onetcode; // original onet code like 11-2022.00

	public static enum Mode{
		COLLECT, TEST
		//CREATE, EXPECTED
	}

	public Job(String row, Mode mode) {
		String[] fields = row.split("\t");
		//System.out.println(fields.length);
		//System.out.println(fields);

		// NOTE: make sure the order of fields is correct
		switch(mode) {
			case COLLECT:
				// jobid, description, title, onetcode
				jobid = fields[0];
				description = fields[1];
				title = fields[2];
				onetcode = fields[3];
				break;
			case TEST:
			// File Name, Original Title, Expected V2.1 title, Comments, ONet SOCs, Description
			// v2, DIRECTOR OF SUSTAINABILITY, Director of Strategy OR "Director of Sustainability", We don't have ..., [11, 13], pstrongemspan style ...
				title = fields[1];
				original_expected_titles = fields[2];
				expected_titles = toExpectedTitles(fields[2]);
				original_expected_socs = fields[4];
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
		String[] titles = str.toLowerCase().split(" or ");
		return new ArrayList<String>(Arrays.asList(titles));
	}

	public ArrayList<Integer> toExpectedSocs(String str) {
		// trim [, ], "
		String[] socs = str.toLowerCase().trim().replace("[", "").replace("]","")
				.replace("\"","").split(",");	
		//System.out.println(str + ": " + socs.length + " " + socs);
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for(String soc: socs) {
			//System.out.println(soc.trim());
			ints.add(Integer.parseInt(soc.trim()));	
		}
		return ints;
		//return new ArrayList<Integer>(Arrays.asList(titles));
	}

	public boolean isExpectedTitle(String caroteneTitle) {
		if(caroteneTitle.equalsIgnoreCase(title.toLowerCase())) {
			return true;
		}
		for(String title: expected_titles) {
			if(title.equalsIgnoreCase(caroteneTitle))
				return true;
		}
		return false;
	}

	public String getOriginalExpectedTitles() {
		return original_expected_titles;
	}
	
	public String getOriginalExpectedSocs() {
		return original_expected_socs;
	}
	
	public ArrayList<String> getExpectedTitles() {
		return expected_titles;
	}

	public String getJobId() {
		return jobid;
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
