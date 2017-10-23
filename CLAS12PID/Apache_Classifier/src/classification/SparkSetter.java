package classification;

import java.io.File;

//Program to start an Apache-Spark-Session
//Files are loaded/created here
//All basic classifier operations such as loading, or saving
//and the classifier definitions are given here
//Last date worked on: 09/27/17
//Written by: Daniel Lersch d.lersch@fz-juelich.de


import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.Estimator;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.Transformer;
import org.apache.spark.ml.classification.GBTClassificationModel;
import org.apache.spark.ml.classification.GBTClassifier;
import org.apache.spark.ml.classification.LinearSVC;
import org.apache.spark.ml.classification.LinearSVCModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.param.IntParam;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.param.ParamPair;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.ml.tuning.TrainValidationSplit;
import org.apache.spark.ml.tuning.TrainValidationSplitModel;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import scala.Tuple2;

public class SparkSetter implements Serializable{

	//Stuff for a spark-session:
	//++++++++++++++++++++++++++++++++++++
    private SparkSession spark = null;	
    //++++++++++++++++++++++++++++++++++++
    
    //Stuff for load data from json-file:
    //++++++++++++++++++++++++++++++++++++
    private Dataset<Row> dataSet = null;
    //++++++++++++++++++++++++++++++++++++
	
    //Stuff to load various classifier
    //and train them:
    //++++++++++++++++++++++++++++++++++++
    private PipelineModel classifier = null;
    private Pipeline PL = null;
    private PipelineStage[] stage = null;
    private int nIterations = 0;
    private int nTrainingEpochs = 0;
    private boolean isCLSet = false;
    private boolean isTrainingDone = false;
    private String clType = null;
    private IntParam trainParam = null;
    
    //For the MLP:
    private MultilayerPerceptronClassifier MLP = null;
    private MultilayerPerceptronClassificationModel mlpModel = null;
    private int[] MLPARCH = null;
    private String MLPSOLVER = null;
    private int nPidVars = 0;
    
    //For the GBT:
    private GBTClassifier GBT = null;
    private GBTClassificationModel gbtModel = null;
    private int GBTDEPTH = 0;
    
    //For the SVM:
    private LinearSVC SVM = null;
    private LinearSVCModel svmModel = null;
    private double SVMREGPARAM = 0.0;
    
    //kMeans Cluster:
    private KMeans kMCluster = null;
    private KMeansModel kmclusterModel = null;
    private int nClusters = 0;
    
    //++++++++++++++++++++++++++++++++++++
    
    //Stuff for testing a classifier:
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private  JavaRDD<Tuple2<Object, Object>> rocClassifier = null;
    private JavaRDD<Tuple2<Object, Object>> precisionClassifier = null;
    private double areaUnderRoc = 0;
    private double accuracyClassifier = 0;
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    //Stuff for optimizing the classifiers parameter:
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private ParamGridBuilder gridBuilder = null;
    private ParamMap[] gridMap = null;
    private TrainValidationSplit simpleTester = null;
    private TrainValidationSplitModel testModel = null;
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
	SparkSetter(){
				
	}
	
	//Setup a spark-session to either store data or
    //run an ML-algorithm:
	//************************************************************************************************
	public void setSparkSession(String sessionName, int NThreads) {
		String setThreads = "local[" + NThreads + "]";
  		 spark = SparkSession
  					  .builder()
  					  .appName(sessionName)
  					  .config( "spark.driver.host", "localhost" )
  					  .config("spark.master", setThreads)
  					  .getOrCreate();
  		 spark.sparkContext().setLogLevel("ERROR");
  	}
	
	//=======================================
	
	public void stopSpark() {
		spark.stop();
	}
	
	//=======================================
	
