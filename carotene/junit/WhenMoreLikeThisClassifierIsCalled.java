import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import com.carrotsearch.careerbuilder.classification.Assignment;
import com.carrotsearch.careerbuilder.classification.ClassifierResponse;
import com.carrotsearch.careerbuilder.classification.GroupsModel;
//import com.carrotsearch.careerbuilder.classification.MoreLikeThisClassifier;

public class WhenMoreLikeThisClassifierIsCalled extends LuceneTestCase {

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
	public static void setup() throws IOException{

		//TODO: Check how to configure to use LuceneTestCase class - newDirectory(), et al.
		
		String[] stopWords = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,its,just,least,let,like,likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your,experienced,entry level,part,full,time,job,career,junior,senior,mid level,jr,sr,freelance,temporary,temp,needed,wanted,position,hiring,opportunity,shifts,shift".split(",");
		ArrayList<String> stopWordsList = new ArrayList<String>();

		for (String s : stopWords) {
			stopWordsList.add(s);
		}

		CharArraySet stop_words = new CharArraySet(Version.LUCENE_40, stopWordsList, true);

		analyzer = new StandardAnalyzer(Version.LUCENE_40, stop_words);

		//store index in memory
		//directory = newDirectory();
		directory = new RAMDirectory();

		groupsModel = new GroupsModel();
		groupsModel.add(5000, new String[]{"Hadoop Engineer"});
		groupsModel.add(950, new String[]{".Net Developer"});
		groupsModel.add(400, new String[]{"Dentist"});
		groupsModel.add(600, new String[]{"Surgeon"});
		groupsModel.add(100, new String[]{"Chief Executive Officer"});
		groupsModel.add(200, new String[]{"Chief Financial Officer"});

		groupsModel.add(700, new String[]{"Hadoop Developer"});
		groupsModel.add(720, new String[]{"Principal Engineer"});
		groupsModel.add(740, new String[]{"Hadoop Architect"});
		groupsModel.add(760, new String[]{"Software Engineer"});
		groupsModel.add(550, new String[]{"QA Engineer"});
		groupsModel.add(650, new String[]{"Hadoop Cloud Engineer"});
		groupsModel.add(2000, new String[]{"Hadoop Manager"});


		config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		iWriter = new IndexWriter(directory, config);

		AddDocumentsToIndex(iWriter);

		iReader = DirectoryReader.open(directory);
		iSearcher = new IndexSearcher(iReader);

		mlt = new MoreLikeThis(iReader);
		mlt.setAnalyzer(analyzer);
		mlt.setFieldNames(new String[]{"title"});
		mlt.setMinTermFreq(1);
	}


	@Test
	public void ItShouldFindTheAppropriateTitle() throws IOException, InterruptedException
	{
		q = mlt.like(new StringReader("Hadoop Engineer"), "");

//		response = MoreLikeThisClassifier.knn(iSearcher, q, 20, 2, groupsModel, false);

		Assignment[] assignments =  response.assignments;

		assertNotNull(assignments);
		
		Assignment topAssignment = assignments[0];
		
		assertEquals(topAssignment.pathToRoot[0], "Hadoop Engineer");
	}


	static class DocumentObject
	{
		public DocumentObject(String title, String summary, String gids, String id ) {
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

	private static void AddDocumentsToIndex(IndexWriter iWriter) throws IOException
	{

		@SuppressWarnings("serial")
		ArrayList<DocumentObject> documents = new ArrayList<DocumentObject>() {{
			add(new DocumentObject("Hadoop Engineer", "Hadoop Engineer @ Yahoo", "5000", "5000"));
			add(new DocumentObject("Hadoop Engineer", "Hadoop Engineer @ Google", "5000", "5000"));
			add(new DocumentObject("Hadoop Engineer", "Hadoop Engineer @ Amazon", "5000", "5000"));
			add(new DocumentObject("Hadoop Engineer", "Hadoop Engineer @ Microsoft", "5000", "5000"));
			add(new DocumentObject("Hadoop Engineer", "Hadoop Engineer @ IBM", "5000", "5000"));
			add(new DocumentObject("Hadoop Engineer", "Hadoop Engineer @ Apple", "5000", "5000"));
			add(new DocumentObject("Hadoop Engineer", "Hadoop Engineer @ Careerbuilder", "5000", "5000"));
			add(new DocumentObject("Dentist", "Summary Dentist", "400", "400"));
			add(new DocumentObject(".Net Developer", "Summary .Net Developer", "950", "950"));
			add(new DocumentObject("Surgeon", "Summary Surgeon", "600", "600"));
			add(new DocumentObject("Chief Financial Officer", "Summary Chief Financial Officer", "200", "200"));
			add(new DocumentObject("Chief Executive Officer", "Summary Chief Executive Officer", "100", "100"));
			add(new DocumentObject("Hadoop Developer", "Summary Hadoop Developer", "700", "700"));
			add(new DocumentObject("Principal Engineer", "Summary Principal Engineer", "720", "720"));
			add(new DocumentObject("Hadoop Architect", "Summary Hadoop Architect", "740", "740"));
			add(new DocumentObject("Software Engineer", "Software Engineer @ Yahoo", "760", "760"));
			add(new DocumentObject("Software Engineer", "Software Engineer @ Google", "760", "760"));
			add(new DocumentObject("Software Engineer", "Software Engineer @ Amazon", "760", "760"));
			add(new DocumentObject("Software Engineer", "Software Engineer @ Careerbuilder", "760", "760"));
			add(new DocumentObject("QA Engineer", "Summary QA Engineer", "550", "550"));
			add(new DocumentObject("Hadoop Cloud Engineer", "Summary Hadoop Cloud Engineer", "650", "650"));
			add(new DocumentObject("Hadoop Manager", "Summary Hadoop Manager", "2000", "2000"));
		}};


		//Note: words occurring fewer than minDocFreq value of 5 will be filtered out and not used in constructing
		//the query. See method like in lucene class MoreLikeThis

		for(DocumentObject document : documents)
		{
			org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
			doc.add(new TextField("title", document.title, Store.YES));
			doc.add(new TextField("summary", document.summary, Store.NO));
			doc.add(new StoredField("gids", document.gids));
			doc.add(new StoredField("id", document.id));
			iWriter.addDocument(doc);
		}

		iWriter.close();
	}

}
