package ds.demo.dto;

import java.util.List;

// unstructured Data
//[첫 컬럼 : 숫자,기본키, 나머지 컬럼 : 문자(4000)]으로 테이블을 우선 생성해야 함 ㅜㅜ?
public class DocData {
	private int size; //colNum
	private Object[] colNames; 
	private Object[] colValues;

	public DocData(int size, Object[] colNames, Object[] colValues) {
		this.size = size;
		this.colNames = colNames;
		this.colValues = colValues;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Object[] getColNames() {
		return colNames;
	}

	public void setColNames(Object[] colNames) {
		this.colNames = colNames;
	}

	public Object[] getColValues() {
		return colValues;
	}

	public void setColValues(Object[] colValues) {
		this.colValues = colValues;
	}

}
