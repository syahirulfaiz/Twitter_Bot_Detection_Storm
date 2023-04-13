package com.group2.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

/**
 * <br> ✔ This is the main (driver) class of the Storm system is the Topology_Group2 class
 * <br> ✔ In this class, we design a Storm Topology (using TopologyBuilder(); instantiation command), which consists of Spout class (Spout_Group2), and two Bolt class (PreprocessingBolt_Group2 and OutputBolt_Group2).
 * <br> ✔ We use three objects of Spouts to collect a large number of tweets, 2 PreprocessingBolt for balancing the works, and 1 OutputBolt for aggregating the result into a single local file
 * <br> ✔ In the main class (Topology_Group2), we put our consumer keys and access token keys for the Twitter API authentication. We also set keywords array that we can use for searching some specific tweets related to those keywords. We then pass the keys and the keywords as the parameters for the Spout class constructor. Subsequently, using a cluster.submitTopology() command, we submit the Topology.
 * <br> ✔ we define how long the Storm will pull the tweets by using Thread.sleep(x), where the x is the amount of time in milliseconds. We have to make sure that we pull the tweet in a reasonable amount of time in order not to violate the Twitter API terms and condition (for example: about less than one hour, then stop. Then we can pull tweet again for one hour). 
 * @author Syahirul Faiz
 * @version 2020.04
 * @since 2020-05-08
 *
 */
public class Topology_Group2 {
	
	public static void main(String[] args) throws Exception {
		
		try {
			String consumerKey = "YourKey";
			String consumerSecret = "YourSecretKey";
			String accessToken = "YourAccessToken";
			String accessTokenSecret = "YourAccessTokenSecret";
			String[] keyWords = {"#bot","#bots","#bots","#chatbot","#chatbots","#chatbotagency","#chatbotbuilder","#chatbotcompany","#chatbotdeveloper","#chatbotdevelopment","#chatbotexpert","#chatbotfacebook","#chatbotmarketing","#chatbotmessenger"};			
			
			Config config = new Config();
			//config.setDebug(false);
			//design the TOpology
			TopologyBuilder builder = new TopologyBuilder();
			//build the Spout and define how many we create the objects
			builder.setSpout("Spout_Group2", new Spout_Group2(consumerKey,
					consumerSecret, accessToken, accessTokenSecret, keyWords),3);
			//build the 1st Bolt for preprocessing
			builder.setBolt("PreprocessingBolt_Group2", new PreprocessingBolt_Group2(),2)
					.shuffleGrouping("Spout_Group2");
			//build 2nd Bolt for output
			builder.setBolt("OutputBolt_Group2",
					new OutputBolt_Group2(),1).fieldsGrouping(
					"PreprocessingBolt_Group2", new Fields("hashtag"));
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("Topology_Group2", config,
					builder.createTopology());
			Thread.sleep(60000);
			//Thread.sleep(1200000); //20 mins
			//Thread.sleep(1800000); //0.5 hours
			//Thread.sleep(2700000); //45 mins
			//Thread.sleep(3600000); //1 hours
			//Thread.sleep(7200000); //2 hours
			//Thread.sleep(10800000); //3 hours
			cluster.shutdown();
		}catch(Exception e) {
			System.out.println("\nApplication Terminates. Please see 'Result' above. Thank you for using this application. Group 2 @ University of Liverpool (c) 2020");
		}
		
		
	}
}