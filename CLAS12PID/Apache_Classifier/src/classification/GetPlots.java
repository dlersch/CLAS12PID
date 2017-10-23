package classification;

//Program to define histograms
//Last date worked on: 10/16/17
//Written by: Daniel Lersch d.lersch@fz-juelich.de

import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.TCanvas;

public class GetPlots {
	
	//Stuff for defining histograms:
	//++++++++++++++++++++++++++++++++++++++++++++++
    private H2F[] geoPlots = null;
	private H2F[] npheLTCC_vs_Mom = null;
	private H2F[] npheHTCC_vs_Mom = null;
	private H2F[] deECIN_vs_Mom = null;
	private H2F[] deECOUT_vs_Mom = null;
	private H2F[] dePCAL_vs_Mom = null;
	private H2F[] TOF_vs_Mom = null;
		
	private H1F[] MomHist = null;
	private H1F[] npheLTCCHist = null;
	private H1F[] npheHTCCHist = null;
	private H1F[] deECINHist = null;
	private H1F[] deECOUTHist = null;
	private H1F[] dePCALHist = null;
	private H1F[] timeHist = null;
	private H1F[] vertexHist = null;
	
	private H1F[] probDist = null;
	private H2F[] betaVsMom = null;
	private H2F[] betaVsTof = null;
	private H2F[] diffMom_vs_Prob = null;
	private H2F[] diffBeta_vs_Prob = null;
		
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
	private H1F[] vertexHistCut = null;
	private H1F[] sumDEcalHist = null;
	private H1F[] phiHists = null;
	//++++++++++++++++++++++++++++++++++++++++++++++
		
	//Stuff for canvases:
	//++++++++++++++++++++++++++++++++++++++++++++++
	private TCanvas[] monCanvas2D = null;
	private TCanvas[] monCanvas1D = null;
	private TCanvas[] monCanvas2DCut = null;
	private TCanvas probCanvas = null;
	
	private TCanvas betaCanvas = null;
	private TCanvas diffCanvas = null;
	
	private TCanvas vertexCanvas = null;
	private TCanvas sumDECanvas = null;
	
	private TCanvas geoCanvas = null;
	
	//++++++++++++++++++++++++++++++++++++++++++++++

	
	GetPlots(){
	
	}
	
	
	//First, set and define histograms:
	//***********************************************************************
	public void setHitsograms(boolean showAcceptance, double maxMom) {
		String geoNameX = "Theta x sin{Phi}";
		String geoNameY = "Theta x cos{Phi}";
  		String momName = "Momentum [GeV/c]";
  		String ltccName = "NPHE (LTCC)";
  		String htccName = "NPHE (HTCC)";
  		String deecinName = "dE (EC:IN) [GeV]";
  		String deecoutName = "dE (EC:OUT) [GeV]";
  		String depcalName = "dE (PCAL) [GeV]";
  		String tofName = "Time (ToF) [ns]";
  		String vertexName = "Vertex [mm]";
  		String calName = "dE(cal)/p";
  		String countName;
  		if(showAcceptance) {
  			countName = "Acceptance";
  		}else countName = "Entries";
  		
  		String[] addName = new String[3];
  		addName[0] = "_all";
  		addName[1] = "_signal";
  		addName[2] = "_background";
  		
  		String[] baseName2D = new String[7];
  		baseName2D[0] = "npheLTCC_vs_Mom";
  		baseName2D[1] = "npheHTCC_vs_Mom";
  		baseName2D[2] = "deECIN_vs_Mom";
  		baseName2D[3] = "deECOUT_vs_Mom";
  		baseName2D[4] = "dePCAL_vs_Mom";
  		baseName2D[5] = "TOF_vs_Mom";
  		baseName2D[6] = "geoPlots";
  		
  		int nHists = 3;
  		npheLTCC_vs_Mom = new H2F[nHists];
  		npheHTCC_vs_Mom = new H2F[nHists];
  		deECIN_vs_Mom = new H2F[nHists];
  		deECOUT_vs_Mom = new H2F[nHists];
  		dePCAL_vs_Mom = new H2F[nHists];
  		TOF_vs_Mom = new H2F[nHists];
  		geoPlots = new H2F[nHists];
  		
  		npheLTCC_vs_MomCut = new H2F[nHists];
  		npheHTCC_vs_MomCut = new H2F[nHists];
  		deECIN_vs_MomCut = new H2F[nHists];
  		deECOUT_vs_MomCut = new H2F[nHists];
  		dePCAL_vs_MomCut = new H2F[nHists];
  		TOF_vs_MomCut = new H2F[nHists];
  		
  		String[] baseName1D = new String[10];
  		baseName1D[0] = "MomHist";
  		baseName1D[1] = "npheLTCCHist";
  		baseName1D[2] = "npheHTCCHist";
  		baseName1D[3] = "deECINHist";
  		baseName1D[4] = "deECOUTHist";
  		baseName1D[5] = "dePCALHist";
  		baseName1D[6] = "timeHist";
  		baseName1D[7] = "vertexHist";
  		baseName1D[8] = "sumDEcalHist";
  		baseName1D[9] = "phiHist";
  		
  		MomHist = new H1F[nHists];
  		npheLTCCHist = new H1F[nHists];
  		npheHTCCHist = new H1F[nHists];
  		deECINHist = new H1F[nHists];
  		deECOUTHist = new H1F[nHists];
  		dePCALHist = new H1F[nHists];
  		timeHist= new H1F[nHists];
  		vertexHist = new H1F[nHists];
  		sumDEcalHist = new H1F[nHists];
  		
  		MomHistCut = new H1F[nHists];
  		npheLTCCHistCut = new H1F[nHists];
  		npheHTCCHistCut = new H1F[nHists];
  		deECINHistCut = new H1F[nHists];
  		deECOUTHistCut = new H1F[nHists];
  		dePCALHistCut = new H1F[nHists];
  		timeHistCut= new H1F[nHists];
  		vertexHistCut= new H1F[nHists];
  		phiHists = new H1F[nHists];
  		
  		String histName;
  		String histNameCut;
  		int lineWidth = 3;
  		
  		for(int h=0;h<3;h++) {
  			 //2D hists:
  			 //---------------------------------------------------------------
  			 histName = baseName2D[0] + addName[h];
  			 npheLTCC_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,100);
  			 npheLTCC_vs_Mom[h].setTitleX(momName);
  			 npheLTCC_vs_Mom[h].setTitleY(ltccName);
  			 histNameCut = baseName2D[0] + "Cut" + addName[h];
  			 npheLTCC_vs_MomCut[h] = new H2F(histNameCut,100,0,maxMom,100,0,100);
 			 npheLTCC_vs_MomCut[h].setTitleX(momName);
 			 npheLTCC_vs_MomCut[h].setTitleY(ltccName);
  			 
  			 histName = baseName2D[1] + addName[h];
 			 npheHTCC_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,100);
 			 npheHTCC_vs_Mom[h].setTitleX(momName);
 			 npheHTCC_vs_Mom[h].setTitleY(htccName);
 			 histNameCut = baseName2D[1] + "Cut" + addName[h];
 			 npheHTCC_vs_MomCut[h] = new H2F(histNameCut,100,0,maxMom,100,0,100);
 			 npheHTCC_vs_MomCut[h].setTitleX(momName);
 			 npheHTCC_vs_MomCut[h].setTitleY(htccName);
  			 
