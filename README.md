# Twitter_Bot_Detection_Storm

A Storm project for Bot detection in Twitter
We initially designed our project as a combination of Streaming Analysis (Apache Storm) and Machine Learning (Python). We decided to use this combination since Apache Storm is suitable middleware for the Streaming Analysis

We fully develop the system using Apache Storm as a mini-batch approach. We decide to use a mini-batch approach, where we do half part of analysis processing in 'real-time streaming' and another half part as 'batch mode' by utilising training dataset from a local file. Backtype and Twitter only intend to develop the Storm as a Big Data middleware, and they never meant to develop it for Machine Learning purpose. That is the reason why we also decide to implement a third-party external library from WEKA in one of our bolts to accommodate the Machine Learning task. Another limitation is that it does not have a built-in function for visualisation. Therefore, we only manage to provide the tweet logs (pulled_tweets.csv) and the output result in a simple HTML file (BOTS.html) to list the accounts indicated as bot.

The total of 1795 accounts collected, about 80% (1433 accounts) identified as bots. However, one of the academic research stated about 9%-15% accounts are bots. The discrepancy in our result probably caused by limited monitoring time and improper methodology we used to identify a bot correctly. In conclusion, it is evident that our system is inaccurate, and we need to improve in the future.

<hr/>
<h4>To Edit the Twitter Token Keys, in <code>Twitter4j.properties</code></h4>
<code>
debug=true
oauth.consumerKey=YourKey
oauth.consumerSecret=YourSecretKey
oauth.accessToken=YourAccessToken
oauth.accessTokenSecret=YourAccessTokenSecret
</code>

<h4>To Edit the Twitter Token Keys, in <code>Topology_Group2.java</code></h4>
<code>
public class Topology_Group2 {
	
	public static void main(String[] args) throws Exception {
		
		try {
			String consumerKey = "YourKey";
			String consumerSecret = "YourSecretKey";
			String accessToken = "YourAccessToken";
			String accessTokenSecret = "YourAccessTokenSecret";
      ...
</code>
