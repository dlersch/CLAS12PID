package classification;

import java.io.FileNotFoundException;

//Here, all analysis steps (e.g. cuts, combination of particle information, histograms, etc.) 
//are defined
//Last date worked on 09/24/17
//Written by: Daniel Lersch - d.lersch@fz-juelich.de
//Using: Apache-Spark-FrameWork, CLAS12 reconstruction software


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.jlab.groot.data.DataVector;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;

import breeze.features.FeatureVector;


public class AnalysisModule {
	//Stuff to read out the variables:
    //++++++++++++++++++++++++++++++++++++++++++++++
	private float[] myPx = null;
	private float[] myPy = null;
	private float[] myPz = null;
	private float[] myVx = null;
	private float[] myVy = null;
	private float[] myVz = null;
	private byte[] myCharge = null;
	private int[] myPID = null;
	private float[] myBeta = null;
		
	private float[] myLTCCnphe = null;
		
	private short[] myPIcherenkov = null;
	private byte[] myDcherenkov = null;
	private short[] myHTCCnphe = null;
		
	private short[] myPIcal = null;
	private byte[] myDcal = null;
	private float[] myDEcal = null;
	private byte[] myLcal = null;
		
	private short[] myPIscint = null;
	private byte[] myDscint = null;
	private float[] myTscint = null;
	
	//++++++++++++++++++++++++++++++++++++++++++++++
		
	//Stuff to create a collection of used variables:
	//Those you really care about!
	//++++++++++++++++++++++++++++++++++++++++++++++
	private double[] momentum = null;
	private double[] theta = null;
	private double[] phi = null;
	private double[] beta = null;
	private double[] vertex = null;
	private int[] pidEventBuilder = null;
	private double[] npheLTCC = null;
	private double[] npheHTCC = null;
	private double[] sumDEcal = null;
	private double[] sumDEcal_per_MOM = null;
	private double[] deECIN = null;
	private double[] deECOUT = null;
	private double[] dePCAL = null;
	private double[] tTof = null;
	private boolean[] infoUsed = null;
	private boolean[] acceptAngle = null;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff for plotting histograms:
	//++++++++++++++++++++++++++++++++++++++++++++++
	private GetPlots GP = new GetPlots();
	
	private boolean areHistsSet = false;
	private boolean showAcceptance = false;
	private double maxMom = 0.0;
	private int allEvents = 0;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff for saving data:
	//++++++++++++++++++++++++++++++++++++++++++++++
    private boolean isSparkSet = false;
    private int NThreads = 0;
	private SparkSetter SPS = new SparkSetter();
	private List<Row> myList = new ArrayList<>();
	private String outputJsonFile = null;
	private boolean doSaveList = false;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff for using an already trained classifier:
	//++++++++++++++++++++++++++++++++++++++++++++++
	private Vector[] featureVec = null;
	private List<String> varName = null;
	private List<String> varMeaning = null;
	private List<Boolean> isVarUsed = null;
	private List<Object> pidVars = null;
	private List<String> refList = null;
	private boolean varsSet = false;
	private boolean varsLoad = false;
	private String[] vNames = null;
	private boolean isCLset = false;
	private String clType = null;
	private String clDir = null;
	private ClassifierResponse CR = new ClassifierResponse();
	private ClassifierResponse CRin = new ClassifierResponse();
	private ClassifierResponse CRout = new ClassifierResponse();
	private ClassifierResponse[] CRMom = null;
	private double[] clResponse = null;
	private double probCut = 0.0;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
		
    public AnalysisModule() {
		
	}
    
