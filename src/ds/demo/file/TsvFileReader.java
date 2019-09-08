package ds.demo.file;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.Data;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import ds.demo.dto.DocData; 
 
// tsv파일 파싱 및 DB 입력 클래스
public class TsvFileReader { 
	public static List<DocData> getAllData(String fileName) throws SQLException{ 
		
		   TsvParserSettings settings = new TsvParserSettings(); 
		   settings.getFormat().setLineSeparator("\n"); 
		   
		   TsvParser parser = new TsvParser(settings); 
		   
		   List<String[]> parsedData = parser.parseAll(new File(fileName)); 
		   List<DocData> allData = new ArrayList<>();
		   
		   // 첫 번째 데이터 라인은 나머지 데이터 라인들의 컬럼명이 된다.
		   String[] metaData = parsedData.get(0);
		   
		   // DocData(colNum(size), colNames[], colValues[])
		   for (int i=1; i<parsedData.size(); i++) {
			   allData.add(new DocData(metaData.length, metaData, parsedData.get(i)));
		   }
		   
		   return allData;
		   // List에서 첫 줄을 제거하는게 나은지, for문으로 두 번째 인덱스부터 사용하는게 나은지?
	   } 
} 
