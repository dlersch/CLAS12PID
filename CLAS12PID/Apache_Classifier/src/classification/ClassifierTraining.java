package classification;

//Program to train a specified classifier
//on a given data set
//All parameters such as the variables, the classification algorithm
//or the number of iterations are read in by a configuration file
//Last date worked on: 10/16/17
//Written by: Daniel Lersch d.lersch@fz-juelich.de

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.spark.ml.Model;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.param.ParamPair;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.ui.TCanvas;

public class ClassifierTraining{

	//Stuff for reading in the training data:
	//++++++++++++++++++++++++++++++++++++++
	private String DataDir = null;
	private String BaseDir = null;
	private String nameTrainingData = null;
	private int nJasonFiles = 0;
	private String[] pidVars = null;
	//++++++++++++++++++++++++++++++++++++++
	
	//Stuff for using spark:
	//++++++++++++++++++++++++++++++++++++++
	private SparkSetter SPS = new SparkSetter();
	private int NThreads = 0;
	//++++++++++++++++++++++++++++++++++++++
	
	//Stuff for setting the training and testing data set:
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private String trainingMode = null;
	private int splitPercentage = 0;
	private Dataset<Row> inputData = null;
	private Dataset<Row> trainingData = null;
	private Dataset<Row> testData = null;
	private String classifierOutputDir = null;
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff to load the specified classifier:
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private String classifierType = null;
	private String classifierName = null;
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff for further evaluation of the classifier:
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private ClassifierResponse CR = new ClassifierResponse();
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	//Stuff for training and parameter grid search
	//--> Supposed to find the best classifier parameters:
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private String[] pidVarCombinations = null;
	private int[] trainingIntervals = null;
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	
	public static void main(String[] args) throws IOException {
		FileInputStream input = new FileInputStream("/Users/daniellersch/Desktop/CLAS/NN_studies/Apache_Classifier/testTrainConf.txt");
		
		//FileInputStream input = new FileInputStream(args[0]);
		new ClassifierTraining(input);
	}
	
	
	public ClassifierTraining(FileInputStream fstream) throws IOException {
		System.out.println("Start classifier training...");
		System.out.println("                            ");
		
		System.out.println("Load configuration parameter...");
		getConfPars(fstream);
		System.out.println("...done!");
		System.out.println("        ");
		
		System.out.println("Load Apache-Spark-Session...");
		loadSpark();
		System.out.println("...done!");
		System.out.println("        ");
		
		System.out.println("Load and set data for training and testing...");
		setCLTrainingData();
		System.out.println("...done!");
		System.out.println("        ");
		
		System.out.println("Perform training...");
		if(trainingMode.equals("fast")) {
		  doTraining();
		}else doTrainingAndMonitor();
		System.out.println("...done!");
		System.out.println("        ");
		
		System.out.println("Perform testing...");
        doTesting();
		System.out.println("...done!");
		System.out.println("        ");
		
		System.out.println("End Apache-Spark-Session...");
		endSpark();
		System.out.println("...done! ");
		System.out.println("        ");
		
		System.out.println("...Finished classifier training. Have fun and a nice day!");
	}
	
	
	//Get the json-file with the training-data:
	//********************************************************************
	public String getBaseDir() {
		return BaseDir;
	}

	//==========================================

	public void setBaseDir(String baseDir) {
		BaseDir = baseDir;
	}
	
	//==========================================
	
	public String getDataDir() {
		return DataDir;
	}
	
	//==========================================
	
	public void setDataDir(String dataDir) {
		DataDir = dataDir;
	}
	
	//==========================================
	
	public String getNameTrainingData() {
		return nameTrainingData;
	}
	
	//==========================================
	
	public void setNameTrainingData(String nameTrainingData) {
		this.nameTrainingData = nameTrainingData;
	}
	
	//==========================================
	
	public int getnJasonFiles() {
		return nJasonFiles;
	}
	
	//==========================================
	
	public void setnJasonFiles(int nJasonFiles) {
		this.nJasonFiles = nJasonFiles;
	}
	
	//==========================================
	
	public String[] getPidVars() {
		return pidVars;
	}
	
	//==========================================

	public void setPidVars(String[] pidVars) {
		this.pidVars = pidVars;
	}
	//********************************************************************
	
	//Set spark:
	//********************************************************************
	public void loadSpark() {
		SPS.setSparkSession("ClassifierTraining",getNThreads());
	}
	
