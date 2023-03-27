/**
@Author: Akshat Kiritkumar Jain(aj3186)
@Author: Ankit Sati(as14128)

@Date: 10/11/2022

@Class_Description: Variable class which defines the structure of the variables. 
**/

package rep;

public class Variable {
	private String varID;
	private int value;
	private int index;
	
	protected Variable(int idx) {
		this.index = idx;
		this.value = idx*10; //initialized to 10*i
		this.varID = "x" + idx;
	}
	
	protected void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	public int getIndex() {
		return index;
	}
	public String getVarID() {
		return varID;
	}
}