/**
@Author: Akshat Kiritkumar Jain(aj3186)
@Author: Ankit Sati(as14128)

@Date: 10/11/2022

@Class_Description: Site class which contains site structure definition with index, status, variables. 
**/

package rep;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

public class Site {
	private int siteIndex;
	private boolean isSiteUp;
	private HashMap<Variable, Boolean> siteVariables; 
	private HashMap<Variable, HashMap<Transaction, String>> lockTable; 

	protected Site(int index) {
		isSiteUp = true;
		this.siteIndex = index;
		siteVariables = new HashMap<>();
		lockTable = new HashMap<Variable, HashMap<Transaction, String>>();
		int i = 1;
		while(i <= 20) {
			Variable var = new Variable(i);
			if (i % 2 == 0) { // even variables at all sites
				siteVariables.put(var, true);
			} else {
				if (i % 10 + 1== siteIndex) { // odd variables 
					siteVariables.put(var, true);
				} else {
					siteVariables.put(var, false);
				}
			}
			lockTable.put(var, new HashMap<Transaction, String>());
			i++;
		}
	}
	
	public int getSiteIndex() {
		return siteIndex;
	}
	
	public void lockVariable(String variableID, Transaction tx, String lockType) {
		if(!tx.getType().equals("RO")) {

			for (Variable var : lockTable.keySet()) {
				String tempVarId = var.getVarID();
				
				if(tempVarId.equals(variableID)) {
					
					HashMap<Transaction, String> value = new HashMap<Transaction, String>();
					value.put(tx, lockType);

					lockTable.get(var).putAll(value);
				} 
			}
		}
	}
	
	public Set<Transaction> getLockTransaction(String variableID) {
		for (Variable v : lockTable.keySet()) {
			String tempVarID = v.getVarID();

			if(tempVarID.equals(variableID)) {
				return lockTable.get(v).keySet();
			}
		}
		return null;
		
	}
	
	protected void unlockTx(Transaction transaction) {
		for (Variable var : lockTable.keySet()) {
			for(Transaction tx : lockTable.get(var).keySet()) {
				if(tx.equals(transaction)) {
					lockTable.get(var).remove(tx);
				}
			}
		}
	}
	
	protected Variable getVariable(String variableID) {
		for (Variable v : siteVariables.keySet()) {
			String tempVarID = v.getVarID();
			if((siteVariables.get(v) == true) && variableID.equals(tempVarID)) {
				return v;
			}
		}
		return null;
	}

	protected void eraselockTable() {
		lockTable.clear();
	}
	
	protected void eraseVariableList() {
		for(Variable v : siteVariables.keySet()) {
			siteVariables.replace(v, false);
		}
	}
	
	protected boolean isTxInSite(Transaction tx) {
		HashMap<Transaction, String> readTX = new HashMap<>();
		readTX.put(tx, "RL");
		if(lockTable.containsValue(readTX)) {
			return true;
		} 

		HashMap<Transaction, String> writeTx = new HashMap<>();
		writeTx.put(tx, "WL");
		if(lockTable.containsValue(writeTx)) {
			return true;
		}

		return false;
	}
	
	protected boolean isVarInSite(String variableID) {
		for (Variable v : siteVariables.keySet()) {
			if (siteVariables.get(v) && variableID.equals(v.getVarID())){			
				return true;
			}
		}
		return false;
	}
	
	protected boolean isSelfLocked(Transaction tx, String variableID) {
		for (Variable var : lockTable.keySet()) {
			String tempVarID = var.getVarID(); 
			if (variableID.equals(tempVarID)){
				if (lockTable.get(var) != null){
					if (lockTable.get(var).containsKey(tx)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	protected String getLockType(String variableID) {
		String lockType = "";
		
		for (Variable var : lockTable.keySet()) {
			String tempVarID = var.getVarID();
			if (variableID.equals(tempVarID) && siteVariables.get(var) && !lockTable.get(var).isEmpty()) {
				
				Iterator<String> lockList = lockTable.get(var).values().iterator();
				while (lockList.hasNext()) {
					String nextType = lockList.next();
					if (nextType.equals("WL") || nextType.equals("RL")){
						lockType= nextType;
					}
				}
			}
		}
		return lockType;
	}

	protected boolean checkLockState(String variableID, String typeLock) {
	
		boolean isLocked = false;
		
		for (Variable var : lockTable.keySet()) {
			if (variableID.equals(var.getVarID())){
				if (lockTable.get(var) != null){
					isLocked = true;
				}
			}
		}
		
		String lockType = getLockType(variableID);
		
		if (isLocked && (lockType.equals(typeLock))) {
			return true;
		}
		return false;
	}
	
	protected boolean isSiteUp() {
		return isSiteUp;
	}
	
	protected void updateVarValue(String variableID, int value) {
		for(Variable var:siteVariables.keySet()){
			if(siteVariables.get(var)) {
				String tempVarID = var.getVarID();
				if(tempVarID.equals(variableID)) {
					var.setValue(value);
				}
			}
		}
	}
	
	protected void updateVarStatus(String variableID, boolean value) {
		for(Variable var : siteVariables.keySet()) {
			String tempVarID = var.getVarID();
			if(tempVarID.equals(variableID)) {
				siteVariables.replace(var, value);
			}
		}
	}
	
	protected void recover() {
		isSiteUp = true;
		
		int i = 1;
		while (i <= 20) {
			if (i % 10 + 1== siteIndex) {
				updateVarStatus("x" + i, true);
			}
			
			for(Variable var : siteVariables.keySet()) {
				lockTable.put(var, new HashMap<Transaction, String>());
			}
			i++;
		}
		
	}

	protected void fail() {
		isSiteUp = false;
	}
}