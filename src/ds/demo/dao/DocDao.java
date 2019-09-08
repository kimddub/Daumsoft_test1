package ds.demo.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ds.demo.dto.DocData;
import ds.demo.util.DBUtil;
import ds.demo.util.DBUtil.DBLink;

// DB 관리
public class DocDao {
	private String sql;
	private ResultSet rs;
	private ResultSetMetaData rsMd;
	private List<String> dataLines;
	private DBLink dbLink;

	// JDBC 관련 객체 불어옴()
	private void callJDBC() {
		dbLink = DBUtil.getNewDBLink();
	}

	// JDBC 관련 객체 종료
	private void closeJDBC() {
		dbLink.close();
	}

	// 테이블의 총 데이터 수
	
	public void countData(String table) {

		callJDBC();

		sql = "SELECT COUNT(*)\r\n FROM " + table + ";";
		long dataSize = (Long) dbLink.getRow(sql);

		System.out.println("Number of tabe " + table + "'s data : " + (int) dataSize);

		closeJDBC();
	}
	
	// DB 읽어들임 (order = 0:오름차순, 이상:나머지)
	public List<DocData> testReadData(String table, int order) {
		callJDBC();
		
		List<DocData> allData = new ArrayList<>();

		if (order > 0) {
			sql = "SELECT *\r\n" + "FROM " + table + "\r\n" + "ORDER BY `DOC_SEQ` DESC;";
		} else {
			sql = "SELECT *\r\n" + "FROM " + table + "\r\n;";
		}

		rs = dbLink.getRows(sql);
		rsMd = dbLink.getMetaData();
		int colCnt;
				
		try {
			colCnt = rsMd.getColumnCount();
			Object[] colNames = new Object[colCnt];
			List<Object[]> dataArr = new ArrayList<>();
			Object[] colValues;
			
			for (int i=0; i<colCnt; i++) {
				colNames[i] = rsMd.getColumnName(i+1);
			}
			
			int dataIdx = 0;
			while(rs.next()) {
				colValues = new Object[colCnt];
				
				for (int i=0; i<colCnt; i++) {
				
					colValues[i] = rs.getObject((String)colNames[i]);
					
				}
				
				dataArr.add(colValues);
				
				allData.add(new DocData(rsMd.getColumnCount(), colNames, dataArr.get(dataIdx++)));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return allData;
	}

	// DB 읽어들임 (order = 0:오름차순, 이상:나머지)
	public void readData(String table, int order) {

		callJDBC();

		if (order > 0) {
			sql = "SELECT *\r\n" + "FROM " + table + "\r\n" + "ORDER BY `DOC_SEQ` DESC;";
		} else {
			sql = "SELECT *\r\n" + "FROM " + table + "\r\n;";
		}

		rs = dbLink.getRows(sql);
		rsMd = dbLink.getMetaData();

	}

	public void readDataAsTsvType(String table, int order, String fileName) {

		readData(table, order);
		
		dataLines = new ArrayList<>();
		int colNum;

		try {

			colNum = rsMd.getColumnCount();

			String[] colName = new String[colNum];

			for (int i = 0; i < colNum; i++) {

				colName[i] = rsMd.getColumnName(i + 1);

			}

			dataLines.add(convertToTsvType(colName));

			while (rs.next()) {

				dataLines.add(convertToTsvType(
						new String[] { rs.getString("DOC_SEQ"), rs.getString("TITLE"), rs.getString("REG_DT") }));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {

			writeDB(dataLines, fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		closeJDBC();
	}

	public void readDataAsTaggedType(String table, int order, String fileName) {

		readData(table, order);
		
		List<String> dataLines = new ArrayList<>();

		int colNum;

		try {

			colNum = rsMd.getColumnCount();

			String colName = "";

			while (rs.next()) {

				String[] taggedData = new String[2 + colNum * 2];
				int idx = 0;

				taggedData[idx++] = "^[START]";

				for (int i = 0; i < colNum; i++) {

					colName = rs.getMetaData().getColumnName(i + 1);
					taggedData[idx++] = "[" + colName + "]";
					taggedData[idx++] = rs.getString(colName).replaceAll("\\r\\n", "\r\n");

				}

				taggedData[idx++] = "^[END]";

				dataLines.add(convertToTaggedType(taggedData));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {

			writeDB(dataLines, fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}

		closeJDBC();
	}

	public void readDataAsJsonType(String table, int order, String fileName) {
		readData(table, order);
		
		List<String> dataLines = new ArrayList<>();

		JsonArray Json = new JsonArray();

		try {

			while (rs.next()) {
				int colNum = rsMd.getColumnCount();
				JsonObject obj = new JsonObject();

				for (int i = 1; i < colNum + 1; i++) {
					String colName = rsMd.getColumnName(i);

					obj.addProperty(colName, rs.getString(colName));

				}

				Json.add(obj);
			}

		} catch (SQLException e) {
			e.printStackTrace();
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

		closeJDBC();
	}

	// 출력 및 파일 생성
	private void writeDB(List<String> dataLines, String fileName) throws IOException {

		System.out.println("Completed processing " + dataLines.size() + " data) ");
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

	// 한 데이터의 값들을 tsv 타입으로 결합
	private String convertToTsvType(String[] data) {
		return String.join("\t", data);
		//return Stream.of(data).map(this::escapeSpecialCharacters).collect(Collectors.joining("\t"));
		// .collect(Collectors.joining(" "));
	}

	// 한 데이터의 값들을 tagged 타입으로 결합
	private String convertToTaggedType(String[] data) {
		return String.join("\n", data);
		//return Stream.of(data).map(this::escapeSpecialCharacters).collect(Collectors.joining("\n"));
	}

	// 특수문자 탈출
	private String escapeSpecialCharacters(String data) {
		String escapedData = data.replaceAll("\\R", " ");
		if (data.contains(",") || data.contains("\"") || data.contains("'")) {
			data = data.replace("\"", "\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}

	private void trucate(String table) {
		callJDBC();

		sql = "TRUNCATE TABLE " + table + ";";

		try {

			dbLink.executeUpdate(sql);
			System.out.println(table + " table is truncated!");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		closeJDBC();
	}
	
	private void newSetTable(String table,DocData data) {
		callJDBC();

		try {
			sql = "DROP TABLE IF EXISTS " + table + ";";

			dbLink.executeUpdate(sql);
			System.out.println("deleted a " + table + " that already exists!");
			
			sql = "CREATE TABLE " + table + "(\r\n";
			
			// 데이터 형식 셋팅
			int dataSize = data.getSize();
			Object[] colNames = data.getColNames();
			
			// 데이터 형식에 맞는 쿼리문 셋팅 시작
			sql += "`" + colNames[0] + "` INT PRIMARY KEY";
			
			for (int i=1; i<dataSize; i++) {
				sql += ",\r\n `" + colNames[i] + "` VARCHAR(4000) ";
			}
			
			sql += ");";
			
			dbLink.executeUpdate(sql);
			System.out.println(table + " table is created!");
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		closeJDBC();
	}

	public void insert(List<String[]> allData, String table) throws SQLException {

		// 해당 테이블 TRUNCATE
		trucate(table);

		callJDBC();

		// batch insert batch 실행 시간 : 17초
		sql = "INSERT INTO `" + table + "`(`DOC_SEQ`,`TITLE`,`REG_DT`) VALUES (?,?,?)";
		dbLink.prepareSql(sql);

		int DOC_SEQ = 0;
		String TITLE = "";
		String REG_DT = "";

		int total = allData.size(); //
		int batchSize = 10000;
		int batchTerm = ((total - 1) / batchSize);

		double totalElapsedTime = 0;
		System.out.print("Inserting Data ");

		// file의 첫번째 데이터가 헤더가 아닌 형식일 때 직접
		if (!(allData.get(0)[0]).equals("DOC_SEQ")) {
			DOC_SEQ = allData.get(0)[0] != null ? Integer.parseInt(allData.get(0)[0]) : 0;
			TITLE = allData.get(0)[1] != null ? allData.get(0)[1].replaceAll("'", "''") : "";
			REG_DT = allData.get(0)[2] != null ? allData.get(0)[2] : "";

			dbLink.prepareValue(DOC_SEQ, TITLE, REG_DT);
		}

		for (int i = 0; i < batchTerm; i++) {
			for (int j = 1 + i * batchSize; j <= (i + 1) * batchSize; j++) {

				DOC_SEQ = allData.get(j)[0] != null ? Integer.parseInt(allData.get(j)[0]) : 0;
				TITLE = allData.get(j)[1] != null ? allData.get(j)[1].replaceAll("'", "''") : "";
				REG_DT = allData.get(j)[2] != null ? allData.get(j)[2] : "";

				dbLink.prepareValue(DOC_SEQ, TITLE, REG_DT);

			}

			totalElapsedTime += dbLink.executeBatch();
		}

		for (int j = 1 + batchTerm * batchSize; j < total; j++) {
			DOC_SEQ = allData.get(j)[0] != null ? Integer.parseInt(allData.get(j)[0]) : 0;
			TITLE = allData.get(j)[1] != null ? allData.get(j)[1].replaceAll("'", "''") : "";
			REG_DT = allData.get(j)[2] != null ? allData.get(j)[2] : "";

			dbLink.prepareValue(DOC_SEQ, TITLE, REG_DT);
		}

		totalElapsedTime += dbLink.executeBatch();
		// System.out.println(total - 1 - batchTerm*batchSize + " Data INSERT (Number of
		// accumulated data : " + cnt + ")");
		System.out.println("Completed processing " + total + " data / DB Check ");
		countData(table);

		System.out.println("Average Time to insert " + batchSize + " Data : "
				+ Math.round(totalElapsedTime / batchTerm * 1000.0) / 1000.0 + "'s ");

		dbLink.close();

	}

	public void testDataInsert(List<DocData> allData, String table) throws SQLException {

		// 해당 테이블 TRUNCATE
		DocData firstData = allData.get(0);
		newSetTable(table,firstData);
		// JDBC 셋팅
		callJDBC();
		
		// 데이터 형식 셋팅
		int dataSize = firstData.getSize();
		Object[] colNames = firstData.getColNames();
		
		// 데이터 형식에 맞는 쿼리문 셋팅 시작
		String colNameStr = "";
		String valEntStr = "";
		
		int idx = 0;
		for (Object colName : colNames ) {
			if (idx++ != 0) {
				colNameStr += ", ";
			}
			colNameStr += "`" + colName + "`";
		}
		
		valEntStr += "?";
		for (int i=1; i<dataSize; i++) {
			valEntStr += ", ?";
		}
		
		sql = "INSERT INTO `" + table + "`(" + colNameStr + ") VALUES (" + valEntStr + ");";
		
		dbLink.prepareSql(sql);
		
		// 데이터 형식에 맞는 쿼리문 셋팅 끝
		
		int DOC_SEQ = 0;
		String TITLE = "";
		String REG_DT = "";

		int total = allData.size(); //
		int cnt = 0;
		int batchSize = 10000;
		int batchTerm = ((total - 1) / batchSize);

		double totalElapsedTime = 0;
		System.out.print("Inserting Data ");
		
		for (int i = 0; i < batchTerm; i++) {
			for (int j = i * batchSize; j < (i + 1) * batchSize; j++) {
				
				Object[] values = allData.get(j).getColValues();
				dbLink.testPrepareValue(values);

			}

			totalElapsedTime += dbLink.executeBatch();
		}

		for (int j = batchTerm * batchSize; j < total; j++) {
			Object[] values = allData.get(j).getColValues();
			dbLink.testPrepareValue(values);

		}

		totalElapsedTime += dbLink.executeBatch();
				
		System.out.print("Completed processing " + total + " data / DB Check ");
		countData(table);

		System.out.println("Average Time to insert " + batchSize + " Data : " + Math.round(totalElapsedTime / batchTerm * 1000.0) / 1000.0 + ""
						+ "s ");

		dbLink.close();

	}

}
