package ds.demo.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ds.demo.dto.DocData;

public class JsonFileWriter extends FileWriter{
	private String formatType = "\t";

	public void testPutAllData(List<DocData> testData, String fileName) {
		
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
		
	}
}
