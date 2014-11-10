package cb.helpers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OnetHelper {
	public static int ONET_AGREE = 1;
	public static int ONET_DISAGREE = 2;
	public static int ONET_NOTSURE = 0;

	/*
	 * http://api.careerbuilder.com/v1/onettagger/retrieveonets.xml?DeveloperKey=
	 * WDHS2YS6DRP48VK5L71C
	 * &Title=Math%20and%20Geometry%20Teacher&ONetCodeType=ONet17&ScoreFloor=80
	 */

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


	public int checkWithONET(String title, int SOC_code) {
		Set<Integer> onets =null;

		try {
			onets = getONETCodes(title);
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
		OnetHelper utils = new OnetHelper();
		System.out.println(utils.checkWithONET("Tax Manager", 13));
		

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
		}
		return ints;
	}
 
	public Set<Integer> getONETCodes(String title) throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException {
		StringBuffer buffer = new StringBuffer();

		try {
			String urlString = "http://api.careerbuilder.com/v1/onettagger/retrieveonets.xml?DeveloperKey=WDHS2YS6DRP48VK5L71C&Title="
					+ URLEncoder.encode(title, "UTF-8")
					+ "&ONetCodeType=ONet17&ScoreFloor=50";
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

}
