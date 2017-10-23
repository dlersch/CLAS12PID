package classification;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;

public class testStuff {

	//Stuff to read out the variables:
    //++++++++++++++++++++++++++++++++++++++++++++++
	private float[] myPx = null;
	private float[] myPy = null;
	private float[] myPz = null;
	private byte[] myCharge = null;
	private int[] myPID = null;
	private float[] myMomentum = null;
	private short[] myHTCC = null;
	private float[] myLTCC = null;
	private float[] myBeta = null;
	private float[] myDESumCal = null;
	private int foundPos = 0;
		
	private float[] myLTCCnphe = null;
	private int countAll = 0;
	private int countIn = 0;
	private int countOut = 0;
	private int countAll4 = 0;
	private int countAll3 = 0;
	private int countAll2 = 0;
	private int countOut2 = 0;
	private int countOut3 = 0;
	private int countOut4 = 0;
	
	
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
	//++++++++++++++++++++++++++++++++++++++++++++++
	private double momentum = 0.0;
	private int pidEventBuilder = 0;
	private float npheLTCC = 0;
	private short npheHTCC = 0;
	private double sumDEcal = 0;
	private double sumDEcal_per_MOM = 0;
	private double deECIN = 0;
	private double deECOUT = 0;
	private double dePCAL = 0;
	private double tTof = 0;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff for defining histograms:
	//++++++++++++++++++++++++++++++++++++++++++++++
	private H2F[] npheLTCC_vs_Mom = null;
	private H2F[] npheHTCC_vs_Mom = null;
	private H2F[] deECIN_vs_Mom = null;
	private H2F[] deECOUT_vs_Mom = null;
	private H2F[] dePCAL_vs_Mom = null;
	private H2F[] TOF_vs_Mom = null;
	private H2F[] Cal_vs_Mom = null;
	private H2F[] npheHTCC_vs_npheLTCC = null;
	private H2F[] momPlus_vs_momMinus = null;
	
	private H1F[] MomHist = null;
	private H1F[] npheLTCCHist = null;
	private H1F[] npheHTCCHist = null;
	private H1F[] deECINHist = null;
	private H1F[] deECOUTHist = null;
	private H1F[] dePCALHist = null;
	private H1F[] timeHist = null;
	
	private H2F[] npheLTCC_vs_MomCut = null;
	private H2F[] npheHTCC_vs_MomCut = null;
	private H2F[] deECIN_vs_MomCut = null;
	private H2F[] deECOUT_vs_MomCut = null;
	private H2F[] dePCAL_vs_MomCut = null;
	private H2F[] TOF_vs_MomCut = null;
	
	private H1F[] MomHistCut = null;
	private H1F[] npheLTCCHistCut = null;
	private H1F[] npheHTCCHistCut = null;
	private H1F[] deECINHistCut = null;
	private H1F[] deECOUTHistCut = null;
	private H1F[] dePCALHistCut = null;
	private H1F[] timeHistCut = null;
	
