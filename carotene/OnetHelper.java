//package cb.helpers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import org.apache.commons.lang3.StringEscapeUtils;

//import static Job.Mode;

public class OnetHelper {
	/*
	 * http://api.careerbuilder.com/v1/onettagger/retrieveonets.xml?DeveloperKey=
	 * WDHS2YS6DRP48VK5L71C
	 * &Title=Math%20and%20Geometry%20Teacher&ONetCodeType=ONet17&ScoreFloor=80
	 */
	private static final String url = "http://api.careerbuilder.com/v1/onettagger/retrieveonets.xml";
	private static final String devkey = "WDHS2YS6DRP48VK5L71C";
	private String codetype = "ONet15";
	private int scorefloor = 50;

	public static int ONET_AGREE = 1;
	public static int ONET_DISAGREE = 2;
	public static int ONET_NOTSURE = 0;

	public static void main(String[] args) throws ParserConfigurationException,
		SAXException, IOException, XPathExpressionException {
	/*	BufferedReader reader = new BufferedReader(new FileReader(new File(
				"datasets/input-categories.txt")));
		String s = "";
		long startTime = System.currentTimeMillis();

		OnetHelper utils = new OnetHelper();
		
		while ((s = reader.readLine()) != null) {
			String text = s.split("\\|")[1].split("\\(")[0];
			utils.checkWithONET(text, 13);
		}
		reader.close();
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		System.out.println("Time:=" + duration);
		*/
		OnetHelper helper = new OnetHelper();
		String testfile = "jobs250_ONETs-EDITED.txt";
		if (args.length > 0)
			testfile = args[0];
		helper.test(testfile);	

		String desc = "Tradesmen International is looking for sprinkler fitters, both journeymen and apprentices for potential upcoming work in the Jacksonville and Gainesville, Fl, areas.br / Must have valid driverscense, reliable transportation, and tools of the trade.br /";
	//	System.out.println(helper.getONETCodes("SPRINKLER FITTERS", desc));
		//System.out.println(utils.getONETCodesWithGetRequest("SPRINKLER FITTERS", desc));
		//System.out.println(utils.checkWithONET("SPRINKLER FITTERS", desc, 47));
/*
		System.out.println(utils.checkWithONET("Tax Manager", "office", 13));
		System.out.println(utils.checkWithONET("Surgeons", "", 29));
		System.out.println(utils.checkWithONET("Physicians and Surgeons", "", 29));
		System.out.println(utils.checkWithONET("Oral and Maxillofacial Surgeons", "", 29));
*/
		//System.out.println(utils.checkWithONET("Tax Manager", 13));
	}

