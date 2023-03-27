/**
@Author: Akshat Kiritkumar Jain(aj3186)
@Author: Ankit Sati(as14128)

@Date: 10/11/2022

@Class_Description: Transaction class which defines a transaction's structure like its creation time, type, etc.
**/

package rep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Hashtable;

public class Transaction {
	private String txID, txType;
	private long txTimeStamp;
	protected HashMap<Integer ,List<String>> locks;
	protected String currentState;
	public Hashtable<String, Integer> tempCache = new Hashtable<>(); 
	
	protected Transaction(String id, String txType) {
		this.txID = id; 
		this.txType = txType;
		this.txTimeStamp = System.nanoTime();
		
		locks = new HashMap<Integer ,List<String>>();
		
		for(int i : locks.keySet())
			locks.put(i, new ArrayList<String>());
	}
	
	protected String getTxID() {
		return txID;
	}
	
	protected long getTxTimeStamp() {
		return txTimeStamp;
	}
	
	protected String getType() {
		return txType;
	}
}