    //Define operations and cuts on the variables to look at:
  	//***********************************************************************************
  	public void readoutVars(List<Object> varList, List<String> varName, List<String> varBank) {
  		int nEntries = varList.size();
  		
  		
  		for(int i=0;i<nEntries;i++) {
  			if(varList.get(i) != null) {
  				if(varName.get(i).equals("px") && varBank.get(i).equals("REC::Particle"))setMyPx((float[])varList.get(i));
  				if(varName.get(i).equals("py") && varBank.get(i).equals("REC::Particle"))setMyPy((float[])varList.get(i));
  				if(varName.get(i).equals("pz") && varBank.get(i).equals("REC::Particle"))setMyPz((float[])varList.get(i));
  				if(varName.get(i).equals("vx") && varBank.get(i).equals("REC::Particle"))setMyVx((float[])varList.get(i));
  				if(varName.get(i).equals("vy") && varBank.get(i).equals("REC::Particle"))setMyVy((float[])varList.get(i));
  				if(varName.get(i).equals("vz") && varBank.get(i).equals("REC::Particle"))setMyVz((float[])varList.get(i));
  				if(varName.get(i).equals("charge") && varBank.get(i).equals("REC::Particle"))setMyCharge((byte[])varList.get(i));
  				if(varName.get(i).equals("pid") && varBank.get(i).equals("REC::Particle"))setMyPID((int[])varList.get(i));
  				if(varName.get(i).equals("beta") && varBank.get(i).equals("REC::Particle"))setMyBeta((float[])varList.get(i));
  				//------------------------------------------------
  				
  				//------------------------------------------------
  				if(varName.get(i).equals("nphe") && varBank.get(i).equals("LTCC::clusters"))setMyLTCCnphe((float[])varList.get(i));
  				//------------------------------------------------
  				
  				//------------------------------------------------
  				if(varName.get(i).equals("pindex") && varBank.get(i).equals("REC::Cherenkov"))setMyPIcherenkov((short[])varList.get(i));
  				if(varName.get(i).equals("detector") && varBank.get(i).equals("REC::Cherenkov"))setMyDcherenkov((byte[])varList.get(i));
  				if(varName.get(i).equals("nphe") && varBank.get(i).equals("REC::Cherenkov"))setMyHTCCnphe((short[])varList.get(i));
  				//------------------------------------------------
  				
  				//------------------------------------------------
  				if(varName.get(i).equals("pindex") && varBank.get(i).equals("REC::Calorimeter"))setMyPIcal((short[])varList.get(i));
  				if(varName.get(i).equals("detector") && varBank.get(i).equals("REC::Calorimeter"))setMyDcal((byte[])varList.get(i));
  				if(varName.get(i).equals("energy") && varBank.get(i).equals("REC::Calorimeter"))setMyDEcal((float[])varList.get(i));
  				if(varName.get(i).equals("layer") && varBank.get(i).equals("REC::Calorimeter"))setMyLcal((byte[])varList.get(i));
  				//------------------------------------------------
  				
  				//------------------------------------------------
  				if(varName.get(i).equals("pindex") && varBank.get(i).equals("REC::Scintillator"))setMyPIscint((short[])varList.get(i));
  				if(varName.get(i).equals("detector") && varBank.get(i).equals("REC::Scintillator"))setMyDscint((byte[])varList.get(i));
  				if(varName.get(i).equals("time") && varBank.get(i).equals("REC::Scintillator"))setMyTscint((float[])varList.get(i));
  				//------------------------------------------------
  			}
  		}
  	}
  	
  	//==============================================================================
  	
  	public void createEventFromVars(int charge, int particleType) {
  		
  		//First, collect the information from banks:
  		getInformationFromBanks(charge,particleType);
  		
  		//Use PID-variables, if requested:
  		if(varsSet)loadPIDVariables();
  		if(varsLoad)setFeatureVec();
  		
  		//Then, filter them, fill or do whatever here:
  		fillData(particleType);
  	}
  	
    //===========================================
  	
