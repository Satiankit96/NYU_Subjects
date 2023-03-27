/**
@Author: Akshat Kiritkumar Jain(aj3186)
@Author: Ankit Sati(as14128)

@Date: 9/11/2022

@Class_Description: DataManager class which manages the database, its sites and variables. TxManager makes use of the operations 
provided by the Data Manager.
**/

package rep;

import java.util.List;
import java.util.ArrayList;


public class DataManager {
	
	protected static List<Site> db;
	
	protected DataManager() {
		db = new ArrayList<Site>();
		int i;
		for (i = 1; i <= 10; i++) {
			Site s = new Site(i);
			db.add(s);
		}
	}
	
	protected static void fail(int siteIdx) {
		for (Site site : db) {
			if (site.getSiteIndex() == siteIdx) {
				site.fail();
				site.eraselockTable();
				site.eraseVariableList();
				System.out.println("[ Fail ] :  Site " + siteIdx + " is down.");
				break;
			}
		}
	}
	
	protected static void recover(int siteIdx) {
		for (Site site : db) {
			if (site.getSiteIndex() == siteIdx) {
				site.recover();
				System.out.println("[ Recover ] : Site " + siteIdx + " is up after recovery.");
				break;
			}
		}
	}
	
	protected static boolean checkReadWriteForVariable(String varID) {
		for(Site site : db) {
			if(site.isSiteUp()) {
				if (site.isVarInSite(varID)) {
					return true;
				}	
			} 
		}
		return false;
	}
	

	
	protected static void setLock(Transaction tx, String varID, String lockType) {
		for(Site site : db) {
			if(site.isSiteUp() && site.isVarInSite(varID)) {
				site.lockVariable(varID, tx, lockType); 
			}
		}
	}
	
	protected static void removeLock(Transaction tx) {
		for(Site site : db) {
			site.unlockTx(tx);
		}	
	}

	protected static boolean checkWriteLock(String varID) {
		for(Site site : db) {
			if(site.isSiteUp() && site.isVarInSite(varID)) {
				return site.checkLockState(varID, "WL");
			}
		}
		return false;
	}
	
	protected static boolean checkReadLock(String varID) {
		for(Site site : db) {
			if(site.isSiteUp() && site.isVarInSite(varID)) {
				return  site.checkLockState(varID, "RL");
			}
		}
		return false;
	}
	
	protected static boolean selfLockLoop(Transaction tx, String varID) {
		for(Site site : db) {
			if(site.isSiteUp() && site.isVarInSite(varID)) {
				return site.isSelfLocked(tx, varID);
			}
		}
		return false;
	}

	protected static boolean checkSelfLock(Transaction tx, String varID) {
		
		boolean result = selfLockLoop(tx, varID);
		return result;
	}
	
	protected static Variable readVar(int SiteIndex, String variableID) {	
		Site site = db.get(SiteIndex - 1);
		Variable varID = site.getVariable(variableID); 
		return varID;
	}
	
	protected static void updateDB(String changedVariableID, int changedValue) {
		for(Site site : DataManager.db) {
			if(site.isSiteUp() && site.isVarInSite(changedVariableID)) {
				site.updateVarValue(changedVariableID, changedValue);
			}
		}
	}
	
	protected static void printDetails() {
		for (Site site : db) {
			
			if (site.isSiteUp()) {
				System.out.print("site " + site.getSiteIndex() + " - ");
				
				int i = 1;
				while (i<=20){
					String var = "x" + Integer.toString(i);
					
					if (site.isVarInSite(var)) {
						Variable variable = readVar(site.getSiteIndex(), var);
						if (i == 20) {
							System.out.print(variable.getVarID() + ": " + variable.getValue() + "  ");
						}
						else {
							System.out.print(variable.getVarID() + ": " + variable.getValue() + ", ");
						}
					}
					i++;
				}
			}
			System.out.println();
		}
	}
	protected static void dump() {

		System.out.println("<-----******----->");
		System.out.println("[ Dump ] :");
		printDetails();
	}
	
	protected static void printSiteDetails(int siteIndex) {
		for (Site site : db) {
			if (site.getSiteIndex() == siteIndex) {
				if (site.isSiteUp()){
					System.out.print("site " + site.getSiteIndex() + " - ");
					
					int i = 1;
					while (i<=20){
						String var = "x" + Integer.toString(i);
						
						if (site.isVarInSite(var)) {
							Variable variable = readVar(site.getSiteIndex(), var);
							if (i == 20) {
								System.out.print(variable.getVarID() + ": " + variable.getValue() + "  ");
							}
							else {
								System.out.print(variable.getVarID() + ": " + variable.getValue() + ", ");
							}
							
						}
						i++;
					}
				}
				else {
					System.err.println("[ Site ] " + siteIndex + " is down.");
				}
			}
		}
	}

	protected static void dump(int siteIndex) {
		System.out.println("<-----******----->");
		System.out.println("[ Dump ] :");
		printSiteDetails(siteIndex);
		
	}
	
	protected static void dump(String variableID) {
		System.out.println("<-----******----->");
		System.out.println("[ Dump ] :");
		
		System.out.println("[ Variable ID ] : " + variableID);
		for (Site site : db) {
			if (site.isSiteUp()) {
				String siteID = "site " + site.getSiteIndex();

				if (site.isVarInSite(variableID)) {
					Variable var = readVar(site.getSiteIndex(), variableID);
					System.out.println(siteID + ": " + var.getValue());
				}

			}
		}
	}
}