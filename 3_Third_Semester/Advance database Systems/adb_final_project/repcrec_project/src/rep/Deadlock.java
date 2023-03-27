/**
@Author: Akshat Kiritkumar Jain(aj3186)
@Author: Ankit Sati(as14128)

@Date: 10/11/2022

@Class_Description: DeadlockG class which detects deadlocks using graphs
**/

package rep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Deadlock {

	private Map<String, ArrayList<String>> adjacencyList;
	private HashMap<String, Boolean> verticeList;
	ArrayList<String> cycleList;
	
	public Deadlock() {
		adjacencyList = new HashMap<String, ArrayList<String>>();
		verticeList = new HashMap<>();
	}
	
	protected int getVerticeNum() {
		return verticeList.size();
	}
	
	protected boolean isDeadLocked() {
		Set<String> graphCycle = new HashSet<>();
		
		for(String v : verticeList.keySet()) {

			if(checkDeadlock(v, graphCycle)) {
				return true;
			}
		}
		return false;
	}
	
	
	protected boolean checkDeadlock(String v, Set<String> graphCycle) {
		cycleList = new ArrayList<String>();
		cycleList.add(v);
		if(verticeList.get(v) == false) {
			verticeList.replace(v, true);
			graphCycle.add(v);
			for(String i : adjacencyList.get(v)) {
				if(verticeList.get(i) == false && checkDeadlock(i, graphCycle)) { return true; }
				else if(graphCycle.contains(i)) {
					cycleList.add(i);
					return true;
				}
			}
		}
		graphCycle.remove(v);
		return false;
		
	}
	protected void addEdge(String nextTx, String lockedTx) {
		this.adjacencyList.get(nextTx).add(lockedTx);
	}

	protected void addVertices(String txID) {
		
		if(verticeList.containsKey(txID) == false) {
			this.adjacencyList.put(txID, new ArrayList<String>());
			this.verticeList.put(txID, false);
		}
	}
	
	protected ArrayList<String> cycleList() {
		return cycleList;
	}
	
}