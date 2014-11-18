import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Iterator;

import java.lang.Iterable;

public class JobList implements Iterable<Job>{
	ArrayList<Job> jobs = new ArrayList<Job>();

	public JobList(String file) {
		String line;
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null) {
				if(!line.startsWith("File Name"))
					jobs.add(new Job(line));
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}

	public int size() {
		return jobs.size();
	}

	public Iterator<Job> iterator() {
		return jobs.iterator();
	}
}