  	public void getInformationFromBanks(int charge, int particleType) {
  		int nRowsRP,nRowsRCh,nRowsRCa,nRowsRS,nRowsLT;
  		if(myPx!=null) {
  			nRowsRP = myPx.length;
  		}else nRowsRP = 0;
  		
        if(myHTCCnphe != null) {
          	nRowsRCh = myHTCCnphe.length;
        }else nRowsRCh = 0;
  		 
  		if(myDEcal!=null) {
  			nRowsRCa = myDEcal.length;
  		}else nRowsRCa = 0;
        
        if(myTscint!=null) {
          	nRowsRS = myTscint.length;
        }else nRowsRS = 0;
  		
 		
        if(myLTCCnphe!=null) {
         	nRowsLT = myLTCCnphe.length;
        }else nRowsLT = 0;
  	
        
        //-----------------------------------
        momentum = null;
        theta = null;
        phi = null;
        pidEventBuilder = null;
        npheLTCC = null;
        npheHTCC = null;
        sumDEcal = null;
        sumDEcal_per_MOM = null;
        deECIN = null;
        deECOUT = null;
        dePCAL = null;
    	    tTof = null;
    	    infoUsed = null;
    	    beta = null;
    	    acceptAngle = null;
    	    
    	    momentum = new double[nRowsRP];
    	    theta = new double[nRowsRP];
    	    phi = new double [nRowsRP];
    	    vertex = new double[nRowsRP];
    	    beta = new double[nRowsRP];
    	    pidEventBuilder = new int[nRowsRP];
    	    npheLTCC = new double[nRowsRP];
    	    npheHTCC = new double[nRowsRP];
    	    sumDEcal = new double[nRowsRP];
    	    sumDEcal_per_MOM = new double[nRowsRP];
    	    deECIN = new double[nRowsRP];
    	    deECOUT = new double[nRowsRP];
    	    dePCAL = new double[nRowsRP];
    	    tTof = new double[nRowsRP];
    	    infoUsed = new boolean[nRowsRP];
    	    acceptAngle = new boolean[nRowsRP];
    	    //-----------------------------------
       
        
  		//-------------------------------------------------------------------------------
  		for(int i=0;i<nRowsRP;i++) {
  			 infoUsed[i] = false;
  			 acceptAngle[i] = false;
  			//Get the momentum and pid:
  			//##########################################################
  		    momentum[i] = Math.sqrt(myPx[i]*myPx[i] + myPy[i]*myPy[i] + myPz[i]*myPz[i]);
  		    vertex[i] = Math.sqrt(myVx[i]*myVx[i] + myVy[i]*myVy[i] + myVz[i]*myVz[i]);
  		    pidEventBuilder[i] = myPID[i];
  		   //##########################################################
  		    
  		    //-------------------------------------------------------------------------------
  		   	if(momentum[i] > 0 && myCharge[i] == charge) {
  		    	    this.allEvents++;
  		    	    infoUsed[i] = true;
  		    	    beta[i] = myBeta[i];
  		    	    
  		    	    //Calculate theta and phi: from the momentum components:
  		    	    //------------------------------------------------------------
  		    	    
  		    	    theta[i] = Math.acos(myPz[i]/momentum[i]);
  		    	    
  		    	    if(myPx[i] > 0) {
  		    	    	   phi[i] = Math.atan(myPy[i]/myPx[i]);
  		    	    }else if(myPx[i] == 0) {
  		    	    	    phi[i] = Math.signum(myPy[i])*Math.PI*0.5;
  		    	    }else if(myPx[i] < 0 && myPy[i] >= 0) {
  		    	      	phi[i] = Math.atan(myPy[i]/myPx[i]) + Math.PI;
  		    	    }else if(myPx[i] < 0 && myPy[i] < 0) {
  		    	      	phi[i] = Math.atan(myPy[i]/myPx[i]) - Math.PI;
  		    	    }
  		    	   //------------------------------------------------------------
  		    	    
  		    	    if(theta[i] >= 0 && theta[i] <= 0.5*Math.PI && phi[i] >= -Math.PI && phi[i] <= Math.PI) {
  		    	    	  acceptAngle[i] = true;
  		    	    }else acceptAngle[i] = false;
  		    	   
  		    	    
  		    	    //Get nphe from ltcc:
  		      	//##########################################################
  		    	    npheLTCC[i] = 0;
  		    	    if(nRowsLT > 0) {
  		    	    	   npheLTCC[i] = myLTCCnphe[0];
  		    	    }
  		      	//##########################################################
  		    	    
  		    	    //Get nphe from HTCC:
  		    	    //##########################################################
  		    	    npheHTCC[i] = 0;
  		    	    for(int iHT=0;iHT<nRowsRCh;iHT++) {
  		    	    	   if(myPIcherenkov[iHT] == i && myDcherenkov[iHT] == 6) {
  		    	    		   npheHTCC[i] = myHTCCnphe[iHT];
  		    	    	   }
  		    	    }
  		     	//##########################################################
  		    	    
  		    	    //Get the total energy from the calorimeter:
  		    	   //##########################################################
  		    	    sumDEcal[i] = sumDEcal_per_MOM[i] = 0;
  		    	    for(int iC=0;iC<nRowsRCa;iC++) {
  		    	    	   if(myPIcal[iC] == i) {
  		    	    		  sumDEcal[i] += myDEcal[iC];
  		    	    		  //-------------------------------------------- 
  		    	    		 if(myLcal[iC] < 4) {
    		    	    		   dePCAL[i] += myDEcal[iC];
    		    	    	      }else if(myLcal[iC] >= 4 && myLcal[iC] < 7) {
    		    	    		   deECIN[i] += myDEcal[iC];
    		    	    	      }else deECOUT[i] += myDEcal[iC];
  		    	    		 //--------------------------------------------  
  		    	    	   }
  		    	    }
  		    	    sumDEcal_per_MOM[i] = sumDEcal[i]/momentum[i];
  		     	//##########################################################
  		    	    
  		    	    //Get TOF-information:
  		      	//##########################################################
  		    	    tTof[i] = 0;
  		    	    for(int iSC=0;iSC<nRowsRS;iSC++) {
  		    	    	    if(myPIscint[iSC] == i && myDscint[iSC] == 17) {
  		    	    	    	   tTof[i] += myTscint[iSC];
  		    	    	    }
  		    	    }
  		    	    if(nRowsRS > 0)tTof[i] /= nRowsRS;
  		     	//##########################################################
  		    }
  		    //-------------------------------------------------------------------------------
  		}
  	   //-------------------------------------------------------------------------------	
  		
  		myPx = null;
  		myPy = null;
  		myPz = null;
  		myVx = null;
  		myVy = null;
  		myVz = null;
  		myBeta = null;
  		myLTCCnphe = null;
  		myCharge = null;
  		myPIcherenkov = null;
  		myDcherenkov = null;
  		myHTCCnphe = null;
  		myPIcal = null;
  		myDEcal = null;
  		myPIscint = null;
  		myDscint = null;
  		myTscint = null;
  	}
  	