	public void saveListToJsonFile(List<Row> someList, String outputFile) {
		System.out.println("Define variables for the list");
 		 StructType schema = new StructType(new StructField[]{
			      new StructField("label", DataTypes.IntegerType, false, Metadata.empty()),
			      new StructField("variable1", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable2", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable3", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable4", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable5", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable6", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable7", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable8", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable9", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable10", DataTypes.DoubleType, false, Metadata.empty()),
			      new StructField("variable11", DataTypes.DoubleType, false, Metadata.empty())
			    });
		 
 		 System.out.println("...done. Now go for saving the file itself...");
		  Dataset<Row> trainingset = spark.createDataFrame(someList, schema);
		  trainingset.write().format("json").mode(SaveMode.Append).save(outputFile);
		  System.out.println("...done!");
		  someList.clear();
 	}
	
	//=======================================
	
	public void getPIDDataFromFile(String dataSetName, int nSubFiles, String[] pidVars) {
		  String json_name = null;
	      String[] datarowname = new String[nSubFiles];
	        
	      Dataset<Row> input_file = null;
	      Dataset<Row> current_file = null;
	      Dataset<Row> updated_file = null;
	        
	      boolean isFileThere = true;
	      String part;
	      int fileCounter = 0;
	      int trueCounter = 0;
	      File currentFile;
	      long nRows = 0;
	      
	      while(isFileThere) {
	    	     part = "/dataPart" + fileCounter;
	    	     currentFile = new File(dataSetName+part);
	    	     trueCounter = 0;
	    	     
	    	     if(currentFile.exists()) {
	           //-------------------------------------------------------------------- 
	    	    	   for(int z=0;z<nSubFiles;z++){
	    		            json_name = "/part-0000" + z + "-*.json";
	    		            datarowname[z] = dataSetName + part + json_name;
	    		            current_file = spark.read().format("json").json(datarowname[z]);
	    		            
	    		            nRows = current_file.count();
	    		            if(nRows > 0) {
	    		            	  
	    		              if(trueCounter==0 && fileCounter == 0){
	    		                 input_file = current_file;
	    		             }else input_file = updated_file.union(current_file);
	    		              
	    		              trueCounter++;
	    		              updated_file = input_file;
	    		              
	    		            }
	    	    	   }
	    	    	   //-------------------------------------------------------------------- 
	    	    	   
	    	    	   isFileThere = true;
	    	    	   fileCounter++;
	    	     }else isFileThere = false;
	    	      currentFile.delete();
	         }
	    
	        VectorAssembler assem = new VectorAssembler();
	        assem.setInputCols(pidVars);
	        assem.setOutputCol("features");
	        setDataSet(assem.transform(input_file));    
	        
	        setnPidVars(pidVars.length); //This one is important for the MLP
	}

	//=======================================

	public Dataset<Row> getDataSet() {
		return dataSet;
	}

	//=======================================

	public void setDataSet(Dataset<Row> dataSet) {
		this.dataSet = dataSet;
	}
	
	//=======================================
	
	public int getnPidVars() {
		return nPidVars;
	}
	
	//=======================================

	public void setnPidVars(int nPidVars) {
		this.nPidVars = nPidVars;
	}
	//************************************************************************************************
	
	//Everything for setting up a classifier:
	//************************************************************************************************
	public String getClType() {
		return clType;
	}

	//=======================================
	
	public void setClType(String clType) {
		this.clType = clType;
		this.simpleTester = new TrainValidationSplit();
	}
	
	//=======================================
	
	public void setMLP() {
		  MLP = null;
		 
		  int nLayers = MLPARCH.length;
		  
		  int[] layers = new int[nLayers + 1];
		  layers[0] = getnPidVars();
		  for(int k=1;k<nLayers+1;k++) {
		      layers[k] = MLPARCH[k-1]; 	  
		  }
		  
          double stepSize = 1.0/nIterations;
         // double stepSize = 0.01;
          this.MLP = new MultilayerPerceptronClassifier();
          setTrainParam(MLP.maxIter());
          simpleTester.setEstimator(MLP);
          
          MLP.setLabelCol("label")
             .setFeaturesCol("features")
             .setLayers(layers)
             .setStepSize(stepSize)
             .setSeed(1234L)
             .setSolver(MLPSOLVER)
             .setMaxIter(getnTrainingEpochs());
          
	}
	
	//=======================================
	
	public void setGBT() {
		GBT = null;
		
		this.GBT = new GBTClassifier();
		setTrainParam(GBT.maxIter());
		simpleTester.setEstimator(GBT);
		
	    GBT.setLabelCol("label")
	        .setFeaturesCol("features")
	        .setMaxDepth(GBTDEPTH)
	        .setSeed(1234L)
	        .setMaxIter(getnTrainingEpochs());
	}
	
	//=======================================
	
	public void setSVM() {
		SVM = null;
		
		this.SVM = new LinearSVC();
		setTrainParam(SVM.maxIter());
		simpleTester.setEstimator(SVM);
		
		SVM.setLabelCol("label")
			    .setFeaturesCol("features")
			    .setMaxIter(getnTrainingEpochs())
			    .setRegParam(SVMREGPARAM);
	}
	
	//=======================================
	
	public void setkMCluster() {
		kMCluster = null;
		
		this.kMCluster = new KMeans();
		setTrainParam(kMCluster.maxIter());
		simpleTester.setEstimator(kMCluster);
		
		kMCluster.setFeaturesCol("features")
				.setK(nClusters)
				.setMaxIter(getnTrainingEpochs())
				.setSeed(1234L);
	}
	
	//=======================================
	
	public void setModel(String clType, String trainingMode) {
		setClType(clType);
		PL = null;
		stage = null;
		this.isCLSet = false;
		
		this.PL = new Pipeline();
		this.stage = new PipelineStage[1];
		
		if(clType.equals("MLP")) {
			setMLP();
			if(trainingMode.equals("fast")) {
				stage[0] = MLP;
				PL.setStages(stage);
			}
			this.isCLSet = true;
		}else if(clType.equals("GBT")) {
			setGBT();
			if(trainingMode.equals("fast")) {
				stage[0] = GBT;
				PL.setStages(stage);
			}
			this.isCLSet = true;
		}else if(clType.equals("SVM")){
			 setSVM();
			 if(trainingMode.equals("fast")) {
				 stage[0] = SVM;
				 PL.setStages(stage);
			 }
			 this.isCLSet = true;
		}else if(clType.equals("kMEANS")) {
			 setkMCluster();
			 if(trainingMode.equals("fast")) {
				 stage[0] = kMCluster;
				 PL.setStages(stage);
			 }
			 this.isCLSet = true;
		}else System.out.println("Sorry. You did not specify any (known) classifier!");
	}
	
	//************************************************************************************************
	
	
	//Training, storing, testing and loading the classifier: 
	//************************************************************************************************
	public void trainClassifier(Dataset<Row> trainingSet, String clType) {
		setModel(clType,"fast");
		classifier = null;
		
		if(isCLSet) {
		    classifier = PL.fit(trainingSet);
		    setClassifier(classifier);
			this.isTrainingDone = true;
		}else {
			System.out.println("Sorry! You did not specify a classifier. Training can not be done. Have a nice day!");
			this.isTrainingDone = false;
		}
	}
	
	//====================================
	
	//UNDER CONSTRUCTION!!!!
	public void trainClassifierPerEpoch(Dataset<Row> trainingSet, String clType, int nEpochs, double Ratio) throws IOException {
		int nInterEpochs = nIterations / nEpochs;
		setnTrainingEpochs(nInterEpochs);
		
		setModel(clType,"fast");
		classifier = null;
		
		PipelineModel[] currentModel = new PipelineModel[nEpochs];
		Pipeline[] currentPL = new Pipeline[nEpochs];
		
		currentPL[0] = PL;
		double err,epoch;
		
		double[] weights = null;
		
		MultilayerPerceptronClassificationModel mmodel;
		MultilayerPerceptronClassificationModel new_mmodel = null;
		ParamMap[] currentMap = new ParamMap[nEpochs];
		currentMap[0] = currentPL[0].extractParamMap();
		
		
		
		Param<?>[] currentParms = null;
		int nParms = 0;
	
		
		for(int k=0; k<nEpochs;k++) {
			epoch = (k+1)*nInterEpochs;
					
			currentModel[k] = currentPL[k].fit(trainingSet);

			
			
			isTrainingDone = true;
			err = trainingError(currentModel[k],trainingSet);
			
			
			mmodel = (MultilayerPerceptronClassificationModel)currentModel[k].stages()[0];
			currentParms = mmodel.parent().params();
			nParms = currentParms.length;
			
			
			
			/*
			for(int z=0;z<nParms;z++) {
				System.out.println("Parameter: " + currentParms[z].name());
			}
			*/
			System.out.println("Epoch: " + epoch + " and Error: " + err);
			
			if(k < nEpochs-1) {
				currentPL[k+1] = new Pipeline();
				new_mmodel = mmodel;
				
				
				stage[0] = new_mmodel.parent();
				currentPL[k+1].setStages(stage);
				
				currentMap[k+1] = new_mmodel.parent().extractParamMap();
			}
			
		}
		
		
		/*
		setModel(clType,"slow");
		classifier = null;
		
		if(isCLSet) {
			int nInterEpochs = nIterations / nEpochs;
		    int[] epoch = new int[nEpochs];
			
		    for(int k=0;k<nEpochs;k++) {
		    	  epoch[k] = (k+1)*nInterEpochs;
		    }
			
			this.gridBuilder = new ParamGridBuilder();
		    gridBuilder.addGrid(trainParam,epoch);
		    this.gridMap = gridBuilder.build();
			
		    MulticlassClassificationEvaluator eval = new MulticlassClassificationEvaluator();
		    eval.setMetricName("accuracy");
		    
		    simpleTester.setEvaluator(eval);
		    simpleTester.setEstimatorParamMaps(gridMap);
			simpleTester.setTrainRatio(Ratio);
			
			stage[0] = simpleTester;
			PL.setStages(stage);
			
		   classifier = PL.fit(trainingSet);
		    setClassifier(classifier);
		    setTestModel((TrainValidationSplitModel)classifier.stages()[0]);
			this.isTrainingDone = true;
		}else {
			System.out.println("Sorry! You did not specify a classifier. Training can not be done. Have a nice day!");
			this.isTrainingDone = false;
		}
		*/
		
	}
	
	//====================================
	
	public void saveClassifier(String clDir) throws IOException {
	   if(isTrainingDone)classifier.save(clDir);	
	}
	
	//====================================
	
	public void testClassifier(Dataset<Row> testData) {
		 if(isTrainingDone){
	            //=====================================================================================================
	            Dataset<Row> result_class;
	            Dataset<Row> compare_class;
	            result_class = classifier.transform(testData);
	            compare_class = result_class.select("prediction" , "label");
	            
	            //Get the accuracy of the classifier:
	            MulticlassClassificationEvaluator eval_classifier = new MulticlassClassificationEvaluator().setMetricName("accuracy");
	           
	            JavaRDD<Row>  compare_class_rdd = compare_class.toJavaRDD();
	            JavaRDD<Tuple2<Object, Object>> compare_class_tuple = compare_class_rdd.map(new Function<Row, Tuple2<Object, Object>>(){
	                @Override
	                public Tuple2<Object, Object> call(Row myrow){
	                    long old_val = (long)myrow.get(1);
	                    Double new_val = (double) old_val;
	                    
	                    return new Tuple2<Object, Object>(myrow.get(0),new_val);
	                }
	            });
	            
	            BinaryClassificationMetrics metrics_classifier =
	            new BinaryClassificationMetrics(compare_class_tuple.rdd());
	            //get apache-spark build in roc-curve:
	            setRocClassifier(metrics_classifier.roc().toJavaRDD());
	            //get area und roc-curve:
	            setAreaUnderRoc(metrics_classifier.areaUnderROC());
	            //get precision by threshold:
	            setPrecisionClassifier(metrics_classifier.precisionByThreshold().toJavaRDD());
	            //Get accuracy of the classifier:
	            setAccuracyClassifier(eval_classifier.evaluate(compare_class));
	            
	           
	        }else System.out.println("No training has been done. So there is nothing to evelaute. Have a nice day!");
	}
	
	//====================================
	
	public double trainingError(PipelineModel model, Dataset<Row> testData) {
		double out = -1.0;
		 if(isTrainingDone){
	            //=====================================================================================================
	            Dataset<Row> result_class;
	            Dataset<Row> compare_class;
	            result_class = model.transform(testData);
	            compare_class = result_class.select("prediction" , "label");
	            
	            //Get the accuracy of the classifier:
	            MulticlassClassificationEvaluator eval_classifier = new MulticlassClassificationEvaluator().setMetricName("accuracy");
	             out = 1.0 - eval_classifier.evaluate(compare_class);
		 }
		 
		 return out;
	}
	
	//====================================

	public void loadClassifier(String modelDir, String modelType) {
		classifier  = null;
		mlpModel = null;
		gbtModel = null;
		svmModel = null;
		kmclusterModel = null;
		
		setClassifier(PipelineModel.load(modelDir));
		if(modelType.equals("MLP")) {
			this.mlpModel = (MultilayerPerceptronClassificationModel)(classifier.stages()[0]);
		}else if(modelType.equals("GBT")) {
			this.gbtModel = (GBTClassificationModel)(classifier.stages()[0]);
		}else if(modelType.equals("SVM")) {
			this.svmModel = (LinearSVCModel)(classifier.stages()[0]);
		}else if(modelType.equals("kMEANS")) {
			this.kmclusterModel = (KMeansModel)(classifier.stages()[0]);
		}
	}
	
	//====================================
	
	public GBTClassificationModel getGBTModel() {
		return gbtModel;
	}
	
	//====================================
	
	public MultilayerPerceptronClassificationModel getMLPModel() {
		return mlpModel;
	}
	//************************************************************************************************
	
	

	//Getters and Setters..Do you want more?
	//************************************************************************************************
	public int[] getMLPARCH() {
		return MLPARCH;
	}
	
	//=======================================

	public void setMLPARCH(int[] mLPARCH) {
		MLPARCH = mLPARCH;
	}
	
	//=======================================

	public String getMLPSOLVER() {
		return MLPSOLVER;
	}
	
	//=======================================

	public void setMLPSOLVER(String mLPSOLVER) {
		MLPSOLVER = mLPSOLVER;
	}
	
	//=======================================

	public int getGBTDEPTH() {
		return GBTDEPTH;
	}
	
	//=======================================

	public void setGBTDEPTH(int gBTDEPTH) {
		GBTDEPTH = gBTDEPTH;
	}
	
	//=======================================

	public PipelineModel getClassifier() {
		return classifier;
	}
	
	//=======================================

	public void setClassifier(PipelineModel classifier) {
		this.classifier = classifier;
	}
	
	//=======================================

	public Pipeline getPL() {
		return PL;
	}
	
	//=======================================

	public void setPL(Pipeline pL) {
		PL = pL;
	}
	
	//=======================================
	
	public PipelineStage[] getStage() {
		return stage;
	}
	
	//=======================================

	public void setStage(PipelineStage[] stage) {
		this.stage = stage;
	}
	
	//=======================================
	
	public MultilayerPerceptronClassifier getMLP() {
		return MLP;
	}

	//=======================================
	
	public void setMLP(MultilayerPerceptronClassifier mLP) {
		MLP = mLP;
	}
	
	//=======================================

	public GBTClassifier getGBT() {
		return GBT;
	}
	
	//=======================================

	public void setGBT(GBTClassifier gBT) {
		GBT = gBT;
	}
	
	//=======================================
	
	public LinearSVC getSVM() {
			return SVM;
	}
		
	//=======================================

	public LinearSVCModel getSvmModel() {
			return svmModel;
	}
		
	//=======================================

	public void setSvmModel(LinearSVCModel svmModel) {
			this.svmModel = svmModel;
	}
		
	//=======================================
		
	public double getSVMREGPARAM() {
			return SVMREGPARAM;
	}
		
	//=======================================

	public void setSVMREGPARAM(double sVMREGPARAM) {
			SVMREGPARAM = sVMREGPARAM;
	}
	
	//=======================================
	
	public KMeans getkMCluster() {
		return kMCluster;
	}

	//=======================================
	
	public KMeansModel getKmclusterModel() {
		return kmclusterModel;
	}
	
	//=======================================

	public void setKmclusterModel(KMeansModel kmclusterModel) {
		this.kmclusterModel = kmclusterModel;
	}
	
	//=======================================

	public int getnClusters() {
		return nClusters;
	}
	
	//=======================================

	public void setnClusters(int nClusters) {
		this.nClusters = nClusters;
	}

	//=======================================
	
	public int getnIterations() {
		return nIterations;
	}
	
	//=======================================

	public void setnIterations(int nIterations) {
		this.nIterations = nIterations;
	}
	
	//=======================================
	
	public int getnTrainingEpochs() {
		return nTrainingEpochs;
	}

	//=======================================
	
	public void setnTrainingEpochs(int nTrainingEpochs) {
		this.nTrainingEpochs = nTrainingEpochs;
	}
	
	//=======================================
	
	public JavaRDD<Tuple2<Object, Object>> getRocClassifier() {
		return rocClassifier;
	}
	
	//=======================================

	public void setRocClassifier(JavaRDD<Tuple2<Object, Object>> rocClassifier) {
		this.rocClassifier = rocClassifier;
	}
	
	//=======================================

	public JavaRDD<Tuple2<Object, Object>> getPrecisionClassifier() {
		return precisionClassifier;
	}
	
	//=======================================

	public void setPrecisionClassifier(JavaRDD<Tuple2<Object, Object>> precisionClassifier) {
		this.precisionClassifier = precisionClassifier;
	}
	
	//=======================================

	public double getAreaUnderRoc() {
		return areaUnderRoc;
	}
	
	//=======================================

	public void setAreaUnderRoc(double areaUnderRoc) {
		this.areaUnderRoc = areaUnderRoc;
	}
	
	//=======================================

	public double getAccuracyClassifier() {
		return accuracyClassifier;
	}
	
	//=======================================

	public void setAccuracyClassifier(double accuracyClassifier) {
		this.accuracyClassifier = accuracyClassifier;
	}
	
	//=======================================
	
	public IntParam getTrainParam() {
		return trainParam;
	}
	
	//=======================================

	public void setTrainParam(IntParam trainParam) {
		this.trainParam = trainParam;
	}
	
	//=======================================

	public TrainValidationSplitModel getTestModel() {
		return testModel;
	}

	//=======================================
	
	public void setTestModel(TrainValidationSplitModel testModel) {
		this.testModel = testModel;
	}
	//************************************************************************************************

	

	

	

	
	
	

	

	

	

	

	

	

	
	
	
	
	
	
	
}
