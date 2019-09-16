package ds.demo.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ds.demo.util.DocData;

public class TsvFileWriter extends FileWriter{
	private String formatType = "\t";

	public void printAllData(List<Map<String,Object>> allData, String fileName) {
		printData = "";		
		
		for (String colName:DocData.getColNames()) {
			printData += colName + formatType;
		}
		
		dataLines.add(printData);
		
		for (Map<String,Object> data : allData) {
			printData = "";
			
			for (String colName:DocData.getColNames()) {
				
				printData += data.get(colName) + formatType;
			}
			
			dataLines.add(printData);
		}
		
		try {

			writeDB(dataLines, fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		List<String> dataLines = new ArrayList<>(); //출력용 데이터 리스트
		String tsvData = ""; // DocData를 출력용 문자열로 변경
		
		int Size = testData.get(0).getSize();
		int dataIdx = 0;
		
		for (Object colName:testData.get(0).getColNames()) {
			tsvData += (String)colName + formatType;
		}
		
		dataLines.add(tsvData);

		for (DocData data : testData) {
			tsvData = "";

			for (int i = 0; i < Size; i++) {

				if (data.getColValues()[i] instanceof Integer) {
					tsvData += data.getColValues()[i].toString() + formatType;
				} else {
					tsvData += (String)data.getColValues()[i] + formatType;
				}

			}
			
			dataLines.add(tsvData);
		}

		try {

			writeDB(dataLines, fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		*/
		
	}
}
