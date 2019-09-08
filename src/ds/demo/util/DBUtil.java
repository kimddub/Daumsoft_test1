package ds.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBUtil {
	
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_ID;
	private static String DB_PW;

	public static DBLink getNewDBLink() {
		DBLink dbLink = new DBLink();

		return dbLink;
	}
	
	public static class DBLink {
		private Connection con;
		private Statement stmt;
		private PreparedStatement pstmt;
		private ResultSet rs;
		private ResultSetMetaData rsMd;
		private int colNum;
		
		private DBLink() {
			DB_DRIVER = "com.mysql.cj.jdbc.Driver";
			DB_URL = "jdbc:mysql://localhost:3306/ds?characterEncoding=UTF-8&serverTimezone=UTC&useBulkCopyForBatchInsert=true;";
			DB_ID = "root";
			DB_PW = "";
			
			makeNewConnection();
		}
		
		public void close() {
			rsMd = null;
			DBUtil.close(rs);
			rs = null;
			DBUtil.close(pstmt);
			pstmt = null;
			DBUtil.close(stmt);
			stmt = null;
			DBUtil.close(con);
			con = null;
		}
		
		private Connection makeNewConnection() {
			try {

				Class.forName(DB_DRIVER);
				//system.out.println("Driver is loaded");

			} catch (ClassNotFoundException e) {

				System.out.println("ClassNotFoundException : " + e.getMessage());

			}

			try {
				con = DriverManager.getConnection(DB_URL, DB_ID, DB_PW);
				//system.out.println("Connection is successed");
				
				con.setAutoCommit(false);
				con.commit();

				stmt = con.createStatement();
				//system.out.println("Statement is created");
								
			} catch (SQLException e) {
				System.out.println("SQLException : " + e.getMessage());
			}

			return con;
			
		}
		
		public void prepareSql(String sql)  {
			try {
				
				pstmt = con.prepareStatement(sql);
				//system.out.println("Statement about query is prepared");
								
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		public void prepareValue(int val_1,String val_2,String val_3){
			try {
				pstmt.setInt(1, val_1);
				pstmt.setString(2, val_2);
				pstmt.setString(3, val_3);
				
				pstmt.addBatch();
				pstmt.clearParameters() ;
								
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		public void testPrepareValue(Object[] values){
			try {
				
				int idx = 1;
				pstmt.setInt(idx++,Integer.parseInt((String)values[0]));

				for (int i=1; i<values.length; i++) {
					pstmt.setString(idx++,(String)(values[i]));
				}
				
				pstmt.addBatch();
				pstmt.clearParameters() ;
								
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		public double executeBatch() {
			System.out.print(". ");
			
			double startTime = 0;
			double endTime = 0;
			
			try {
				startTime = System.nanoTime();
				
				pstmt.executeBatch();
				con.commit();
				
				endTime = System.nanoTime();   // 프로그램 끝나는 시점 계산
			    
				pstmt.clearBatch();
		 		 
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return ((endTime - startTime)/1000000000.0);

		}
		
		public void executeUpdate(String sql) throws SQLException {
			try {
				
				stmt.executeUpdate(sql);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
				
		private void executeQuery(String sql) {
			try {
				
				rs = stmt.executeQuery(sql);
				rsMd = rs.getMetaData();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		public Object getRow(String sql) {
			executeQuery(sql);
			
			try {
				if (rs.next()) {
					
						return rs.getObject(1);
					
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return null;
				
		}
		
		public ResultSet getRows(String sql) {
			executeQuery(sql);
			
			if (rs != null) {
				return rs;
			} else {
				return null;
			}
		}
		
		public ResultSet getRows() {
			if (rs != null) {
				return rs;
			} else {
				return null;
			}
		}
		
		public ResultSetMetaData getMetaData() {
			if (rsMd != null) {
				return rsMd;
			} else {
				return null;
			}
		}
	}
	
	public static void close(Connection con) {

		if (con != null) {

			try {

				con.close();

			} catch (SQLException e) {

				System.out.println("SQLException : " + e.getMessage());

			}

		}

	}

	public static void close(Statement stmt) {

		if (stmt != null) {

			try {

				stmt.close();

			} catch (SQLException e) {

				System.out.println("SQLException : " + e.getMessage());

			}

		}

	}

	public static void close(ResultSet rs) {

		if (rs != null) {

			try {

				rs.close();

			} catch (SQLException e) {

				System.out.println("SQLException : " + e.getMessage());

			}

		}

	}
}
