package classification;

//Program to translate the Apache-Spark classifier response
//to a continuous probability distribution
//Also, performance parameter such as the ROC-curves are calculated here
//Last date worked on: 10/23‚/17
//Written by: Daniel Lersch d.lersch@fz-juelich.de
//Using: Apache-Spark-FrameWork, CLAS12 reconstruction software


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

//Last date worked on: 09/01/2017
//Purpose of this thing: Translate the (discrete) response of a trained Apache Spark classifier to a 
//(continuous) probability
//Main packages included here: Apache Spark ML
//Daniel Lersch (d.lersch@fz-juelich.de)


//List all classification models here:
import org.apache.spark.ml.PipelineModel;
//Gradient Boosted Decision Trees:
import org.apache.spark.ml.classification.GBTClassificationModel;
import org.apache.spark.ml.classification.LinearSVCModel;
import org.apache.spark.ml.regression.DecisionTreeRegressionModel;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.data.H1F;
import org.jlab.groot.ui.TCanvas;
//Articifcial Neural Networks:
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.ml.linalg.Matrices;
import org.apache.spark.ml.linalg.Matrix;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.ml.ann.*;

public class ClassifierResponse {
    
	//Getting the classifier-response as a continuous variable:
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private PipelineModel classifier;
	private MultilayerPerceptronClassificationModel mlpModel = null;
	private Vector MLPWeights = null;
	private int[] MLPlayers = null;
	private GBTClassificationModel gbtModel = null;
	private DecisionTreeRegressionModel[] Trees = null;
	private double[] GBTWeights = null;
	private int NTrees = 0;
	private LinearSVCModel svmModel = null;
	private KMeansModel kmeansModel = null;
	
	private String usedModel;
	private String modelDir;
	private boolean isCLSet = false;
	private String ClassifierResponseName = null;
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	//Go for the ROC-curve:
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private int Nsteps = 0;
	private int NSignal_events = 0;
	private int NBackground_events = 0;
	private int NSignal_events_current = 0;
	private int NBackground_events_current = 0;
	private int NSignal_true_current = 0;
	private int NBackground_true_current = 0;
	private int NSignal_false_current = 0;
	private int NBackground_false_current = 0;
	private int NSignal_current = 0;
	private int NBackground_current = 0;
	private double currentSignalEfficiency = 0.0;
	private double currentSignalPurity = 0.0;
	private double currentSignalFPNR = 0.0;
	private double currentBackgroundEfficiency = 0.0;
	private double currentBackgroundPurity = 0.0;
	private double currentBackgroundFPNR = 0.0;
	private double currentCut = 0.0;
	private int[] Nsignal = null;
	private int[] Nbackground = null;
	private int[] Nsignal_true = null;
	private int[] Nbackground_true = null;
	private int[] Nsignal_false = null;
	private int[] Nbackground_false = null;
	private TCanvas rocCanvas = null;
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	//Rough estimate of variable importance:
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private boolean is_initialised = false;
	private double[] totalD = null;
	private double[] corrD = null;
	private double[] corrDNorm = null;
	private double[] finalDNorm = null;
	private double[] finalD = null;
	private int[] isVarActive = null;
	private int nEvents = 0;
	private Matrix CorrMatrix = null;
	private Matrix CorrMatrixNorm = null;
	private Vector tDVector = null;
	private Vector tDVectorCorr = null;
	private Vector tDVectorNorm = null;
	private TCanvas impCanvas = null;
	private double[] testSum = null;
	private H1F[] importanceHist = null;
	private H1F[] chi2Hist = null;
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	

	//Set and get the classifier features:
	//==============================================================================================================
	public void setCL(String usedModel, PipelineModel classifier) {
		this.usedModel = usedModel;
		this.classifier = classifier;
		this.isCLSet = false;
		
		if(usedModel.equals("MLP")) {
			this.mlpModel = (MultilayerPerceptronClassificationModel)(classifier.stages()[0]);
			this.MLPlayers = mlpModel.layers();
			this.MLPWeights = mlpModel.weights();
			this.isCLSet = true;
		}else if(usedModel.equals("GBT")) {
			this.gbtModel = (GBTClassificationModel)(classifier.stages()[0]);
			this.Trees = gbtModel.trees();
			this.GBTWeights = gbtModel.treeWeights();
			this.NTrees = gbtModel.getNumTrees();
			this.isCLSet = true;
		}else if(usedModel.equals("SVM")) {	
			setSvmModel((LinearSVCModel)(classifier.stages()[0]));
		}else if(usedModel.equals("kMEANS")) {
			this.kmeansModel = (KMeansModel)(classifier.stages()[0]);
		}else System.out.println("Sorry. The classifier you specified is not implemented or does not exist.");
	}
	
	//Get general information:
	//********************************************************
	public String getCLModel() {
		return usedModel;
	}
	
	public void setCLDir(String modelDir) {
		this.modelDir = modelDir;
	}
	
	public String getCLDir() {
		return modelDir;
	}
	
	public PipelineModel getCL(){
		return classifier;
	}
	//********************************************************
	
	//Get information about the MLP:
	//********************************************************
	public Vector getMLPWeights() {
		return MLPWeights;
	}
	
	public int[] getMLPLayers() {
		return MLPlayers;
	}
	
	public MultilayerPerceptronClassificationModel getMLP() {
		return mlpModel;
	}
	//********************************************************
	
