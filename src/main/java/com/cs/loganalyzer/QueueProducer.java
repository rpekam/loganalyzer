package com.cs.loganalyzer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QueueProducer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(QueueProducer.class);
	private String filePath;
	public QueueProducer(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public void run() {
		logger.info("QueueProducer started...");
		readFile();
	}

	static BlockingQueue<JsonNode> linesReadQueue = AppConstantProvider.linesReadQueue;
	static boolean producerIsDone = Boolean.FALSE;
	private Stream<String> lines;

	public void readFile() {
		Path file = Paths.get(this.filePath);
		try {
			lines = Files.lines(file, StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();

			for (String line : (Iterable<String>) lines::iterator) {
				JsonNode jsonObj = mapper.readTree(line);
				linesReadQueue.put(jsonObj);
				
				logger.info(Thread.currentThread().getName() + ":: producer count = "
						+ linesReadQueue.size() + "---:" + line);
			}

		} catch (Exception e) {
			logger.error("Exception in producer ", e);
		}
		producerIsDone = true; 
		
		logger.info(Thread.currentThread().getName() + " producer is done");
	}
}
