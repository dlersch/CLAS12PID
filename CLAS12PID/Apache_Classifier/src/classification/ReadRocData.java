package classification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.ui.TCanvas;

public class ReadRocData {
	
	//Stuff to read in the roc-curve pars:
	//++++++++++++++++++++++++++++++++++++++
	private String inputFile = null;
	private Scanner sc = null;
	//++++++++++++++++++++++++++++++++++++++
	
	//Stuff to plot the results:
	//++++++++++++++++++++++++++++++++++++++
	private GraphErrors gr_eff_vs_pur_sig = null;
	private GraphErrors gr_eff_vs_pur_bkg = null;
	private GraphErrors gr_eff_vs_fpnr_sig = null;
	private GraphErrors gr_eff_vs_fpnr_bkg = null;
	private GraphErrors gr_opt_sig = null;
	private GraphErrors gr_opt_bkg = null;
	private GraphErrors gr_optfpnr_sig = null;
	private GraphErrors gr_optfpnr_bkg = null;
	
	private GraphErrors gr_dist_sig = null;
	private GraphErrors gr_fscore_sig = null;
	private GraphErrors gr_dist_bkg = null;
	private GraphErrors gr_fscore_bkg = null;
	
	private GraphErrors gr_mcc = null;
	private TCanvas metricCanvas = null;
	
	private TCanvas effPurCanvas = null;
	private List<Double> probList = null;
	private List<Double> effListSig = null;
	private List<Double> purListSig = null;
	private List<Double> fpnrListSig = null;
	private List<Double> effListBkg = null;
	private List<Double> purListBkg = null;
	private List<Double> fpnrListBkg = null;
	//++++++++++++++++++++++++++++++++++++++


	public static void main(String args[]) throws FileNotFoundException {
		String inFile = "/Users/lersch/Desktop/CLAS/NN_studies/Apache_Classifier/MLP_HL10753V12345678N50000R1T-1S1/ROCCurves_MLP.txt";
		
		//String inFile = args[0];
		new ReadRocData(inFile);
	}
	
	
	
	public ReadRocData(String inFile) throws FileNotFoundException {
		setInputFile(inFile);
		getRocPars();
		plotROC();
		
		System.out.println("Optimum found by min. Distance: " + getOptimum(gr_dist_sig,gr_eff_vs_fpnr_sig,"min")[0] + " at a probaility: " + getOptimum(gr_dist_sig,gr_eff_vs_fpnr_sig,"min")[1] + " with efficiency: " + getOptimum(gr_dist_sig,gr_eff_vs_fpnr_sig,"min")[2] + " and FPR: " + getOptimum(gr_dist_sig,gr_eff_vs_fpnr_sig,"min")[3]);
		System.out.println("Optimum found by F1score: " + getOptimum(gr_fscore_sig,gr_eff_vs_pur_sig,"max")[0] + " at a probaility: " + getOptimum(gr_fscore_sig,gr_eff_vs_pur_sig,"max")[1] + " with efficiency: " + getOptimum(gr_fscore_sig,gr_eff_vs_pur_sig,"max")[2] + " and purity: " + getOptimum(gr_fscore_sig,gr_eff_vs_pur_sig,"max")[3]);
		System.out.println("Optimum found by MCC: " + getOptimum(gr_mcc,gr_eff_vs_pur_sig,"max")[0] + " at a probaility: " + getOptimum(gr_mcc,gr_eff_vs_pur_sig,"max")[1] + " with efficiency: " + getOptimum(gr_mcc,gr_eff_vs_pur_sig,"max")[2] + " and purity: " + getOptimum(gr_mcc,gr_eff_vs_pur_sig,"max")[3] + " and fpnr: " + getOptimum(gr_mcc,gr_eff_vs_fpnr_sig,"max")[3]);
		
	}
	