 			 histName = baseName2D[2] + addName[h];
			 deECIN_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,1);
			 deECIN_vs_Mom[h].setTitleX(momName);
			 deECIN_vs_Mom[h].setTitleY(deecinName);
			 histNameCut = baseName2D[2] + "Cut" + addName[h];
			 deECIN_vs_MomCut[h] = new H2F(histNameCut,100,0,maxMom,100,0,1);
			 deECIN_vs_MomCut[h].setTitleX(momName);
			 deECIN_vs_MomCut[h].setTitleY(deecinName);
			 
			 histName = baseName2D[3] + addName[h];
			 deECOUT_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,1);
			 deECOUT_vs_Mom[h].setTitleX(momName);
			 deECOUT_vs_Mom[h].setTitleY(deecoutName);
			 histNameCut = baseName2D[3] + "Cut" + addName[h];
			 deECOUT_vs_MomCut[h] = new H2F(histNameCut,100,0,maxMom,100,0,1);
			 deECOUT_vs_MomCut[h].setTitleX(momName);
			 deECOUT_vs_MomCut[h].setTitleY(deecoutName);
  			 
			 histName = baseName2D[4] + addName[h];
			 dePCAL_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,1);
			 dePCAL_vs_Mom[h].setTitleX(momName);
			 dePCAL_vs_Mom[h].setTitleY(depcalName);
			 histNameCut = baseName2D[4] + "Cut" + addName[h];
			 dePCAL_vs_MomCut[h] = new H2F(histNameCut,100,0,maxMom,100,0,1);
			 dePCAL_vs_MomCut[h].setTitleX(momName);
			 dePCAL_vs_MomCut[h].setTitleY(depcalName);
			 
			 histName = baseName2D[5] + addName[h];
			 TOF_vs_Mom[h] = new H2F(histName,100,0,maxMom,100,0,1);
			 TOF_vs_Mom[h].setTitleX(momName);
			 TOF_vs_Mom[h].setTitleY(tofName);
			 histNameCut = baseName2D[5] + "Cut" + addName[h];
			 TOF_vs_MomCut[h] = new H2F(histNameCut,100,0,maxMom,100,0,1);
			 TOF_vs_MomCut[h].setTitleX(momName);
			 TOF_vs_MomCut[h].setTitleY(tofName);
			 
			 histName = baseName2D[6] + addName[h];
			 geoPlots[h] = new H2F(histName,100,-90,90,100,-90,90);
			 geoPlots[h].setTitleX(geoNameX);
			 geoPlots[h].setTitleY(geoNameY);
			 
			 //---------------------------------------------------------------
			 
			 //1D hists:
			 //---------------------------------------------------------------
			 histName = baseName1D[0] + addName[h];
			 MomHist[h] = new H1F(histName,100,0,maxMom);
			 MomHist[h].setTitleX(momName);
			 MomHist[h].setTitleY(countName);
			 MomHist[h].setLineWidth(lineWidth);
			 histNameCut = baseName1D[0] + "Cut" + addName[h];
			 MomHistCut[h] = new H1F(histNameCut,100,0,maxMom);
			 MomHistCut[h].setTitleX(momName);
			 MomHistCut[h].setTitleY(countName);
			 MomHistCut[h].setLineWidth(lineWidth);
			 
			 histName = baseName1D[1] + addName[h];
			 npheLTCCHist[h] = new H1F(histName,100,0,100);
			 npheLTCCHist[h].setTitleX(ltccName);
			 npheLTCCHist[h].setTitleY(countName);
			 npheLTCCHist[h].setLineWidth(lineWidth);
			 histNameCut = baseName1D[1] + "Cut" + addName[h];
			 npheLTCCHistCut[h] = new H1F(histNameCut,100,0,100);
			 npheLTCCHistCut[h].setTitleX(ltccName);
			 npheLTCCHistCut[h].setTitleY(countName);
			 npheLTCCHistCut[h].setLineWidth(lineWidth);
			 
			 histName = baseName1D[2] + addName[h];
			 npheHTCCHist[h] = new H1F(histName,100,0,100);
			 npheHTCCHist[h].setTitleX(htccName);
			 npheHTCCHist[h].setTitleY(countName);
			 npheHTCCHist[h].setLineWidth(lineWidth);
			 histNameCut = baseName1D[2] + "Cut" + addName[h];
			 npheHTCCHistCut[h] = new H1F(histNameCut,100,0,100);
			 npheHTCCHistCut[h].setTitleX(htccName);
			 npheHTCCHistCut[h].setTitleY(countName);
			 npheHTCCHistCut[h].setLineWidth(lineWidth);
			 
			 histName = baseName1D[3] + addName[h];
			 deECINHist[h] = new H1F(histName,100,0,1.0);
			 deECINHist[h] .setTitleX(deecinName);
			 deECINHist[h] .setTitleY(countName);
			 deECINHist[h].setLineWidth(lineWidth);
			 histNameCut = baseName1D[3] + "Cut" + addName[h];
			 deECINHistCut[h] = new H1F(histNameCut,100,0,1.0);
			 deECINHistCut[h] .setTitleX(deecinName);
			 deECINHistCut[h] .setTitleY(countName);
			 deECINHistCut[h].setLineWidth(lineWidth);
			 
			 histName = baseName1D[4] + addName[h];
			 deECOUTHist[h] = new H1F(histName,100,0,1.0);
			 deECOUTHist[h].setTitleX(deecoutName);
			 deECOUTHist[h].setTitleY(countName);
			 deECOUTHist[h].setLineWidth(lineWidth);
			 histNameCut = baseName1D[4] + "Cut" + addName[h];
			 deECOUTHistCut[h] = new H1F(histNameCut,100,0,1.0);
			 deECOUTHistCut[h].setTitleX(deecoutName);
			 deECOUTHistCut[h].setTitleY(countName);
			 deECOUTHistCut[h].setLineWidth(lineWidth);
			 
			 histName = baseName1D[5] + addName[h];
			 dePCALHist[h] = new H1F(histName,100,0,1.0);
			 dePCALHist[h].setTitleX(depcalName);
			 dePCALHist[h].setTitleY(countName);
			 dePCALHist[h].setLineWidth(lineWidth);
			 histNameCut = baseName1D[5] + "Cut" + addName[h];
			 dePCALHistCut[h] = new H1F(histNameCut,100,0,1.0);
			 dePCALHistCut[h].setTitleX(depcalName);
			 dePCALHistCut[h].setTitleY(countName);
			 dePCALHistCut[h].setLineWidth(lineWidth);
			 
			 histName = baseName1D[6] + addName[h];
			 timeHist[h] = new H1F(histName,100,142,150);
			 timeHist[h].setTitleX(tofName);
			 timeHist[h].setTitleY(countName);
			 histName = baseName1D[6] + "Cut" +  addName[h];
			 timeHistCut[h] = new H1F(histName,100,142,150);
			 timeHistCut[h].setTitleX(tofName);
			 timeHistCut[h].setTitleY(countName);
			 
			 histName = baseName1D[7] + addName[h];
			 vertexHist[h] = new H1F(histName,500,0,100);
			 vertexHist[h].setTitleX(vertexName);
			 vertexHist[h].setTitleY(countName);
			 histNameCut = baseName1D[7] + "Cut" + addName[h];
			 vertexHistCut[h] = new H1F(histName,500,0,100);
			 vertexHistCut[h].setTitleX(vertexName);
			 vertexHistCut[h].setTitleY(countName);
			 
			 histName = baseName1D[8] + addName[h];
			 sumDEcalHist[h] = new H1F(histName,100,0,0.5);
			 sumDEcalHist[h].setTitleX(calName);
			 sumDEcalHist[h].setTitleY(countName);
			 
			 histName = baseName1D[9] + addName[h];
			 phiHists[h] = new H1F(histName,1000,-180,180);
			 phiHists[h].setTitleX("Reconstructed Phi Angle [deg]");
			 phiHists[h].setTitleY(countName);
			 
			  
			 //--------------------------------------------------------------- 
  		}
  		
  		probDist = new H1F[3];
  		probDist[0] = new H1F("Probability: All",100,0,1.02);
  		probDist[0].setTitleX("Probability");
  		probDist[0].setTitleY("Entries");
  		probDist[0].setLineWidth(lineWidth);
  		
  		probDist[1] = new H1F("Probability: Signal",100,0,1.02);
  		probDist[1].setTitleX("Probability");
  		probDist[1].setTitleY("Entries");
  		probDist[1].setLineWidth(lineWidth);
  		probDist[1].setLineColor(8);
  		
  		probDist[2] = new H1F("Probability: Background",100,0,1.02);
  		probDist[2].setTitleX("Probability");
  		probDist[2].setTitleY("Entries");
  		probDist[2].setLineWidth(lineWidth);
  		probDist[2].setLineColor(2);
  		
  		String betaName = "Beta";
  		String diffMomName = "Momentum(rec) - Momentum(pid) [GeV/c]";
  		String diffBetaName = "Beta(rec) - Beta(pid) [ns]";
  		
  		betaVsMom = new H2F[3];
  		betaVsMom[0] = new H2F("Beta vs. Momentum: All Particles",100,0,maxMom,100,0,1.1);
  		betaVsMom[0].setTitleX(momName);
  		betaVsMom[0].setTitleY(betaName);
  		
  		betaVsMom[1] = new H2F("Beta vs. Momentum: Accepted",100,0,maxMom,100,0,1.1);
  		betaVsMom[1].setTitleX(momName);
  		betaVsMom[1].setTitleY(betaName);
  	
  		betaVsMom[2] = new H2F("Beta vs. Momentum: Rejected",100,0,maxMom,100,0,1.1);
  		betaVsMom[2].setTitleX(momName);
  		betaVsMom[2].setTitleY(betaName);
  		
  		
  		betaVsTof = new H2F[3];
  		betaVsTof[0] = new H2F("Beta vs. Tof: All Particles",100,142,150,100,0,1.1);
  		betaVsTof[0].setTitleX(tofName);
  		betaVsTof[0].setTitleY(betaName);
  		
  		betaVsTof[1] = new H2F("Beta vs. Tof: Accepted",100,142,150,100,0,1.1);
  		betaVsTof[1].setTitleX(tofName);
  		betaVsTof[1].setTitleY(betaName);
  	
  		betaVsTof[2] = new H2F("Beta vs. Tof: Rejected",100,142,150,100,0,1.1);
  		betaVsTof[2].setTitleX(tofName);
  		betaVsTof[2].setTitleY(betaName);
  		
  		diffMom_vs_Prob = new H2F[3];
  		diffMom_vs_Prob[0] = new H2F("Difference in Momentum: All Particles",100,0,1.02,100,-10,10);
  		diffMom_vs_Prob[0].setTitleX("Probability");
  		diffMom_vs_Prob[0].setTitleY(diffMomName);
  		
  		diffMom_vs_Prob[1] = new H2F("Difference in Momentum: Accepted Particles",100,0,1.02,100,-10,10);
  		diffMom_vs_Prob[1].setTitleX("Probability");
  		diffMom_vs_Prob[1].setTitleY(diffMomName);
  		
  		diffMom_vs_Prob[2] = new H2F("Difference in Momentum: Rejected Particles",100,0,1.02,100,-10,10);
  		diffMom_vs_Prob[2].setTitleX("Probability");
  		diffMom_vs_Prob[2].setTitleY(diffMomName);
  		
  		
  		diffBeta_vs_Prob = new H2F[3];
  		diffBeta_vs_Prob[0] = new H2F("Difference in Beta: All Particles",100,0,1.02,100,-0.5,0.5);
  		diffBeta_vs_Prob[0].setTitleX("Probability");
  		diffBeta_vs_Prob[0].setTitleY(diffBetaName);
  		
  		diffBeta_vs_Prob[1] = new H2F("Difference in Beta: Accepted Particles",100,0,1.02,100,-0.5,0.5);
  		diffBeta_vs_Prob[1].setTitleX("Probability");
  		diffBeta_vs_Prob[1].setTitleY(diffBetaName);
  		
  		diffBeta_vs_Prob[2] = new H2F("Difference in Beta: Rejected Particles",100,0,1.02,100,-0.5,0.5);
  		diffBeta_vs_Prob[2].setTitleX("Probability");
  		diffBeta_vs_Prob[2].setTitleY(diffBetaName);
  		
  		
  	}
	//***********************************************************************
	
	//And now the canvas:
	//***********************************************************************
	public void setCanvas(boolean showNorm, int Norm) {
  		//2 dimensional canvas:
  		//==============================================================
  		String[] canvasName2D = new String[3];
  		canvasName2D[0] = "Correlation Plots: All Particles";
  		canvasName2D[1] = "Correlation Plots: Signal Particles";
  		canvasName2D[2] = "Correlation Plots: Background Particles";
  		monCanvas2D = new TCanvas[3];
  		
  		for(int k=0;k<3;k++) {
  			monCanvas2D[k] = new TCanvas(canvasName2D[k],1600,600);
  			monCanvas2D[k].divide(3,2);
  			monCanvas2D[k].cd(0);
  			monCanvas2D[k].draw(npheLTCC_vs_Mom[k]);
  			monCanvas2D[k].cd(1);
  			monCanvas2D[k].draw(npheHTCC_vs_Mom[k]);
  			monCanvas2D[k].cd(2);
  			monCanvas2D[k].draw(deECIN_vs_Mom[k]);
  			monCanvas2D[k].cd(3);
  			monCanvas2D[k].draw(deECOUT_vs_Mom[k]);
  			monCanvas2D[k].cd(4);
  			monCanvas2D[k].draw(dePCAL_vs_Mom[k]);
  			monCanvas2D[k].cd(5);
  			monCanvas2D[k].draw(TOF_vs_Mom[k]);
  		}
  		
  		String[] canvasName2DCut = new String[3];
  		canvasName2DCut[0] = "Correlation Plots: All Particles";
  		canvasName2DCut[1] = "Correlation Plots: Accepted Particles";
  		canvasName2DCut[2] = "Correlation Plots: Rejected Particles";
  		monCanvas2DCut = new TCanvas[3];
  		
  		for(int k=0;k<3;k++) {
  			monCanvas2DCut[k] = new TCanvas(canvasName2DCut[k],1600,600);
  			monCanvas2DCut[k].divide(3,2);
  			monCanvas2DCut[k].cd(0);
  			monCanvas2DCut[k].draw(npheLTCC_vs_MomCut[k]);
  			monCanvas2DCut[k].cd(1);
  			monCanvas2DCut[k].draw(npheHTCC_vs_MomCut[k]);
  			monCanvas2DCut[k].cd(2);
  			monCanvas2DCut[k].draw(deECIN_vs_MomCut[k]);
  			monCanvas2DCut[k].cd(3);
  			monCanvas2DCut[k].draw(deECOUT_vs_MomCut[k]);
  			monCanvas2DCut[k].cd(4);
  			monCanvas2DCut[k].draw(dePCAL_vs_MomCut[k]);
  			monCanvas2DCut[k].cd(5);
  			monCanvas2DCut[k].draw(TOF_vs_MomCut[k]);
  		}
  	    //==============================================================
  		
  	    //1 dimensional canvas:
  		//==============================================================
  		 for(int h=0;h<3;h++) {
  	  		 //include normalisation, if wanted:
  				 if(showNorm) {
  					MomHist[h].divide(Norm);
  					npheLTCCHist[h].divide(Norm);
  					npheHTCCHist[h].divide(Norm);
  					deECINHist[h].divide(Norm);
  					deECOUTHist[h].divide(Norm);
  					dePCALHist[h].divide(Norm);
  					timeHist[h].divide(Norm);
  				 }
  	  	  }
  		
  		 int sigColor = 8;
  		 int bkgColor = 2;
  		
  		String[] canvasName1D = new String[3];
  		canvasName1D[0] = "1D Plots: Before PID";
  		canvasName1D[1] = "1D Plots: After PID";
  		canvasName1D[2] = "1D Plots: Comparison: true Signal vs. ID Signal";
  		monCanvas1D = new TCanvas[3];
  		
  		//Before PID:
  		//------------------------------------------------------------
  		monCanvas1D[0] = new TCanvas(canvasName1D[0],1600,800);
  		monCanvas1D[0].divide(3,2);
  		
  		monCanvas1D[0].cd(0);
  		monCanvas1D[0].draw(npheLTCCHist[0]);
  		npheLTCCHist[1].setLineColor(sigColor);
  		npheLTCCHist[2].setLineColor(bkgColor);
  		monCanvas1D[0].draw(npheLTCCHist[2],"same");
  		monCanvas1D[0].draw(npheLTCCHist[1],"same");
  		
  		monCanvas1D[0].cd(1);
  		monCanvas1D[0].draw(npheHTCCHist[0]);
  		npheHTCCHist[1].setLineColor(sigColor);
  		npheHTCCHist[2].setLineColor(bkgColor);
  		monCanvas1D[0].draw(npheHTCCHist[2],"same");
  		monCanvas1D[0].draw(npheHTCCHist[1],"same");
  		
  		monCanvas1D[0].cd(2);
  		monCanvas1D[0].draw(deECINHist[0]);
  		deECINHist[1].setLineColor(sigColor);
  		deECINHist[2].setLineColor(bkgColor);
  		monCanvas1D[0].draw(deECINHist[2],"same");
  		monCanvas1D[0].draw(deECINHist[1],"same");
  		
  		monCanvas1D[0].cd(3);
  		monCanvas1D[0].draw(deECOUTHist[0]);
  		deECOUTHist[1].setLineColor(sigColor);
  		deECOUTHist[2].setLineColor(bkgColor);
  		monCanvas1D[0].draw(deECOUTHist[2],"same");
  		monCanvas1D[0].draw(deECOUTHist[1],"same");
  		
  		monCanvas1D[0].cd(4);
  		monCanvas1D[0].draw(dePCALHist[0]);
  		dePCALHist[1].setLineColor(sigColor);
  		dePCALHist[2].setLineColor(bkgColor);
  		monCanvas1D[0].draw(dePCALHist[2],"same");
  		monCanvas1D[0].draw(dePCALHist[1],"same");
  		
  		monCanvas1D[0].cd(5);
  		monCanvas1D[0].draw(MomHist[0]);
  		MomHist[1].setLineColor(sigColor);
  		MomHist[2].setLineColor(bkgColor);
  		monCanvas1D[0].draw(MomHist[2],"same");
  		monCanvas1D[0].draw(MomHist[1],"same");
  	   //------------------------------------------------------------
  		
  		//After PID:
  	    //------------------------------------------------------------
  		monCanvas1D[1] = new TCanvas(canvasName1D[1],1600,800);
  		monCanvas1D[1].divide(3,2);
  		
  		monCanvas1D[1].cd(0);
  		monCanvas1D[1].draw(npheLTCCHistCut[0]);
  		npheLTCCHistCut[1].setLineColor(sigColor);
  		npheLTCCHistCut[2].setLineColor(bkgColor);
  		monCanvas1D[1].draw(npheLTCCHistCut[2],"same");
  		monCanvas1D[1].draw(npheLTCCHistCut[1],"same");
  		
  		monCanvas1D[1].cd(1);
  		monCanvas1D[1].draw(npheHTCCHistCut[0]);
  		npheHTCCHistCut[1].setLineColor(sigColor);
  		npheHTCCHistCut[2].setLineColor(bkgColor);
  		monCanvas1D[1].draw(npheHTCCHistCut[2],"same");
  		monCanvas1D[1].draw(npheHTCCHistCut[1],"same");
  		
  		monCanvas1D[1].cd(2);
  		monCanvas1D[1].draw(deECINHistCut[0]);
  		deECINHistCut[1].setLineColor(sigColor);
  		deECINHistCut[2].setLineColor(bkgColor);
  		monCanvas1D[1].draw(deECINHistCut[2],"same");
  		monCanvas1D[1].draw(deECINHistCut[1],"same");
  		
  		monCanvas1D[1].cd(3);
  		monCanvas1D[1].draw(deECOUTHistCut[0]);
  		deECOUTHistCut[1].setLineColor(sigColor);
  		deECOUTHistCut[2].setLineColor(bkgColor);
  		monCanvas1D[1].draw(deECOUTHistCut[2],"same");
  		monCanvas1D[1].draw(deECOUTHistCut[1],"same");
  		
  		monCanvas1D[1].cd(4);
  		monCanvas1D[1].draw(dePCALHistCut[0]);
  		dePCALHistCut[1].setLineColor(sigColor);
  		dePCALHistCut[2].setLineColor(bkgColor);
  		monCanvas1D[1].draw(dePCALHistCut[2],"same");
  		monCanvas1D[1].draw(dePCALHistCut[1],"same");
  		
  		monCanvas1D[1].cd(5);
  		monCanvas1D[1].draw(MomHistCut[0]);
  		MomHistCut[1].setLineColor(sigColor);
  		MomHistCut[2].setLineColor(bkgColor);
  		monCanvas1D[1].draw(MomHistCut[2],"same");
  		monCanvas1D[1].draw(MomHistCut[1],"same");
  	    //-----------------------------------------------------------
  		
  		
  	    //Compare PID and true:
  	    //------------------------------------------------------------
  		monCanvas1D[2] = new TCanvas(canvasName1D[2],1600,800);
  		monCanvas1D[2].divide(3,2);
  		
  		monCanvas1D[2].cd(0);
  		npheLTCCHist[1].setLineColor(1);
  		npheLTCCHist[1].setFillColor(4);
  		npheLTCCHistCut[1].setLineColor(4);
  		npheLTCCHistCut[1].setFillColor(4);
  		monCanvas1D[2].draw(npheLTCCHistCut[1]);
  		monCanvas1D[2].draw(npheLTCCHist[1],"same");
  		
  		monCanvas1D[2].cd(1);
  		npheHTCCHist[1].setLineColor(1);
  		npheHTCCHist[1].setFillColor(4);
  		npheHTCCHistCut[1].setLineColor(4);
  		npheHTCCHistCut[1].setFillColor(4);
  		monCanvas1D[2].draw(npheHTCCHistCut[1]);
  		monCanvas1D[2].draw(npheHTCCHist[1],"same");
  		
  		monCanvas1D[2].cd(2);
  		deECINHist[1].setLineColor(1);
  		deECINHist[1].setFillColor(4);
  		deECINHistCut[1].setLineColor(4);
  		deECINHistCut[1].setFillColor(4);
  		monCanvas1D[2].draw(deECINHistCut[1]);
  		monCanvas1D[2].draw(deECINHist[1],"same");
  		
  		monCanvas1D[2].cd(3);
  		deECOUTHist[1].setLineColor(1);
  		deECOUTHist[1].setFillColor(4);
  		deECOUTHistCut[1].setLineColor(4);
  		deECOUTHistCut[1].setFillColor(4);
  		monCanvas1D[2].draw(deECOUTHistCut[1]);
  		monCanvas1D[2].draw(deECOUTHist[1],"same");
  		
  		monCanvas1D[2].cd(4);
  		dePCALHist[1].setLineColor(1);
  		dePCALHist[1].setFillColor(4);
  		dePCALHistCut[1].setLineColor(4);
  		dePCALHistCut[1].setFillColor(4);
  		monCanvas1D[2].draw(dePCALHistCut[1]);
  		monCanvas1D[2].draw(dePCALHist[1],"same");
  		
  		monCanvas1D[2].cd(5);
  		MomHist[1].setLineColor(1);
  		MomHistCut[1].setLineColor(4);
  		MomHistCut[1].setFillColor(4);
  		monCanvas1D[2].draw(MomHist[1]);
  		monCanvas1D[2].draw(MomHistCut[1],"same");
  	    //-----------------------------------------------------------
  		
  	    //==============================================================
  		
  		
  		probCanvas = new TCanvas("Probability for being e-",500,500);
  		probCanvas.draw(probDist[0]);
  		probCanvas.draw(probDist[1],"same");
  		probCanvas.draw(probDist[2],"same");
  		
  		
  		
  	}
	
	//==============================================================
	
	public void setMoreCanvas() {
	   betaCanvas = new TCanvas("Beta Plots",1600,800);
	   betaCanvas.divide(3,2);
	   for(int j=0;j<6;j++) {
		   betaCanvas.cd(j);
		   if(j < 3) {
			   betaCanvas.draw(betaVsMom[j]);
		   }else betaCanvas.draw(betaVsTof[j-3]);
	   }
	    
	   diffCanvas = new TCanvas("Residual Plots",500,500);
	   diffCanvas.draw(diffBeta_vs_Prob[0]);
	   
	   
	   vertexCanvas = new TCanvas("Particle Verticies and Times",1200,600);
	   vertexCanvas.divide(2, 2);
	   vertexCanvas.cd(0);
	   vertexHist[0].setLineWidth(3);
	   vertexCanvas.draw(vertexHist[0]);
	   vertexHist[1].setLineWidth(3);
	   vertexHist[1].setLineColor(8);
	   vertexCanvas.draw(vertexHist[1],"same");
	   vertexHist[2].setLineWidth(3);
	   vertexHist[2].setLineColor(2);
	   vertexCanvas.draw(vertexHist[2],"same");
	   vertexCanvas.cd(1);
	   vertexHistCut[0].setLineWidth(3);
	   vertexCanvas.draw(vertexHistCut[0]);
	   vertexHistCut[1].setLineWidth(3);
	   vertexHistCut[1].setLineColor(8);
	   vertexCanvas.draw(vertexHistCut[1],"same");
	   vertexHistCut[2].setLineWidth(3);
	   vertexHistCut[2].setLineColor(2);
	   vertexCanvas.draw(vertexHistCut[2],"same");
	   
	   vertexCanvas.cd(2);
	   timeHist[0].setLineWidth(3);
	   vertexCanvas.draw(timeHist[0]);
	   timeHist[1].setLineWidth(3);
	   timeHist[1].setLineColor(8);
	   vertexCanvas.draw(timeHist[1],"same");
	   timeHist[2].setLineWidth(3);
	   timeHist[2].setLineColor(2);
	   vertexCanvas.draw(timeHist[2],"same");
	   vertexCanvas.cd(3);
	   timeHistCut[0].setLineWidth(3);
	   vertexCanvas.draw(timeHistCut[0]);
	   timeHistCut[1].setLineWidth(3);
	   timeHistCut[1].setLineColor(8);
	   vertexCanvas.draw(timeHistCut[1],"same");
	   timeHistCut[2].setLineWidth(3);
	   timeHistCut[2].setLineColor(2);
	   vertexCanvas.draw(timeHistCut[2],"same");
	   
	   sumDECanvas = new TCanvas("Sum of Energy Deposits in Calorimeter divided by Momentum and ToF",1200,500);
	   sumDECanvas.divide(2, 1);
	   sumDEcalHist[0].setLineWidth(3);
	   sumDEcalHist[1].setLineWidth(3);
	   sumDEcalHist[2].setLineWidth(3);
	   sumDEcalHist[1].setLineColor(8);
	   sumDEcalHist[2].setLineColor(2);
	   sumDECanvas.draw(sumDEcalHist[0]);
	   sumDECanvas.draw(sumDEcalHist[2],"same");
	   sumDECanvas.draw(sumDEcalHist[1],"same");
	   
	   sumDECanvas.cd(1);
	   timeHistCut[0].setLineWidth(3);
	   timeHistCut[1].setLineWidth(3);
	   timeHistCut[1].setLineColor(8);
	   timeHistCut[2].setLineWidth(3);
	   timeHistCut[2].setLineColor(2);
	   sumDECanvas.draw(timeHistCut[0]);
	   sumDECanvas.draw(timeHistCut[2],"same");
	   sumDECanvas.draw(timeHistCut[1],"same");
	   
	   geoCanvas = new TCanvas("Geometric Acceptance",1200,600);
	   geoCanvas.divide(3, 2);
	   geoCanvas.cd(0);
	   geoCanvas.draw(geoPlots[0]);
	   geoCanvas.cd(1);
	   geoCanvas.draw(geoPlots[1]);
	   geoCanvas.cd(2);
	   geoCanvas.draw(geoPlots[2]);
	   geoCanvas.cd(3);
	   geoCanvas.draw(phiHists[0]);
	   geoCanvas.cd(4);
	   geoCanvas.draw(phiHists[1]);
	   geoCanvas.cd(5);
	   geoCanvas.draw(phiHists[2]);
	   
	}
	//***********************************************************************
	
	//Fill histograms:
	//***********************************************************************
	public void fillPIDHists(int pType, double mom, double ltcc, double htcc, double ecIN, double ecOUT, double pcal, double tof, double vert, double theta, double phi) {
		//2D histograms:
	      //=========================================================
	      npheLTCC_vs_Mom[0].fill(mom,ltcc);
	      npheHTCC_vs_Mom[0].fill(mom,htcc);
	      deECIN_vs_Mom[0].fill(mom, ecIN);
	      deECOUT_vs_Mom[0].fill(mom, ecOUT);
	      dePCAL_vs_Mom[0].fill(mom, pcal);
	      TOF_vs_Mom[0].fill(mom, pcal);
	      
	      geoPlots[0].fill((180/Math.PI)*theta*Math.sin(phi), (180/Math.PI)*theta*Math.cos(phi));
	      phiHists[0].fill((180/Math.PI)*phi);
	  
          npheLTCC_vs_Mom[2-pType].fill(mom,ltcc);
	      npheHTCC_vs_Mom[2-pType].fill(mom,htcc);
	      deECIN_vs_Mom[2-pType].fill(mom, ecIN);
	      deECOUT_vs_Mom[2-pType].fill(mom, ecOUT);
	      dePCAL_vs_Mom[2-pType].fill(mom, pcal);
	      TOF_vs_Mom[2-pType].fill(mom,pcal);
	      
	      geoPlots[2-pType].fill((180/Math.PI)*theta*Math.sin(phi), (180/Math.PI)*theta*Math.cos(phi));
	      phiHists[2-pType].fill((180/Math.PI)*phi);
          //=========================================================	   
	   
	     //1D histograms:
	     //=========================================================
	     npheLTCCHist[0].fill(ltcc);
	     npheHTCCHist[0].fill(htcc);
	     deECINHist[0].fill(ecIN);
	     deECOUTHist[0].fill(ecOUT);
	     dePCALHist[0].fill(pcal);
	     timeHist[0].fill(tof);
	     MomHist[0].fill(mom);
	     vertexHist[0].fill(vert);
 
	     npheLTCCHist[2-pType].fill(ltcc);
	     npheHTCCHist[2-pType].fill(htcc);
	     deECINHist[2-pType].fill(ecIN);
	     deECOUTHist[2-pType].fill(ecOUT);
	     dePCALHist[2-pType].fill(pcal);
	     timeHist[2-pType].fill(tof);
	     MomHist[2-pType].fill(mom);
	     vertexHist[2-pType].fill(vert);
   //=========================================================		
	}
	
	
	//=========================================================	
	
	public void fillPIDprob(int pType, double prob) {
		probDist[0].fill(prob);
		probDist[2-pType].fill(prob);
	}
	
	//=========================================================	
	
	public void fillPIDHistsCut(double clResponse, double mom, double ltcc, double htcc, double ecIN, double ecOUT, double pcal, double tof, double vert) {
		//2D histograms:
	      //=========================================================
	      npheLTCC_vs_MomCut[0].fill(mom,ltcc);
	      npheHTCC_vs_MomCut[0].fill(mom,htcc);
	      deECIN_vs_MomCut[0].fill(mom, ecIN);
	      deECOUT_vs_MomCut[0].fill(mom, ecOUT);
	      dePCAL_vs_MomCut[0].fill(mom, pcal);
	      TOF_vs_MomCut[0].fill(mom, tof);
	  
          npheLTCC_vs_MomCut[2-(int)clResponse].fill(mom,ltcc);
	      npheHTCC_vs_MomCut[2-(int)clResponse].fill(mom,htcc);
	      deECIN_vs_MomCut[2-(int)clResponse].fill(mom, ecIN);
	      deECOUT_vs_MomCut[2-(int)clResponse].fill(mom, ecOUT);
	      dePCAL_vs_MomCut[2-(int)clResponse].fill(mom, pcal);
	      TOF_vs_MomCut[2-(int)clResponse].fill(mom, tof);
        //=========================================================	   
		
		
	     //1D histograms:
	     //=========================================================
	     npheLTCCHistCut[0].fill(ltcc);
	     npheHTCCHistCut[0].fill(htcc);
	     deECINHistCut[0].fill(ecIN);
	     deECOUTHistCut[0].fill(ecOUT);
	     dePCALHistCut[0].fill(pcal);
	     MomHistCut[0].fill(mom);
	     timeHistCut[0].fill(tof);
	     sumDEcalHist[0].fill((ecIN+ecOUT+pcal)/mom);
	     vertexHistCut[0].fill(vert);
 
	     npheLTCCHistCut[2-(int)clResponse].fill(ltcc);
	     npheHTCCHistCut[2-(int)clResponse].fill(htcc);
	     deECINHistCut[2-(int)clResponse].fill(ecIN);
	     deECOUTHistCut[2-(int)clResponse].fill(ecOUT);
	     dePCALHistCut[2-(int)clResponse].fill(pcal);
	     MomHistCut[2-(int)clResponse].fill(mom);
	     timeHistCut[2-(int)clResponse].fill(tof);
	     sumDEcalHist[2-(int)clResponse].fill((ecIN+ecOUT+pcal)/mom);
	     vertexHistCut[2-(int)clResponse].fill(vert);
   //=========================================================		
	}
	
	//=========================================================	
	
	public void fillBetaHists(double clResponse, double prob, double mom, double betaCalc, double betaMeas, double tof) {
	    betaVsMom[0].fill(mom, betaCalc);
	    betaVsTof[0].fill(tof, betaCalc);
	    diffBeta_vs_Prob[0].fill(prob, betaMeas - betaCalc);
	    
	    betaVsMom[2-(int)clResponse].fill(mom, betaCalc);
	    betaVsTof[2-(int)clResponse].fill(tof, betaCalc);
	}
	//***********************************************************************
	
	
}
