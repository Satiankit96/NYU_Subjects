/**
@Author: Akshat Kiritkumar Jain(aj3186)
@Author: Ankit Sati(as14128)

@Date: 9/11/2022

@Class_Description: TxManager class is our transaction manager which takes care of all the transactions performed on the variables with the help of 
DataManager.   
**/

package rep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class TxManager {
	private List<Transaction> activeTxs;
	private LinkedHashMap<Transaction, ArrayList<String>> waitingCommands;
	private Deadlock waitListGraph;
	
	protected TxManager() {
		activeTxs = new ArrayList<Transaction>();
		waitingCommands = new LinkedHashMap<Transaction, ArrayList<String>>();
	}
	
	public void begin(String txID, String txType) {
		Transaction tx = new Transaction(txID, txType);
		System.out.println("[ Begin ] : " + txID + " (type " + txType + ") " + " initialized.");
		activeTxs.add(tx);
		return;
	}
	
	public boolean read(String txID, String readVarID) { 
		Transaction tx = getTx(txID);
		if(tx == null) {
			System.out.println("WARNING: Transaction with ID " + txID + " does not exist.");
			return true;
		}
		if(tx.getType().equals("RO")) {
			int tempReadVal;
			if (tx.tempCache.get(readVarID) == null){
				tempReadVal = -1;
			}else {
				tempReadVal = tx.tempCache.get(readVarID);
			}
			if (tempReadVal == -1) {
				System.out.println("[ On WaitList ] : Read " + readVarID + " on Transaction " + tx.getTxID());
				addToWaitList(readVarID, "R", null, tx);
			} else {
				System.out.println("[ Read ] : Transaction " + txID + " has read variable " + readVarID + " of value " + tempReadVal);
				return true;
			}
		} else { 
			if(DataManager.checkReadWriteForVariable(readVarID)) {
				if(DataManager.checkWriteLock(readVarID) == false) {
					
					if(!DataManager.checkReadLock(readVarID) && !DataManager.checkSelfLock(tx, readVarID)) {
						DataManager.setLock(tx, readVarID, "RL");
					}
					for(Site site : DataManager.db) {
						if(site.isSiteUp() && site.isVarInSite(readVarID)) {
							
							Variable readVarItem = DataManager.readVar(site.getSiteIndex(), readVarID);
							if (readVarItem.getValue() == -1) {
								System.out.println("WARNING: Cannot find value of " + readVarItem.getVarID());
							} else {
								System.out.println("[ Read ] : Transaction " + txID + " has read variable " + readVarID + "." + site.getSiteIndex() + " of value " + readVarItem.getValue());
								return true;
							}
							break;
						}
					}
				} 
				else {
					if (DataManager.checkSelfLock(tx, readVarID)) {
						int readItem = tx.tempCache.get(readVarID);
						System.out.println("[ Read ] : Transaction " + txID + " has read variable " + readVarID  + " of value " + readItem + " in tempTable");
						return true;
					} else {
		
						if(!waitingCommands.containsKey(tx)) {
							addToWaitList(readVarID, "R", null, tx);
							System.out.println("[ On Waitlist ]: Read " + readVarID + " on Transaction " + tx.getTxID());
						}
						if(deadLockDetection()) {
							System.out.print("[ DEADLOCK ] : Deadlock detected, ");
							KillLatestTx(); 
						}
					}
				}
			} else {
				if(!waitingCommands.containsKey(tx)) {
					addToWaitList(readVarID, "R", null, tx);
					System.out.println("[ On Waitlist] : Read " + readVarID + " on Transaction " + tx.getTxID());
				}
			}
		}
		return false;
	}
	
	public Transaction getTx(String transactionID) {
		for(Transaction tr : activeTxs) {
			if(tr.getTxID().equals(transactionID)) {
				return tr;
			} 
		}
		return null;
	}

	protected void addToWaitList(String varID, String cmd, String newValue, Transaction tx) {
		ArrayList<String> cmdDetails = new ArrayList<>();
		if(cmd == "R") {
			cmdDetails.add(varID);
			cmdDetails.add("R");
			cmdDetails.add(newValue);
		} else if(cmd == "W") {
			cmdDetails.add(varID);
			cmdDetails.add("W");
			cmdDetails.add(newValue);
		}
		waitingCommands.put(tx, cmdDetails);
	}
	
	protected boolean isActiveTx(String txID) {
		for(Transaction tr:activeTxs) {
			if(tr.getTxID().equals(txID)) {
					return true;
			}
		}
		return false;
	}
	
	public boolean write(String txID, String updateVarID, Integer updateVal) {
		Transaction tx = getTx(txID);
		if(tx == null) {
			System.out.println("[ WARNING ]: Transaction " + txID + " does not exist");
			return true;
		}
		if(DataManager.checkReadWriteForVariable(updateVarID) == true) {
			if((DataManager.checkWriteLock(updateVarID) == false) && (DataManager.checkReadLock(updateVarID) == false)) {
				DataManager.setLock(tx, updateVarID, "WL");

				tx.tempCache.put(updateVarID, updateVal);
				System.out.println("[ Write ] : Transaction " + tx.getTxID() + " has updated variable " + updateVarID + " to " + updateVal + " in local copy");
				return true;
			} else {
				if(DataManager.checkSelfLock(tx, updateVarID)) {
					DataManager.setLock(tx, updateVarID, "WL");
					tx.tempCache.put(updateVarID, updateVal);
					System.out.println("[ Write ] : Transaction " + tx.getTxID() + " has updated variable " + updateVarID + " to " + updateVal + " in local copy");
					return true;
				} else {
					if(!waitingCommands.containsKey(tx)) {
						addToWaitList(updateVarID, "W", updateVal.toString(), tx);
						System.out.println("[ On Waitlist ] : Write " + updateVarID + " on Transaction " + tx.getTxID());
					}
					if(deadLockDetection()){
						System.out.print("[ Deadlock ] : Deadlock detected, ");
						KillLatestTx();
					}
				}

			}
		} else {
			if(!waitingCommands.containsKey(tx)) {
				addToWaitList(updateVarID, "W", updateVal.toString(), tx);
				System.out.println("[ On Waitlist ]: Write " + updateVarID + " on Transaction " + tx.getTxID());
			}
		}
		return false;
	}
	
	protected Set<Transaction> getTxsWithLocks(String variableID){
		Set<Transaction> LockholdingTxs = new HashSet<Transaction>();
			for(Site site : DataManager.db) {
				if(site.isVarInSite(variableID)) {
					LockholdingTxs = site.getLockTransaction(variableID);
					break;
				}
			}
			return LockholdingTxs;
	}

	public boolean deadLockDetection() {
		waitListGraph = new Deadlock();
		for(Transaction tr : waitingCommands.keySet()) {
			if(tr.getType().equals("RO")) {
				continue;
			}
			waitListGraph.addVertices(tr.getTxID());
			String variableID = waitingCommands.get(tr).get(0);
			Set<Transaction> LockholdingTxs = getTxsWithLocks(variableID);
			for(Transaction holdTr : LockholdingTxs) {
				waitListGraph.addEdge(tr.getTxID(), holdTr.getTxID());
				waitListGraph.addVertices(holdTr.getTxID());
			}
		}
		return waitListGraph.isDeadLocked();
	}
	
	public void KillLatestTx() {
		
		long latest = Long.MIN_VALUE;
		Transaction latestTx = new Transaction(null, null);
		ArrayList<String> waitListtx = waitListGraph.cycleList();
		for(String txID : waitListtx) {
			for(Transaction tr : waitingCommands.keySet()) {
				if(tr.getTxID().equals(txID)) {
					if (tr.getTxTimeStamp() > latest) {
						latest = tr.getTxTimeStamp();
						latestTx = tr;
					}
					break;
				}
			}
		}
		System.out.println("" + latestTx.getTxID() + " aborted");
		terminate(latestTx);
	}
	
	public void end(String txID) {
	
		Transaction tx = getTx(txID);
		if(tx == null) {
			System.out.println("WARNING: Transaction " + txID + " does not exist");
			return;
		}
		if(tx.getType().equals("RW")) {
			for(String updatedVarID : tx.tempCache.keySet()) {
				int updatedVal = tx.tempCache.get(updatedVarID);
				DataManager.updateDB(updatedVarID, updatedVal);
				System.out.println("[ Commit ] : Transaction " + tx.getTxID() + "- updated the value of " + updatedVarID + " to "+ updatedVal + " in database.");
			}
		}
		System.out.println("[ Terminate ] : Transaction " + tx.getTxID() + " terminated.");
		terminate(tx);
		
	}
	
	public void terminate(Transaction tx) {
		
		if(activeTxs.contains(tx)) {
			activeTxs.remove(tx);
		}

		if(waitingCommands.containsKey(tx)) {
			waitingCommands.remove(tx);
		}
		
		tx.tempCache.clear();
		
		if(tx.getType().equals("RW")) {
			DataManager.removeLock(tx);
		}

		if(waitingCommands.size() != 0) {
			waitListedCommands();
		}
	}

	private void waitListedCommands() {
		boolean removeFlag = false;
		Set<Transaction> set = new LinkedHashSet<Transaction>(waitingCommands.keySet());
		for (Transaction tx : set) {
			String txID = tx.getTxID();
			String varID = waitingCommands.get(tx).get(0);
			String cmdDetails = waitingCommands.get(tx).get(1);
			if(!activeTxs.contains(tx)) {
				activeTxs.add(tx);
			}
			if(cmdDetails.equals("R")) {
				removeFlag = read(txID, varID);
			} else {
				String strVal = waitingCommands.get(tx).get(2);
				int value = Integer.parseInt(strVal);
				removeFlag = write(txID, varID, value);
			}
			if(removeFlag) {
				waitingCommands.remove(tx);
			}
			break;
		}
	}

	public void recover(String siteNum) {
		int siteID = Integer.parseInt(siteNum);
		DataManager.recover(siteID);
		for(Site site : DataManager.db) {
			for(Transaction tx : waitingCommands.keySet()) {
				if(tx.getType().equals("RO")) {
					String varID = waitingCommands.get(tx).get(0);
					if(site.isVarInSite(varID)) {
						int value = site.getVariable(varID).getValue();
						tx.tempCache.replace(varID, value);
				}
				}
			}
		}
		waitListedCommands();
	}

	public void fail(String siteNum) {
		
		int siteID = Integer.parseInt(siteNum);
		ArrayList<Transaction> abortTxList = new ArrayList<Transaction>();
		for(Site site : DataManager.db) {
			if(site.getSiteIndex() == siteID) {
				for(Transaction tx : activeTxs) {
					if(site.isTxInSite(tx)) {
						abortTxList.add(tx);
					}
				}
				DataManager.fail(siteID);
				for(Transaction tx : abortTxList) {
					System.out.println("WARNING: " + tx.getTxID() + " is aborted because Site " + siteID + " is down.");
					terminate(tx);
				}		
				break;
			}
		}
	}
	

	public void dump() {
		DataManager.dump();
	}
	
	public void dump(int siteNum) {
		DataManager.dump(siteNum);
	}
	
	public void dump(String variable) {}
}