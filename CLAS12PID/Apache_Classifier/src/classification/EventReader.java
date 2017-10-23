package classification;

//Read in hipo-files and setup banks that will be used in the later analysis
//The parameters used here (such as directories, number of files) are read in
//from a config-file
//Output is a list of all events with specified banks
//Last date worked on 10/16/17
//Written by: Daniel Lersch - d.lersch@fz-juelich.de

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

public class EventReader {

	//Basic variables to read in hipo-files:
		//++++++++++++++++++++++++++++++++++++++++++++++
		private String DATADIR = null;
		private int nParticles = 0;
		private int[] particleLabels = null;
		private int[] eventLabels = null;
		private String[] inputFiles = null;
		private int[] filesPerParticle = null;
		private int[] startIndexPerFile = null;
		private int sumAllFiles = 0;
		private int[] particleCharge = null;
		private int[] eventCharge = null;
		//++++++++++++++++++++++++++++++++++++++++++++++
		
		//Stuff for reading in hipo-files:
		//++++++++++++++++++++++++++++++++++++++++++++++
		private HipoDataSource[] reader = null;
		private int[] nEventsPerFile = null;
		//++++++++++++++++++++++++++++++++++++++++++++++
		
		//Stuff for looking at banks and their variables:
		//++++++++++++++++++++++++++++++++++++++++++++++
		private int nBanks = 0;
		private List<String> bankNameList = null;
		boolean areBanksSet = false;
		private DataEvent event = null;
		private List<String> yourVarList = null;
		private List<String> yourVarType = null;
		private List<String> yourVarBank = null;
		private int nVariables = 0;
		private List<DataBank> bankList = null;
		private List<Object> myVars = null;
		private boolean areVarsSet = false;
		//++++++++++++++++++++++++++++++++++++++++++++++
		
				
		EventReader(FileInputStream confFile) throws IOException{
			  //Initialise the config-file-reader:
			  GetRunPars grp = new GetRunPars();
			
			  //Set the names you want to read in:
			  //------------------------------------------
			  grp.setConfigFile(confFile);
			  grp.setFirstSeperator(": ");
			  grp.setSecondSeperator(", ");
			  grp.addToList("DATADIR", "string");
			  grp.addToList("INPUTFILES", "string");
			  grp.addToList("NUMBEROFFILES", "int");
			  grp.addToList("FIRSTFILE", "int");
			  grp.addToList("SPECIES", "int");
			  grp.addToList("CHARGE", "int");
			  //------------------------------------------
			
			  //Scan the file for the specified parameters above:
			  grp.setConfigPars();
			
			  //And no retrieve the parameters you specified
			  //-----------------------------------------------------------
			  setDataDir(grp.getStringFromList("DATADIR")[0]);
			  setInputFiles(grp.getStringFromList("INPUTFILES"));
			  setFilesPerParticle(grp.getIntFromList("NUMBEROFFILES"));
			  setStartIndex(grp.getIntFromList("FIRSTFILE"));
			  setParticleLabels(grp.getIntFromList("SPECIES"));
			  setParticleCharge(grp.getIntFromList("CHARGE"));
			  //-----------------------------------------------------------
			  
		  
			  //Clean all lists afterwards:
			  grp.cleanLists();
			  
			  //Initialise bank lists to be read:
			  setLists();
		}
		
		//Setup basic parameters:
		//**********************************************************************************
		public void setDataDir(String DATADIR) {
			this.DATADIR = DATADIR;
		}
		
		//===========================================================
		
		public void setInputFiles(String[] inputFiles) {
			this.inputFiles = inputFiles;
			this.nParticles = inputFiles.length;
		}
		
		//===========================================================
		
		public void setFilesPerParticle(int[] filesPerParticle) {
			this.filesPerParticle = filesPerParticle;
			
			int N = filesPerParticle.length;
			sumAllFiles = 0;
			if(N == nParticles) {
				for(int k=0;k<N;k++) {
					sumAllFiles += filesPerParticle[k];
				}
			}else System.out.println("Warning! Missmatch in config-parameters!");
			
		}
		
		//===========================================================
		
		public void setStartIndex(int[] startIndexPerFile) {
			this.startIndexPerFile = startIndexPerFile;
		}
		
		//===========================================================
		
		public void setParticleLabels(int[] particleLabels) {
			this.particleLabels = particleLabels;
		}
		
		//===========================================================
		
		public void setParticleCharge(int[] particleCharge) {
			this.particleCharge = particleCharge;
		}
		
		//**********************************************************************************
		
		//Setup readers:
		//**********************************************************************************
		public void setReaders() {		
			reader = new HipoDataSource[sumAllFiles];
			nEventsPerFile = new int[sumAllFiles];
			eventLabels = new int[sumAllFiles];
			eventCharge = new int[sumAllFiles];

			String DataName;
			String startDataName = DATADIR + "/out_";
			String endDataName = ".hipo";
			
			int readerIndex = 0;
			int fileIndex = 0;
			//--------------------------------------------------------------------------------
			for(int nPart=0;nPart<nParticles;nPart++) {
				for(int file=0;file<filesPerParticle[nPart];file++) {
					fileIndex = file + startIndexPerFile[nPart];
					DataName = startDataName + inputFiles[nPart] + fileIndex + endDataName;
					
					reader[readerIndex] = new HipoDataSource();
					reader[readerIndex].open(DataName);
					nEventsPerFile[readerIndex] = reader[readerIndex].getSize();
					eventLabels[readerIndex] = particleLabels[nPart];
					eventCharge[readerIndex] = particleCharge[nPart];
					readerIndex++;
				}
			}
			//--------------------------------------------------------------------------------
		}
		//**********************************************************************************
		
