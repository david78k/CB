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

//import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class ClientTest {

	public static void main(String[] args) {
		System.out.println("Starting ClientTest ...");
		String caroteneURL = "http://localhost:8080/CaroteneClassifier/gettitle";
		//String caroteneURL = "http://ec2-184-73-68-184.compute-1.amazonaws.com:8080/CaroteneClassifier/gettitle";
		if (args.length < 2)
			throw new IllegalArgumentException("args.length = " + args.length);
		String inputFile = args[0];
		String outputFile = args[1];
		String qinlongFile = "output250_qinlong.txt";
		OnetHelper onetHelper = new OnetHelper();
		//Integer[] onetsocs;
		//Set<Integer> onetsocs;
		//ArrayList<Integer> onetsocs;
		ONetResponse onets;
		int onetMatch = 0;
		int onetCount = 0;
		int onetInMatch = 0;
		int onetInCount = 0;
		int onetInvalids = 0;

		PrintWriter writer;
		/*
		 * String[] titles = { "Sales Representative",
		 * "B2B Sales Representative", "Project Manager", "CDL Driver",
		 * "Hadoop Developer", "Data Architect", "Personal Trainer",
		 * "Surgical Aide", "Store Supervisor", "Human Resources Assistant" };
		 */

		try {
			ArrayList<String[]> qinlongList = getRowsFromCSV(qinlongFile, "\t");
			//ArrayList<String> titleList = getTitlesFromCSV(inputFile, "\t");
			ArrayList<JobQuery> jobList = getJobsFromJSON(inputFile);
			int counter = jobList.size();
			writer = new PrintWriter(outputFile, "UTF-8");
			writer.println("File Name\tOriginal Title\tCarotene ID\tCarotene Title\tConfidence\tFirst Title Match\tONet Titles\tONet SOCs\tONet Match\tONet In Match\tCarotene Title by Qinlong\tFirst Title Match by Qinlong\tCarotene ID by Qinlong\tConfidence by Qinlong\tDescription");

			long startTime = System.nanoTime();

			JSONParser parser = new JSONParser();
			final int NUM = 1;
			int matchCount = 0;

			Iterator qiter = qinlongList.iterator();
			String[] qinlong = (String[])qiter.next();				
			String title_qinlong = qinlong[0];
		//	System.out.println(title_qinlong);
			for(JobQuery job : jobList) {
				String caroteneID = null;
				String caroteneTitle = null;
				Double confidence = 0.0;
				int majorMatch = 0;
				int leafMatch = 0;
				String title = job.getTitle();
				String description = job.getDescription();
				//description = "";

				//System.out.println("title:"+title);

				qinlong = (String[])qiter.next();				
				title_qinlong = qinlong[0];
				String version = "v2";
				
				String response = getResponse(caroteneURL, title, description, version);
				// String response = getResponse(caroteneURL, title, "", "v2.x");
				JSONObject obj1 = (JSONObject) parser.parse(response);
				JSONArray array = (JSONArray) obj1.get("assignments");
				for (int itr = 0; itr < array.size(); itr++) {
					JSONObject obj2 = (JSONObject) array.get(itr);
					String gID = (String) obj2.get("groupId");
					JSONArray p2r = (JSONArray) obj2.get("pathToRoot");
					String gLabel = (String) p2r.get(0);
					Double score = (Double) obj2.get("confidence");

					if (title.equalsIgnoreCase(gLabel)) {
						if (itr == 0)
							majorMatch = leafMatch = 1;
						else
							leafMatch = 1;
					}
					if (itr == 0) {
						caroteneID = gID;
						caroteneTitle = gLabel;
						confidence = score;
					}
				}
				if (caroteneTitle.equalsIgnoreCase(title)) {
					majorMatch = 1;
					matchCount ++;
				} else if (caroteneTitle.equalsIgnoreCase(title_qinlong)) {
					majorMatch = Integer.parseInt(qinlong[1]);
					if (majorMatch == 1)
						matchCount ++;
				}
				//onetsocs = onetHelper.getONETCodes(title);
				onets = onetHelper.getONETCodes(title, description);
				//onetsocs = onetHelper.getONETCodesWithGetRequest(title, description);
				onetMatch = 0;
				onetInMatch = 0;
				if (onets == null) {
					onetInvalids ++;
				} else if (onets.size() > 0) { 
					if(onets.contains(new Integer((int)(Double.parseDouble(caroteneID))))){
						onetInMatch = 1;
						onetInCount ++;
						//if(onets.get(0).intValue() == (int)(Double.parseDouble(caroteneID))) {
						if(onets.isFirstSOC(caroteneID)) {
							onetMatch = 1;
							onetCount ++;
						}
					}
				}
				writer.println(version + "\t" + title + "\t" + caroteneID + "\t"
						+ caroteneTitle + "\t" + confidence + "\t" + majorMatch
						+ "\t" + (onets == null?onets:onets.titles) 
						+ "\t" + (onets == null?onets:onets.socs) + "\t" + onetMatch + "\t" + onetInMatch
						+ "\t" + qinlong[0] + "\t" + qinlong[1]
						+ "\t" + qinlong[2] + "\t" + qinlong[3]
						+ "\t" + description);
						//+ "\t" + leafMatch + "\t" + description);
			}

			long endTime = System.nanoTime();
			long difference = endTime - startTime;
			writer.println();
			writer.println("Number of Titles: " + counter * NUM);
			writer.println("Elapsed milliseconds: " + difference / 1000000f);
			writer.println("milliseconds/title: " + difference / 1000000f
					/ (counter * NUM));
			
			int totalCounts = counter * NUM;
			double accuracy = 100.0 * matchCount / totalCounts;
			String accustr = "Accuracy (%): " + accuracy + " (" + matchCount + "/" + totalCounts + ")";
			writer.println(accustr);
			System.out.println(accustr);

			accustr = "Accuracy (%) by Qinlong: 78.31 (195/249)";
			writer.println(accustr);
			System.out.println(accustr);

			int valids = totalCounts - onetInvalids;
			accuracy = 100.0 * onetCount / valids;
			accustr = "Accuracy (%) to ONetSOC: " + accuracy + " (" + onetCount + "/" + valids
					 + ", Invalids: " + onetInvalids + ")";
			writer.println(accustr);
			System.out.println(accustr);

			accuracy = 100.0 * onetInCount / valids;
			accustr = "Accuracy (%) in ONetSOCs: " + accuracy + " (" + onetInCount + "/" + valids
					 + ", Invalids: " + onetInvalids + ")";
			writer.println(accustr);
			System.out.println(accustr);

			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("END ClientTest");
	}

	private static ArrayList<JobQuery> getJobsFromJSON(String inputFile) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		ArrayList<JobQuery> jobs = new ArrayList<JobQuery>();
		
		JSONObject obj1 = (JSONObject) parser.parse(br);
		JSONArray array = (JSONArray) obj1.get("jobs");
		for (int itr = 0; itr < 250; itr++) {
			JSONObject obj2 = (JSONObject) array.get(itr);
			String title = (String) obj2.get("jobTitle");
			if(title.toLowerCase().contains("https:".toLowerCase())) {
				//itr--;
				continue;
			}
			//System.out.println(title);
			String description = (String) obj2.get("jobDescriptionAndRequirements");
			jobs.add(new JobQuery(title, description));
		}
		return jobs;
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

