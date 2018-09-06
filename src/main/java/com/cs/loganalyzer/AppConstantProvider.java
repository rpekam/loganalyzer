package com.cs.loganalyzer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.fasterxml.jackson.databind.JsonNode;

public interface AppConstantProvider {

	static boolean producerIsDone = false;
	static BlockingQueue<JsonNode> linesReadQueue = new ArrayBlockingQueue<JsonNode>(100);

}
