package classification;

//Module to steer the whole analysis, which variables are shown, what is saved, etc.
//Parameters are read in from a config file
//Last date worked on 10/23/17
//Written by: Daniel Lersch - d.lersch@fz-juelich.de
//Using: Apache-Spark-FrameWork, CLAS12 reconstruction software


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ClassifierVariables {

	//Stuff to read events from the hipo-files:
	//++++++++++++++++++++++++++++++++++++++++++++++
	private EventReader evr;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff to run the analysis-module:
	//++++++++++++++++++++++++++++++++++++++++++++++
	private AnalysisModule am = new AnalysisModule();
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff to read in conf-pars:
	//++++++++++++++++++++++++++++++++++++++++++++++
	private String baseDir = null;
	private String outputTrainFile = null;
	private boolean doMonitoring = false;
	private boolean storeData = false;
	private String[] runMode = null;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff for using pid:
	//++++++++++++++++++++++++++++++++++++++++++++++
	private String[] pidVars = null;
	private boolean isCLset = false;
	private String clDir = null;
	private String clType = null;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	public static void main(String[]args) throws IOException {
		
		
		FileInputStream[] input = new FileInputStream[2];
		input[0] = new FileInputStream("/Users/lersch/Desktop/CLAS/NN_studies/Apache_Classifier/testConf.txt");
	    input[1] = new FileInputStream("/Users/lersch/Desktop/CLAS/NN_studies/Apache_Classifier/testConf.txt");
	    
	     //input[0] = new FileInputStream(args[0]);
         //input[1] = new FileInputStream(args[0]);
		
	    new ClassifierVariables(input); 
	   
	} 
	
	//******************************************************************
	public ClassifierVariables(FileInputStream[] input) throws IOException {
		getConfPars(input[0]);
	    loadVariables(input[1]);
	    prepPID();
	    loadClassifier();
	    runLoop();
	    cleanUp();
	}
	//******************************************************************
	
	
	
	//******************************************************************
	public void loadVariables(FileInputStream fstream) throws IOException {
		this.evr = new EventReader(fstream);
		
		//Check if data shall be plotted, stored or both:
		//-------------------------------------------------------------------------------------------
		if(runMode[0].equals("F") || (runMode[0].equals("L") && runMode[1].equals("S"))) {
					setStoreData(true);
					setDoMonitoring(false);
	    }else if(runMode[0].equals("L")){
					if(runMode[1].equals("M") || runMode[1].equals("SM") || runMode[1].equals("MS")) {
						if(runMode[1].equals("M")) {
							setStoreData(false);
						}else setStoreData(true);
						   setDoMonitoring(true);
					}
		}
	   //-------------------------------------------------------------------------------------------
	
	   //Check, if classifier name and type are set:
	   //-------------------------------------------------------------------------------------------
		if(runMode[2].equals("X") || runMode[3].equals("X")) {
			setCLset(false);
		}else {
			setCLset(true);
			setClDir(runMode[3]);
			setClType(runMode[2]);
		}
	   //-------------------------------------------------------------------------------------------
		
		//Load stuff from the rec::particle-bank:
  		//------------------------------------------------
  	    evr.addBank("REC::Particle");
  		evr.addVar("REC::Particle", "px", "float");
  		evr.addVar("REC::Particle", "py", "float");
  		evr.addVar("REC::Particle", "pz", "float");
  		evr.addVar("REC::Particle", "vx", "float");
  		evr.addVar("REC::Particle", "vy", "float");
  		evr.addVar("REC::Particle", "vz", "float");
  	    evr.addVar("REC::Particle", "charge", "byte");
  	    evr.addVar("REC::Particle", "pid", "int");
  	    evr.addVar("REC::Particle", "beta", "float");
  		//------------------------------------------------
  		
  	    //------------------------------------------------
        evr.addBank("LTCC::clusters");
  	    evr.addVar("LTCC::clusters", "nphe", "float");
  	    //------------------------------------------------
  	     
  	    //------------------------------------------------
  	    evr.addBank("REC::Cherenkov");
  	    evr.addVar("REC::Cherenkov", "pindex", "short");
  	    evr.addVar("REC::Cherenkov", "detector", "byte");
  	    evr.addVar("REC::Cherenkov", "nphe", "short");
  	    //------------------------------------------------
  	     
  	    //------------------------------------------------
        evr.addBank("REC::Calorimeter");
        evr.addVar("REC::Calorimeter", "pindex", "short");
  	    evr.addVar("REC::Calorimeter", "detector", "byte");
  	    evr.addVar("REC::Calorimeter", "layer", "byte");
  	    evr.addVar("REC::Calorimeter", "energy", "float");
  	    //------------------------------------------------
  	     
  	     //------------------------------------------------
         evr.addBank("REC::Scintillator");
         evr.addVar("REC::Scintillator", "pindex", "short");
  	     evr.addVar("REC::Scintillator", "detector", "byte");
  	     evr.addVar("REC::Scintillator", "time", "float");
  	     //------------------------------------------------
	}
	
	//===========================================
	
	public void runLoop() throws FileNotFoundException {
		
		am.setAreHistsSet(doMonitoring);
		am.setupHitsograms(false, 5.5);
		if(!isCLset) {
			am.setSpark(storeData);
			am.loadSpark();
		}
		
	   am.setDoSaveList(storeData);
	   am.setOutputJsonFile(outputTrainFile);
		 
	System.out.println("Start loop through events...");
		evr.loopThroughEvents(am);
		am.setupAndDrawCanvas();
	}
	
	//===========================================
	
	public void cleanUp() {
		evr.clearBanks();
		evr.clearVars();
		am.endSpark();
	}
	//******************************************************************
	
	//Prepare for using pid:
	//******************************************************************
	public void prepPID() {
		am.setvNames(pidVars);
		am.setLists();
		am.setPIDVariables("variable1", "momentum");
		am.setPIDVariables("variable2", "theta");
		am.setPIDVariables("variable3", "phi");
		am.setPIDVariables("variable4", "npheLTCC");
		am.setPIDVariables("variable5", "npheHTCC");
		am.setPIDVariables("variable6", "deECIN");
		am.setPIDVariables("variable7", "deECOUT");
		am.setPIDVariables("variable8", "dePCAL");
		am.setPIDVariables("variable9", "sumDEcal");
		am.setPIDVariables("variable10", "sumDEcalPerMom");
		am.setPIDVariables("variable11", "tof");
	}
	
	//===========================================
	
	public void loadClassifier() {
		am.setSpark(isCLset);
		am.loadSpark();
		setClDir(runMode[3]);
		am.setCLset(isCLset());
		am.setClType(runMode[2]);
		String clDir = getBaseDir() + getClDir();
		am.setClDir(clDir);
		am.loadClassifier();
	}
	
	//******************************************************************
	
	
	//Get configuration parameters:
	//******************************************************************
	public void setDataOutput(String outputTrainFile) {
		this.outputTrainFile = outputTrainFile;
	}
	
	//===========================================
	
	public boolean isDoMonitoring() {
		return doMonitoring;
	}

	//===========================================

	public void setDoMonitoring(boolean doMonitoring) {
		this.doMonitoring = doMonitoring;
	}

	//===========================================

	public boolean isStoreData() {
		return storeData;
	}

	//===========================================

	public void setStoreData(boolean storeData) {
		this.storeData = storeData;
	}
	
	//===========================================
	
	public String[] getRunMode() {
		return runMode;
	}

	//===========================================

	public void setRunMode(String[] runMode) {
		this.runMode = runMode;
	}
	
	//===========================================
	
	public String[] getPidVars() {
		return pidVars;
	}
	
	//===========================================

	public void setPidVars(String[] pidVars) {
		this.pidVars = pidVars;
	}
	
	//===========================================
	
	public void getConfPars(FileInputStream fstream) throws IOException {
		GetRunPars grp = new GetRunPars();
		grp.setFirstSeperator(": ");
		grp.setSecondSeperator(", ");
		grp.setConfigFile(fstream);
		grp.addToList("BASEDIR", "string");
		grp.addToList("OUTPUTFILE", "string");
		grp.addToList("MODE", "string");
		grp.addToList("VARS", "string");
		grp.addToList("NCORES", "int");
		grp.addToList("PROB-CUT", "double");
		
		grp.setConfigPars();
		
		setBaseDir(grp.getStringFromList("BASEDIR")[0]);
		setDataOutput(grp.getStringFromList("OUTPUTFILE")[0]);
		setRunMode(grp.getStringFromList("MODE"));
		setPidVars(grp.getStringFromList("VARS"));
		am.setNThreads(grp.getIntFromList("NCORES")[0]);
		am.setProbCut(grp.getDoubleFromList("PROB-CUT")[0]);
		
		grp.cleanLists();
		grp = null;
	}
	//******************************************************************

	public boolean isCLset() {
		return isCLset;
	}

	public void setCLset(boolean isCLset) {
		this.isCLset = isCLset;
	}

	public String getClDir() {
		return clDir;
	}

	public void setClDir(String clDir) {
		this.clDir = clDir;
	}

	public String getClType() {
		return clType;
	}

	public void setClType(String clType) {
		this.clType = clType;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	
	
	

	


	
	
	
}