	//==========================================
	
	public void endSpark() {
		SPS.stopSpark();
	}
	//********************************************************************

	//Set up the training data set:
	//********************************************************************
	public void setCLTrainingData() {
		SPS.getPIDDataFromFile(nameTrainingData, nJasonFiles, pidVars);
	    setInputData(SPS.getDataSet());	
	    
	    double percent_train = 0.01*splitPercentage;
        double percent_test = 1.0 - percent_train;
        
        System.out.println("");
        System.out.println("     Using " + 100*percent_train + "% of the data set for training");
        System.out.println("     Using " + 100*percent_test + "% of the data set for testing");
        System.out.println("");
         
        Dataset<Row>[] split = inputData.orderBy(functions.rand()).randomSplit(new double[]{percent_train, percent_test}, 1234L);
        //Just do some shuffling of the data parts:
         setTrainingData(  split[0].orderBy(functions.rand())  );
         setTestData(  split[1].orderBy(functions.rand())  ); 
	}
	
	//==========================================
	
	public int getSplitPercentage() {
		return splitPercentage;
	}
	
	//==========================================
	
	public void setSplitPercentage(int splitPercentage) {
		this.splitPercentage = splitPercentage;
	}
	
	//==========================================

	public Dataset<Row> getInputData() {
		return inputData;
	}
	
	//==========================================

	public void setInputData(Dataset<Row> inputData) {
		this.inputData = inputData;
	}
	
	//==========================================
	
	public Dataset<Row> getTrainingData() {
		return trainingData;
	}
	
	//==========================================

	public void setTrainingData(Dataset<Row> trainingData) {
		this.trainingData = trainingData;
	}
	
	//==========================================

	public Dataset<Row> getTestData() {
		return testData;
	}
	
	//==========================================

	public void setTestData(Dataset<Row> testData) {
		this.testData = testData;
	}
	//********************************************************************

	//Train and test the specified classifier and save it afterwards:
	//********************************************************************
	public void doTraining() throws IOException {
		SPS.setnTrainingEpochs(SPS.getnIterations());
		SPS.trainClassifier(trainingData,classifierType);
		
		setClassifierOutputDir(BaseDir + classifierName);
		System.out.println("       ");
		System.out.println("     Classifier will be stored at: " + getClassifierOutputDir());
		System.out.println("       ");
		SPS.saveClassifier(classifierOutputDir);
	}
	
	//==========================================
	
	public void doTrainingAndMonitor() throws IOException {
		int nEpochs = 5;
		int nInterEpochs = SPS.getnIterations() / nEpochs;
		SPS.setnTrainingEpochs(nInterEpochs);
		SPS.trainClassifierPerEpoch(trainingData, classifierType, nEpochs,0.01*splitPercentage);
		
		double[] metrics = SPS.getTestModel().validationMetrics();
		
		for(int h=0;h<metrics.length;h++) {
			double err = 1.0 - metrics[h];
			System.out.println("Current stage: " + h + " kind of error: " + err);
		}
		
		setClassifierOutputDir(BaseDir + classifierName);
		System.out.println("       ");
		System.out.println("     Classifier will be stored at: " + getClassifierOutputDir());
		System.out.println("       ");
		SPS.saveClassifier(classifierOutputDir);
	}
	
	//==========================================
	
	public void doTesting() throws FileNotFoundException {
		if(  !(SPS.getClType().equals("kMEANS"))  ) {
		  System.out.println("     ");
		  System.out.println("     Get classifier performance parameters:");
		  System.out.println("     ");
	      SPS.testClassifier(testData);	
	   
	      System.out.println("     ACCURACY: " + SPS.getAccuracyClassifier());
          System.out.println("     ROC CURVE: " + SPS.getRocClassifier().collect());
          System.out.println("     AREA UNDER ROC: " + SPS.getAreaUnderRoc());
          System.out.println("     PRECISION BY THRESHOLD: " + SPS.getPrecisionClassifier().collect());
          System.out.println("     ");
        
          System.out.println("     Calculate ROC-curves...");
          goForRoc(testData);
          System.out.println("     ...done!");
          System.out.println("     ");
		}else System.out.println("You used k-means clustering. No conventional ROC-Curve possible.");
	}
	
	//==========================================
	
