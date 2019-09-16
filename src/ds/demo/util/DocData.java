package ds.demo.util;

public class DocData {
	private static int size;
	private static String[] colNames; // 데이터 순서
	
	
	public static int getSize() {
		return size;
	}
	public static void setSize(int size) {
		DocData.size = size;
	}
	public static String[] getColNames() {
		return colNames;
	}
	public static void setColNames(String[] colNames) {
		DocData.colNames = colNames;
		DocData.size = colNames.length;
	}
}