	private boolean areHistsSet = false;
	private boolean showAcceptance = false;
	private double maxMom = 0.0;
	private double allEvents = 0.0;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff for canvases:
	//++++++++++++++++++++++++++++++++++++++++++++++
	private TCanvas[] monCanvas2D = null;
	private TCanvas[] calCanvas = null;
	private TCanvas[] monCanvas1D = null;
	//++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff for saving data:
	//++++++++++++++++++++++++++++++++++++++++++++++
    private boolean isSparkSet = false;
	private SparkSession spark = null;
	private List<Row> myList = new ArrayList<>();
	//++++++++++++++++++++++++++++++++++++++++++++++
	
		
    public testStuff() {
		
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
  		momentum = npheLTCC = npheHTCC = 0;
  		sumDEcal = sumDEcal_per_MOM = 0;
  		deECIN = deECOUT = dePCAL = tTof = 0;
  		
  		
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
  	
        myMomentum = new float[nRowsRP];
        myHTCC = new short[nRowsRP];
        myLTCC = new float[nRowsRP];
        myDESumCal = new float[nRowsRP];
        
        double minDiff = 1.0;
        foundPos = -2;
        
  		//-------------------------------------------------------------------------------
  		for(int i=0;i<nRowsRP;i++) {
  			//Get the momentum:
  		    momentum = Math.sqrt(myPx[i]*myPx[i] + myPy[i]*myPy[i] + myPz[i]*myPz[i]);
  		    pidEventBuilder = myPID[i];
  		    
  		    //-------------------------------------------------------------------------------
  		   // if(momentum > 0 && (pidEventBuilder == 11 || pidEventBuilder == -11)) {
  			//if(momentum > 0 && myCharge[i] == charge) {
  		   	if(momentum > 0 && myCharge[i] != 0 && nRowsRP >= 3) {
  		    	    this.allEvents++;
  		    	
  		    	   
  		    	    
  		    	    if(myCharge[i] < 0) {
  		    	      myMomentum[i] = (float) momentum;
  		    	    }else if(myCharge[i] > 0) {
  		    	  //  	 System.out.println("BETA: " + myBeta[i]);
  		    	    	
  		    	    	   if(Math.abs(1.0-myBeta[i]) < minDiff) {
  		    	    		   minDiff = Math.abs(1.0-myBeta[i]);
  		    	    		   myMomentum[i] = (float) momentum;
  		    	    		   foundPos = i;
  		    	    	   }else myMomentum[i] = -1;
  		    	    }
  		    	    
  		    	    //Get nphe from ltcc:
  		    	    npheLTCC = 0;
  		    	    if(nRowsLT > 0) {
  		    	    	   npheLTCC = myLTCCnphe[0];
  		    	    }
  		    	    myLTCC[i] = npheLTCC;
  		    	    
  		    	    //Get nphe from HTCC:
  		    	    npheHTCC = 0;
  		    	    for(int iHT=0;iHT<nRowsRCh;iHT++) {
  		    	    	   if(myPIcherenkov[iHT] == i && myDcherenkov[iHT] == 6) {
  		    	    		   npheHTCC = myHTCCnphe[iHT];
  		    	    		   myHTCC[i] = npheHTCC;
  		    	    	   }
  		    	    }
  		    	    
  		    	    //Get the total energy from the calorimeter:
  		    	    sumDEcal = sumDEcal_per_MOM = 0;
  		    	    for(int iC=0;iC<nRowsRCa;iC++) {
  		    	    	   if(myPIcal[iC] == i) {
  		    	    		  sumDEcal += myDEcal[iC];
  		    	    		  //-------------------------------------------- 
  		    	    		 if(myLcal[iC] < 4) {
    		    	    		   dePCAL += myDEcal[iC];
    		    	    	      }else if(myLcal[iC] >= 4 && myLcal[iC] < 7) {
    		    	    		   deECIN += myDEcal[iC];
    		    	    	      }else deECOUT += myDEcal[iC];
  		    	    		 //--------------------------------------------  
  		    	    	   }
  		    	    }
  		    	    
  		    	    sumDEcal_per_MOM = sumDEcal/momentum;
  		    	    myDESumCal[i] = (float) sumDEcal;
  		    	    
  		    	    tTof = 0;
  		    	    for(int iSC=0;iSC<nRowsRS;iSC++) {
  		    	    	    if(myPIscint[iSC] == i && myDscint[iSC] == 17) {
  		    	    	    	   tTof += myTscint[iSC];
  		    	    	    }
  		    	    }
  		    	    if(nRowsRS > 0)tTof /= nRowsRS;
  		    	    
  		    	    if(areHistsSet) {
  		    	    	   //2D histograms:
  		    	    	   //=========================================================
  		    	    	
  		    	
  		    	    	
  		    	   
  		    	    		
  		    	    	
  		    	    		
  		    	    		
  		    	    	//	System.out.println("Found PID: " + pidEventBuilder);
  		    	    	
  		    	   
  		    	    	  /*
  		    	       npheLTCC_vs_Mom[2-particleType].fill(momentum,npheLTCC);
		    	    	   npheHTCC_vs_Mom[2-particleType].fill(momentum,npheHTCC);
		    	    	   deECIN_vs_Mom[2-particleType].fill(momentum, deECIN);
		    	    	   deECOUT_vs_Mom[2-particleType].fill(momentum, deECOUT);
		    	    	   dePCAL_vs_Mom[2-particleType].fill(momentum, dePCAL);
		    	    	   TOF_vs_Mom[2-particleType].fill(momentum, tTof);
		    	    	   */
		    	       //=========================================================	   
		    	    	   
		    	    	   //1D histograms:
  		    	    	   //=========================================================
  		    	    	   npheLTCCHist[0].fill(npheLTCC);
  		    	    	   npheHTCCHist[0].fill(npheHTCC);
  		    	    	   deECINHist[0].fill(deECIN);
  		    	    	   deECOUTHist[0].fill(deECOUT);
  		    	    	   dePCALHist[0].fill(dePCAL);
  		    	    	   timeHist[0].fill(sumDEcal);
  		    	    	   MomHist[0].fill(momentum);
  		    	       
  		    	    	   npheLTCCHist[2-particleType].fill(npheLTCC);
		    	    	   npheHTCCHist[2-particleType].fill(npheHTCC);
		    	    	   deECINHist[2-particleType].fill(deECIN);
		    	    	   deECOUTHist[2-particleType].fill(deECOUT);
		    	    	   dePCALHist[2-particleType].fill(dePCAL);
		    	    	   timeHist[2-particleType].fill(sumDEcal);
		    	    	   MomHist[2-particleType].fill(momentum);
		    	       //=========================================================	
  		    	    }
  		    	    
  		    	    if(isSparkSet) {
  		    	    	   myList.add(RowFactory.create(particleType,momentum,npheLTCC,npheHTCC,deECIN,deECOUT,dePCAL,sumDEcal,sumDEcal_per_MOM,tTof));
  		    	    }
  		    	    
  		    	    
  		    }
  		    //-------------------------------------------------------------------------------
  		}
  	   //-------------------------------------------------------------------------------	
  		
  	double cut = 0.5;
  	  for(int k=0;k<nRowsRP;k++) {
  		  for(int l=0;l<nRowsRP;l++) {
  			  if(l > k) {
  				  if(myCharge[k] * myCharge[l] == -1 && myMomentum[k] > 0  && myMomentum[l] > 0  && foundPos !=-2) {
  					  if(myCharge[k] > 0 && foundPos == k) {
  						  countAll++;
  						  
  						//  System.out.println("index: " + foundPos + " and PID: " + myPID[k]);
  						  
  						//
  						//  System.out.println("BETA FOUND: " + myBeta[k]);
  						  
  						  
  						if(myMomentum[k] <= 4.0 && myMomentum[l] <= 4.0) {
  						  countAll4++;
  						}
  						
  						if(myMomentum[k] <= 3.0 && myMomentum[l] <= 3.0) {
    						  countAll3++;
    						}
  						
  						if(myMomentum[k] <= 2.0 && myMomentum[l] <= 2.0) {
    						  countAll2++;
    						}
  						  
  						  
     		    	    	      npheLTCC_vs_Mom[0].fill(myMomentum[l],myLTCC[l]);
     		    	    	      npheHTCC_vs_Mom[0].fill(myMomentum[l],myHTCC[l]);
     		    	    	      npheHTCC_vs_npheLTCC[0].fill(myLTCC[l],myHTCC[l]);
     		    	    	      Cal_vs_Mom[0].fill(myMomentum[l], myDESumCal[l]);
  						  momPlus_vs_momMinus[0].fill(myMomentum[l], myMomentum[k]);
  						  if(myMomentum[k] <= cut && myMomentum[l] <= cut) { 
  						    countIn++;
  							momPlus_vs_momMinus[1].fill(myMomentum[l], myMomentum[k]);
  							
  						    npheLTCC_vs_Mom[1].fill(myMomentum[l],myLTCC[l]);
		    	    	            npheHTCC_vs_Mom[1].fill(myMomentum[l],myHTCC[l]);
		    	    	            npheHTCC_vs_npheLTCC[1].fill(myLTCC[l],myHTCC[l]);
		    	    	            Cal_vs_Mom[1].fill(myMomentum[l], myDESumCal[l]);
  						  }else {
  							countOut++;
  							momPlus_vs_momMinus[2].fill(myMomentum[l], myMomentum[k]);
  							
  						    npheLTCC_vs_Mom[2].fill(myMomentum[l],myLTCC[l]);
	    	    	                npheHTCC_vs_Mom[2].fill(myMomentum[l],myHTCC[l]);
	    	    	                npheHTCC_vs_npheLTCC[2].fill(myLTCC[l],myHTCC[l]);
	    	    	                Cal_vs_Mom[2].fill(myMomentum[l], myDESumCal[l]);
	    	    	                
	    	    	                
	    	    	                if(myMomentum[k] <= 4.0 && myMomentum[l] <= 4.0) {
	    	    						  countOut4++;
	    	    						}
	    	    						
	    	    						if(myMomentum[k] <= 3.0 && myMomentum[l] <= 3.0) {
	    	      						  countOut3++;
	    	      						}
	    	    						
	    	    						if(myMomentum[k] <= 2.0 && myMomentum[l] <= 2.0) {
	    	      						  countOut2++;
	    	      						}
	    	    	                
	    	    	                
	    	    	                
  						  }
  							  
  					  }
  					  
  					  
  					  if(myCharge[l] > 0 && foundPos == l) {
  						 countAll++;
  						 
  						 momPlus_vs_momMinus[0].fill(myMomentum[k], myMomentum[l]);
  						 
  						 npheLTCC_vs_Mom[0].fill(myMomentum[k],myLTCC[k]);
		    	    	         npheHTCC_vs_Mom[0].fill(myMomentum[k],myHTCC[k]);
		    	    	         npheHTCC_vs_npheLTCC[0].fill(myLTCC[k],myHTCC[k]);
		    	    	         Cal_vs_Mom[0].fill(myMomentum[k], myDESumCal[k]);
		    	    	         
		    	    	         
		    	    	         if(myMomentum[k] <= 4.0 && myMomentum[l] <= 4.0) {
		     						  countAll4++;
		     						}
		     						
		     						if(myMomentum[k] <= 3.0 && myMomentum[l] <= 3.0) {
		       						  countAll3++;
		       						}
		     						
		     						if(myMomentum[k] <= 2.0 && myMomentum[l] <= 2.0) {
		       						  countAll2++;
		       						}
  						 
  						 
 						  if(myMomentum[k] <= cut && myMomentum[l] <= cut) { 
 						    countIn++;
 							momPlus_vs_momMinus[1].fill(myMomentum[k], myMomentum[l]);
 							
 							npheLTCC_vs_Mom[1].fill(myMomentum[k],myLTCC[k]);
	    	    	                npheHTCC_vs_Mom[1].fill(myMomentum[k],myHTCC[k]);
	    	    	                npheHTCC_vs_npheLTCC[1].fill(myLTCC[k],myHTCC[k]);
	    	    	                Cal_vs_Mom[1].fill(myMomentum[k], myDESumCal[k]);
 						  }else {
 							countOut++;
 							momPlus_vs_momMinus[2].fill(myMomentum[k], myMomentum[l]);
 							
 							npheLTCC_vs_Mom[2].fill(myMomentum[k],myLTCC[k]);
	    	                    npheHTCC_vs_Mom[2].fill(myMomentum[k],myHTCC[k]);
	    	                    npheHTCC_vs_npheLTCC[2].fill(myLTCC[k],myHTCC[k]);
	    	                    Cal_vs_Mom[2].fill(myMomentum[k], myDESumCal[k]);
	    	                    
	    	                    
	    	                    if(myMomentum[k] <= 4.0 && myMomentum[l] <= 4.0) {
	    	  						  countOut4++;
	    	  						}
	    	  						
	    	  						if(myMomentum[k] <= 3.0 && myMomentum[l] <= 3.0) {
	    	    						  countOut3++;
	    	    						}
	    	  						
	    	  						if(myMomentum[k] <= 2.0 && myMomentum[l] <= 2.0) {
	    	    						  countOut2++;
	    	    						}
	    	                    
 						  }
  						 
  						 
  					  }
  					  
  					  
  				  }
  			  }
  		  }
  	  }
  		
  		
  		
  		
  		
  	}
  	//***********************************************************************************
  	
  	
  	//Define histograms:
    //***********************************************************************************
  	public void setupHitsograms(boolean showAcceptance, double maxMom) {
  		this.areHistsSet = false;
  		
  		this.showAcceptance = showAcceptance;
  		this.maxMom = maxMom;
  		
  		String momName = "Momentum [GeV/c]";
  		String ltccName = "NPHE (LTCC)";
  		String htccName = "NPHE (HTCC)";
  		String deecinName = "dE (EC:IN) [GeV]";
  		String deecoutName = "dE (EC:OUT) [GeV]";
  		String depcalName = "dE (PCAL) [GeV]";
  		String tofName = "Time (TOF) [ns]";
  		String calName = "dE (PCAL) + dE (EC:IN) + dE (EC:OUT) [GeV]";
  		String countName;
  		if(showAcceptance) {
  			countName = "Acceptance";
  		}else countName = "Entries";
  		
  		String[] addName = new String[3];
  		addName[0] = "_all";
  		addName[1] = "_IN";
  		addName[2] = "_OUT";
  		
  		String[] baseName2D = new String[8];
  		baseName2D[0] = "npheLTCC_vs_Mom";
  		baseName2D[1] = "npheHTCC_vs_Mom";
  		baseName2D[2] = "deECIN_vs_Mom";
  		baseName2D[3] = "deECOUT_vs_Mom";
  		baseName2D[4] = "dePCAL_vs_Mom";
  		baseName2D[5] = "TOF_vs_Mom";
  		baseName2D[6] = "Cal_vs_Mom";
  		baseName2D[7] = "npheHTCC_vs_npheLTCC";
  		
  		int nHists = 3;
  		npheLTCC_vs_Mom = new H2F[nHists];
  		npheHTCC_vs_Mom = new H2F[nHists];
  		deECIN_vs_Mom = new H2F[nHists];
  		deECOUT_vs_Mom = new H2F[nHists];
  		dePCAL_vs_Mom = new H2F[nHists];
  		TOF_vs_Mom = new H2F[nHists];
  		Cal_vs_Mom = new H2F[nHists];
  		npheHTCC_vs_npheLTCC = new H2F[nHists];
  		
  		
  		String[] baseName1D = new String[7];
  		baseName1D[0] = "MomHist";
  		baseName1D[1] = "npheLTCCHist";
  		baseName1D[2] = "npheHTCCHist";
  		baseName1D[3] = "deECINHist";
  		baseName1D[4] = "deECOUTHist";
  		baseName1D[5] = "dePCALHist";
  		baseName1D[6] = "timeHist";
  		
  		MomHist = new H1F[nHists];
  		npheLTCCHist = new H1F[nHists];
  		npheHTCCHist = new H1F[nHists];
  		deECINHist = new H1F[nHists];
  		deECOUTHist = new H1F[nHists];
  		dePCALHist = new H1F[nHists];
  		timeHist= new H1F[nHists];
  		
  		String histName;
  		for(int h=0;h<3;h++) {
  			 //2D hists:
  			 //---------------------------------------------------------------
  			 histName = baseName2D[0] + addName[h];
  			 npheLTCC_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,100);
  			 npheLTCC_vs_Mom[h].setTitleX(momName);
  			 npheLTCC_vs_Mom[h].setTitleY(ltccName);
  			 
  			 histName = baseName2D[1] + addName[h];
 			 npheHTCC_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,100);
 			 npheHTCC_vs_Mom[h].setTitleX(momName);
 			 npheHTCC_vs_Mom[h].setTitleY(htccName);
  			 
