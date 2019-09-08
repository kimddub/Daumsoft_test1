package ds.demo.util;

public class Timer {
	private static long startTime;  
	private static long endTime;
		
	public static void turnOnTimer(String title) {
		startTime = 0;
		endTime = 0;
		
		System.out.println(getCPad(title,25,"="));
		startTime = System.currentTimeMillis();
	}
	
	public static void turnOffTimer() {
		endTime = System.currentTimeMillis();  
	    System.out.println("Total elapsed Time : " + (endTime - startTime)/1000.0 + "'s");
	    System.out.println("=========================");
	}
	
    public static String getCPad(String str, int size, String strFillText) {
        int intPadPos = 0;
        for(int i = (str.getBytes()).length; i < size; i++) {
            if(intPadPos == 0) {
                str += strFillText;
                intPadPos = 1;
            } else {
                str = strFillText + str;
                intPadPos = 0;
            }
        }
        return str;
    }
}