	//************************************************************
	public void readRocPars() throws FileNotFoundException {
		FileInputStream fstream = new FileInputStream(inputFile);
		sc = new Scanner(fstream);
		String currentLine;
		
		effListSig = new ArrayList<Double>();
		purListSig = new ArrayList<Double>();
		fpnrListSig = new ArrayList<Double>();
		
		probList = new ArrayList<Double>();
		
		effListBkg = new ArrayList<Double>();
		purListBkg = new ArrayList<Double>();
		fpnrListBkg = new ArrayList<Double>();
		
  
		while(sc.hasNextLine()) {
			currentLine = sc.nextLine();
			
			//---------------------------------------------------------------------------------
			if(currentLine.contains("EFFVSPURSIG")) {
				String[] segments = currentLine.split(": ");
				String[] sp = segments[2].split(" ");
				String[] seff = segments[3].split(" ");
				String[] spur = segments[4].split(" ");
				    
			    effListSig.add( Double.parseDouble(seff[0])  );
				purListSig.add( Double.parseDouble(spur[0])  );
				probList.add(   Double.parseDouble(sp[0])    );
				   
                 segments = null;
                 seff = null;
                 spur = null;
                 sp = null;
			}
			//---------------------------------------------------------------------------------
			
			//---------------------------------------------------------------------------------
			if(currentLine.contains("EFFVSPURBKG")) {
				String[] segments = currentLine.split(": ");
				String[] seff = segments[3].split(" ");
				String[] spur = segments[4].split(" ");
				String[] sp = segments[2].split(" ");
			
			    effListBkg.add(  Double.parseDouble(seff[0])  );
				purListBkg.add(  Double.parseDouble(spur[0])  );
				   
                 segments = null;
                 seff = null;
                 spur = null;
                 sp = null;
			}
			//---------------------------------------------------------------------------------
			
			//---------------------------------------------------------------------------------
			if(currentLine.contains("EFFVSFPNRSIG")) {
				String[] segments = currentLine.split(": ");
				String[] spur = segments[4].split(" ");
                
				fpnrListSig.add(   Double.parseDouble(spur[0])   );
				   
                 segments = null;
                 spur = null;
			}
			//---------------------------------------------------------------------------------
			
			//---------------------------------------------------------------------------------
			if(currentLine.contains("EFFVSFPNRBKG")) {
				String[] segments = currentLine.split(": ");
				String[] spur = segments[4].split(" ");
	
				fpnrListBkg.add(  Double.parseDouble(spur[0])  );
	
                 segments = null;
                 spur = null;
			}
			//---------------------------------------------------------------------------------
		}	
	}
	
	//==============================
	
	public void plotROC() {
		
		effPurCanvas = new TCanvas("Efficiency vs. Purity",1200,500);
		effPurCanvas.divide(2, 1);
		effPurCanvas.cd(0);
		gr_eff_vs_pur_sig.setTitleX("Purity [%]");
		gr_eff_vs_pur_sig.setTitleY("Efficiency [%]");
		effPurCanvas.draw(gr_eff_vs_pur_sig);
		gr_eff_vs_pur_bkg.setMarkerColor(2);
		effPurCanvas.draw(gr_eff_vs_pur_bkg,"same");
		effPurCanvas.cd(1);
		gr_eff_vs_fpnr_sig.setTitleX("False Positive/Negative Rate [%]");
		gr_eff_vs_fpnr_sig.setTitleY("Efficiency [%]");
		effPurCanvas.draw(gr_eff_vs_fpnr_sig);
		gr_eff_vs_fpnr_bkg.setMarkerColor(2);
		effPurCanvas.draw(gr_eff_vs_fpnr_bkg,"same");
		
		metricCanvas = new TCanvas("Different ROC-Metrics",1600,500);
		metricCanvas.divide(3, 1);
		metricCanvas.cd(0);
		gr_dist_sig.setTitleX("Probability");
		gr_dist_sig.setTitleY("Minimum Distance");
		gr_dist_bkg.setMarkerColor(2);
		metricCanvas.draw(gr_dist_sig);
		metricCanvas.draw(gr_dist_bkg,"same");
		metricCanvas.cd(1);
		gr_fscore_sig.setTitleX("Probability");
		gr_fscore_sig.setTitleY("F1 Score");
		gr_fscore_bkg.setMarkerColor(2);
		metricCanvas.draw(gr_fscore_sig);
		metricCanvas.draw(gr_fscore_bkg,"same");
		metricCanvas.cd(2);
		gr_mcc.setTitleX("Probability");
		gr_mcc.setTitleY("MCC");
		metricCanvas.draw(gr_mcc);
	}
	
	//==============================
	
