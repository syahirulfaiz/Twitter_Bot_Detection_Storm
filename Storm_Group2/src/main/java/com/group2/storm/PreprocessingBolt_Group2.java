package com.group2.storm;


import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
//import java.util.regex.*; //use regex package to simplify the mapper function

import twitter4j.Status;
//import twitter4j.UserMentionEntity;
//import twitter4j.HashtagEntity;
//import twitter4j.User;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;




/**
 * <br> ✔	In the PreprocessingBolt_Group2, we breakdown the tweets into smaller objects, such as screen name (username), the username's description, location, number of followers, profile picture, tweets, and other entities. 
* <br> ✔	The pre-processing tasks which we do in this class are clearing the strings using regular expression (we will also remove stop-words as the pre-processing filter in the machine learning class). 
* <br> ✔	We use these attributes for several tasks, such as flagging a username if it matches specific criteria, and for the machine learning part. We also record these collected attributes in the local file (pulled_tweets.csv). 
* <br> ✔	In the flagging task, we flag a username if it matches specific criteria and calculates the sum of flags obtained. 
* <br> ✔	For instance, we will raise a flag if there is a word 'bot' mentioned in a username.  Since we have 12 flagging criteria, we divide the total number of the flags by 12, and we will get the probability whether a username is a bot or not. 
* <br> ✔	If the probability is more than the threshold (we define 0.5 as the threshold), we set that username as a bot (in the last column: '1' for the bot, '0' for non-bot). This scoring will be used for the classification task of the machine learning library.
 * @author Syahirul Faiz
 * @version 2020.03
 * @since 2020-05-08
 */
public class PreprocessingBolt_Group2 implements IRichBolt {

	/**
	  * <br> ✔ Pre-defined non-abstract data type in Storm (to collect tuple)
	 */
	private OutputCollector collector;
	
	/**
	  * <br> ✔ collect tuples from Spout
	 */
	@Override
	public void prepare(Map conf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
	}

