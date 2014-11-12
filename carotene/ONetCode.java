
public class ONetCode {
	private String title = "";
	private String code = "";
	private int score = 0;
	private int soc = 0;

	public ONetCode () {
	}

	public ONetCode (String title, String code, int score) {
		this.title = title;
		this.code = code;
		this.score = score;
		soc = Integer.parseInt(code.split("-")[0]);
		//soc = (int)(Double.parseDouble(code));
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getTitle() {
		return title;
	}
	
	public String getCode() {
		return code;
	}

	public int getScore() {
		return score;
	}
	
	public int getSOC() {
		return soc;
	}
	
	public String xml() {
		return "<ONetCode>\n" 
			+ "\t<Title>" + title + "</Title>\n"
			+ "\t<Code>" + code + "</Code>\n"
			+ "\t<Score>" + score + "</Score>\n"
			+ "</ONetCode>"
		;
	}

	public String toString() {
		return xml();
	}
}
