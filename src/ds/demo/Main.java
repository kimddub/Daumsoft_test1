package ds.demo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import ds.demo.dao.DocDao;
import ds.demo.file.JsonFileWriter;
import ds.demo.file.TagFileReader;
import ds.demo.file.TagFileWriter;
import ds.demo.file.TsvFileReader;
import ds.demo.file.TsvFileWriter;
import ds.demo.util.DocData;
import ds.demo.util.Timer;

public class Main {

	public static void main(String[] args) throws IOException, SQLException {
				
		DocDao docDao = new DocDao();
		List<Map<String,Object>> allData = null;
		
		// 1. File to DB
		Timer.turnOnTimer("File to DB");
		
		List<String[]> allTsvData = TsvFileReader.readAllData("C:\\work\\daumsoft_file\\doc.tsv"); // tsv파일 경로, table명
		docDao.putAllData(allTsvData, "DOC1");
		
		Timer.turnOffTimer();
		
		// 2-1. DB to File(tsv)
		Timer.turnOnTimer("DB to File(tsv1)");
		
		allData = docDao.getAllData("DOC1",0); // table명, order option (0:오름차순, 이상:나머지)
		TsvFileWriter tsvFw = new TsvFileWriter();
		tsvFw.printAllData(allData,"doc_tsv_1.tsv");
		
		Timer.turnOffTimer();
		
		Timer.turnOnTimer("DB to File(tsv2)");

		allData = docDao.getAllData("DOC1",1); // table명, order option (0:오름차순, 이상:나머지)
		tsvFw.printAllData(allData,"doc_tsv_2.tsv");
		
		Timer.turnOffTimer();
		
		
		
		// 2-2. DB to File(tagged type)
	    Timer.turnOnTimer("DB to File(tagged type)");
	    
	    allData = docDao.getAllData("DOC1",0); // table명, order option (0:오름차순, 이상:나머지)
	    TagFileWriter tagFw = new TagFileWriter();
	    tagFw.printAllData(allData,"doc_tag.txt");
		
		Timer.turnOffTimer();
		
		
		
		// 2-3. DB to File(json type)
	    Timer.turnOnTimer("DB to File(json type)");
	     
	    allData = docDao.getAllData("DOC1",0); // table명, order option (0:오름차순, 이상:나머지)
	    JsonFileWriter jsonFw = new JsonFileWriter();
	    jsonFw.printAllData(allData,"doc_json.json");
		
		Timer.turnOffTimer();
		
		// 3. Tagged type File to DB
	    Timer.turnOnTimer("Tagged type File to DB");
	    
		List<String[]> allTagData = TagFileReader.readAllData("C:\\work\\daumsoft_file\\doc_tag_test.txt"); // tsv파일 경로, table명
		docDao.putAllData(allTagData, "DOC2");
		
		Timer.turnOffTimer();
	}

}