    //===========================================
  	
  	double betaCalc = 0.0;
  	double massPid = 0.0;
  	double arg = 0;
  	public void fillData(int particleType) {
  		int nRows = momentum.length;
  		for(int i=0;i<nRows;i++) {
  			if(infoUsed[i] && (npheLTCC[i] > 0 || sumDEcal[i] > 0 || npheHTCC[i] > 0) && momentum[i] >= 0.05 && momentum[i] < 5.0) {
  				
  				//Fille histograms if wanted:
  				//-----------------------------------------
  				if(areHistsSet) {
  				  GP.fillPIDHists(particleType, momentum[i], npheLTCC[i], npheHTCC[i], deECIN[i], deECOUT[i], dePCAL[i], tTof[i],vertex[i],theta[i],phi[i]);
  				}
  			    //-----------------------------------------
  				
  				if(isCLset) {
  					clResponse = null;
  					setClResponse(CR.getResponse(featureVec[i], getProbCut()));
  				    
  				   double cut = 0.0; 
  				   if(cutBasedPID(momentum[i], npheLTCC[i], npheHTCC[i], dePCAL[i], deECIN[i], deECOUT[i])) {
  					   cut = 1.0;
  					 CR.calcCurrentRoc(featureVec[i], particleType, 0.95); 
  					 
  					 CRin.scanANDaccumulate(featureVec[i], particleType);
  						
  				   }else {
  					 CRout.scanANDaccumulate(featureVec[i], particleType);
  					 CR.calcCurrentRoc(featureVec[i], particleType, 0.855);  
  				   }
  				   
  				   
  				 CR.scanANDaccumulate(featureVec[i], particleType);
  				 
  				 
  				 double mom_step = (5.0-0.05)/10;
  				 double current_mom_min = 0.0;
  				 double current_mom_max = 0.0;
  				// double[] mom_prob_cut = {0.7125,0.95,0.95,0.855,0.855,0.9025,0.9025,0.9025,0.855,0.95};
  				 double[] mom_prob_cut = {0.57,0.5225,0.5225,0.5225,0.475,0.6175,0.665,0.475,0.5225,0.5225};
  				 
  				 for(int z=0;z<10;z++) {
  					 current_mom_min = 0.05 + mom_step*z;
  					 current_mom_max = 0.05 + mom_step*(z+1);
  					 
  					 if(momentum[i] >= current_mom_min && momentum[i] < current_mom_max) {
  						 //CRMom[z].scanANDaccumulate(featureVec[i], particleType);
  						CR.calcCurrentRoc(featureVec[i], particleType, mom_prob_cut[z]);
  					 }
  				 }
  				 
  				   
  				 
				    GP.fillPIDprob(particleType, getClResponse()[1]);
  				    
  				   //GP.fillPIDHistsCut(getClResponse()[2], momentum[i], npheLTCC[i], npheHTCC[i], deECIN[i], deECOUT[i], dePCAL[i], tTof[i],vertex[i]);
  				 GP.fillPIDHistsCut(cut, momentum[i], npheLTCC[i], npheHTCC[i], deECIN[i], deECOUT[i], dePCAL[i], tTof[i],vertex[i]);
   				    
  				   /*
  				   Vector[] testVec = CRClu1.getkmeansCluster();
  				   
  				   if(particleType == 0) {
  				   for(int n=0;n<testVec.length;n++) {
  					  double[] testCenter = testVec[n].toArray();
  					  System.out.println("Vector: " + n + " :");
  					  for(int u=0;u<testCenter.length;u++) {
  						  System.out.println("With centers: " + testCenter[u]);
  					  }
  				   }
  				   
  				   }
   				    */
   				    if(getClResponse()[2] == 1) {
   				    	  massPid = 0.000511;
   				    }else  massPid = 0.140;
   				    
   				    arg = momentum[i]/massPid;
   				    betaCalc = Math.sqrt(arg*arg / (1 + arg*arg));
   				    
   				    
   				    GP.fillBetaHists(getClResponse()[2], getClResponse()[1], momentum[i], betaCalc, beta[i], tTof[i]);
  				}
  				
  			   //Fill list for Spark:
  			   //-----------------------------------------
  			   if(isSparkSet) {
	    	    	        myList.add(RowFactory.create(particleType,momentum[i],theta[i],phi[i],npheLTCC[i],npheHTCC[i],deECIN[i],deECOUT[i],dePCAL[i],sumDEcal[i],sumDEcal_per_MOM[i],tTof[i]));
	    	       }
  			   //-----------------------------------------
  			}
  		}
  	  momentum = null;
  	  theta = null;
  	  phi = null;
  	  vertex = null;
  	  beta = null;
  	  vertex = null;
  	  npheLTCC = null;
  	  npheHTCC = null;
  	  deECIN = null;
      deECOUT = null;
      dePCAL = null;
      sumDEcal = null;
      sumDEcal_per_MOM = null;
      tTof = null;
      infoUsed = null;
      featureVec = null;
  	}
  	
