package ds.demo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import ds.demo.dao.DocDao;
import ds.demo.dto.DocData;
import ds.demo.file.JsonFileWriter;
import ds.demo.file.TagFileReader;
import ds.demo.file.TsvFileReader;
import ds.demo.util.Timer;

public class Main {

	public static void main(String[] args) throws IOException, SQLException {
		DocDao docDao = new DocDao();
		// test


		Timer.turnOnTimer("DB to File(json type)");
		
		List<DocData> testData1 = docDao.testReadData("testDOC",0); 
		JsonFileWriter tfw1 = new JsonFileWriter();
		tfw1.testPutAllData(testData1,"doc_json_test.json");
		
		Timer.turnOffTimer();
		
		Timer.turnOnTimer("test tag INSERT");

		List<DocData> testData2 = TagFileReader.testGetAllData("C:\\work\\daumsoft_file\\doc_tag.txt");
		docDao.testDataInsert(testData2, "testDOC");
		
		Timer.turnOffTimer();
		
		Timer.turnOnTimer("test tsv INSERT");

		List<DocData> testData3 = TsvFileReader.testGetAllData("C:\\work\\daumsoft_file\\doc.tsv");
		docDao.testDataInsert(testData3, "testDOC");
		
		Timer.turnOffTimer();
		
		// 1. File to DB
		Timer.turnOnTimer("File to DB");
		
		List<String[]> allTsvData = TsvFileReader.getAllData("C:\\work\\daumsoft_file\\doc.tsv"); // tsv파일 경로, table명
		docDao.insert(allTsvData, "DOC1");
		
		Timer.turnOffTimer();
	    
		
		
		// 2-1. DB to File(tsv)
		Timer.turnOnTimer("DB to File(tsv1)");
		docDao.readDataAsTsvType("DOC1",0,"doc_tsv_0.tsv"); // table명, order option (0:오름차순, 이상:나머지)
		Timer.turnOffTimer();

	    Timer.turnOnTimer("DB to File(tsv2)");
		docDao.readDataAsTsvType("DOC1",1,"doc_tsv_0.tsv");
		Timer.turnOffTimer();
		
		
		
		// 2-2. DB to File(tagged type)
	    Timer.turnOnTimer("DB to File(tagged type)");
		docDao.readDataAsTaggedType("DOC1",0,"doc_tag.txt"); 
		Timer.turnOffTimer();
		
		
		
		// 2-3. DB to File(json type)
	    Timer.turnOnTimer("DB to File(json type)");
		docDao.readDataAsJsonType("DOC1",0,"doc_json.json"); 
		Timer.turnOffTimer();
		
		
		
		// 3. Tagged type File to DB
	    Timer.turnOnTimer("Tagged type File to DB");
	    
		List<String[]> allTaggedData = TagFileReader.getAllData("C:\\work\\daumsoft_file\\doc_tag.txt"); // tsv파일 경로, table명
		docDao.insert(allTaggedData, "DOC2");
		
		Timer.turnOffTimer();
	}

}
