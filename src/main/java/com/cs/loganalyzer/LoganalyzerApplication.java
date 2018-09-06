package com.cs.loganalyzer;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoganalyzerApplication implements CommandLineRunner {

	private static final int CONSUMER_COUNT = 5;

	private static final Logger logger = LoggerFactory.getLogger(LoganalyzerApplication.class);

	private static String filePath = null;

	public static void main(String[] args) {
		if (args != null && args.length > 1 && isValidPath(args[0])) {
			filePath = args[0];
			SpringApplication.run(LoganalyzerApplication.class, args);
		}else{
			logger.error("Invalid commandline argument for file to be read");
		}
			
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Application started with command-line arguments: {}", Arrays.toString(args));

		long startTime = System.nanoTime();

		ExecutorService producerPool = Executors.newFixedThreadPool(1);
		// String filePath = "C:/Users/rpekam/Desktop/stream_emp.txt";
		producerPool.submit(new QueueProducer(filePath));

		// create a pool of consumer threads to parse the lines read
		ExecutorService consumerPool = Executors.newFixedThreadPool(CONSUMER_COUNT);
		for (int i = 0; i < CONSUMER_COUNT; i++) {
			consumerPool.submit(new QueueConsumer());
		}

		producerPool.shutdown();
		consumerPool.shutdown();

		while (!producerPool.isTerminated() && !consumerPool.isTerminated()) {
			DBManager.stopDBServer();
		}

		long endTime = System.nanoTime();
		long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime),
				TimeUnit.NANOSECONDS);
		logger.info("Total elapsed time: " + elapsedTimeInMillis + " ms");
	}

	public static boolean isValidPath(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException | NullPointerException ex) {
			logger.error("Exception in isValidPath() ", ex);
			return false;
		}
		return true;
	}
}
