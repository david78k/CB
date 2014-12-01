import java.util.ArrayList;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ONetResponse {
	ArrayList<ONetCode> codes = new ArrayList<ONetCode>();
	ArrayList<String> titles = new ArrayList<String>();
	ArrayList<Integer> socs = new ArrayList<Integer>();

	public ONetResponse () {
	}

	public ONetResponse (String xml) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));

		NodeList nodeList= doc.getElementsByTagName("ONetCode");
		for(int i=0; i< nodeList.getLength();i++){
			Element node = (Element) nodeList.item(i);

			NodeList nodes = node.getElementsByTagName("Title");
			String title = (nodes.item(0).getFirstChild().getTextContent());

			nodes = node.getElementsByTagName("Code");
			String code = (nodes.item(0).getFirstChild().getTextContent());

			nodes = node.getElementsByTagName("Score");
			int score = Integer.parseInt(nodes.item(0).getFirstChild().getTextContent());

			//int SOCCode = Integer.parseInt(nodes.item(0).getFirstChild().getTextContent().split("-")[0]);
			
			add(new ONetCode(title, code, score));
		}
	}

	public ONetResponse (ArrayList<ONetCode> codes) {
		this.codes = codes;
		for (ONetCode code: codes) {
			titles.add(code.getTitle());
			socs.add(code.getSOC());
		}
	}

	// ex caroteneID: 11.35
	public boolean isFirstSOC(String caroteneID) {
		return socs.get(0).intValue() == (int)(Double.parseDouble(caroteneID));	
	}

	public boolean contains(int soc) {
		return socs.contains(new Integer(soc));	
	}

	public ArrayList<String> getTitles() {
		return titles;	
	}

	public ArrayList<Integer> getSocs() {
		return socs;
	}

	public ArrayList<Integer> getSOCListInOrderedSet() {
		ArrayList<Integer> set = new ArrayList<Integer>();
		for(Integer soc: socs) {
			if(!set.contains(soc))
				set.add(soc);	
		}	
		return set;
	}

	public void add(ONetCode code) {
		codes.add(code);
		titles.add(code.getTitle());
		socs.add(code.getSOC());	
	}

	public int size() {
		return codes.size();
	}

	public String xml() {
		StringBuffer buff = new StringBuffer();
		for(ONetCode code: codes) {
			buff.append(code.toString());	
		}
		return buff.toString();	
	}

	public String toString() {
		return xml();
	}
}
