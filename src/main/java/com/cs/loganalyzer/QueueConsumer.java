package com.cs.loganalyzer;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class QueueConsumer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(QueueConsumer.class);

	ConcurrentHashMap<String, JsonNode> myMapJson = new ConcurrentHashMap<String, JsonNode>();
	static int count = 0;

	@Override
	public void run() {
		logger.info("QueueConsumer started...");
		consume();
	}

	public void consume() {

		try {
			while (!QueueProducer.producerIsDone
					|| (QueueProducer.producerIsDone && !QueueProducer.linesReadQueue.isEmpty())) {
				JsonNode jsonObj = QueueProducer.linesReadQueue.take();

				if (myMapJson.get(jsonObj.get("id").asText()) == null) {
					myMapJson.put(jsonObj.get("id").asText(), jsonObj);
				} else {
					JsonNode startLogJson;
					JsonNode finishLogJson;
					JsonNode jsonOtherObj = myMapJson.get(jsonObj.get("id").asText());

					if (jsonObj.get("state").asText().equals("STARTED")) {
						startLogJson = jsonObj;
						finishLogJson = jsonOtherObj;
					} else {
						finishLogJson = jsonObj;
						startLogJson = jsonOtherObj;
					}

					long diff = finishLogJson.get("timestamp").numberValue().longValue()
							- startLogJson.get("timestamp").numberValue().longValue();
					final boolean alert;
					if (diff > 4) {
						alert = true;
					} else {
						alert = false;
					}
					HSQLDBService.insertToDB(finishLogJson, diff, alert);
					logger.info(count + ":..." + Thread.currentThread().getName() + "..:.."
							+ QueueProducer.linesReadQueue.size() + "..procesed:" + jsonObj + ""
							+ " \n-----------" + jsonOtherObj);
					count++;
				}
			}
		} catch (Exception e) {
			logger.error("Exception in consumer ", e);
		}
		logger.info(Thread.currentThread().getName() + " consumer is done");
	}

}
