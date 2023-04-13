package com.group2.storm;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import backtype.storm.tuple.Fields;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

/**
 * <br> ✔	In the OutputBolt, or our last bolt we map the account received as key-value pair.
* <br> ✔	In the cleanup method, we write the account indicated as bolt in the local file.
 * @author Syahirul Faiz
 * @version 2020.03
 * @since 2020-05-08
 *
 */
public class OutputBolt_Group2 implements IRichBolt {
	Map<String, Integer> counterMap;
	private OutputCollector collector;
	
	/**
	 *<br> ✔collect tuples from previous bolts
	 */
	@Override
	public void prepare(Map conf, TopologyContext context,
			OutputCollector collector) {
		this.counterMap = new HashMap<String, Integer>();
		this.collector = collector;
	}

	/**
	 *<br> ✔map the tuples from previous bolts into key-value pairs
	 */
	@Override
	public void execute(Tuple tuple) {
		String key = tuple.getString(0);
		if (!counterMap.containsKey(key)) {
			counterMap.put(key, 1);
		} else {
			Integer c = counterMap.get(key) + 1;
			counterMap.put(key, c);
		}
		collector.ack(tuple);	
	}

	/**
	 *<br> ✔executed when the OutputBolt is finishing its execution.
	* <br> ✔write the username indicated as BOT in a html file.
	 */
	@Override
	public void cleanup() {
		for (Map.Entry<String, Integer> entry : counterMap.entrySet()) {
     		
		Helper helper = new Helper();
		helper.writeToFileTxt(entry.getKey(),"BOTS.html");
		System.out.println("INFO from OutputBolt : (finalising the result) :...Please wait...");
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("hashtag"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}