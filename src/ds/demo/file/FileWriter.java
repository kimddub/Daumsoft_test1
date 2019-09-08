package ds.demo.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import ds.demo.dto.DocData;

public abstract class FileWriter {
	public String formatType;
	
	public abstract void testPutAllData(List<DocData> testData, String fileName);
	
	protected void writeDB(List<String> dataLines, String fileName) throws IOException {

		System.out.println("Completed processing " + dataLines.size() + " data ");
		
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

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
