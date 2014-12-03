import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.careerbuilder.classification.Assignment;
import com.carrotsearch.careerbuilder.classification.ClassifierResponse;
import com.carrotsearch.careerbuilder.classification.GroupsModel;
//import com.carrotsearch.careerbuilder.classification.MoreLikeThisClassifier;

public class WhenGroupLabelIsCalled extends LuceneTestCase {

	static StandardAnalyzer analyzer;
	static Directory directory;
	static GroupsModel groupsModel;
	static MoreLikeThis mlt;
	static IndexReader iReader;
	static IndexSearcher iSearcher;
	static IndexWriterConfig config;
	static IndexWriter iWriter;
	static Query q;
	static ClassifierResponse response;

	@BeforeClass
	public static void setup() throws IOException {

		String[] stopWords = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your,experienced,entry level,part,full,time,job,career,junior,senior,mid level,jr,sr,freelance,temporary,temp,needed,wanted,position,hiring,opportunity,shifts,shift"
				.split(",");
		ArrayList<String> stopWordsList = new ArrayList<String>();
		for (String s : stopWords) {
			stopWordsList.add(s);
		}
		CharArraySet stop_words = new CharArraySet(Version.LUCENE_40,
				stopWordsList, true);

		analyzer = new StandardAnalyzer(Version.LUCENE_40, stop_words);

		directory = new RAMDirectory();

		groupsModel = BuildGroupsModel();

		config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		iWriter = new IndexWriter(directory, config);

		AddDocumentsToIndex(iWriter);

		iReader = DirectoryReader.open(directory);
		iSearcher = new IndexSearcher(iReader);

		mlt = new MoreLikeThis(iReader);
		mlt.setAnalyzer(analyzer);
		mlt.setFieldNames(new String[] { "title", "gLabel" });
		mlt.setMinTermFreq(1);
	}

	@Test
	public void MultiFieldQueryParserFindTitle() throws IOException,
			InterruptedException, ParseException {
//		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
//				Version.LUCENE_44, new String[] { "title", "gLabel" }, analyzer);
		q = MultiFieldQueryParser.parse(new String[] { "title", "gLabel" }, 
				new String[] {"CDL Driver", "CDL Driver"}, analyzer);
//		q = queryParser.parse("CDL Driver");
//		response = MoreLikeThisClassifier.knn_Weighted_Float(iSearcher, q, 20,
//				2, groupsModel, false);

		Assignment[] assignments = response.assignments;

		assertNotNull(assignments);
		System.out.println("MultiField");
		for (int itr = 0; itr < assignments.length; itr++) {
			System.out.print("[" + itr + "] " + assignments[itr].groupId + " ");
			for (String gLabel : assignments[itr].pathToRoot) {
				System.out.print(gLabel + " ");
			}
			System.out.println(" " + assignments[itr].confidence);
		}

		Assignment topAssignment = assignments[0];

		assertEquals(topAssignment.pathToRoot[0], "CDL Driver");
	}

	@Test
	public void ItShouldFindTheAppropriateTitle() throws IOException,
			InterruptedException {

		q = mlt.like(new StringReader("CDL Driver"), "");

//		response = MoreLikeThisClassifier.knn_Weighted_Float(iSearcher, q, 20,
//				2, groupsModel, false);

		// System.out.println("query: " + q.toString() + "   ; fields: "
		// + mlt.getFieldNames().toString() + " ::: params: "
		// + mlt.describeParams());

		Assignment[] assignments = response.assignments;

		assertNotNull(assignments);
		System.out.println("MoreLikeThis");
		for (int itr = 0; itr < assignments.length; itr++) {
			System.out.print("[" + itr + "] " + assignments[itr].groupId + " ");
			for (String gLabel : assignments[itr].pathToRoot) {
				System.out.print(gLabel + " ");
			}
			System.out.println(" " + assignments[itr].confidence);
		}

		Assignment topAssignment = assignments[0];
		
		//assertEquals(topAssignment.pathToRoot[0], "CDL Driver");

		assertEquals(topAssignment.pathToRoot[0], "Truck Driver");
	}

	static class DocumentObject {
		public DocumentObject(String title, String summary, String gids,
				String id) {
			this.title = title;
			this.summary = summary;
			this.gids = gids;
			this.id = id;
		}

		String title;
		String summary;
		String gids;
		String id;
	}

