package com.group2.storm;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <br> ✔ In our Spout, we use API keys from the main class and query the twitter by using the keywords defined.
* <br> ✔ we filter only English language tweets.
 * @author Syahirul Faiz
 * @version 2020.03
 * @since 2020-05-08
 */
public class Spout_Group2 extends BaseRichSpout {
	SpoutOutputCollector _collector;
	LinkedBlockingQueue<Status> queue = null;
	TwitterStream _twitterStream;
	String consumerKey;
	String consumerSecret;
	String accessToken;
	String accessTokenSecret;
	String[] keyWords;


	/**
	 * <br> ✔using Twitter API keys for authentication
	 * @param consumerKey Supply the Consumer Key for the Twitter API
	 * @param consumerSecret Supply the Consumer Secret Key for the Twitter API
	 * @param accessToken Supply the Access Token Key for the Twitter API
	 * @param accessTokenSecret Supply the Access Token Secret Key for the Twitter API
	 * @param keyWords The keyword passed from the Topology.java
	 */
	public Spout_Group2(String consumerKey, String consumerSecret,
			String accessToken, String accessTokenSecret, String[] keyWords) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
		this.keyWords = keyWords;
	}


	/**
	 *<br> ✔opening the spout for producing the tuple
	 */
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		queue = new LinkedBlockingQueue<Status>(1000);
		_collector = collector;
		
		//create the listener object
		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				queue.offer(status);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice sdn) {
			}

			@Override
			public void onTrackLimitationNotice(int i) {
			}

			@Override
			public void onScrubGeo(long l, long l1) {
			}

			@Override
			public void onException(Exception ex) {
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
			}
		};
		
		
		//using configuration builder class for manage the authentication setting
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessTokenSecret);
		_twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		_twitterStream.addListener(listener);
		if (keyWords.length == 0) {
			_twitterStream.sample();
		} else {
			//filter the tweets using the keywords provided
			FilterQuery query = new FilterQuery().track(keyWords);
			//// we are not using geolocation for now.... any EN language from any country should be fine
			////query.locations(new double[][]{new double[]{-126.562500,30.448674},new double[]{-61.171875,44.087585}}); 
			query.language(new String[]{"en"}); 
			
			_twitterStream.filter(query);
			
		}
		System.out.println("INFO : From Spout");
	}

	/**
	 *<br> ✔ pass the tuple to next bolt
	 */
	@Override
	public void nextTuple() {
		Status ret = queue.poll();
		if (ret == null) {
			Utils.sleep(50);
		} else {
			_collector.emit(new Values(ret));
		}
	}


	/**
	 *<br> ✔shutdown the spout according to the given interval in topology
	 */
	@Override
	public void close() {
		_twitterStream.shutdown();
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config ret = new Config();
		ret.setMaxTaskParallelism(1);
		return ret;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet"));
	}
}