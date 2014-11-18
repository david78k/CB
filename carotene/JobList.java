import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Iterator;

import java.lang.Iterable;

public class JobList implements Iterable<Job>{
	ArrayList<Job> jobs = new ArrayList<Job>();

	public JobList(String file, int startingLine, Job.Mode mode) {
		String line;
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(file));
			for(int i = 0; i < startingLine; i ++)
				br.readLine();
			while((line = br.readLine()) != null) {
				System.out.println(line);
				jobs.add(new Job(line, mode));
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
