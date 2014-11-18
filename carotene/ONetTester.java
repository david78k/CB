public class ONetTester{
	
	String infile = ""

	public ONetTester() {
	}

	public static void main(String[] args) {
		ONetTester.test();		
	}

	public static void test(String file) {
		ONetResponse onets = null;
		try {
			
			onets = new ONetResponse(response);
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
}