  	//***********************************************************************************
  	
  	
  	//Define histograms and canvases:
    //***********************************************************************************
  	public void setAreHistsSet(boolean areHistsSet) {
  		this.areHistsSet = areHistsSet;
  	}
  	
    //===========================================
  	
  	public boolean getAreHistsSet() {
  		return areHistsSet;
  	}
  	
    //===========================================
  	
  	public void setupHitsograms(boolean showAcceptance, double maxMom) {
  		if(areHistsSet) {
  		
  	       this.showAcceptance = showAcceptance;
  		   this.maxMom = maxMom;
  		
  		   GP.setHitsograms(showAcceptance, maxMom);
  		}
  	}
    
    //===========================================
  	
  	public void setupAndDrawCanvas() throws FileNotFoundException {
  		if(areHistsSet) {
  		  GP.setCanvas(showAcceptance, allEvents);
  		  GP.setMoreCanvas();
  		 if(isCLset)CR.showCurrentPerformance();
  		 if(isCLset) {
  			 CR.getROC("PlotAndText");
  			 CRin.getROC("PlotAndText");
  			 CRout.getROC("PlotAndText");
  			 /*
  			 for(int z=0;z<10;z++) {
  				 CRMom[z].getROC("GetMCC");
  			 }
  			 */
  		  }
  		}
  	}
  	//***********************************************************************************
  	
  	
  //Everything around spark:
  //******************************************************************
  	public void setSpark(boolean isSparkSet) {
  		this.isSparkSet = isSparkSet;
  	}
  	
  	//===========================================
  	
  	public boolean runSpark() {
  		return isSparkSet;
  	}
  	
  //===========================================
  	
  	public void loadSpark() {
  		if(isSparkSet) {
  		SPS.setSparkSession("ClassifierVariables",getNThreads());	
  	  }
  	}
  	
  	//===========================================
  	
  	public void saveList(int i) {
  	  String part = "/dataPart" + i;
  	  String allName = outputJsonFile + part;
  	  if(isSparkSet && isDoSaveList()) {
  	    SPS.saveListToJsonFile(myList, allName);
  	  }
  	}
  	
    //===========================================
  	
  	public void endSpark() {
  		if(isSparkSet)SPS.stopSpark();
  	}
  	
    //***********************************************************************************
  	
  	//Include a pre-existing classifier into the analysis:
    //***********************************************************************************
  	public void emptyList(List<?> list) {
		if(!list.isEmpty())list.clear();
	}
  	
  	//=====================================
  	
  	public void setLists() {
  		varName = new ArrayList<String>();
  		varMeaning = new ArrayList<String>();
  		isVarUsed = new ArrayList<Boolean>();
  		pidVars = new ArrayList<Object>();
  		refList = new ArrayList<String>();
  		
  		refList.add("momentum");
  		refList.add("theta");
  		refList.add("phi");
  		refList.add("npheLTCC");
  		refList.add("npheHTCC");
  		refList.add("deECIN");
  		refList.add("deECOUT");
  		refList.add("dePCAL");
  		refList.add("sumDEcal");
  		refList.add("sumDEcalPerMom");
  		refList.add("tof");
  	}
  	
