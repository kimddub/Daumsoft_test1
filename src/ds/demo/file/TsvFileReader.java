package ds.demo.file;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import ds.demo.util.DocData;

public class TsvFileReader extends FileReader{ 
	private static List<String[]> parsedData = new ArrayList<>();
	private static List<String[]> allData = new ArrayList<>();
	
	// 데이터만 가공 
	public static List<String[]> readAllData(String fileName) throws SQLException{ 
		parseData(fileName);
		
		List<String[]> allData = new ArrayList<>();
		
	    for (int i=1; i<parsedData.size(); i++) {
		    allData.add(parsedData.get(i));
	    }
	   
	   return allData;
	} 

	// 문서 전체 파싱
	private static void parseData(String path) {
		// 이전 데이터 지우기
		emptyParsedData();
		
		TsvParserSettings settings = new TsvParserSettings(); 
		settings.getFormat().setLineSeparator("\n"); 
	   
		TsvParser parser = new TsvParser(settings); 
	
		parsedData = parser.parseAll(new File(path));
		
		System.out.println("Parsing the [" +path + "] ");
		
		// 데이터 포맷 셋팅
		setDocDataFormat();
	}
	
	private static void emptyParsedData() {
		
		parsedData.clear();
		allData.clear();
		
	}
	
	private static void setDocDataFormat() {
		List<String> tmpColNames = new ArrayList<>();
		
		for (int i=0; i<parsedData.get(0).length; i++) {
			if (parsedData.get(0)[i] != null && parsedData.get(0)[i].trim() != "") {
				tmpColNames.add(parsedData.get(0)[i].trim());
			}
		}
		
		String[] colNames = new String[tmpColNames.size()];
		
		for (int i=0; i<tmpColNames.size(); i++) {
			colNames[i] = tmpColNames.get(i);
		}
		
		DocData.setSize(colNames.length);
		DocData.setColNames(colNames);
		
	}
} 