	//Get information about the GBT:
	//********************************************************
    public double[] getGBTWeights() {
	   return GBTWeights;
	}
		
	public DecisionTreeRegressionModel[] getTrees() {
		return Trees;
	}
	
	public int getNTrees() {
		return NTrees;
	}
		
	public GBTClassificationModel getGBT() {
		return gbtModel;
	}
	
	public LinearSVCModel getSvmModel() {
		return svmModel;
	}

	public void setSvmModel(LinearSVCModel svmModel) {
		this.svmModel = svmModel;
	}
	//********************************************************
	//==============================================================================================================
	
	//Now get the probability of the (trained) classifier:
	//==============================================================================================================
	
	//Get the (outputs) for a MLP:
	//********************************************************
	public double[] mlpResponse(Vector inputVec, double Threshold) {
		double[] out = {0,0,0};
		
		//Explanation:
		//out[0] --> Default classifier response from Apache-Spark
		//out[1] --> Classifier Response translated to a continuous probability
		//out[2] --> Classifier response after a threshold cut
		
		//Get the default output from Apache-Spark:
		out[0] = mlpModel.predict(inputVec);
		
		//Get the probability-distribution:
		FeedForwardTopology FWT = FeedForwardTopology.multiLayerPerceptron(MLPlayers, true);
		//true --> Use softmax function: have a normalised prob.
		//false --> Turn off softmax and recieve an  unnormalised prob.
		TopologyModel tpModel = FWT.model(MLPWeights);
	    out[1] = ( (tpModel.predict(inputVec)).toArray() )[1];
		
		//Get a discriminated output according to a specified output:
		if(out[1] >= Threshold) {
			out[2] = 1.0;
		}else out[2] = 0.0;
		
		return out;
	}
	//********************************************************
	
	//Get the (outputs) for a BDT:
	//********************************************************
    public double[] bdtResponse(Vector inputVec, double Threshold) {
    	   double[] out = {0,0,0};
    	   
    	   //Explanation:
   	   //out[0] --> Default classifier response from Apache-Spark
   	   //out[1] --> Classifier Response translated to a continuous probability
   	   //out[2] --> Classifier response after a threshold cut
    	   
    	   //Default output from Apache-Spark:
    	   out[0] = gbtModel.predict(inputVec);
    	   
    	   //Continuous probability distribution:
    	   /* ---> This exactly doing the same
    	   double sum = 0;
	   for(int k=0;k<NTrees;k++) {
			  sum += Trees[k].predict(inputVec) * GBTWeights[k];	
	   }
       out[1] = 1/(1 + Math.exp(-sum*2) );
       */
    	   
    	   out[1] = gbtModel.predictProbability(inputVec).toArray()[1]; // as this thing!!!!!
       
       //Get a discriminated output according to a specified output:
    	   
   	   if(out[1] >= Threshold) {
   			out[2] = 1.0;
   		}else out[2] = 0.0;
    	   
    	   return out;
    }
    //********************************************************
    
  //Get the (outputs) for a SVM:
  	//********************************************************
      public double[] svmResponse(Vector inputVec, double Threshold) {
      	   double[] out = {0,0,0};
      	   
      	   //Explanation:
     	   //out[0] --> Default classifier response from Apache-Spark
     	   //out[1] --> Classifier Response translated to a continuous probability
     	   //out[2] --> Classifier response after a threshold cut
      	   
      	   //Default output from Apache-Spark:
      	   out[0] = svmModel.predict(inputVec);
      	 
      	   //Continuous probability distribution:
      	   double val1 = svmModel.predictRaw(inputVec).toArray()[0];
      	   double val2 = svmModel.predictRaw(inputVec).toArray()[1];
      	   
      	   out[1] = val2 / (val1 + val2);
      	   
     	   if(out[1] >= Threshold) {
     			out[2] = 1.0;
     		}else out[2] = 0.0;
      	   
      	   return out;
      }
      //********************************************************
      
     public Vector[] getkmeansCluster() {
    	   Vector[] out = kmeansModel.clusterCenters();
    	   return out;
     }
	
	//Get response depending on specified classifier:
    //********************************************************
	public double[]  getResponse(Vector inputVec, double Threshold) {
		double[] clOUTPUT = null;
		
		if(usedModel.equals("MLP")) {
			clOUTPUT = mlpResponse(inputVec, Threshold);
		}else if(usedModel.equals("GBT")) {
			clOUTPUT = bdtResponse(inputVec, Threshold);
		}if(usedModel.equals("SVM")) {
			clOUTPUT = svmResponse(inputVec, Threshold);
		}
		
		return clOUTPUT;	
	}
	//********************************************************
	
	//=================================================================================================================
	
	
	
	//Now scan several cut threshold to get the roc-curve:
	//=================================================================================================================
	//Some getters and setters first:
	//********************************************************
	public void setNSignalEvents(int NSignal_events) {
		this.NSignal_events = NSignal_events;
	}
	
	public int getNSignalEvents() {
		return NSignal_events;
	}
	
	public void setNBackgroundEvents(int NBackground_events) {
		this.NBackground_events = NBackground_events;
	}
	
	public int getNBackgroundEvents() {
		return NBackground_events;
	}
	
	public void setNsignal(int[] Nsignal) {
		this.Nsignal = Nsignal;
	}
	