    //=====================================
  	
    public void setPIDVariables(String Name, String Meaning) {
    	   varName.add(Name);
    	   varMeaning.add(Meaning);
    	   this.varsSet = true;
    }
    
    //======================================
    
    public void loadPIDVariables() {
    	         pidVars.add(getMomentum());
    	         pidVars.add(getTheta());
    	         pidVars.add(getPhi());
    	         pidVars.add(getNpheLTCC());
    	         pidVars.add(getNpheHTCC());
    	         pidVars.add(getDeECIN());
    	         pidVars.add(getDeECOUT());
    	         pidVars.add(getDePCAL());
    	         pidVars.add(getSumDEcal());
    	         pidVars.add(getSumDEcal_per_MOM());
    	         pidVars.add(gettTof());
    }
    
    

	//======================================
    
    public void setFeatureVec() {
       int nVars = vNames.length;
 	   int nParticles = momentum.length;
 	   featureVec = new Vector[nParticles];
    	
 	   int callIndex,trueIndex;
 	   callIndex = trueIndex = 1;
 	   for(int i=0;i<nParticles;i++) {
 		    callIndex = trueIndex = -1;
 		    double[] out = new double[nVars];
 		    
 		    for(int j=0;j<nVars;j++) {
 		    	    callIndex = varName.indexOf(vNames[j]);
 		    	    trueIndex = refList.indexOf(varMeaning.get(callIndex));
 		    	    out[j] = ( (double[]) pidVars.get(trueIndex)   )[i];
 		    }
 		    
 		    featureVec[i] = Vectors.dense(out);
 		    out = null;
 	   }
 	   	  emptyList(pidVars);  	   
    }
    
    //======================================
    
    public void loadClassifier() {
    	   if(isCLset) {  
    		   SPS.loadClassifier(clDir, clType); 
    		   String crName = "Classifier Response for: " + clType;
    		   CR.setClassifierResponseName(crName);
    		   CR.setCL(clType, SPS.getClassifier());
    		   CR.setANDresetCounts(20);
    		   
    		   CRin.setClassifierResponseName("Classifier Response inside Cut");
    		   CRin.setCL(clType, SPS.getClassifier());
    		   CRin.setANDresetCounts(20);
    		   
    		   CRout.setClassifierResponseName("Classifier Response outside Cut");
    		   CRout.setCL(clType, SPS.getClassifier());
    		   CRout.setANDresetCounts(20);
    		   
    		   /*
    		   String crMomName;
    		   CRMom = new ClassifierResponse[10];
    		   for(int z=0;z<10;z++) {
    			   crMomName = "Momentum Range" + z;
    			   CRMom[z] = new ClassifierResponse();
    			   CRMom[z].setClassifierResponseName(crMomName);
    			   CRMom[z].setCL(clType, SPS.getClassifier());
    			   CRMom[z].setANDresetCounts(20);
    		   }
    		   */
    		   
    	   }
    }
    
    
    //***********************************************************************************
    
    //Include "simple" cut for reference analysis:
    //***********************************************************************************
    public boolean cutBasedPID(double mom, double Nltcc, double Nhtcc, double dePCAL, double deECIN, double deECOUT) {
    	  boolean out = false;
    	  
    	  boolean passLTCC = false;
    	  boolean passHTCC = false;
    	  boolean passPCAL = false;
    	  boolean passECIN = false;
    	  boolean passECOUT = false;
    	  
    	  if(mom < 2.5) {
    		  if(Nltcc >= 5) {
    			  passLTCC = true;
    		  }else passLTCC = false;
    	  }
    	  
    	  if(Nhtcc >= 20) {
    		  passHTCC = true;
    	  }else passHTCC = false;
    	  
    	  if(deECOUT >= 0.01) {
    		  passECOUT = true;
    	  }else passECOUT = false;
    	  
    	  double mECIN = (0.3-0.05)/(5.0-2.0);
    	  double bECIN = -mECIN*2.0 + 0.05;
    	  
    	  if(mECIN*mom+bECIN < deECIN) {
    		  passECIN = true;
    	  }else passECIN = false;
    	  
    	  double mPCAL = (0.6-0.1)/(5.0-1.0);
    	  double bPCAL = -mPCAL*1.0 + 0.1;
    	  
    	  if(mPCAL*mom+bPCAL < dePCAL) {
    		  passPCAL = true;
    	  }else passPCAL = false;
    	  
    	  
    	  if((passLTCC || passHTCC) && (passPCAL || passECIN || passECOUT)) {
    		  out = true;
    	  }else out = false;
    	  
    	  return out;
    }
    //***********************************************************************************
  		
