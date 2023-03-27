/**
@Author: Akshat Kiritkumar Jain(aj3186)
@Author: Ankit Sati(as14128)

@Date: 9/11/2022

@Class_Description: Main class which runs the project.  
Reads input file and creates command actions 
**/


package rep;

import java.io.*;

public class Main {
	public static void readInputfile(String fileName) throws IOException {
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);

		DataManager dmObj = new DataManager();
		TxManager tmObj = new TxManager();

		String line = null;
		
		while((line = br.readLine()) != null) {
			
			String[] lineterms = line.split("\\(");
			
			String cmd = lineterms[0];
			
			if(cmd.equals("begin")) {
				String txID = lineterms[1].split("\\)")[0].trim();
				tmObj.begin(txID, "RW");
			}

			else if (cmd.equals("beginRO")) {
				String txID = lineterms[1].split("\\)")[0].trim();
				tmObj.begin(txID, "RO");
			}

			else if(cmd.equals("W")) {
				String[] cmdDetails = lineterms[1].split(",");
				String txID = cmdDetails[0].trim();
				String wvarID = cmdDetails[1].trim();
				int val = Integer.parseInt(cmdDetails[2].split("[\\)]")[0].trim());
				
				tmObj.write(txID, wvarID, val);
			}

			else if(cmd.equals("R")) {
				String[] cmdDetails = lineterms[1].split(",");
				String txID = cmdDetails[0].trim();
				String rvarID = cmdDetails[1].split("\\)")[0].trim();
				tmObj.read(txID, rvarID);
			}

			else if(cmd.equals("dump")) {
				String[] dumpVal = lineterms[1].split("\\)");
				
				if(lineterms[1].equals(")")) {
					tmObj.dump();
				} 
				else if(dumpVal[0].trim().matches("[0-9]+")) {
					tmObj.dump(Integer.parseInt(dumpVal[0].trim()));
				} else {
					tmObj.dump(dumpVal[0].trim());
				}
			}

			else if(cmd.equals("fail")) {
				String fail_SiteID = lineterms[1].split("\\)")[0].trim();
				tmObj.fail(fail_SiteID);
			}

			else if(cmd.equals("recover")) {
				String rec_SiteID = lineterms[1].split("\\)")[0].trim();
				tmObj.recover(rec_SiteID);
			}

			else if(cmd.equals("end")) {
				// only related to write action
				String txID = lineterms[1].split("\\)")[0].trim();
				tmObj.end(txID);
			} 
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		
		if (args.length != 1) {
			System.out.println("Please provide input file!");
		}

		readInputfile(args[0]);
	}
}