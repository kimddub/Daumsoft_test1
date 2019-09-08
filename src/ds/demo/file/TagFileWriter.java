package ds.demo.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ds.demo.dto.DocData;

public class TagFileWriter extends FileWriter {
	private String formatType = "\n";

	public void testPutAllData(List<DocData> testData, String fileName) {
		
		List<String> dataLines = new ArrayList<>(); //출력용 데이터 리스트
		String taggedData = ""; // DocData를 출력용 문자열로 변경
		
		int Size = testData.get(0).getSize();
		int dataIdx = 0;

		for (DocData data : testData) {
			taggedData = "^[START]" + formatType;
			
			for (int i = 0; i < Size; i++) {

				taggedData += "[" + data.getColNames()[i] + "]" + formatType;
				if (data.getColValues()[i] instanceof Integer) {
					taggedData += data.getColValues()[i].toString() + formatType;
				} else {
					taggedData += (String)data.getColValues()[i] + formatType;
				}

			}
			
			taggedData += "^[END]";
			
			dataLines.add(taggedData);
		}

		try {

			writeDB(dataLines, fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	/*
	// 한 데이터의 값들을 tag 타입으로 결합 및 escape
	public String convertToFileType(String[] data) {
		return String.join("\n", data);
		//return Stream.of(data).map(this::escapeSpecialCharacters).collect(Collectors.joining("\t"));
		// .collect(Collectors.joining(" "));
	}
	*/
}
