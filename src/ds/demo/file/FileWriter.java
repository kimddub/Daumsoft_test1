package ds.demo.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ds.demo.util.DocData;

public abstract class FileWriter {
	public String formatType;
	public List<String> dataLines = new ArrayList<>(); //출력용 데이터 리스트
	public String printData = ""; // DocData를 출력용 문자열로 변경
	
	public abstract void printAllData(List<Map<String,Object>> allData, String fileName);
	
	protected void writeDB(List<String> dataLines, String fileName) throws IOException {
		
		printDB(dataLines);
		createFileFromDB(dataLines, fileName);
	}

	private void printDB(List<String> dataLines) {
		for (String dataLine : dataLines) {
			//System.out.println(dataLine);
		}
	}

	private void createFileFromDB(List<String> dataLines, String fileName) {
		File csvOutputFile = new File("C:\\work\\daumsoft_file\\" + fileName);

		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {

			for (String dataLine : dataLines) {
				pw.println(dataLine);
			}

			pw.close();
			
			System.out.println("Printing to the file " + fileName);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
