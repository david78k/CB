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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

//import Mode.*;

public class ClientTest {

	final static String version = "v2";
				
	public static void main(String[] args) {
		System.out.println("Starting ClientTest ...");
		String caroteneURL = "http://localhost:8080/CaroteneClassifier/gettitle";
		//String caroteneURL = "http://ec2-184-73-68-184.compute-1.amazonaws.com:8080/CaroteneClassifier/gettitle";
		if (args.length < 2)
			throw new IllegalArgumentException("args.length = " + args.length);
		String inputFile = args[0];
		String outputFile = args[1];

		int matchCount = 0;
		int socMatchCount = 0;
		int socMatch = 0;
		int socInCount = 0;
		int socIn = 0;

		OnetHelper onetHelper = new OnetHelper();
		ONetResponse onets;
		int onetMatch = 0;  // exact match with the top onet soc
		int onetCount = 0;
		int onetInMatch = 0; // if soc is in onet socs
		int onetInCount = 0;
		int onetInvalids = 0;

		PrintWriter writer;

		ArrayList<String> expectedTitles;
		ArrayList<Integer> expectedSocs;
		ArrayList<Integer> caroteneSocs;

		try {
			//ArrayList<JobQuery> jobList = getJobsFromJSON(inputFile);
			JobList jobList = new JobList(inputFile, 1, Job.Mode.TEST);
			int counter = jobList.size();
			writer = new PrintWriter(outputFile, "UTF-8");
			writer.println("File Name\tOriginal Title\tExptected Title\tCarotene Expected Title\tExpected SOCs\tCarotene Socs\tCarotene ID\tCarotene Title\tConfidence\tIn SOCs\tTitle Match\tDescription");

			long startTime = System.nanoTime();

			JSONParser parser = new JSONParser();

			for(Job job : jobList) {
				String caroteneID = null;
				String caroteneTitle = null;
				int caroteneSoc = -1;
				Double confidence = 0.0;
				int titleMatch = 0;
				int leafMatch = 0;
				socMatch = 0;
				socIn = 0;

				String title = job.getTitle();
				String description = job.getDescription();
				expectedTitles = job.getExpectedTitles();

				caroteneSocs = new ArrayList<Integer>();
			//	System.out.println("new carotneSocs");
				String response = getResponse(caroteneURL, title, description, version);
				JSONObject obj1 = (JSONObject) parser.parse(response);
				JSONArray array = (JSONArray) obj1.get("assignments");
				for (int itr = 0; itr < array.size(); itr++) {
					JSONObject obj2 = (JSONObject) array.get(itr);
					String gID = (String) obj2.get("groupId");
					JSONArray p2r = (JSONArray) obj2.get("pathToRoot");
					String gLabel = (String) p2r.get(0);
					Double score = (Double) obj2.get("confidence");
					//System.out.println(caroteneSocs + " " + gID);
					addSoc(caroteneSocs, gID);
					//System.out.println(caroteneSocs + " " + gID);

					if (title.equalsIgnoreCase(gLabel)) {
						if (itr == 0)
							titleMatch = leafMatch = 1;
						else
							leafMatch = 1;
					}
					if (itr == 0) {
						caroteneID = gID;
						caroteneTitle = gLabel;
						confidence = score;
						caroteneSoc = (int)(Double.parseDouble(gID));
					}
				}

				if (job.hasExpectedTitle(caroteneTitle)) {
					titleMatch = 1;
					matchCount ++;
				}
				
				expectedSocs = job.getExpectedSocs();
				//if(expectedSocs.contains(new Integer(caroteneSoc))) {
				if(matchSocs(expectedSocs, caroteneSocs)) {
					socIn = 1;
					socInCount ++;		
					/*
					if(expectedSocs.get(0).intValue() == caroteneSoc)  {
						socMatch = 1;
						socMatchCount ++;
					}
					*/
				}
				writer.println(version + "\t" + title 
						+ "\t" + job.getOriginalExpectedTitles()
						+ "\t" + expectedTitles + "\t" + expectedSocs 
						+ "\t" + caroteneSocs + "\t" + caroteneID + "\t" + caroteneTitle + "\t" + confidence
						+ "\t" + socIn
						//+ "\t" + socMatch + "\t" + socIn
						+ "\t" + titleMatch
						+ "\t" + description);
						//+ "\t" + leafMatch + "\t" + description);
			}

			long endTime = System.nanoTime();
			long difference = endTime - startTime;

			int totalCounts = counter;
			writer.println();
			writer.println("Number of Titles: " + totalCounts);
			writer.println("Elapsed milliseconds: " + difference / 1000000f);
			writer.println("milliseconds/title: " + difference / 1000000f
					/ (totalCounts));
			
			double accuracy = 100.0 * matchCount / totalCounts;
			String accustr = "Accuracy (%): " + accuracy + " (" + matchCount + "/" + totalCounts + ")";
			writer.println(accustr);
			System.out.println(accustr);

			/*
			accuracy = 100.0 * socMatchCount / totalCounts;
			accustr = "Accuracy (%) for top SOC match: " + accuracy + " (" + socMatchCount + "/" + totalCounts + ")";
			writer.println(accustr);
			System.out.println(accustr);
			*/
			accuracy = 100.0 * socInCount / totalCounts;
			accustr = "Accuracy (%) for inSOCs: " + accuracy + " (" + socInCount + "/" + totalCounts + ")";
			writer.println(accustr);
			System.out.println(accustr);

			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
/*		} catch (IOException e) {
			e.printStackTrace();
*/		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("END ClientTest");
	}

	private static boolean matchSocs(ArrayList<Integer> expectedSocs, ArrayList<Integer> caroteneSocs) {
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
	private static void addSoc(ArrayList<Integer> socs, String gid) {
		ArrayList<Integer> newsocs = new ArrayList<Integer>();
		int newsoc = (int)(Double.parseDouble(gid));
		if(!socs.contains(new Integer(newsoc)))	
			socs.add(new Integer(newsoc));
	}

	private static ArrayList<String[]> getRowsFromCSV(String inputFile, String splitby)
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

	private static ArrayList<String> getTitlesFromCSV(String inputFile, String splitby)
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

	private static String getResponse(String targetURL, String title,
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

	private static String prepareText(String text) {
		String preppedText = "";

		try {
			preppedText = URLEncoder.encode(text, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return preppedText;
	}
}

