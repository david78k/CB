//package CaroteneClassifier;
//package CaroteneClassifier.CaroteneClassifier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

//import Mode.*;

public class ONetTest {

	final static String version = "v2";
	int matchCount = 0;
	int socMatchCount = 0;
	int socMatch = 0;
	int socInCount = 0;
	int socIn = 0;
	int totalCount = 0;
	int validCount = 0;

	OnetHelper onetHelper = new OnetHelper();
	ONetResponse onets;
	int onetMatch = 0;  // exact match with the top onet soc
	int onetCount = 0;
	int onetInMatch = 0; // if soc is in onet socs
	int onetInCount = 0;
	int invalids = 0;

	ArrayList<ONetCode> onetCodes = null;
	ArrayList<String> expectedTitles = null;
	ArrayList<Integer> expectedSocs = null;
	ArrayList<Integer> onetSocs = null;
	ArrayList<String> onetTitles = null;
	String onetID = null;
	String onetTitle = null;
	int caroteneSoc = -1;
	double confidence = 0.0;
	int titleMatch = 0;
	int leafMatch = 0;

	PrintWriter writer;
				
	public static void main(String[] args) {
		String caroteneURL = "http://localhost:8080/CaroteneClassifier/gettitle";
		//String caroteneURL = "http://ec2-184-73-68-184.compute-1.amazonaws.com:8080/CaroteneClassifier/gettitle";
		if (args.length < 2) {
			System.out.println("usage: inputfile outputfile");
			//throw new IllegalArgumentException("args.length = " + args.length);
			System.exit(1);
		}
		String inputFile = args[0];
		String outputFile = args[1];

		ONetTest test = new ONetTest();
		test.start(inputFile, outputFile);
	}
	