	public int[] getNsignal() {
		return Nsignal;
	}
	
	public void setNbackground(int[] Nbackground) {
		this.Nbackground = Nbackground;
	}
	
	public int[] getNbackground() {
		return Nbackground;
	}
	
	public void setNsignalTrue(int[] Nsignal_true) {
		this.Nsignal_true = Nsignal_true;
	}
	
	public int[] getNsignalTrue() {
		return Nsignal_true;
	}
	
	public void setNbackgroundTrue(int[] Nbackground_true) {
		this.Nbackground_true = Nbackground_true;
	}
	
	public int[] getNbackgroundTrue() {
		return Nbackground_true;
	}
	
	public void setNsignalFalse(int[] Nsignal_false) {
		this.Nsignal_false = Nsignal_false;
	}
	
	public int[] getNsignalFalse() {
		return Nsignal_false;
	}
	
	public void setNbackgroundFalse(int[] Nbackground_false) {
		this.Nbackground_false = Nbackground_false;
	}
	
	public int[] getNbackgroundFalse() {
		return Nbackground_false;
	}
	//********************************************************
	
	
	//Set and reset the individual counters:
	//********************************************************
	public void setANDresetCounts(int Nsteps) {
		this.Nsteps = Nsteps;
		
	   	NSignal_events = NBackground_events = 0;
	   	NSignal_events_current = NBackground_events_current = 0;
	   	NSignal_true_current = NBackground_true_current = 0;
	   	NSignal_false_current = NBackground_false_current = 0;
	   	NSignal_current = NBackground_current = 0;
	   	
	   	currentSignalEfficiency = currentSignalPurity = 0.0;
	   	currentBackgroundEfficiency = currentBackgroundPurity = 0.0;
	   	
	   	Nsignal = new int[Nsteps];
	   	Nbackground = new int[Nsteps];
	   	Nsignal_true = new int[Nsteps];
	   	Nbackground_true = new int[Nsteps];
	   	Nsignal_false = new int[Nsteps];
	   	Nbackground_false = new int[Nsteps];
	   	
	   	for(int i=0;i<Nsteps;i++) {
	   		Nsignal[i] = Nbackground[i] = 0;
	   		Nsignal_true[i] = Nbackground_true[i] = 0;
	   		Nsignal_false[i] = Nbackground_false[i] = 0;
	   	}
	}
	
	//Get the steps for the scan:
	public int getNsteps() {
		return Nsteps;
	}

	//And dump everything:
	public void clearCounts() {
		Nsignal = null;
	   	Nbackground = null;
	   	Nsignal_true = null;
	   	Nbackground_true = null;
	   	Nsignal_false = null;
	   	Nbackground_false = null;
	}
	//********************************************************
	
	//Calculate efficiency and purity for one specified cut:
	//********************************************************
	public void calcCurrentRoc(Vector inVec, int Label, double cut) {
		this.currentCut = cut;
		
		double[] vars = inVec.toArray();
		int Nvars = vars.length;
		
		double[] smear_vars = new double[Nvars];
		Random rnd = new Random();
		double smear = 0.0;
		for(int t=0;t<Nvars;t++) {
			smear_vars[t] = vars[t] + rnd.nextGaussian()*smear*vars[t];
		}
		
		Vector inputVec = Vectors.dense(smear_vars);
		
		if(Label == 1) {
			   NSignal_events_current++;
		}else NBackground_events_current++;
		
		double cl_output = ( getResponse(inputVec, cut) )[2];
		
		 if(cl_output == 1) {
  		   NSignal_current++;
  	   }else NBackground_current++;
  	   
  	   if(Label == 1) {
  		   if(cl_output == 1) {
  			   NSignal_true_current++;
  		   }else NBackground_false_current++;
  	   }else {
  		   if(cl_output == 0) {
  			 NBackground_true_current++;
  		   }else NSignal_false_current++;
  	   }
		
	}
	
	//==================================================
	
	public double getCurrentSignalEfficiency() {
		this.currentSignalEfficiency = (double)NSignal_true_current / (double)NSignal_events_current*100;
		return currentSignalEfficiency;
	}
	
	//==================================================
	
	public double getCurrentSignalPurity() {
		this.currentSignalPurity = (double)NSignal_true_current / (double)NSignal_current*100;
		return currentSignalPurity;
	}
	
	//==================================================
	
	public double getCurrentSignalFPNR() {
		this.currentSignalFPNR = (double)NSignal_false_current / (double)NBackground_events_current*100;
		return currentSignalFPNR;
	}
	
	//==================================================
	
	public double getCurrentBackgroundFPNR() {
		this.currentBackgroundFPNR = (double)NBackground_false_current / (double)NSignal_events_current*100;
		return currentBackgroundFPNR;
	}
	
	//==================================================
	
	public double getDistSig() {
		return Math.sqrt( Math.pow(100-currentSignalEfficiency, 2) + Math.pow(100-currentSignalPurity,2)  );
	}
	
	//==================================================
	
		public double getCurrentBackgroundEfficiency() {
			this.currentBackgroundEfficiency = (double)NBackground_true_current / (double)NBackground_events_current*100;
			return currentBackgroundEfficiency;
		}
		
		//==================================================
		
		public double getCurrentBackgroundPurity() {
			this.currentBackgroundPurity = (double)NBackground_true_current / (double)NBackground_current*100;
			return currentBackgroundPurity;
		}
		
