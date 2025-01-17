package classification;

//Read in parameters (e.g. directories, names of data-files) from a txt-file. These parameters may be used
//in a analysis later. The idea is to control a whole analysis by a simple txt-file
//See for example the class: ReadInVariables.java
//Last date worked on 09/24/17
//Written by: Daniel Lersch - d.lersch@fz-juelich.de


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetRunPars {

	//+++++++++++++++++++++++++++++++++++++++++
	private boolean isLineFound = false;
	private String[] myString = null;
	private int[] myInt = null;
	private double[] myDouble = null;
	private List<Object> confList = null;
	private List<String> confNames = null;
	private List<String> confTypes = null;
	private List<String> confLines = null;
	private FileInputStream configFile = null;
	private boolean isConfSet = false;
	private String firstSeperator = null;
	private String secondSeperator = null;
	//+++++++++++++++++++++++++++++++++++++++++
	
	/*
	public static void main(String[] args) throws IOException {
		
		FileInputStream fstream = new FileInputStream("/Users/daniellersch/Desktop/CLAS/NN_studies/Apache_Classifier/testConf.txt");
		
		String[] cName = {"DATADIR","NUMBEROFFILES"};
		String[] cType = {"string","int"};
		
		new GetRunPars(fstream,cName,cType);
	   
	}
	*/
	
	public GetRunPars() {
		setLists();
	}

	//Scan the existing config-file for the specified parameters:
	//************************************************************************************************
	public void scanConfFile() throws IOException {
		if(isConfSet) {
		  Scanner sc = new Scanner(configFile);
		  String line;
		  confLines = new ArrayList<String>();
		
		  while(sc.hasNextLine()) {
			line = sc.nextLine();
			confLines.add(line);
		  }
		  sc.close();
         configFile.close();
		}else System.out.println("Sorry. You have to specify the config-file first!");
	}
	
	
	public void setConfigPars() throws IOException {
		if(isConfSet) {
		  isLineFound = false;
		  String currentLine;
		  int nEntries = confNames.size();     
	      scanConfFile();
	   
		  //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	     int iter = 0;
	     for(int h=0;h<nEntries;h++) {
		     iter = 0;
		     while(iter < confLines.size()) {
		    	   currentLine = confLines.get(iter);
			     loadConf(currentLine,confNames.get(h),confTypes.get(h));
			     iter++;
		       }
		     currentLine = null;
	     }
	     //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		   isLineFound = false;
		}else System.out.println("Sorry. No config-file found. Have you set one?");
	}
	
	//=======================================================================
	
	public void setConfigFile(FileInputStream configFile) {
		isConfSet = false;
		this.configFile = configFile;
		isConfSet = true;
	}
	
	
	//************************************************************************************************
	
	//Set up lists to load names in the config file:
	//************************************************************************************************
	public void setLists() {
		confList = new ArrayList<Object>();
		confNames = new ArrayList<String>();
		confTypes = new ArrayList<String>();
	}
	
	//=======================================================================
	
	public void addToList(String name, String type) {
		confNames.add(name);
		confTypes.add(type);
	}
	
	//=======================================================================
	
	public String getConfNameFromList(int index) {
		String out = confNames.get(index);
		return out;
	}
	
	//=======================================================================
	
	public String getConfTypeFromList(int index) {
		String out = confTypes.get(index);
		return out;
	}
	
	//=======================================================================
	
	public void emptyList(List<?> list) {
		if(!list.isEmpty())list.clear();
	}
	//************************************************************************************************
	
	//Stuff for reading in names:
	//************************************************************************************************
	public void setConfigString(String currentLine, String configLine) {
		isLineFound = false;
		if(currentLine.contains(configLine)) {
			isLineFound = true;
			String[] firstSegments = currentLine.split(firstSeperator);
			String[] secondSegments = firstSegments[1].split(secondSeperator);
			this.myString = null;
			
			if(secondSegments.length > 1) {
				this.myString = new String[secondSegments.length];
				for(int h=0;h<this.myString.length;h++) {
					this.myString[h] = secondSegments[h];
				}
			}else {
			    this.myString = new String[firstSegments.length-1];
				for(int h=1;h<this.myString.length+1;h++) {
					this.myString[h-1] = firstSegments[h];
				}
			}
		}else return;
	}
	
	//=======================================================================
	
	public String[] getConfigString() {
		return myString;
	}
	//************************************************************************************************
	
	//Stuff for reading in int numbers:
	//************************************************************************************************
	public void setConfigInt(String currentLine, String configLine) {
		isLineFound = false;
		if(currentLine.contains(configLine)) {
			isLineFound = true;
			String[] firstSegments = currentLine.split(": ");
			String[] secondSegments = firstSegments[1].split(", ");
			this.myInt = null;
			
			
			if(secondSegments.length > 1) {
				this.myInt = new int[secondSegments.length];
				for(int h=0;h<this.myInt.length;h++) {
					this.myInt[h] = Integer.parseInt(secondSegments[h]);
					//System.out.println(myInt[h]);
				}
			}else {
				this.myInt = new int[firstSegments.length-1];
				for(int h=1;h<this.myInt.length+1;h++) {
					this.myInt[h-1] = Integer.parseInt(firstSegments[h]);
					
					//System.out.println(myInt[h-1]);
				}
			}
			
		}else return;
	}
	
	//=======================================================================
	
	public int[] getConfigInt() {
		return myInt;
	}
	//************************************************************************************************
	
	//Stuff for reading in double numbers:
		//************************************************************************************************
		public void setConfigDouble(String currentLine, String configLine) {
			isLineFound = false;
			if(currentLine.contains(configLine)) {
				isLineFound = true;
				String[] firstSegments = currentLine.split(": ");
				String[] secondSegments = firstSegments[1].split(", ");
				this.myDouble = null;
				
				
				if(secondSegments.length > 1) {
					this.myDouble = new double[secondSegments.length];
					for(int h=0;h<this.myDouble.length;h++) {
						this.myDouble[h] = Double.parseDouble(secondSegments[h]);
					}
				}else {
					this.myDouble = new double[firstSegments.length-1];
					for(int h=1;h<this.myDouble.length+1;h++) {
						this.myDouble[h-1] = Double.parseDouble(firstSegments[h]);
					}
				}
				
			}else return;
		}
		
		//=======================================================================
		
		public double[] getConfigDouble() {
			return myDouble;
		}
		//************************************************************************************************
	
	//Now read from the config-file, according to the names and types you specified:
	//************************************************************************************************
	public void loadConf(String currentLine, String name, String type) {
		if(type.equals("int")) {
			setConfigInt(currentLine,name);
			if(isLineFound) {
				confList.add(getConfigInt());
			}
		}else if(type.equals("string")) {
			setConfigString(currentLine,name);
			if(isLineFound) {
				confList.add(getConfigString());
			}
		}else if(type.equals("double")) {
			setConfigDouble(currentLine,name);
			if(isLineFound) {
				confList.add(getConfigDouble());
			}
		}
	}
	//************************************************************************************************
	
	//Get information from list, that has been loaded:
	//************************************************************************************************
	public String[] getStringFromList(String name) {
		String[] out = null;
		
		int listIter = 0;
		while(listIter < confList.size()) {
			if(confNames.get(listIter).equals(name) && confTypes.get(listIter).equals("string")) {
				out = (String[]) confList.get(listIter);
			}
			listIter++;
		}
		
		return out;
	}
	
	//=======================================================================
	
	public int[] getIntFromList(String name) {
		int[] out = null;
		
		int listIter = 0;
		while(listIter < confList.size()) {
			if(confNames.get(listIter).equals(name) && confTypes.get(listIter).equals("int")) {
				out = (int[]) confList.get(listIter);
			}
			listIter++;
		}
		
		return out;
	}
	
	//=======================================================================
	
	public double[] getDoubleFromList(String name) {
			double[] out = null;
			
			int listIter = 0;
			while(listIter < confList.size()) {
				if(confNames.get(listIter).equals(name) && confTypes.get(listIter).equals("double")) {
					out = (double[]) confList.get(listIter);
				}
				listIter++;
			}
			
			return out;
		}
	
	//=======================================================================
	
	public void cleanLists() {
		emptyList(confList);
		emptyList(confNames);
		emptyList(confTypes);
		emptyList(confLines);
		
		isLineFound = false;
		myString = null;
		myInt = null;
		myDouble = null;
		configFile = null;
		isConfSet = false;
		firstSeperator = null;
		secondSeperator = null;
	}
	
	//=======================================================================

	public String getFirstSeperator() {
		return firstSeperator;
	}
	
	//=======================================================================

	public void setFirstSeperator(String firstSeperator) {
		this.firstSeperator = firstSeperator;
	}
	
	//=======================================================================

	public String getSecondSeperator() {
		return secondSeperator;
	}
	
	//=======================================================================

	public void setSecondSeperator(String secondSeperator) {
		this.secondSeperator = secondSeperator;
	}
	//*******************************************************************************************
}