	/**
	  * <br> ✔execute after prepare method.
	  * <br> ✔ we breakdown the tweets into smaller objects, such as screen name (username), the username's description, location, number of followers, profile picture, tweets, and other entities. 
	  * <br> ✔ <a href="https://github.com/RohanBhirangi/Twitter-Bot-Detection">  https://github.com/RohanBhirangi/Twitter-Bot-Detection </a>
	 * <br> ✔ Flagging
	 * <br> ✔ 'BlackBox' Classification
	 */
	@Override
	public void execute(Tuple tuple) {
		//get the tweet object from tuple
		Status tweet = (Status) tuple.getValueByField("tweet");
  
	        Helper helper = new Helper();
	        //initialise 20 columns for ML
	        //Credit to https://github.com/RohanBhirangi/Twitter-Bot-Detection
	        String[] toBeWritten= {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","0"};
	        toBeWritten[0]= String.valueOf(tweet.getUser().getId()); //Id 
	        toBeWritten[1]= String.valueOf(tweet.getUser().getId()); //id_str
	        toBeWritten[2]= String.valueOf(tweet.getUser().getScreenName()); //screen_name
	        toBeWritten[3]= String.valueOf(tweet.getUser().getLocation()).replaceAll("[; , \\n \\r]"," "); //get location of the user
	        toBeWritten[4]= String.valueOf(tweet.getUser().getDescription()).replaceAll("[; , \\n \\r]"," "); //get the description of the user
	        toBeWritten[5]= String.valueOf(tweet.getUser().getURL()); //whether a user has url in its bio
	        toBeWritten[6]= String.valueOf(tweet.getUser().getFollowersCount()); //followers_count
	        toBeWritten[7]= String.valueOf(tweet.getUser().getFriendsCount()); //friends_count
	        toBeWritten[8]= String.valueOf(tweet.getUser().getListedCount()); //listedcount
	        toBeWritten[9]= String.valueOf(tweet.getUser().getCreatedAt()); //created_at
	        toBeWritten[10]= String.valueOf(tweet.getUser().getFavouritesCount()); //favourites_count
	        toBeWritten[11]= String.valueOf(tweet.getUser().isVerified()); //verified
	        toBeWritten[12]= String.valueOf(tweet.getUser().getStatusesCount()); //statuses_count
	        
	        toBeWritten[13]= tweet.getLang(); //get language of the tweet. (For sanity check the 'english only' query works or not) 
	        toBeWritten[14]= tweet.getText().replaceAll("[; , \\n \\r]"," "); //the contents of the tweet
	        
	        toBeWritten[15]= String.valueOf(tweet.getUser().isDefaultProfile()); //default_profile 
	        toBeWritten[16]= String.valueOf(tweet.getUser().isDefaultProfileImage()); //default_profile_image
	        
	        //toBeWritten[17]= String.valueOf(tweet.getUser().isDefaultProfile()); 
	        toBeWritten[17]= String.valueOf(tweet.getUser().isDefaultProfile()); //extended_profile
	        
	        toBeWritten[18]= String.valueOf(tweet.getUser().getName()); //name
	     
	        //=================FLAGGING==============
	        int sumOfFlag = helper.flagIfBotWordExist(toBeWritten[2]) + //check 'Bot' word in screenName
	        		helper.flagIfBotWordExist(toBeWritten[4]) + //check 'Bot' word in description
	        		helper.flagIfBotWordExist(toBeWritten[14]) + //check 'Bot' word in tweet
	        		helper.flagIfBotWordExist(toBeWritten[18]) + //check 'Bot' word in name
	        		helper.flagIfURLExist(toBeWritten[4]) + //check URL in description
	        		helper.flagIfURLExist(toBeWritten[14]) + //check URL in tweets
	        		helper.flagIfImbalanceFollowerFriends(toBeWritten[6],toBeWritten[7]) + //compare imbalance follower:friends
	        		helper.flagIfNull(toBeWritten[3]) + //location is empty
	        		helper.flagIfNull(toBeWritten[4]) + //description is empty
	        		helper.flagIfNull(toBeWritten[16]) + //profile picture is empty/default
	        		helper.flagIfNameIsRandom(toBeWritten[2]) + //check if username is random
	        		flagIfTweetTooMuch(toBeWritten[2]); //check if a user tweet too much 
	        		;
	        
	        float probability = (float) sumOfFlag / 12; //calculate the probability based on the flags
	        
	        float threshold = (float) 0.5 ; //set the threshold
	        if (probability > threshold) toBeWritten[19] = "1"; //if the probability outweighs the threshold, the username indicated as BOT
	        
	        //=================FLAGGING==============
	        try {
				helper.writeToFile(toBeWritten,"pulled_tweets.csv"); //write the striped-tweet-contents into local file
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        
	        MachineLearning classifier = new MachineLearning(); //instantiate the ML class for the prediction
	        try {
				if(classifier.classify(toBeWritten)) 
					//{System.out.println(tweet.getUser().getScreenName()+" - is bot");}
					{
						System.out.println("INFO from PreprocessingBolt:....Please wait...");
						this.collector.emit(new Values(toBeWritten[2])); //send the username 'indicated' as bot to next bolt
					}
				else
					{System.out.println(toBeWritten[2]+" - is not bot");}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
	       
	        
	        //this.collector.emit(new Values(tweet.getUser().getScreenName()));
	        
	        //helper.readFromFile("pulled_tweets.txt");
	        
	}

	@Override
	public void cleanup() {
	}

	/**
	  * <br> ✔ Field Grouping
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("hashtag"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
	
	/**
	 * <br> ✔ helper method for flagging the username,
	* <br> ✔ if during 24 hours, the user produced more than 100 tweets, then we flag it
	 * @param user the username for checking
	 * @return '1' (true) or '0' (false)
	 */
	public int flagIfTweetTooMuch(String user) {
		int flag = 0;
		 Twitter twitter = new TwitterFactory().getInstance();
	        try {
	            List<Status> statuses;
	          statuses = twitter.getUserTimeline(user);
	            int sum = 0;
	            for (Status status : statuses) {
	                int calendarDate = Calendar.getInstance().get(Calendar.DATE);
	                int statusDate = status.getCreatedAt().getDate();
	                //check for today and yesterday, at CURRENT HOUR, because during system demo, we might not meet 24 hrs
	                if(calendarDate == statusDate || calendarDate-1 == statusDate) {sum=sum+1;}	                
	            }
	            if(sum >= 100) {flag=1;} //if in a day tweet more than 100, then it is a bot
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            System.out.println("Failed to get timeline: " + te.getMessage());
	            System.exit(-1);
	        }
		return flag = 1;
	}
	
}