		//==================================================
		
		public double getDistBkg() {
			return Math.sqrt( Math.pow(100-currentBackgroundEfficiency, 2) + Math.pow(100-currentBackgroundPurity,2)  );
		}
	
		//==================================================
		
		public double getRecRatio() {
			return (double) NBackground_current / (double)NSignal_current;
		}
		
		//==================================================
		
		public double getTrueRatio() {
			return (double)NBackground_events_current / (double)NSignal_events_current;
		}
		
		//==================================================
		
		public void showCurrentPerformance() {
			System.out.println("Performance of a " + usedModel + " classifier with a probability cut of: " + currentCut);
			System.out.println("Signal Efficiency: " + getCurrentSignalEfficiency() + " Signal Purity: " + getCurrentSignalPurity() + " with distance to optimum: " + getDistSig() + " and false positive rate: " + getCurrentSignalFPNR());
			System.out.println("Background Efficiency: " + getCurrentBackgroundEfficiency() + " Background Purity: " + getCurrentBackgroundPurity() + " with distance to optimum: " + getDistBkg() + " and false negative rate: " + getCurrentBackgroundFPNR());
			System.out.println("Reconstructed ratio N(B)/N(S): " + getRecRatio());
			System.out.println("True ratio N(B)/N(S): " + getTrueRatio());
		}
		
	//********************************************************
	
	
	
	//Simply scan the probability distribution and count events per probability range:
	//********************************************************
	public void scanANDaccumulate(Vector inVec, int Label) {
		if(Label == 1) {
		   NSignal_events++;
		}else NBackground_events++;
		
		double[] vars = inVec.toArray();
		int Nvars = vars.length;
		
		double[] smear_vars = new double[Nvars];
		Random rnd = new Random();
		double smear = 0.0;
		for(int t=0;t<Nvars;t++) {
			smear_vars[t] = vars[t] + rnd.nextGaussian()*smear*vars[t];
		}
		
		Vector inputVec = Vectors.dense(smear_vars);
		
		double scan_thresh = 0.0;
		double cl_output = 0.0;
		double step = 0.95/(double)Nsteps;
		
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    for(int i=0;i<Nsteps;i++) {
	    	   scan_thresh = step*(i+1);
	    	  
	    	   cl_output = ( getResponse(inputVec, scan_thresh) )[2];
	    	   
	    	 //  System.out.println("Step: " + scan_thresh + " output: " + cl_output + "  Label: " + Label);
	    	
	    	   if(cl_output == 1) {
	    		   Nsignal[i]++;
	    	   }else Nbackground[i]++;
	    	   
	    	   if(Label == 1) {
	    		   if(cl_output == 1) {
	    			   Nsignal_true[i]++;
	    		   }else Nbackground_false[i]++;
	    	   }else {
	    		   if(cl_output == 1) {
	    			   Nsignal_false[i]++;
	    		   }else Nbackground_true[i]++;
	    	   }
	    }
	   //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
	}
	//********************************************************
	
	//Now get parameters for the ROC-CURVE:
	//********************************************************
	//The efficiency:
	public double[][] getEfficiency() {
		double[][] out = null;
		out = new double[2][Nsteps];
		
		for(int step=0;step<Nsteps;step++) {
			//Signal first:
			out[0][step] =  ((double)Nsignal_true[step] / (double)NSignal_events)*100;
			//Now the Background:
			out[1][step] =  ((double)Nbackground_true[step] / (double)NBackground_events)*100;
			
			//System.out.println("Efficiency: " + out[0][step] + "   "  + out[1][step]);
		}
		
		return out;
	}
	//********************************************************
	
	//The purity:
	//********************************************************
	public double[][] getPurity() {
	   double[][] out = null;
	   out = new double[2][Nsteps];
			
	   for(int step=0;step<Nsteps;step++) {
		   //Signal first:
		   out[0][step] =  ((double)Nsignal_true[step] / (double)Nsignal[step])*100;
		   //Now the Background:
		   out[1][step] =  ((double)Nbackground_true[step] / (double)Nbackground[step])*100;
		   
		 //  System.out.println("Purity: " + out[0][step] + "   "  + out[1][step]);
		}
			
		return out;
	}
	//********************************************************
	
	//False false positive/negative rate:
	//********************************************************
	public double[][]getFPNR(){
		double out[][] = null;
		out = new double[2][Nsteps];
		
		for(int step=0;step<Nsteps;step++) {
			//False positive rate first: --> In correlation with the signal efficiency:
			out[0][step] = ((double)Nsignal_false[step] / (double) NBackground_events)*100;
			
			//False negative rate: --> In correlation with the background efficiency:
			out[1][step] = ((double)Nbackground_false[step] / (double) NSignal_events)*100;
			
			//System.out.println("FPNR: " + out[0][step] + "   "  + out[1][step]);
		}
		return out;
	}
	//********************************************************
	