  	//Getters and Setters aka the garbage collection:
  	//***********************************************************************************
  	public float[] getMyPx() {
  		return myPx;
  	}

  	//========================================

  	public void setMyPx(float[] myPx) {
  		this.myPx = myPx;
  	}
   
  	//======================================== 

  	public float[] getMyPy() {
  		return myPy;
  	}

  	//========================================

  	public void setMyPy(float[] myPy) {
  		this.myPy = myPy;
  	}

  	//========================================
  	
  	public float[] getMyPz() {
  		return myPz;
  	}

  	//========================================
  	
  	public void setMyPz(float[] myPz) {
  		this.myPz = myPz;
  	}

  	//========================================

  	public byte[] getMyCharge() {
  		return myCharge;
  	}

  	//========================================

  	public void setMyCharge(byte[] myCharge) {
  		this.myCharge = myCharge;
  	}

  	//========================================

  	public float[] getMyLTCCnphe() {
  		return myLTCCnphe;
  	}

  	//========================================

  	public void setMyLTCCnphe(float[] myLTCCnphe) {
  		this.myLTCCnphe = myLTCCnphe;
  	}

  	//========================================

  	public short[] getMyPIcherenkov() {
  		return myPIcherenkov;
  	}

  	//========================================

  	public void setMyPIcherenkov(short[] myPIcherenkov) {
  		this.myPIcherenkov = myPIcherenkov;
  	}

  	//========================================

  	public byte[] getMyDcherenkov() {
  		return myDcherenkov;
  	}

  	//========================================

  	public void setMyDcherenkov(byte[] myDcherenkov) {
  		this.myDcherenkov = myDcherenkov;
  	}

  	//========================================

  	public short[] getMyHTCCnphe() {
  		return myHTCCnphe;
  	}

  	//========================================

  	public void setMyHTCCnphe(short[] myHTCCnphe) {
  		this.myHTCCnphe = myHTCCnphe;
  	}

  	//========================================

  	public short[] getMyPIcal() {
  		return myPIcal;
  	}

  	//========================================

  	public void setMyPIcal(short[] myPIcal) {
  		this.myPIcal = myPIcal;
  	}

  	//========================================

  	public byte[] getMyDcal() {
  		return myDcal;
  	}

  	//========================================

  	public void setMyDcal(byte[] myDcal) {
  		this.myDcal = myDcal;
  	}

  	//========================================

  	public float[] getMyDEcal() {
  		return myDEcal;
  	}

  	//========================================

  	public void setMyDEcal(float[] myDEcal) {
  		this.myDEcal = myDEcal;
  	}

  	//========================================

  	public short[] getMyPIscint() {
  		return myPIscint;
  	}

  	//========================================

  	public void setMyPIscint(short[] myPIscint) {
  		this.myPIscint = myPIscint;
  	}

  	//========================================

  	public byte[] getMyDscint() {
  		return myDscint;
  	}

  	//========================================

  	public void setMyDscint(byte[] myDscint) {
  		this.myDscint = myDscint;
  	}

  	//========================================

  	public float[] getMyTscint() {
  		return myTscint;
  	}

  	//========================================

  	public void setMyTscint(float[] myTscint) {
  		this.myTscint = myTscint;
  	}

  	//========================================

 
  	public double[] getMomentum() {
  		return momentum;
  	}

  	//========================================

  	
  	public void setMomentum(double[] momentum) {
  		this.momentum = momentum;
  	}

  	//========================================
  	
  	public double[] getTheta() {
		return theta;
	}
  	
    //========================================
  	
  	public void setTheta(double[] theta) {
		this.theta = theta;
	}
  	
  //========================================
  	
  	public double[] getPhi() {
		return phi;
	}
  	
    //========================================
  	
  	public void setPhi(double[] phi) {
		this.phi = phi;
	}
  	
  //========================================

  	public double[] getNpheLTCC() {
  		return npheLTCC;
  	}

  	//========================================

  	public void setNpheLTCC(double[] npheLTCC) {
  		this.npheLTCC = npheLTCC;
  	}

  	//========================================