		//Setup banks:
		//**********************************************************************************
		public void setLists() {
			bankNameList = new ArrayList<String>();
			yourVarList = new ArrayList<String>();
			yourVarType = new ArrayList<String>();
			yourVarBank = new ArrayList<String>();
			myVars = new ArrayList<Object>();
			bankList = new ArrayList<DataBank>();
		}
		
		//===========================================================
		
		public void addBank(String bankName) {
			bankNameList.add(bankName);
			areBanksSet = true;
			nBanks = bankNameList.size();
		}
		
		//===========================================================
		
		public void addVar(String varBank,String varName, String varType) {
			yourVarBank.add(varBank);
			yourVarList.add(varName);
			yourVarType.add(varType);
			nVariables = yourVarList.size();
			this.areVarsSet = true;
		}
		
		//===========================================================
		
		public void getBank(DataEvent ev) {
			if(areBanksSet) {
	
				//-----------------------------------------------	
			    String currentBank;
				for(int h=0;h<nBanks;h++) {
			     	currentBank = bankNameList.get(h);
					//-----------------------------------------------
			     	bankList.add(h,null);
					if(ev.hasBank(currentBank)) {
						bankList.set(h,ev.getBank(currentBank));
						//ev.getBank(currentBank).show();
						
					}
					//-----------------------------------------------
				}
				//-----------------------------------------------
			}
		}
		//**********************************************************************************
		
		//Get Variables from banks:
		//**********************************************************************************
		public void getBankVars() {
			//----------------------------------------------
			for(int i=0;i<bankList.size();i++) {
				//----------------------------------------------
				for(int j=0;j<nVariables;j++) {
					if(yourVarBank.get(j).equals(bankNameList.get(i))) {
					   myVars.add(j,null);
					  if(bankList.get(i) != null) {	
						if(yourVarType.get(j).equals("int")) {
					       myVars.set(j, bankList.get(i).getInt(yourVarList.get(j)) );
						}else if(yourVarType.get(j).equals("double")) {
							myVars.set(j, bankList.get(i).getDouble(yourVarList.get(j)) );
						}else if(yourVarType.get(j).equals("float")) {
						   myVars.set(j, bankList.get(i).getFloat(yourVarList.get(j)) );
						}else if(yourVarType.get(j).equals("short")) {
						  myVars.set(j, bankList.get(i).getShort(yourVarList.get(j)) );
						}else if(yourVarType.get(j).equals("long")) {
							myVars.set(j, bankList.get(i).getLong(yourVarList.get(j)) );
						}else if(yourVarType.get(j).equals("byte")) {
							myVars.set(j, bankList.get(i).getByte(yourVarList.get(j)) );
						}
					  }
					}
				}
				//----------------------------------------------
			}
			//----------------------------------------------
		}
		
		
		//===========================================================
		
		public void emptyList(List<?> list) {
			if(!list.isEmpty())list.clear();
		}
		//**********************************************************************************
		
		//Now do analysis and loop through events:
		//**********************************************************************************
		public void loopThroughEvents(AnalysisModule am) {
			if(areBanksSet) {
			
			 setReaders();
			 
			 int fileIndex = 0;
			  //------------------------------------------------------------------ 
			  for(int i=0;i<sumAllFiles;i++) {
			  	event = null;
			  	fileIndex = i+1;
			  	System.out.println("Processing file: " + fileIndex + " of "  + sumAllFiles + " files...");
			  	//------------------------------------------------------------------
				for(int ev=0;ev<nEventsPerFile[i];ev++) {
					event = (DataEvent)reader[i].gotoEvent(ev);
					
		            //Get the specified banks:
					getBank(event);
					getBankVars();
					
					//Now do the analysis according to the analysis module:
					doAnalysis(am, myVars,yourVarList,yourVarBank,eventCharge[i],eventLabels[i]);
					
					//Clear the banks and variables:
					myVars.clear();
					bankList.clear();
				}
				//------------------------------------------------------------------
				reader[i].close();
				am.saveList(i);
				System.out.println("...done!");
				System.out.println("        ");
			  }
			 //------------------------------------------------------------------
			  
			}else System.out.println("Sorry! You did not specify any Banks!");
		}
		
		//===========================================================
		
		
		public void clearBanks() {
			emptyList(bankNameList);
			emptyList(bankList);
		}
		
		//===========================================================
		
		public void clearVars() {
			emptyList(yourVarList);
			emptyList(yourVarType);
			emptyList(yourVarBank);
		}
		//**********************************************************************************
	
		
		//Specify analysis conditions here:
		//**********************************************************************************
		public void doAnalysis(AnalysisModule am, List<Object> varList, List<String> varName, List<String> varBank, int charge, int type) {
			am.readoutVars(varList, varName, varBank);
			am.createEventFromVars(charge,type);
		}
		//**********************************************************************************
		
	
		
		
		  
		
		 
		

	
}