	//Get the roc-curve parameters:
	//********************************************************
	public GraphErrors[] getROCPars(String Option) {
		GraphErrors[] graph = new GraphErrors[2];
		
		graph[0] = new GraphErrors();
		graph[1] = new GraphErrors();
		
		double[][]eff = getEfficiency();
		double [][]pur = getPurity();
		double [][] fpnr = getFPNR();
		
		if(Option.equals("Efficiency_vs_Purity")) {
		  for(int k=0;k<Nsteps;k++) {
		     	graph[0].addPoint(pur[0][k], eff[0][k], 0.0, 0.0);
			    graph[1].addPoint(pur[1][k], eff[1][k], 0.0, 0.0);
		  }
		
		  graph[0].setTitle("ROC-Curve for the Signal");
		  graph[0].setTitleX("Purity [%]");
		  graph[0].setTitleY("Efficiency [%]");
		
		  graph[1].setTitle("ROC-Curve for the Background");
		  graph[1].setTitleX("Purity [%]");
		  graph[1].setTitleY("Efficiency [%]");
		}else if(Option.equals("Efficiency_vs_FPNR")) {
		  for(int k=0;k<Nsteps;k++) {
		     	graph[0].addPoint(fpnr[0][k], eff[0][k], 0.0, 0.0);
			    graph[1].addPoint(fpnr[1][k], eff[1][k], 0.0, 0.0);
		  }
		
		  graph[0].setTitle("ROC-Curve for the Signal");
		  graph[0].setTitleX("False Positive Rate [%]");
		  graph[0].setTitleY("Efficiency [%]");
		
		  graph[1].setTitle("ROC-Curve for the Background");
		  graph[1].setTitleX("False Negative Rate [%]");
		  graph[1].setTitleY("Efficiency [%]");
		}
		return graph;
	}
	//********************************************************
	