  	public double[] getNpheHTCC() {
  		return npheHTCC;
  	}

  	//========================================

  	public void setNpheHTCC(double[] npheHTCC) {
  		this.npheHTCC = npheHTCC;
  	}

  	//========================================
  	
  	public double[] getSumDEcal() {
  		return sumDEcal;
  	}

  	//========================================

  	public void setSumDEcal(double[] sumDEcal) {
  		this.sumDEcal = sumDEcal;
  	}

  	//========================================

  	public double[] getSumDEcal_per_MOM() {
  		return sumDEcal_per_MOM;
  	}

  	//========================================
  	
  	public void setSumDEcal_per_MOM(double[] sumDEcal_per_MOM) {
  		this.sumDEcal_per_MOM = sumDEcal_per_MOM;
  	}

  	//========================================

  	public double[] getDeECIN() {
  		return deECIN;
  	}

  	//========================================

  	public void setDeECIN(double[] deECIN) {
  		this.deECIN = deECIN;
  	}

  	//========================================

  	public double[] getDeECOUT() {
  		return deECOUT;
  	}

  	//========================================

  	public void setDeECOUT(double[] deECOUT) {
  		this.deECOUT = deECOUT;
  	}

  	//========================================

  	public double[] getDePCAL() {
  		return dePCAL;
  	}

  	//========================================

  	public void setDePCAL(double[] dePCAL) {
  		this.dePCAL = dePCAL;
  	}

  	//========================================

  	public double[] gettTof() {
  		return tTof;
  	}

  	//========================================

  	public void settTof(double[] tTof) {
  		this.tTof = tTof;
  	}

  //========================================
  	
	public byte[] getMyLcal() {
		return myLcal;
	}
	
	//========================================

	public void setMyLcal(byte[] myLcal) {
		this.myLcal = myLcal;
	}
  	
	//========================================
	
	public double getMaxMom() {
		return maxMom;
	}

	//========================================
	
	public void setMaxMom(double maxMom) {
		this.maxMom = maxMom;
	}
	
	public int[] getMyPID() {
		return myPID;
	}
	
	//========================================

	public void setMyPID(int[] myPID) {
		this.myPID = myPID;
	}
	
	//========================================
	
	public Vector[] getFeatureVec() {
		return featureVec;
	}
	
	public String[] getvNames() {
		return vNames;
	}
	
	//========================================

	public void setvNames(String[] vNames) {
		this.vNames = vNames;
		this.varsLoad = true;
	}
	//***********************************************************************************	

	public boolean isCLset() {
		return isCLset;
	}

	public void setCLset(boolean isCLset) {
		this.isCLset = isCLset;
	}

	public String getClType() {
		return clType;
	}

	public void setClType(String clType) {
		this.clType = clType;
	}

	public String getClDir() {
		return clDir;
	}

	public void setClDir(String clDir) {
		this.clDir = clDir;
	}

	public double[] getClResponse() {
		return clResponse;
	}

	public void setClResponse(double[] clResponse) {
		this.clResponse = clResponse;
	}

	public int getNThreads() {
		return NThreads;
	}

	public void setNThreads(int nThreads) {
		NThreads = nThreads;
	}

	public String getOutputJsonFile() {
		return outputJsonFile;
	}

	public void setOutputJsonFile(String outputJsonFile) {
		this.outputJsonFile = outputJsonFile;
	}

	public boolean isDoSaveList() {
		return doSaveList;
	}

	public void setDoSaveList(boolean doSaveList) {
		this.doSaveList = doSaveList;
	}

	public double getProbCut() {
		return probCut;
	}

	public void setProbCut(double probCut) {
		this.probCut = probCut;
	}

	public float[] getMyVx() {
		return myVx;
	}

	public void setMyVx(float[] myVx) {
		this.myVx = myVx;
	}

	public float[] getMyVy() {
		return myVy;
	}

	public void setMyVy(float[] myVy) {
		this.myVy = myVy;
	}

	public float[] getMyVz() {
		return myVz;
	}

	public void setMyVz(float[] myVz) {
		this.myVz = myVz;
	}

	public double[] getVertex() {
		return vertex;
	}

	public void setVertex(double[] vertex) {
		this.vertex = vertex;
	}

	public double[] getBeta() {
		return beta;
	}

	public void setBeta(double[] beta) {
		this.beta = beta;
	}

	public float[] getMyBeta() {
		return myBeta;
	}

	public void setMyBeta(float[] myBeta) {
		this.myBeta = myBeta;
	}

	

	

	
}
