import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/* 
<Request>
	<DeveloperKey>YOURDEVELOPERKEY</DeveloperKey>
	<ONetCodeType>ONet15</ONetCodeType>
	<Title>SMS COPYWRITER (United States)</Title>
	<Description>An%20inclusive,%20energetic%20culture.%20Incredible%20opportunity.%20A%20community-focused%20company.</Description>
</Request>
*/
public class ONetRequest {

	private static final String url = "http://api.careerbuilder.com/v1/onettagger/retrieveonets.xml";
	private static String devkey = "WDHS2YS6DRP48VK5L71C";
	private static String codetype = "ONet15";
	private static String scorefloor = 75;
	private String filename;

	public static void main(String[] args) {
		ONetRequest or = new ONetRequest(devkey, codetype);
		//or.setXMLFile(
		or.post();
	}

	public ONetRequest(String DeveloperKey, String ONetCodeType) {
		this.devkey = DeveloperKey;
		this.codetype = ONetCodeType;
	}

	public void setXMLFile(String filename) {
		this.filename = filename;
	}

	// return xml element or file
	public void toRequest(String title, String description) {

	}
	
	// send a POST request in XML format
	public void post() {
		String charset = "UTF-8";
	
		URLConnection urlConnection = null;
		String query = null;
		OutputStreamWriter writer = null;
		String title = "RN - Registry II";
		String desc = "Welcome to Elmhurst Memorial Healthcare. From home care to open-heart surgery, Elmhurst Memorial has a dedication to excellence that keeps the Health System aggressively poised for expansion and at the forefront of quality.brr/brrJob Summary: The staff nurse has the primary responsibility and accountability for the total nursing care of the patients as identified by the Standards of Nursing Practice and the Illinois State Nurse Practice Act.ÊÊ pQualifications:ÊÊ p1. Should be a self-starter, able to handle frequent periods of high stress.ÊÊ p2. Must be flexible to scheduling changes including scheduled overtime, weekend hours and holidays.ÊÊ p3. Communicates clearly in English, both written and oral.ÊÊ pEducation: Maintains current CPR certification.ÊÊ pExperience: minium one year Labor and delivery exp required.Ê p*CBÊ pRequirements vary according to patient care area.ÊÊ pLicense: Current Illinois Registration. p brr/&#160;&#160;brr/a hrefjavascript:openit(?fuseaction=apply.logintemplate=dsp_apply_login.cfmcJobId=914426);brr/strongElmhurst Memorial Healthcare offers a comprehensive employee benefits package, including: competitive salary, vacation time, tuition reimbursement, health insurance,fe insurance, dental insurance, a savings and investment plan and incentive program. Benefits may vary among divisions.Ê";
 
		try {
			title = URLEncoder.encode(title, charset);
			desc = URLEncoder.encode(desc, charset);
		//	query = String.format("DeveloperKey=%s&Title=%s&Description=%s&ONetCodeType=%s&ScoreFloor=50", 
		//			devkey, title, desc, codetype);
			query = toXML(title, desc);

			System.out.println(url + "\n" + query);

			urlConnection = new URL(url).openConnection();
			urlConnection.setUseCaches(false);
			urlConnection.setDoOutput(true); // Triggers POST.
			//urlConnection.setDoInput(true);
			//((HttpURLConnection)urlConnection).setRequestMethod("POST");
			//urlConnection.setRequestProperty("accept-charset", charset);
			//urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");

			writer = new OutputStreamWriter(urlConnection.getOutputStream(), charset);
			writer.write(query); // Write POST query string (if any needed).
			writer.flush();
	
			//InputStream result = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			// Now do your thing with the result.
			// Write it into a String and put as request attribute
			// or maybe to OutputStream of response as being a Servlet behind `jsp:include`.
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) 
				buffer.append(line + "\n");
			br.close();
			System.out.println(buffer);
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    if (writer != null) try { writer.close(); } catch (IOException logOrIgnore) {}
		}
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
	
/*
	public void postWithHTTPClient() {
	String url = "https://yoururl.com"; 

    HttpClient client = new DefaultHttpClient();
    HttpPost post = new HttpPost(url);

    // add header
    post.setHeader("User-Agent", USER_AGENT);

    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    urlParameters.add(new BasicNameValuePair("xml", xmlString));

    post.setEntity(new UrlEncodedFormEntity(urlParameters));

    HttpResponse response = client.execute(post);
    System.out.println("\nSending 'POST' request to URL : " + url);
    System.out.println("Post parameters : " + post.getEntity());
    System.out.println("Response Code : " + 
                                response.getStatusLine().getStatusCode());

    BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

    StringBuffer result = new StringBuffer();
    String line = "";
    while ((line = rd.readLine()) != null) {
        result.append(line);
    }

		System.out.println(result.toString());
	}
*/
}