	//Set canvas and show the final results:
	//********************************************************
	//Now get the final roc-curve:
	public void getROC(String Option) throws FileNotFoundException {
		if(Option.equals("Plot") || Option.equals("PlotAndText") || Option.equals("PlotAndSaveText")) {
		GraphErrors[] gr1 = null;
		gr1 = getROCPars("Efficiency_vs_Purity");
		
		GraphErrors[] gr2 = null;
		gr2 = getROCPars("Efficiency_vs_FPNR");
		
		 String canvasName = getClassifierResponseName() + ": Roc-Curves";
		  
		  rocCanvas = new TCanvas(canvasName,1200,600);
		  rocCanvas.divide(2, 2);
		  rocCanvas.cd(0);
		  rocCanvas.draw(gr1[0]);
		  rocCanvas.cd(1);
		  rocCanvas.draw(gr1[1]);
		  rocCanvas.cd(2);
		  rocCanvas.draw(gr2[0]);
		  rocCanvas.cd(3);
		  rocCanvas.draw(gr2[1]);
		  
		  gr1 = null;
		  gr2 = null;
		}
		
		if(Option.equals("Text") || Option.equals("SaveText") || Option.equals("PlotAndText") || Option.equals("PlotAndSaveText") || Option.equals("GetMCC")) {
			int NrocPoints = Nsteps;
			
			double[][] eff = getEfficiency();
			double[][] pur = getPurity();
			double[][] fpnr = getFPNR();
			
			double probSteps = 0.95/(double)NrocPoints;
			double currentProb;
			
			if(Option.equals("PlotAndSaveText") || Option.equals("SaveText")) {
			  String outFileName = modelDir + "/ROCCurves_" + usedModel + ".txt";
			  PrintWriter writer = new PrintWriter(outFileName);
			
			  writer.println("   ");
			  writer.println(getClassifierResponseName());
			  writer.println("   ");
			  
			  writer.println("*****************************************************");
			  writer.println("*                                                   *");
			  writer.println("*         ROC-CURVES FOR THE " + usedModel + " CLASSIFIER         *");
			  writer.println("*                                                   *");
			  writer.println("*****************************************************");
			
			  writer.println("   ");
			  writer.println("ROC-Curve: Efficiency vs. Purity");
			  writer.println("================================");
			
			  writer.println("   ");
			  writer.println("Results for the signal:");
			  writer.println("   ");
			
			  for(int i=0;i<NrocPoints;i++) {
				currentProb = probSteps*(i+1);
				writer.println("EFFVSPURSIG: Probability: " + currentProb + " Efficiency: " + eff[0][i] + " [%] vs.  Purity: " + pur[0][i] + " [%]");
			  }
			
			  writer.println("   ");
			  writer.println("Results for the background:");
			  writer.println("   ");
			
			  for(int i=0;i<NrocPoints;i++) {
				currentProb = probSteps*(i+1);
				writer.println("EFFVSPURBKG: Probability: " + currentProb + " Efficiency: " + eff[1][i] + " [%] vs. Purity: " + pur[1][i] + " [%]");
			  }
			
			  writer.println("   ");
			  writer.println("ROC-Curve: Efficiency vs. False Positve/Negative Rate");
			  writer.println("=====================================================");
			
			  writer.println("   ");
			  writer .println("Results for the signal:");
			  writer.println("   ");
			
			  for(int i=0;i<NrocPoints;i++) {
				currentProb = probSteps*(i+1);
				writer.println("EFFVSFPNRSIG: Probability: " + currentProb + " Efficiency: " + eff[0][i] + " [%] vs. False Positive Rate: " + fpnr[0][i] + " [%]");
			  }
			
			  writer.println("   ");
			  writer.println("Results for the background:");
			  writer.println("   ");
			
			  for(int i=0;i<NrocPoints;i++) {
				currentProb = probSteps*(i+1);
				writer.println("EFFVSFPNRBKG: Probability: "  + currentProb + " Efficiency: " + eff[1][i] + " [%] vs. False Negative Rate: " + fpnr[1][i] + " [%]");
			  }
			
			  writer.println("   ");
			  writer.println("Determine MCC-Coefficient:   ");
			  writer.println("   ");
			  
			  double mcc_coefficient = -2;
			  double max_mcc_coeff = -2;
			  double found_mcc_cut = -1;
			  double found_mcc_eff = -1;
			  double found_mcc_pur = -1;
			  
			  for(int i=0;i<NrocPoints;i++) {
					currentProb = probSteps*(i+1);
					mcc_coefficient = (eff[0][i]*eff[1][i]-fpnr[0][i]*fpnr[1][i]) / (Math.sqrt(eff[0][i]/pur[0][i] * eff[1][i]/pur[1][i]))*0.01*0.01;
					writer.println("Probability cut: " + currentProb + " MCC-Coefficient: " + mcc_coefficient);
					if(mcc_coefficient > max_mcc_coeff) {
						max_mcc_coeff = mcc_coefficient;
						found_mcc_cut = currentProb;
						found_mcc_eff = eff[0][i];
						found_mcc_pur = pur[0][i];
					}
			  }
			  writer.println("   ");
			  writer.println("Found larges MCC-Coefficient:  " + max_mcc_coeff + " at Probability Cut: " + found_mcc_cut + " with Efficency: " + found_mcc_eff  + " and Purity: " + found_mcc_pur);
			  
			  
			  writer.println("   ");
			  writer.println("************************ FIN ************************");
			  writer.close();
			}
			
			if(Option.equals("Text") || Option.equals("PlotAndText")) {
			  System.out.println("   ");
			  System.out.println(getClassifierResponseName());
			  System.out.println("   ");	
				
			  System.out.println("*****************************************************");
			  System.out.println("*                                                   *");
			  System.out.println("*         ROC-CURVES FOR THE " + usedModel + " CLASSIFIER         *");
			  System.out.println("*                                                   *");
			  System.out.println("*****************************************************");
			
			  System.out.println("   ");
			  System.out.println("ROC-Curve: Efficiency vs. Purity");
			  System.out.println("================================");
			
			  System.out.println("   ");
			  System.out.println("Results for the signal:");
			  System.out.println("   ");
			
			  for(int i=0;i<NrocPoints;i++) {
				currentProb = probSteps*(i+1);
				System.out.println("EFFVSPURSIG: Probability: " + currentProb + " Efficiency: " + eff[0][i] + " [%] vs.  Purity: " + pur[0][i] + " [%]");
			  }
			
			  System.out.println("   ");
			  System.out.println("Results for the background:");
			  System.out.println("   ");
			
			  for(int i=0;i<NrocPoints;i++) {
				currentProb = probSteps*(i+1);
				System.out.println("EFFVSPURBKG: Probability: " + currentProb + " Efficiency: " + eff[1][i] + " [%] vs. Purity: " + pur[1][i] + " [%]");
			  }
			
			  System.out.println("   ");
			  System.out.println("ROC-Curve: Efficiency vs. False Positve/Negative Rate");
			  System.out.println("=====================================================");
			
		  	  System.out.println("   ");
		      System.out.println("Results for the signal:");
			  System.out.println("   ");
			
			  for(int i=0;i<NrocPoints;i++) {
				currentProb = probSteps*(i+1);
				System.out.println("EFFVSFPNRSIG: Probability: " + currentProb + " Efficiency: " + eff[0][i] + " [%] vs. False Positive Rate: " + fpnr[0][i] + " [%]");
			  }
			
			  System.out.println("   ");
			  System.out.println("Results for the background:");
			  System.out.println("   ");
			
			  for(int i=0;i<NrocPoints;i++) {
				currentProb = probSteps*(i+1);
				System.out.println("EFFVSFPNRBKG: Probability: "  + currentProb + " Efficiency: " + eff[1][i] + " [%] vs. False Negative Rate: " + fpnr[1][i] + " [%]");
			  }
			  
			  System.out.println("   ");
			  System.out.println("Determine MCC-Coefficient:   ");
			  System.out.println("   ");
			  
			  double mcc_coefficient = -2;
			  double max_mcc_coeff = -2;
			  double found_mcc_cut = -1;
			  double found_mcc_eff = -1;
			  double found_mcc_pur = -1;
			  
			  for(int i=0;i<NrocPoints;i++) {
					currentProb = probSteps*(i+1);
					mcc_coefficient = (eff[0][i]*eff[1][i]-fpnr[0][i]*fpnr[1][i]) / (Math.sqrt(eff[0][i]/pur[0][i] * eff[1][i]/pur[1][i]))*0.01*0.01;
					System.out.println("Probability cut: " + currentProb + " MCC-Coefficient: " + mcc_coefficient);
					if(mcc_coefficient > max_mcc_coeff) {
						max_mcc_coeff = mcc_coefficient;
						found_mcc_cut = currentProb;
						found_mcc_eff = eff[0][i];
						found_mcc_pur = pur[0][i];
					}
			  }
			  System.out.println("   ");
			  System.out.println("Found larges MCC-Coefficient:  " + max_mcc_coeff + " at Probability Cut: " + found_mcc_cut + " with Efficency: " + found_mcc_eff  + " and Purity: " + found_mcc_pur);
			  
			  System.out.println("   ");
			  System.out.println("************************ FIN ************************");
			}
			
			if(Option.equals("GetMCC")) {
				 System.out.println("   ");
				 System.out.println(getClassifierResponseName());
				 System.out.println("   ");	
				 
				 System.out.println("Determine MCC-Coefficient:   ");
				  System.out.println("   ");
				  
				  double mcc_coefficient = -2;
				  double max_mcc_coeff = -2;
				  double found_mcc_cut = -1;
				  double found_mcc_eff = -1;
				  double found_mcc_pur = -1;
				  
				  for(int i=0;i<NrocPoints;i++) {
						currentProb = probSteps*(i+1);
						mcc_coefficient = (eff[0][i]*eff[1][i]-fpnr[0][i]*fpnr[1][i]) / (Math.sqrt(eff[0][i]/pur[0][i] * eff[1][i]/pur[1][i]))*0.01*0.01;
						if(mcc_coefficient > max_mcc_coeff) {
							max_mcc_coeff = mcc_coefficient;
							found_mcc_cut = currentProb;
							found_mcc_eff = eff[0][i];
							found_mcc_pur = pur[0][i];
						}
				  }
				  System.out.println("   ");
				  System.out.println("Found larges MCC-Coefficient:  " + max_mcc_coeff + " at Probability Cut: " + found_mcc_cut + " with Efficency: " + found_mcc_eff  + " and Purity: " + found_mcc_pur);
				  
				  System.out.println("   ");
			}
			
			
			
			eff = null;
			pur = null;
			fpnr = null;
		
		}
		clearCounts();
		
		
	}
	//********************************************************
	//=================================================================================================================
	