	private static void AddDocumentsToIndex(IndexWriter iWriter)
			throws IOException {

		@SuppressWarnings("serial")
		ArrayList<DocumentObject> documents = new ArrayList<DocumentObject>() {
			{
				add(new DocumentObject("CDL Driver", "CDL Driver Atlanta",
						"1000", "1001"));
				add(new DocumentObject("CDL Driver", "CDL Driver NYC", "1000",
						"1002"));
				add(new DocumentObject("CDL Driver", "CDL Driver LA", "1000",
						"1003"));
				add(new DocumentObject("CDL Driver", "CDL Driver Tampa",
						"1000", "1004"));
				add(new DocumentObject("CDL Driver", "CDL Driver Boston",
						"1000", "1005"));
				add(new DocumentObject("CDL Driver", "CDL Driver SF", "1000",
						"1006"));
				add(new DocumentObject("CDL Driver", "CDL Driver Seattle",
						"1000", "1007"));
				add(new DocumentObject("CDL Driver", "CDL Driver Chicago",
						"1000", "1008"));
				add(new DocumentObject("CDL Driver", "CDL Driver Houston",
						"1000", "1009"));

				add(new DocumentObject("CDL Driver", "CDL Driver Atlanta",
						"2000", "2001"));
				add(new DocumentObject("CDL Driver", "CDL Driver NYC", "2000",
						"2002"));
				add(new DocumentObject("CDL Driver", "CDL Driver LA", "2000",
						"2003"));
				add(new DocumentObject("CDL Driver", "CDL Driver Tampa",
						"2000", "2004"));
				add(new DocumentObject("CDL Driver", "CDL Driver Boston",
						"2000", "2005"));
				add(new DocumentObject("CDL Driver", "CDL Driver SF", "2000",
						"2006"));
				add(new DocumentObject("CDL Driver", "CDL Driver Seattle",
						"2000", "2007"));
				add(new DocumentObject("CDL Driver", "CDL Driver Chicago",
						"2000", "2008"));
				add(new DocumentObject("CDL Driver", "CDL Driver Houston",
						"2000", "2009"));

				add(new DocumentObject("CDL Driver", "Bus Driver Phoenix",
						"3000", "3001"));
				add(new DocumentObject("CDL Driver", "Bus Driver San Diego",
						"3000", "3002"));
				add(new DocumentObject("CDL Driver", "Bus Driver Dallas",
						"3000", "3003"));

				add(new DocumentObject("Taxi Driver", "Taxi Driver Phoenix",
						"4000", "4001"));
				add(new DocumentObject("Taxi Driver", "Taxi Driver San Diego",
						"4000", "4002"));
				add(new DocumentObject("Taxi Driver", "Taxi Driver Dallas",
						"4000", "4003"));
				add(new DocumentObject("Taxi Driver", "Taxi Driver Orlando",
						"4000", "4004"));

				add(new DocumentObject("Pilot", "Pilot AA", "5000", "5001"));
				add(new DocumentObject("Pilot", "Assistant Pilot UA", "5000",
						"5002"));
				add(new DocumentObject("Pilot", "Experienced Pilot", "5000",
						"5003"));

				add(new DocumentObject("Forklift Operator", "Forklift Driver",
						"6000", "6001"));
				add(new DocumentObject("Forklift Driver",
						"Forklift Driver San Diego", "6000", "6002"));

				add(new DocumentObject("Warehouse Manager",
						"Warehouse Manager Dallas", "7000", "7001"));
				add(new DocumentObject("Warehouse Worker",
						"Warehouse Worker Atlanta", "7000", "7002"));

				add(new DocumentObject("Tractor Driver",
						"Tractor Trailer Driver San Diego", "8000", "8002"));
				add(new DocumentObject("Trailer Driver",
						"Tractor Trailer Driver Dallas", "8000", "8001"));

				add(new DocumentObject("Transportation Manager",
						"Transportation Manager", "9000", "9001"));
			}
		};

		for (DocumentObject document : documents) {
			org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
			TextField tField = new TextField("title", document.title, Store.YES);
			tField.setBoost(1.0f);
			doc.add(tField);

			doc.add(new TextField("summary", document.summary, Store.NO));
			doc.add(new StoredField("gids", document.gids));
			doc.add(new StoredField("id", document.id));

			for (String gLabel : groupsModel.getGroupLabelsToRoot(Integer
					.parseInt(document.gids))) {
				TextField gField = new TextField("gLabel", gLabel, Store.YES);
				gField.setBoost(1.1f);
				doc.add(gField);
			}

			iWriter.addDocument(doc);
		}

		iWriter.close();
	}

	private static GroupsModel BuildGroupsModel() {
		GroupsModel groupsModel = new GroupsModel();

		groupsModel.add(1000, new String[] { "CDL Driver" });
		groupsModel.add(2000, new String[] { "Truck Driver" });
		groupsModel.add(3000, new String[] { "Bus Driver" });
		groupsModel.add(4000, new String[] { "Texi Driver" });
		groupsModel.add(5000, new String[] { "Commercial Pilots" });
		groupsModel.add(6000, new String[] { "Forklift Operator" });
		groupsModel.add(7000, new String[] { "Warehouse Manager" });
		groupsModel.add(8000, new String[] { "Tractor Trailer Driver" });
		groupsModel.add(9000, new String[] { "Transportation Manager" });

		return groupsModel;
	}
}
