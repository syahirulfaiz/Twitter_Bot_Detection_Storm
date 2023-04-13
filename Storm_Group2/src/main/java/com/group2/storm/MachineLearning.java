package com.group2.storm;

//credit to : https://waikato.github.io/weka-wiki/documentation/
//this class will be a 'black box' for the classification step.

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.*;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.core.converters.CSVLoader;

import java.io.IOException;
import java.util.ArrayList;

import weka.classifiers.trees.*;

/**
 * <br> ✔ credit to : <a href="https://waikato.github.io/weka-wiki/documentation/"> https://waikato.github.io/weka-wiki/documentation/</a>
 * <br> ✔In this class, we use the Machine Learning Library from the University of Waikato, WEKA, as our black box classification method.
 * @author Syahirul Faiz
 * @version 2020.03
 * @since 2020-05-08
 *
 */
public class MachineLearning {

/**
* <br> ✔	First, we load the training dataset, and we define the last column as the class attributes.
* <br> ✔	We use the random forest, decision tree as our algorithm for classification. We build the base model using the training dataset.
* <br> ✔	Next, we initialise the test instance, 
* <br> ✔	, add it into the last index of the Training Dataset 
* <br> ✔	, and assign each of its values using vectors containing the tweet attributes sent from the Bolts.
 * @param tweet vector array as test instance
 * @return true or false
 * @throws Exception if there is error related to the WEKA library
 */
public boolean classify(String[] tweet) throws Exception {
	//Load the TRAIN DATASET from local file
	DataSource source = new DataSource("Twitter_bots&nonbots_data.csv");
	Instances trainDataset = source.getDataSet();	
	
	// Make the last attribute be the class
	trainDataset.setClassIndex(trainDataset.numAttributes()-1);
	
	//Instantiate the decision tree random forest classification algorithm from library
	RandomTree tree = new RandomTree();
	//build classifier from TRAIN DATASET
	tree.buildClassifier(trainDataset);

	//prepare the TEST DATASET instance
	Instance testInstance = null;
	//create temporary value for the new test instance
	double[] instanceValue1 = new double[trainDataset.numAttributes()];
	//Adds instance to the end of the trainDataset
	trainDataset.add(new DenseInstance(1.0, instanceValue1));

	//filter the stopwords & bag of words :build the co-occurrence vector from the text contained in the strings. 
	StringToWordVector filter = new StringToWordVector();
	filter.getStopwordsHandler();

	//set value for each attributes in the 'added' trainDataset. This new 'added' value is going to be the test set
	for (int i = 0; i<trainDataset.numAttributes(); i++) {
		trainDataset.instance(trainDataset.numInstances()-1).attribute(i).indexOfValue(tweet[i]);
	}

	//last dataset in the trainDataset becomes the test instance
	testInstance= trainDataset.instance(trainDataset.numInstances()-1);

	//initialise the prediction score
	double predTree = 0.0;
	//Classifies the given test instance.
	predTree = tree.classifyInstance(testInstance);
	
	String predString="";
	try {
		//return the predicted value, by 'adapting' the attribute value from the trainDataset (i.e: in WEKA, the attribute type need to be the same)
		predString = trainDataset.classAttribute().value((int) predTree);
	}catch(Exception e) {
		System.err.println("error: "+e.getMessage());
	}

	//return as boolean for the predicted result.
	return Boolean.parseBoolean(predString);

}

}