	//Try (again) to estimate the importance of each classification variable:
	//=================================================================================================================
	//Set and reset derivatives:
	//********************************************************
	public void setAndresetD(int Nvars) {
	
		nEvents = 0;
		totalD = new double[Nvars];
		corrD = new double[Nvars*Nvars];
		corrDNorm = new double[Nvars*Nvars];
		finalDNorm = new double[Nvars];
		finalD = new double[Nvars];
		isVarActive = new int[Nvars];
		testSum = new double[Nvars];
		
		for(int i=0;i<Nvars*Nvars;i++) {
			if(i < Nvars) {
			  totalD[i] = 0.0;
			  testSum[i] = 0.0;
			  finalDNorm[i] = finalD[i] = 0.0;
			  isVarActive[i] = 0;
			}
			corrD[i] = corrDNorm[i] = 0.0;
		}
	}
	//********************************************************
	
	
	public void resetD(int Nvars) {
		nEvents = 0;
		for(int i=0;i<Nvars*Nvars;i++) {
			if(i < Nvars) {
			  totalD[i] = 0.0;
			  finalDNorm[i] = finalD[i] = 0.0;
			  isVarActive[i] = 0;
			}
			corrD[i] = corrDNorm[i] = 0.0;
		}
	}
	
	
	public boolean areVecSim(Vector vec1, Vector vec2) {
		boolean out = false;
		
		int Nvars = vec1.toArray().length;
		double var1,var2;
		for(int t=0;t<Nvars;t++) {
			var1 = vec1.toArray()[t];
			var2 = vec2.toArray()[t];
			if(var1==var2)out = true;
		}
		
		return out;
	}

	//Calculate derivatives:
	//********************************************************
	public void calcD(Vector vec1, Vector vec2) {
		
		if(!areVecSim(vec1,vec2)) {
		
	  	  double[] var1 = vec1.toArray();
		  double[] var2 = vec2.toArray();
		
		  double prob1,prob2;
		  prob1 = getResponse(vec1,-1)[1];
		  prob2 = getResponse(vec2,-1)[1];
		
		  int Nvars = var1.length;
		  int counter = 0;
		  nEvents++;
		
		  for(int i=0;i<Nvars;i++) {
	        
		     
		  	   totalD[i] = (prob2 - prob1) / (var2[i] - var1[i]);
			   isVarActive[i]++;
		     
			   for(int j=0;j<Nvars;j++) {
				  if(i == j) {
					corrD[counter] = corrDNorm[counter] = 1.0;
				  }else {
					corrD[counter] = (var2[i] - var1[i]) / (var2[j] - var1[j]);
					corrDNorm[counter] = Math.abs(corrD[counter]);
				  }
				  counter++;  
			   }
		  }
		
		CorrMatrix = Matrices.dense(Nvars, Nvars, corrD);
		tDVector = Vectors.dense(totalD);
		tDVectorCorr = (CorrMatrix.transpose()).multiply(tDVector);
		
		//Now do the normalisation of the final vector
		//according to all entries in the total differential
		for(int k=0;k<Nvars;k++) {
			finalDNorm[k] = Math.abs( (tDVectorCorr.toArray())[k] );
		}
		
		CorrMatrixNorm = Matrices.dense(Nvars, Nvars, corrDNorm);
		Vector someVector = Vectors.dense(finalDNorm);
		tDVectorNorm = CorrMatrixNorm.multiply(someVector);
		
		}
		
		}
		
	
	//********************************************************
	
