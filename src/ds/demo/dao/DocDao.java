package ds.demo.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ds.demo.util.DBUtil;
import ds.demo.util.DBUtil.DBLink;
import ds.demo.util.DocData;

// DB 관리
public class DocDao {
	private String sql;
	private ResultSet rs;
	private ResultSetMetaData rsMd;
	private DBLink dbLink;

	// JDBC 관련 객체 불어옴()
	private void callJDBC() {
		dbLink = DBUtil.getNewDBLink();
	}

	// JDBC 관련 객체 종료
	private void closeJDBC() {
		dbLink.close();
	}
	
	private void setSelectQuery(String table, int order) {
		
		if (order > 0) {
			sql = "SELECT *\r\n" + "FROM " + table + "\r\n" + "ORDER BY `DOC_SEQ` DESC;";
		} else {
			sql = "SELECT *\r\n" + "FROM " + table + "\r\n;";
		}
	}
	
	private void setInsertQuery(String table) {
		// 쿼리문 셋팅 - 파일 파싱 할 때의 컬럼명 순서로 저장된 배열이므로 순서에 맞음
		String colNameArr = DocData.getColNames()[0];
		
		for (int i=1; i<DocData.getSize(); i++) {
			colNameArr += ",`" + DocData.getColNames()[i] + "`";
		}

		String valArr = "?";
		
		for (int i=1; i<DocData.getSize(); i++) {
			valArr += ", ?";
		}
		
		sql = "INSERT INTO `" + table + "`(" + colNameArr + ") VALUES (" + valArr + ");";
		dbLink.prepareSql(sql);
	}
	
	// DB 읽어들임 (order = 0:오름차순, 이상:나머지)
	public List<Map<String,Object>> getAllData(String table, int order) {
		callJDBC();
		
		List<Map<String,Object>> allData = new ArrayList<>();

		setSelectQuery(table,order);

		rs = dbLink.getRows(sql);
		rsMd = dbLink.getMetaData();
		
		String[] colNames = null;
		Map<String,Object> values = null;
		
		try {
			DocData.setSize(rsMd.getColumnCount());
			colNames = new String[DocData.getSize()];

			for (int i=0; i<DocData.getSize(); i++) {
				colNames[i] = rsMd.getColumnName(i+1);
			}

			DocData.setColNames(colNames);
			
			while(rs.next()) {
				values = new HashMap<>();
				
				for (int i=0; i<DocData.getSize(); i++) {
					
					values.put(colNames[i],rs.getObject(colNames[i]));
				}
				
				allData.add(values);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		/*
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
				
				//allData.add(new DocData(rsMd.getColumnCount(), colNames, dataArr.get(dataIdx++)));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
		

		System.out.println("Completed to read all Data (size : " + allData.size() +")");
		
		return allData;
	}

	public void putAllData(List<String[]> allData, String table) throws SQLException {
		// (수동) DB Table 셋팅
		
		// JDBC 시작
		callJDBC();

		// 해당 테이블 TRUNCATE
		trucateTable(table);
		
		// 쿼리문 셋팅
		setInsertQuery(table);
		
		// 데이터 처리 상황 출력
		double totalElapsedTime = 0;
		System.out.print("Inserting Data ");
		
		// ===데이터 INSERT===
		int total = allData.size();
		int batchSize = 10000;
		int batchTerm = ((total - 1) / batchSize);

		for (int i = 0; i < batchTerm; i++) {
			for (int j = i * batchSize; j < (i + 1) * batchSize; j++) {
				
				dbLink.prepareValue(allData.get(j));
			}
			totalElapsedTime += dbLink.executeBatch();
		}

		for (int j = batchTerm * batchSize; j < total; j++) {
			
			dbLink.prepareValue(allData.get(j));
		}
		totalElapsedTime += dbLink.executeBatch();
		
		// 처리 결과 출력
		System.out.println("Completed insert data : " + total);
		System.out.println("Checked inserted DB data : " + countData(table) + "(missed data : " + 
				(total == countData(table) ? "0" : total - countData(table))
				+ ")");

		System.out.println("Average Time to insert " + batchSize + " Data : " + Math.round(totalElapsedTime / batchTerm * 1000.0) / 1000.0 + ""
						+ "s ");
		
		// JDBC 끝
		dbLink.close();

	}
	
	private void trucateTable(String table) {
		
		sql = "TRUNCATE TABLE " + table + ";";

		try {

			dbLink.executeUpdate(sql);
			System.out.println(table + " table is truncated!");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 테이블의 총 데이터 수
	public int countData(String table) {
		callJDBC();

		sql = "SELECT COUNT(*)\r\n FROM " + table + ";";
		long dataSize = (Long) dbLink.getRow(sql);

		closeJDBC();
		
		return (int) dataSize;
	}

	/*
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
	*/
}