 			 histName = baseName2D[2] + addName[h];
			 deECIN_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,1);
			 deECIN_vs_Mom[h].setTitleX(momName);
			 deECIN_vs_Mom[h].setTitleY(deecinName);
			 
			 histName = baseName2D[3] + addName[h];
			 deECOUT_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,1);
			 deECOUT_vs_Mom[h].setTitleX(momName);
			 deECOUT_vs_Mom[h].setTitleY(deecoutName);
  			 
			 histName = baseName2D[4] + addName[h];
			 dePCAL_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,1);
			 dePCAL_vs_Mom[h].setTitleX(momName);
			 dePCAL_vs_Mom[h].setTitleY(depcalName);
			 
			 histName = baseName2D[5] + addName[h];
			 TOF_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,142,150);
			 TOF_vs_Mom[h].setTitleX(momName);
			 TOF_vs_Mom[h].setTitleY(tofName);
			 
			 histName = baseName2D[6] + addName[h];
			 Cal_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0.0,1.5);
			 Cal_vs_Mom[h].setTitleX(momName);
			 Cal_vs_Mom[h].setTitleY(calName);
			 
			 histName = baseName2D[7] + addName[h];
  			 npheHTCC_vs_npheLTCC[h] = new H2F(histName,100,0,100,100,0,100);
  			 npheHTCC_vs_npheLTCC[h].setTitleX(ltccName);
  			 npheHTCC_vs_npheLTCC[h].setTitleY(htccName);
			 //---------------------------------------------------------------
			 
			 //1D hists:
			 //---------------------------------------------------------------
			 histName = baseName1D[0] + addName[h];
			 MomHist[h] = new H1F(histName,100,0,maxMom);
			 MomHist[h].setTitleX(momName);
			 MomHist[h].setTitleY(countName);
			 
			 histName = baseName1D[1] + addName[h];
			 npheLTCCHist[h] = new H1F(histName,100,0,100);
			 npheLTCCHist[h].setTitleX(ltccName);
			 npheLTCCHist[h].setTitleY(countName);
			 
			 histName = baseName1D[2] + addName[h];
			 npheHTCCHist[h] = new H1F(histName,100,0,100);
			 npheHTCCHist[h].setTitleX(htccName);
			 npheHTCCHist[h].setTitleY(countName);
			 
			 histName = baseName1D[3] + addName[h];
			 deECINHist[h] = new H1F(histName,100,0,1.0);
			 deECINHist[h] .setTitleX(deecinName);
			 deECINHist[h] .setTitleY(countName);
			 
			 histName = baseName1D[4] + addName[h];
			 deECOUTHist[h] = new H1F(histName,100,0,1.0);
			 deECOUTHist[h].setTitleX(deecoutName);
			 deECOUTHist[h].setTitleY(countName);
			 
			 histName = baseName1D[5] + addName[h];
			 dePCALHist[h] = new H1F(histName,100,0,1.0);
			 dePCALHist[h].setTitleX(depcalName);
			 dePCALHist[h].setTitleY(countName);
			 
			 histName = baseName1D[6] + addName[h];
			 timeHist[h] = new H1F(histName,100,142,150);
			 timeHist[h].setTitleX(tofName);
			 timeHist[h].setTitleY(countName);
			  
			 //--------------------------------------------------------------- 
  		}
  		
  		
  		momPlus_vs_momMinus = new H2F[3];
  		momPlus_vs_momMinus[0] = new H2F("momPlus_vs_momMinus",100,0,5.5,100,0,5.5);
  		momPlus_vs_momMinus[0].setTitleX("Momenum (e-) [GeV/c]");
  		momPlus_vs_momMinus[0].setTitleY("Momenum (e+) [GeV/c]");
  		
  		momPlus_vs_momMinus[1] = new H2F("momPlus_vs_momMinusCutIN",100,0,5.5,100,0,5.5);
  		momPlus_vs_momMinus[1].setTitleX("Momenum (e-) [GeV/c]");
  		momPlus_vs_momMinus[1].setTitleY("Momenum (e+) [GeV/c]");
  		
  		momPlus_vs_momMinus[2] = new H2F("momPlus_vs_momMinusCutOUT",100,0,5.5,100,0,5.5);
  		momPlus_vs_momMinus[2].setTitleX("Momenum (e-) [GeV/c]");
  		momPlus_vs_momMinus[2].setTitleY("Momenum (e+) [GeV/c]");
  		
  		this.areHistsSet = true;
  	}
    //***********************************************************************************
  	
  	//Set canvases:
  	//***********************************************************************************
  	public void setupAndDrawCanvas() {
  		//2 dimensional canvas:
  		//==============================================================
  		String[] canvasName2D = new String[3];
  		canvasName2D[0] = "Correlation Plots: All";
  		canvasName2D[1] = "Correlation Plots: Inside Cut";
  		canvasName2D[2] = "Correlation Plots: OutsideCut";
  		monCanvas2D = new TCanvas[3];
  		
  		for(int k=0;k<3;k++) {
  			monCanvas2D[k] = new TCanvas(canvasName2D[k],1200,500);
  			monCanvas2D[k].divide(3,1);
  			monCanvas2D[k].cd(0);
  			monCanvas2D[k].draw(npheLTCC_vs_Mom[k]);
  			monCanvas2D[k].cd(1);
  			monCanvas2D[k].draw(npheHTCC_vs_Mom[k]);
  			monCanvas2D[k].cd(2);
  			monCanvas2D[k].draw(npheHTCC_vs_npheLTCC[k]);
  		}
  		
  		String[] calName2D = new String[3];
  		calName2D[0] = "All";
  		calName2D[1] = "Inside Cut";
  		calName2D[2] = "OutsideCut";
  		calCanvas = new TCanvas[3];
  		for(int k=0;k<3;k++) {
  			calCanvas[k] = new TCanvas(calName2D[k],500,500);
  			calCanvas[k].draw(Cal_vs_Mom[k]);
  		}
  		
  		
  		
  		
  	    //==============================================================
  		/*
  	    //1 dimensional canvas:
  		//==============================================================
  		 for(int h=0;h<3;h++) {
  	  		 //include normalisation, if wanted:
  				 if(showAcceptance) {
  					MomHist[h].divide(allEvents);
  					npheLTCCHist[h].divide(allEvents);
  					npheHTCCHist[h].divide(allEvents);
  					deECINHist[h].divide(allEvents);
  					deECOUTHist[h].divide(allEvents);
  					dePCALHist[h].divide(allEvents);
  					timeHist[h].divide(allEvents);
  				 }
  	  	  }
  		
  		
  		String[] canvasName1D = new String[3];
  		canvasName1D[0] = "1D Plots: All Particles";
  		canvasName1D[1] = "1D Plots: Signal Particles";
  		canvasName1D[2] = "1D Plots: Background Particles";
  		monCanvas1D = new TCanvas[3];
  		
  		for(int k=0;k<3;k++) {
  			monCanvas1D[k] = new TCanvas(canvasName1D[k],1600,800);
  			monCanvas1D[k].divide(3,3);
  			monCanvas1D[k].cd(0);
  			monCanvas1D[k].draw(npheLTCCHist[k]);
  			monCanvas1D[k].cd(1);
  			monCanvas1D[k].draw(npheHTCCHist[k]);
  			monCanvas1D[k].cd(2);
  			monCanvas1D[k].draw(deECINHist[k]);
  			monCanvas1D[k].cd(3);
  			monCanvas1D[k].draw(deECOUTHist[k]);
  			monCanvas1D[k].cd(4);
  			monCanvas1D[k].draw(dePCALHist[k]);
  			monCanvas1D[k].cd(5);
  			monCanvas1D[k].draw(timeHist[k]);
  			monCanvas1D[k].cd(6);
  			monCanvas1D[k].draw(MomHist[k]);
  		}
  		*/
  	    //==============================================================
  		
  		TCanvas ctest = new TCanvas("ctest",1200,500);
  		ctest.divide(3, 1);
  		ctest.cd(0);
  		ctest.draw(momPlus_vs_momMinus[0]);
  		ctest.cd(1);
  		ctest.draw(momPlus_vs_momMinus[1]);
  		ctest.cd(2);
  		ctest.draw(momPlus_vs_momMinus[2]);
  		
  		double percIn1 = (double)countIn / (double)countAll * 100;
  		double percOut1 = (double)countOut / (double)countAll * 100;
  		System.out.println("Inside the cut: " + percIn1 + "  outside the cut: " + percOut1);
  		System.out.println("    ");
  		
  		double percIn2 = (double)countIn / (double)countAll4 * 100;
  		double percOut2 = (double)countOut4 / (double)countAll4 * 100;
  		System.out.println("Inside the cut: " + percIn2 + "  outside the cut: " + percOut2);
  		System.out.println("    ");
  		
  		double percIn3 = (double)countIn / (double)countAll3 * 100;
  		double percOut3 = (double)countOut3 / (double)countAll3 * 100;
  		System.out.println("Inside the cut: " + percIn3 + "  outside the cut: " + percOut3);
  		System.out.println("    ");
  		
  		double percIn4 = (double)countIn / (double)countAll2 * 100;
  		double percOut4 = (double)countOut2 / (double)countAll2 * 100;
  		System.out.println("Inside the cut: " + percIn4 + "  outside the cut: " + percOut4);
  		System.out.println("    ");
  		
  		
  		
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
  			spark = SparkSession
  					  .builder()
  					  .appName("ReadInVariables")
  					  .config( "spark.driver.host", "localhost" )
  					  .config("spark.master", "local[4]")
  					  .getOrCreate();
  			spark.sparkContext().setLogLevel("ERROR");

  	  }
  	}
  	
  	//===========================================
  	
  	public void saveList(String outputFile) {
  		 StructType schema = new StructType(new StructField[]{
			      new StructField("label", DataTypes.IntegerType, false, Metadata.empty()),
			      new StructField("variable1", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable2", DataTypes.FloatType, false, Metadata.empty()),
			      new StructField("variable3", DataTypes.ShortType, false, Metadata.empty()),
                  new StructField("variable4", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable5", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable6", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable7", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable8", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable9", DataTypes.DoubleType, false, Metadata.empty())
			    });
		 
		  Dataset<Row> trainingset = spark.createDataFrame(myList, schema);
		  trainingset.write().format("json").save(outputFile);
  	}
  	//******************************************************************
  	
  	
  	//Getters and Setters:
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

 
  	public double getMomentum() {
  		return momentum;
  	}

  	//========================================

  	public void setMomentum(double momentum) {
  		this.momentum = momentum;
  	}

  	//========================================

  	public float getNpheLTCC() {
  		return npheLTCC;
  	}

  	//========================================

  	public void setNpheLTCC(float npheLTCC) {
  		this.npheLTCC = npheLTCC;
  	}

  	//========================================

  	public short getNpheHTCC() {
  		return npheHTCC;
  	}

  	//========================================

  	public void setNpheHTCC(short npheHTCC) {
  		this.npheHTCC = npheHTCC;
  	}

  	//========================================
  	
  	public double getSumDEcal() {
  		return sumDEcal;
  	}

  	//========================================

  	public void setSumDEcal(float sumDEcal) {
  		this.sumDEcal = sumDEcal;
  	}

  	//========================================

  	public double getSumDEcal_per_MOM() {
  		return sumDEcal_per_MOM;
  	}

  	//========================================
  	
  	public void setSumDEcal_per_MOM(float sumDEcal_per_MOM) {
  		this.sumDEcal_per_MOM = sumDEcal_per_MOM;
  	}

  	//========================================

  	public double getDeECIN() {
  		return deECIN;
  	}

  	//========================================

  	public void setDeECIN(float deECIN) {
  		this.deECIN = deECIN;
  	}

  	//========================================

  	public double getDeECOUT() {
  		return deECOUT;
  	}

  	//========================================

  	public void setDeECOUT(float deECOUT) {
  		this.deECOUT = deECOUT;
  	}

  	//========================================

  	public double getDePCAL() {
  		return dePCAL;
  	}

  	//========================================

  	public void setDePCAL(float dePCAL) {
  		this.dePCAL = dePCAL;
  	}

  	//========================================

  	public double gettTof() {
  		return tTof;
  	}

  	//========================================

  	public void settTof(float tTof) {
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
	//***********************************************************************************	

	public int[] getMyPID() {
		return myPID;
	}

	public void setMyPID(int[] myPID) {
		this.myPID = myPID;
	}

	public float[] getMyMomentum() {
		return myMomentum;
	}

	public void setMyMomentum(float[] myMomentum) {
		this.myMomentum = myMomentum;
	}

	public short[] getMyHTCC() {
		return myHTCC;
	}

	public void setMyHTCC(short[] myHTCC) {
		this.myHTCC = myHTCC;
	}

	public float[] getMyBeta() {
		return myBeta;
	}

	public void setMyBeta(float myBeta[]) {
		this.myBeta = myBeta;
	}

	
	public float[] getMyLTCC() {
		return myLTCC;
	}

	public void setMyLTCC(float[] myLTCC) {
		this.myLTCC = myLTCC;
	}
	
	
}