	//Get the final (normalised) derivative:
	//********************************************************
	public void sumImp(Vector vec1, Vector vec2) {
		
		if(!areVecSim(vec1,vec2)) {
		calcD(vec1,vec2);
		
		int Nvars = finalD.length;
		double vectorNorm = 0.0;
		double someMin = 10000;
		for(int z=0;z<Nvars;z++) {
			vectorNorm += Math.abs(tDVector.toArray()[z]);
			
			if(Math.pow(tDVector.toArray()[z],2)<someMin) {
				someMin = Math.pow(tDVector.toArray()[z],2);
			}
			
		}
		
		for(int h=0;h<Nvars;h++) {
			if((tDVectorCorr.toArray())[h]!=0) {
				chi2Hist[h].fill( Math.pow((tDVectorCorr.toArray())[h], 2)      );
				importanceHist[h].fill((tDVectorCorr.toArray())[h]);
			}
			finalD[h] += (tDVectorCorr.toArray())[h];
		}
		
		}
	}
	//********************************************************
	
	public void setHists() {
		importanceHist = new H1F[5];
		
		importanceHist[0] = new H1F("impVar1",100,-20,20);
		importanceHist[1] = new H1F("impVar2",100,-20,20);
		importanceHist[2] = new H1F("impVar3",100,-20,20);
		importanceHist[3] = new H1F("impVar4",100,-20,20);
		importanceHist[4] = new H1F("impVar5",100,-20,20);
		
		importanceHist[0].setTitleX("Importance Momentum");
		importanceHist[1].setTitleX("Importance LTCC");
		importanceHist[2].setTitleX("Importance HTCC");
		importanceHist[3].setTitleX("Importance ECin");
		importanceHist[4].setTitleX("Importance ECout");
		
		
		chi2Hist = new H1F[5];
		chi2Hist[0] = new H1F("chi2Var1",100,0,100);
		chi2Hist[1] = new H1F("chi2Var2",100,0,100);
		chi2Hist[2] = new H1F("chi2Var3",100,0,100);
		chi2Hist[3] = new H1F("chi2Var4",100,0,100);
		chi2Hist[4] = new H1F("chi2Var5",100,0,100);
		
		chi2Hist[0].setTitleX("Chi2 Momentum");
		chi2Hist[1].setTitleX("Chi2 LTCC");
		chi2Hist[2].setTitleX("Chi2 HTCC");
		chi2Hist[3].setTitleX("Chi2 ECin");
		chi2Hist[4].setTitleX("Chi2 ECout");
		
	}
	
	
	//********************************************************
	public void finSum(int Niterations) {
		int Nvars = finalD.length;
		
		double currentMax = 0.0;
		for(int i=0;i<Nvars;i++) {
			if(Math.abs(finalD[i]) > currentMax) {
				currentMax = Math.abs(finalD[i]);
			}
		}
		
		for(int h=0;h<Nvars;h++) {
			testSum[h] += finalD[h] / (nEvents*Niterations);
		}
	}
	//********************************************************
	
	

	
	//Translate the results into a graph:
	//********************************************************
	public GraphErrors getImp() {
		GraphErrors gr = new GraphErrors();
		
		int Nvars = testSum.length;
		double[] var = new double[Nvars];
		
		double arg,currentMax;
		currentMax = arg = 0.0;
		for(int h=0;h<Nvars;h++) {
			arg = testSum[h];
			
			if(arg > currentMax) {
				currentMax = arg;
			}
		}
		
		
		double sumDiff = 0.0;
		
		for(int i=0;i<Nvars;i++) {
			var[i] = i+1;
			
			sumDiff = testSum[i] / currentMax;
			
			System.out.println("Found importance: " + sumDiff);
			
			gr.addPoint(var[i],sumDiff, 0.0, 0.0);
		}
		
		gr.setTitle("Variable Importance");
		gr.setTitleX("Variable");
		gr.setTitleY("Score");
		
		return gr;
	}
	//********************************************************
	
	//Now plot every thing:
	//********************************************************
	public void showImp() {
		 //evalImp();
		
		 impCanvas = new TCanvas("impCanvas",500,500);
		 impCanvas.draw(getImp());
		 
		 
		 totalD = null;
		 corrD = null;
		 finalD = null;
		 is_initialised = false;
		 
		 
		 
		 TCanvas c = new TCanvas("c",1200,600);
		 c.divide(3, 2);
		 c.cd(0);
		 c.draw(importanceHist[0]);
		 c.cd(1);
		 c.draw(importanceHist[1]);
		 c.cd(2);
		 c.draw(importanceHist[2]);
		 c.cd(3);
		 c.draw(importanceHist[3]);
		 c.cd(4);
		 c.draw(importanceHist[4]);
		 
		 TCanvas c2 = new TCanvas("c2",1200,600);
		 c2.divide(3, 2);
		 c2.cd(0);
		 c2.draw(chi2Hist[0]);
		 c2.cd(1);
		 c2.draw(chi2Hist[1]);
		 c2.cd(2);
		 c2.draw(chi2Hist[2]);
		 c2.cd(3);
		 c2.draw(chi2Hist[3]);
		 c2.cd(4);
		 c2.draw(chi2Hist[4]);
		 
		 
	}
	//********************************************************

	public String getClassifierResponseName() {
		return ClassifierResponseName;
	}

	public void setClassifierResponseName(String classifierResponseName) {
		ClassifierResponseName = classifierResponseName;
	}


	//=================================================================================================================
	
	
	
	
	
	
	
}
