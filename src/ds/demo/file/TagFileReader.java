package ds.demo.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ds.demo.dto.DocData;

public class TagFileReader {
	private static FileReader fr;

	public static List<String[]> getAllData(String fileName){ 
		
		try {
			fr = new FileReader(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(fr);
		List<String[]> dataLines = new ArrayList<>();
		String dataLine = null;
		String DOC_SEQ = "", TITLE = "", REG_DT ="";
		int idx = 0;

		try {
			
			while ( (dataLine = br.readLine()) != null) {   
				
				if (dataLine.contains("^[START]")) {
					DOC_SEQ = "";
					TITLE = "";
					REG_DT ="";
					
					continue;
				} else if (dataLine.contains("[DOC_SEQ]")) {
					DOC_SEQ = br.readLine();
				} else if (dataLine.contains("[TITLE]")) {
					TITLE = br.readLine();
				} else if (dataLine.contains("[REG_DT]")) {
					REG_DT = br.readLine();
				} else if (dataLine.contains("^[END]")) {
				    dataLines.add(new String[] {DOC_SEQ, TITLE, REG_DT});
				} 

			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	   
	   return dataLines;
   } 
	
	public static List<DocData> testGetAllData(String fileName){ 
		
		try {
			fr = new FileReader(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(fr);
		
		List<DocData> allData = new ArrayList<>();
		List<Object> tmpColNames = new ArrayList<>();
		List<Object> tmpColValues = new ArrayList<>();
		Object[] colNames = new Object[0];
		Object[] colValues = new Object[0];
		
		String dataLine = null;
		
		try {
			// 첫 번째 데이터를 이용해 컬럼명 배열을 따고, 데이터 리스트에 추가 시작			
			// 첫 번째 데이터의 컬럼값은 개행문자를 가지면 안된다.
			while ((dataLine = br.readLine()) != null) {
				dataLine.trim();
				
				if (dataLine.contains("^[START]")) {
					continue;
				} else if ( dataLine.substring(0,1).equals("[") && dataLine.substring( dataLine.length() - 1, dataLine.length()).equals("]") ) { // 컬럼명 라인임을 확인하는 조건
					
					tmpColNames.add(dataLine.substring(1,dataLine.length() - 1).trim()); // 순 컬럼명 문자열만 빼내서 저장하기*
					tmpColValues.add(br.readLine()); // 다른 컬럼명 라인이 나오기 전까지 계속 스트링 모으기*
					
					continue;
					
				} else if (dataLine.contains("^[END]")) {
					
					colNames = new Object[tmpColNames.size()];
					
					for (int i=0; i<tmpColNames.size(); i++) {
						colNames[i] = tmpColNames.get(i);
					}
					
					colValues = new Object[tmpColValues.size()];
					
					for (int i=0; i<tmpColValues.size(); i++) {
						colValues[i] = tmpColValues.get(i);
					}
					
					break;
				} 
			}
			
			allData.add(new DocData(colNames.length, colNames, colValues));
			
			List<Object[]> colValueSet = new ArrayList<>();
			
			int dataIdx = 0;
			int valueIdx = -1;
			while ( (dataLine = br.readLine()) != null) {   
				dataLine.trim();
				
				if (dataLine.contains("^[START]")) {
					
					colValueSet.add(new Object[colNames.length]);
					continue;
					
				} else if (dataLine.contains("^[END]")) {
					
					allData.add(new DocData(colNames.length, colNames, colValueSet.get(dataIdx++)));
					continue;
					
				} else if (dataLine.substring(0,1).equals("[") && dataLine.substring( dataLine.length() - 1, dataLine.length()).equals("]")) { 
					
					// 컬럼명 라인임을 확신하는 부분
					for (int i=0; i<colNames.length; i++) {
						if (dataLine.equals("["+colNames[i]+"]")) {
							// 컬럼값을 받기 위해 몇 번째 컬럼인지 구분지음
							valueIdx = i;
							// 컬럼명 바로 다음 컬럼값은 자동으로 넣음
							colValueSet.get(dataIdx)[valueIdx] = br.readLine();
							break;
						}
					}
					
					continue;
					
				} else { 
					
					// 컬럼값들 처리, 몇 번째 컬럼인지 정해진 상태
					colValueSet.get(dataIdx)[valueIdx] += "\r\n" + dataLine;
					
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	   
	   return allData;
   } 
}