	public void start(String inputFile, String outputFile) {
		System.out.println("Starting ONetTest ...");

		try {
			//ArrayList<JobQuery> jobList = getJobsFromJSON(inputFile);
			JobList jobList = new JobList(inputFile, 1, Job.Mode.TEST);
			int counter = jobList.size();
			System.out.println(counter + " jobs are loaded.");
			writer = new PrintWriter(outputFile, "UTF-8");
			writer.println("File Name\tOriginal Title\tExptected Title\tONet Expected Title\tExpected SOCs\tONet Title\tONet Socs\tONet ID\tConfidence\tSOC Match\tIn SOCs\tTitle Match\tDescription");

			long startTime = System.nanoTime();

			for(Job job : jobList) {
				onetID = null;	onetTitle = null; 
				caroteneSoc = -1;
				confidence = 0.0;
				titleMatch = 0; leafMatch = 0;
				socMatch = 0;	socIn = 0;

				String title = job.getTitle();
				String description = job.getDescription();
				expectedTitles = job.getExpectedTitles();

				//String response = getResponse(caroteneURL, title, description, version);
				onets = onetHelper.getONETCodes(title, description);	
				if (onets ==  null) invalids ++;	
				else {
					//System.out.println(onets);
					onetSocs = onets.getSocs();
					onetTitles = onets.getTitles();
					onetTitle = onetTitles.get(0);
					onetCodes = onets.getCodes();
					onetID = onetCodes.get(0).getCode();
					confidence = onetCodes.get(0).getScore();

					// compare with the top title
					if (job.hasExpectedTitle(onetTitle) || title.equalsIgnoreCase(onetTitle)
					//	|| job.hasExpectedTitle(onetTitle + "s") 
					){
						titleMatch = 1;
						matchCount ++;
					}
				
					expectedSocs = job.getExpectedSocs();
					// compare with top socs
					if(matchSocs(expectedSocs, onetSocs)) {
						socIn = 1;
						socInCount ++;		
						if(matchSocs(expectedSocs, new ArrayList<Integer>(onetSocs.subList(0, 1)))) {
							socMatch = 1;
							socMatchCount ++;	
						}
					}
				}
				writer.println(version + "\t" + title 
						+ "\t" + job.getOriginalExpectedTitles()
						+ "\t" + expectedTitles + "\t" + expectedSocs 
						+ "\t" + onetTitles + "\t" + onetSocs + "\t" + onetID + "\t" + confidence
						+ "\t" + socMatch + "\t" + socIn
						+ "\t" + titleMatch
						+ "\t" + description);
						//+ "\t" + leafMatch + "\t" + description);
			}

			long endTime = System.nanoTime();
			long difference = endTime - startTime;

			totalCount = counter;
			writer.println();
			writer.println("Number of Titles: " + totalCount);
			writer.println("Elapsed milliseconds: " + difference / 1000000f);
			writer.println("milliseconds/title: " + difference / 1000000f
					/ (totalCount));
			
			printStats();

			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("END Test");
	}

	//private void printStats(int totalCount, int invalids, int matchCount, int socMatchCount, int socInCount) {
	private void printStats() {
		String str = "Total Count: " + totalCount + " (Invalids = " + invalids + ")";
		print(str);

		validCount = totalCount - invalids;
		double accuracy = 100.0 * matchCount /( totalCount - invalids);
		//str = "Accuracy (%): " + accuracy + " (" + matchCount + "/" + (totalCount - invalids) + ")";
		print("", matchCount);

		accuracy = 100.0 * socMatchCount / (totalCount - invalids);
		//str = "Top SOC Accuracy (%): " + accuracy + " (" + socMatchCount + "/" + (totalCount - invalids)+ ")";
		print("TOP SOC ", socMatchCount);
			
		accuracy = 100.0 * socInCount / (totalCount - invalids);
		//str = "SOC Accuracy (%): " + accuracy + " (" + socInCount + "/" + (totalCount - invalids) + ")";
		print("SOC ", socInCount);
	}

	// print stats in format
	// accPrefix: "SOC" in "SOC Accuracy (%):"
	private void print(String accPrefix, int metric) {
		double accuracy = 100.0 * metric / validCount; 
		print(accPrefix + "Accuracy (%): " + String.format("%.2f", accuracy) + " (" + metric + "/" + validCount + ")");
	}

	private void print(String str) {
		writer.println(str);
		System.out.println(str);
	}

	private boolean matchSocs(ArrayList<Integer> expectedSocs, ArrayList<Integer> caroteneSocs) {
		boolean matched = false;

		for(Integer esoc: expectedSocs) {
			if(caroteneSocs.contains(esoc)) {
				matched = true;
				break;
			}
		}	
		return matched;	
	}

	// example gid: 41.67
	private void addSoc(ArrayList<Integer> socs, String gid) {
		ArrayList<Integer> newsocs = new ArrayList<Integer>();
		int newsoc = (int)(Double.parseDouble(gid));
		if(!socs.contains(new Integer(newsoc)))	
			socs.add(new Integer(newsoc));
	}

	private ArrayList<String[]> getRowsFromCSV(String inputFile, String splitby)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		ArrayList<String[]> titleList = new ArrayList<String[]>();
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tmp = line.split(splitby);
			titleList.add(tmp);
			//titleList.add(tmp[0] + "::" + tmp[1]);
		}
		return titleList;
	}

	private ArrayList<String> getTitlesFromCSV(String inputFile, String splitby)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		ArrayList<String> titleList = new ArrayList<String>();
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tmp = line.split(splitby);
			titleList.add(tmp[0] + "::" + tmp[1]);
		}
		return titleList;
	}

	private String getResponse(String targetURL, String title,
			String description, String version) {

		StringBuffer answer = new StringBuffer();
		// String data = "&version=soc15";
		String data = "&version=" + version;
		data += "&language=en";
		data += "&title=" + prepareText(title);
		data += "&description=" + prepareText(description);
		try {
			// Send the request
			URL url = new URL(targetURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// conn.setRequestMethod("POST");
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			OutputStreamWriter writer = new OutputStreamWriter(
					conn.getOutputStream());

			// write parameters
			writer.write(data);
			writer.flush();

			// Get the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				answer.append(line + "\n");
			}
			writer.close();
			reader.close();
			conn.disconnect();
			// System.out.println(answer);

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			System.out.println(title);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println(title);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(title);
		}
		return answer.toString();
	}

	private String prepareText(String text) {
		String preppedText = "";

		try {
			preppedText = URLEncoder.encode(text, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return preppedText;
	}
}