	public void goForRoc(Dataset<Row> someSet) throws FileNotFoundException {
	   CR.setClassifierResponseName("Classifier Response after Training");	
	   CR.setCL(classifierType, SPS.getClassifier());	
	   CR.setANDresetCounts(20);
	   
	   Vector inputVec;
	   long oldVal;
	   int Label;
	   for(Row row: someSet.collectAsList()) {
 	      inputVec = row.getAs("features");
 	      oldVal = (long)row.getAs("label");
 	      Label = (int)oldVal;
 	      CR.scanANDaccumulate(inputVec, Label);
	   }
	   
	   CR.setCLDir(getClassifierOutputDir());
	   CR.getROC("SaveText");
	}
	//********************************************************************

	//Get Parameters from config-file:
	//********************************************************************
	public void getConfPars(FileInputStream fstream) throws IOException {
		GetRunPars grp = new GetRunPars();
		grp.setFirstSeperator(": ");
		grp.setSecondSeperator(", ");
		grp.setConfigFile(fstream);
		grp.addToList("BASEDIR", "string");
		grp.addToList("DATADIR", "string");
		grp.addToList("TRAININGDATA", "string");
		grp.addToList("NJASONFILES", "int");
		grp.addToList("VARS", "string");
		grp.addToList("PERCENTAGE", "int");
		grp.addToList("CLASSIFIER", "string");
		grp.addToList("NITERATIONS", "int");
		grp.addToList("CLASSINAME", "string");
		grp.addToList("MLP-ARCHITECTURE", "int");
		grp.addToList("MLP-SOLVER", "string");
		grp.addToList("GBT-DEPTH", "int");
		grp.addToList("SVM-REGPARAM", "double");
		grp.addToList("kMEANS-NCLU", "int");
		grp.addToList("TRAININGMODE", "string");
		
		
		grp.setConfigPars();
		
		setBaseDir(grp.getStringFromList("BASEDIR")[0]);
		setDataDir(grp.getStringFromList("DATADIR")[0]);
		setNameTrainingData(getDataDir() + grp.getStringFromList("TRAININGDATA")[0]);
		setnJasonFiles(grp.getIntFromList("NJASONFILES")[0]);
		setPidVars(grp.getStringFromList("VARS"));
		setSplitPercentage(grp.getIntFromList("PERCENTAGE")[0]);
		setClassifierType(grp.getStringFromList("CLASSIFIER")[0]);
		setClassifierName(grp.getStringFromList("CLASSINAME")[0]);
		setTrainingMode(grp.getStringFromList("TRAININGMODE")[0]);
		
		//Set classifier specific parameters:
		SPS.setnIterations(grp.getIntFromList("NITERATIONS")[0]);
		SPS.setMLPARCH(grp.getIntFromList("MLP-ARCHITECTURE"));
		SPS.setMLPSOLVER(grp.getStringFromList("MLP-SOLVER")[0]);
		SPS.setGBTDEPTH(grp.getIntFromList("GBT-DEPTH")[0]);
		SPS.setSVMREGPARAM(grp.getDoubleFromList("SVM-REGPARAM")[0]);
		SPS.setnClusters(grp.getIntFromList("kMEANS-NCLU")[0]);
		setNThreads(grp.getIntFromList("NJASONFILES")[0]);
		
		grp.cleanLists();
		grp = null;
	}
	//******************************************************************


	//Setting up the classifier which shall be trained
	//and tested:
	//******************************************************************
	public String getClassifierType() {
		return classifierType;
	}

	//==========================================

	public void setClassifierType(String classifierType) {
		this.classifierType = classifierType;
	}

	//==========================================

	public String getClassifierName() {
		return classifierName;
	}

	//==========================================

	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}
	
	//==========================================
	
	public String getClassifierOutputDir() {
		return classifierOutputDir;
	}

	//==========================================

	public void setClassifierOutputDir(String classifierOutputDir) {
		this.classifierOutputDir = classifierOutputDir;
	}
	
	//==========================================
	
	public int getNThreads() {
		return NThreads;
	}

	//==========================================

	public void setNThreads(int nThreads) {
		NThreads = nThreads;
	}

	//==========================================

	public String getTrainingMode() {
		return trainingMode;
	}

	//==========================================

	public void setTrainingMode(String trainingMode) {
		this.trainingMode = trainingMode;
	}
	
	//******************************************************************


	


	

	


	
	
	

	
	
	
	
}