	public void getRocPars() throws FileNotFoundException {
		readRocPars();
		int nPoints = effListSig.size();
		
		this.gr_eff_vs_fpnr_bkg = new GraphErrors();
		this.gr_eff_vs_fpnr_sig = new GraphErrors();
		
		this.gr_eff_vs_pur_bkg = new GraphErrors();
		this.gr_eff_vs_pur_sig = new GraphErrors();
		
		this.gr_dist_sig = new GraphErrors();
		this.gr_fscore_sig = new GraphErrors();
		
		this.gr_dist_bkg = new GraphErrors();
		this.gr_fscore_bkg = new GraphErrors();
		
		this.gr_mcc = new GraphErrors();
		
		double effSig,effBkg;
		double purSig,purBkg;
		double fpnrSig,fpnrBkg;
		
		effSig = effBkg = 0.0;
		purSig = purBkg = 0.0;
		fpnrSig = fpnrBkg = 0.0;
		
		//Metrics for choosing a classifier:
		double f1scoreSig,f1scoreBkg;
		double distSig,distBkg;
		double mcc;
		f1scoreSig = f1scoreBkg = distSig = distBkg = mcc = 0.0;
		
		double probCut = 0.0;
		
		for(int i=0;i<nPoints;i++) {
			effSig = effListSig.get(i);
			purSig = purListSig.get(i);
			fpnrSig = fpnrListSig.get(i);
			
			effBkg = effListBkg.get(i);
			purBkg = purListBkg.get(i);
			fpnrBkg = fpnrListBkg.get(i);
			
			probCut = probList.get(i);
			
			//Fill graphs:
			gr_eff_vs_pur_sig.addPoint(purSig, effSig, 0.0, 0.0);
			gr_eff_vs_fpnr_sig.addPoint(fpnrSig, effSig, 0.0, 0.0);
			gr_eff_vs_pur_bkg.addPoint(purBkg, effBkg, 0.0, 0.0);
			gr_eff_vs_fpnr_bkg.addPoint(fpnrBkg, effBkg, 0.0, 0.0);
			
			//F1-score: best performance:1 / worst performance: 0
			f1scoreSig = 2*purSig*effSig*0.01*0.01 / (purSig*0.01 + effSig*0.01);
			f1scoreBkg = 2*purBkg*effBkg*0.01*0.01 / (purBkg*0.01 + effBkg*0.01);
			
			gr_fscore_sig.addPoint(probCut, f1scoreSig, 0.0, 0.0);
			gr_fscore_bkg.addPoint(probCut, f1scoreBkg, 0.0, 0.0);
		
			//Distance to optimum: Should be zer0 for best performance
			distSig = Math.sqrt(Math.pow(1 - effSig*0.01,2) + Math.pow(0-fpnrSig*0.01, 2));
			distBkg = Math.sqrt(Math.pow(1 - effBkg*0.01,2) + Math.pow(0-fpnrBkg*0.01, 2));
			
			gr_dist_sig.addPoint(probCut, distSig, 0.0, 0.0);
			gr_dist_bkg.addPoint(probCut, distBkg, 0.0, 0.0);	
			
			//Mathews Correlation Coefficient: -1: worst performance / +1: best performance:
			mcc  = (effSig*effBkg*0.01*0.01 - fpnrSig*fpnrBkg*0.01*0.01) / (Math.sqrt((effSig/purSig) * (effBkg/purBkg)));
			
			if(mcc >= -1 && mcc <= 1)gr_mcc.addPoint(probCut, mcc, 0.0, 0.0);
		}
	}
	
	//==============================
	
	public double[] getOptimum(GraphErrors gr, GraphErrors grROC, String option) {
		double[] opt = null;
		opt = new double[4];
		
		double extremum = 0.0;
		boolean isMin = false;
		boolean isMax = false;
		
		if(option.equals("min")) {
			extremum = 1000;
			isMin = true;
		}else if(option.equals("max")) {
			extremum = 0.0;
			isMax = true;
		}
		
		double pointY,pointX;
		double foundProb = 0.0;
		
		double foundEff = 0.0;
		double foundPar = 0.0;
		double rocX,rocY;
		
		int nPoints = gr.getDataSize(0);
		for(int i=0;i<nPoints;i++) {
			pointY = gr.getDataY(i);
			pointX = gr.getDataX(i);
			
			rocX = grROC.getDataX(i);
			rocY = grROC.getDataY(i);
			
			if(isMin && pointY < extremum) {
				extremum = pointY;
				foundProb  =pointX;
				foundEff = rocY;
				foundPar = rocX;
			}
			
			if(isMax && pointY > extremum) {
				extremum = pointY;
				foundProb = pointX;
				foundEff = rocY;
				foundPar = rocX;
			}
		}
		
		opt[0] = extremum;
		opt[1] = foundProb;
		opt[2] = foundEff;
		opt[3] = foundPar;
		
		return opt;
	}
	
	
	//************************************************************
	
	
	
	
	
	
	
	
	
	//Getters and Setters:
	//************************************************************
	public String getInputFile() {
		return inputFile;
	}

	//==============================
	
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	//************************************************************
	

}
