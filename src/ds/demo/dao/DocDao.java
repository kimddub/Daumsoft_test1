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
	public List<DocData> readData(String table, int order) {
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

	public void insert(List<DocData> allData, String table) throws SQLException {

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