	private Set<Integer> parseXML(String xml) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Set<Integer> ints = new HashSet<Integer>();

		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		
		NodeList nodeList= doc.getElementsByTagName("ONetCode");
		for(int i=0; i< nodeList.getLength();i++){
			Element node = (Element) nodeList.item(i);
			NodeList nodes=	node.getElementsByTagName("Code");
			int SOCCode = Integer.parseInt(nodes.item(0).getFirstChild().getTextContent().split("-")[0]);
			ints.add(SOCCode);
			//System.out.println(SOCCode);
		}
		return ints;
	}

	//private Integer[] parseXMLToIntArray(String xml) throws ParserConfigurationException,
	private ArrayList<Integer> parseXMLToIntArray(String xml) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		ArrayList<Integer> ints = new ArrayList<Integer>();
		//Set<Integer> ints = new HashSet<Integer>();

		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		//Document doc = builder.parse(new ByteArrayInputStream(StringEscapeUtils.escapeXml11(xml).getBytes()));
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		
		NodeList nodeList= doc.getElementsByTagName("ONetCode");
		for(int i=0; i< nodeList.getLength();i++){
			Element node = (Element) nodeList.item(i);
			NodeList nodes=	node.getElementsByTagName("Code");
			int SOCCode = Integer.parseInt(nodes.item(0).getFirstChild().getTextContent().split("-")[0]);
			ints.add(SOCCode);
			//System.out.println(SOCCode);
		}
		return ints;
		//return ints.toArray(new Integer[ints.size()]);
	}

	private ONetResponse parseXMLToONetResponse(String xml) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		ArrayList<Integer> ints = new ArrayList<Integer>();
		ONetResponse onet = new ONetResponse();

		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		
		NodeList nodeList= doc.getElementsByTagName("ONetCode");
		for(int i=0; i< nodeList.getLength();i++){
			Element node = (Element) nodeList.item(i);
			NodeList nodes=	node.getElementsByTagName("Title");
			nodes=	node.getElementsByTagName("Code");
			nodes=	node.getElementsByTagName("Score");
			String title = (nodes.item(0).getFirstChild().getTextContent().split("-")[0]);
			String code = (nodes.item(0).getFirstChild().getTextContent().split("-")[0]);
			int SOCCode = Integer.parseInt(nodes.item(0).getFirstChild().getTextContent().split("-")[0]);
			int score = Integer.parseInt(nodes.item(0).getFirstChild().getTextContent().split("-")[0]);
			onet.add(new ONetCode(title, code, score));
			//ints.add(SOCCode);
		}
		return onet;
	}

	public void test(String file) {
		String outfile = file + ".onet";
		
		System.out.println("Input file: " + file + "\tOutput file: " + outfile);

		PrintWriter writer;

		try{
			writer = new PrintWriter(outfile);

			// read job data file with title and description and onet code
			JobList joblist = new JobList(file, 0, Job.Mode.CREATE);
			for(Job job: joblist) {
				ONetResponse onets = getONETCodes(job.getTitle(), job.getDescription());
				String original_onetcode = job.getONetCode();
				//onets.getONetSocList();	
				writer.println(job.getTitle() + "\t" + job.getDescription() + "\t" + original_onetcode + "\t" + onets.getSOCListInOrderedSet());
				//writer.println(title + "\t" + description + "\t" + soclist);
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}
			
		System.out.println("Test done.");
	}
 
	public void printAccuracy(PrintWriter writer, int matchCount, int totalCount) {
		double accuracy = 100.0 * matchCount / totalCount;
		String accstr = "Accuracy (%): " + accuracy + " (" + matchCount + "/" + totalCount + ")";
		writer.println(accstr);
		System.out.println(accstr);
	}

	// ONet15, ScoreFloor=75, POST
	// returns SOC list
	//public Set<Integer> getONETCodes(String title, String description) throws XPathExpressionException,
	//public Integer[] getONETCodes(String title, String description) throws XPathExpressionException,
	//public ArrayList<Integer> getONETCodes(String title, String description) throws XPathExpressionException,
	public ONetResponse getONETCodes(String title, String description) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException {
		StringBuffer buffer = new StringBuffer();
		int scorefloor = 75;

		String urlString = "http://api.careerbuilder.com/v1/onettagger/retrieveonets.xml";
		String data = "?DeveloperKey=WDHS2YS6DRP48VK5L71C&Title="
					+ URLEncoder.encode(title, "UTF-8")
					+ "&Description=" 
					+ URLEncoder.encode(description, "UTF-8")
					+ "&ONetCodeType=ONet15&ScoreFloor=" + scorefloor;
		try {
			String encodedTitle = URLEncoder.encode(title, "UTF-8");
			String encodedDescription = URLEncoder.encode(description, "UTF-8");
			data = toXML(encodedTitle, encodedDescription);
			//System.out.println(urlString + "\n" + data);

			URL url = new URL(urlString);

			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			//((HttpURLConnection)conn).setRequestMethod("POST");
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
         
			//write parameters
			writer.write(data);
			writer.flush();
         
			// Get the response
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = "";
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				buffer.append(line);
			}

			writer.close();
			br.close();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		String response = buffer.toString();
		//System.out.println(title + "\n" + response);

		//ArrayList<Integer> codes = null;
		ONetResponse codes = null;
		try {
			codes = new ONetResponse(response);
			//codes = parseXMLToIntArray(response);
		} catch (Exception e) {
			System.out.println(title + "\n" + response);
			e.printStackTrace();
		}

		return codes;
	}

	public String toXML(String title, String description) {
		StringBuffer buff = new StringBuffer();
		buff.append("<Request>\n");
		buff.append("\t<DeveloperKey>" + devkey + "</DeveloperKey>\n");
		buff.append("\t<ONetCodeType>" + codetype + "</ONetCodeType>\n");
		buff.append("\t<ScoreFloor>" + scorefloor + "</ScoreFloor>\n");
		buff.append("\t<Title>" + title + "</Title>\n");
		buff.append("\t<Description>" + description + "</Description>\n");
		buff.append("</Request>\n");
		return buff.toString();
	}
	
	public ArrayList<Integer> getONETCodesWithGetRequest(String title, String description) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException {
		StringBuffer buffer = new StringBuffer();
		int scorefloor = 75;

		String urlString = "http://api.careerbuilder.com/v1/onettagger/retrieveonets.xml";
		String data = "?DeveloperKey=WDHS2YS6DRP48VK5L71C&Title="
					+ URLEncoder.encode(title, "UTF-8")
					+ "&Description=" 
					+ URLEncoder.encode(description, "UTF-8")
					+ "&ONetCodeType=ONet15&ScoreFloor=" + scorefloor;
			//System.out.println(urlString);
		try {
			URL url = new URL(urlString + data);

/*
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			((HttpURLConnection)conn).setRequestMethod("GET");
  */       
			// Get the response
		//	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			InputStream is = url.openStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			String line = "";
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}

			br.close();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 

		return parseXMLToIntArray(buffer.toString());
	}

	public Set<Integer> getONETCodesAsSet(String title, String description) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException {
		StringBuffer buffer = new StringBuffer();
		int scorefloor = 75;

		try {
			String urlString = "http://api.careerbuilder.com/v1/onettagger/retrieveonets.xml?DeveloperKey=WDHS2YS6DRP48VK5L71C&Title="
					+ URLEncoder.encode(title, "UTF-8")
					+ "&Description=" 
					+ URLEncoder.encode(description, "UTF-8")
					+ "&ONetCodeType=ONet15&ScoreFloor=" + scorefloor;
			//System.out.println(urlString);
			URL u = new URL(urlString);

			InputStream is = u.openStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			String line = "";
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}

			br.close();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return parseXML(buffer.toString());
	}

	// ONet15, ScoreFloor=75
	//public Set<Integer> getONETCodes(String title) throws XPathExpressionException,
	//public Integer[] getONETCodes(String title) throws XPathExpressionException,
	public ArrayList<Integer> getONETCodes(String title) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException {
		StringBuffer buffer = new StringBuffer();
		String codetype = "ONet15";
		int scoreFloor = 75;

		try {
			String urlString = "http://api.careerbuilder.com/v1/onettagger/retrieveonets.xml?DeveloperKey=WDHS2YS6DRP48VK5L71C&Title="
					+ URLEncoder.encode(title, "UTF-8")
					+ "&ONetCodeType=" + codetype + "&ScoreFloor=" + scoreFloor;
			//System.out.println(urlString);
			URL u = new URL(urlString);

			InputStream is = u.openStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			String line = "";
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}

			br.close();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return parseXMLToIntArray(buffer.toString());
		//return parseXML(buffer.toString());
	}

/*
	public Map<String, Integer> loadOnetMap(Map<String, Integer> inMap,
			String input, int SOCcode) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader("datasets/"
				+ input + "-categories.txt"));
		String title = null;
		while ((title = reader.readLine()) != null) {
			String formattedTitle = title.split("\\|")[1].split("\\(")[0].trim();
			if (!inMap.containsKey(formattedTitle)) {
				inMap.put(formattedTitle, checkWithONET(formattedTitle, SOCcode));
			}
		}
		reader.close();
		return inMap;

	}

	public Map<String, Integer> loadOnetMap(Map<String, Integer> inMap,
		 Set<String> list, int SOCcode) throws IOException {

		for (String title: list) {
		//	String formattedTitle = title.split("\\|")[1].split("\\(")[0].trim();
			if (!inMap.containsKey(title)) {
				inMap.put(title, checkWithONET(title, SOCcode));
			}
		}

		return inMap;
	}
*/
/*
	public int checkWithONET(String title, String description, int SOC_code) {
		Set<Integer> onets =null;

		try {
			onets = getONETCodesAsSet(title, description);
			//System.out.println(title + " (" + SOC_code + "): " + onets);
			if (description != null && !description.equals(""))
				System.out.println(description);
			if (onets == null || onets.size() < 1)
				return ONET_NOTSURE;
	
			if (onets.contains(SOC_code))
				return ONET_AGREE;
			else
				return ONET_DISAGREE;
		} catch (XPathExpressionException e) {
		
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ONET_NOTSURE;
	}

	public int checkWithONET(String title, int SOC_code) {
		Set<Integer> onets =null;

		try {
			onets = getONETCodes(title);
			//System.out.println(title + " (" + SOC_code + "): " + onets);
			if (onets == null || onets.size() < 1)
				return ONET_NOTSURE;
	
			if (onets.contains(SOC_code))
				return ONET_AGREE;
			else
				return ONET_DISAGREE;
		} catch (XPathExpressionException e) {
		
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ONET_NOTSURE;

	}
*/
}
