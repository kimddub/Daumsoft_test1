package ds.demo.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import ds.demo.util.DocData;

public class JsonFileWriter extends FileWriter{

	public void printAllData(List<Map<String,Object>> allData, String fileName) {
		
		for (Map<String,Object> data : allData) {

			JsonObject obj = new JsonObject();
			
			for (String colName:DocData.getColNames()) {
				
				if (data.get(colName) instanceof Integer) {

					obj.addProperty(colName, (Number)data.get(colName));
				} else {

					obj.addProperty(colName, (String)data.get(colName));
				}

			}
			
			dataLines.add(obj.toString());
		}

		try {

			writeDB(dataLines, fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		int size = testData.get(0).getSize();

		JsonArray Json = new JsonArray();

		for (DocData data : testData) {

			JsonObject obj = new JsonObject();
			
			for (int i = 0; i < size; i++) {
				
				String colName = (String)data.getColNames()[i];
				
				if (data.getColValues()[i] instanceof Integer) {
					obj.addProperty(colName, data.getColValues()[i].toString());
				} else {
					obj.addProperty(colName, (String)data.getColValues()[i]);
				}

			}
			
			Json.add(obj);
		}

		System.out.println("Completed processing " + Json.size() + " data) ");

		// print Json type DB, create Json File from DB
		File csvOutputFile = new File("C:\\work\\daumsoft_file\\" + fileName);

		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {

			for (JsonElement data : Json) {
				// System.out.println(data);
				pw.println(data);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		*/
	}
}
