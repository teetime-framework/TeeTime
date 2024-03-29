/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.examples.wordcounter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.primitives.Longs;

import teetime.framework.AbstractPort;
import teetime.framework.Execution;
import teetime.framework.performancelogging.ActivationStateLogger;
import teetime.framework.pipe.IMonitorablePipe;
import teetime.stage.basic.distributor.strategy.NonBlockingRoundRobinStrategy;
import teetime.stage.util.CountingMap;
import teetime.util.StopWatch;

public class LoggedWordCounterTest { // NOPMD

	private static final Logger LOGGER = LoggerFactory.getLogger(WordCounterTest.class);

	public static void writeTimingsToFile(final File outputFile, final long[] timings) throws IOException {
		final PrintStream ps = new PrintStream(Files.newOutputStream(outputFile.toPath(), StandardOpenOption.APPEND),
				false, "UTF-8");
		try {
			final Joiner joiner = Joiner.on(' ');
			final String timingsString = joiner.join(Longs.asList(timings));
			ps.println(timingsString);
		} finally {
			ps.close();
		}
	}

	public static void main(final String[] args) throws IOException {
		int numWorkerThreads;
		try {
			numWorkerThreads = Integer.valueOf(args[0]);
		} catch (final NumberFormatException e) {
			numWorkerThreads = 3;
		}
		LOGGER.info("# worker threads: {}", numWorkerThreads);

		int numWarmUps;
		try {
			numWarmUps = Integer.valueOf(args[1]);
		} catch (final NumberFormatException e) {
			numWarmUps = 1;
		}
		LOGGER.info("# warm ups: {}", numWarmUps);

		final long[] timings = new long[1];

		final String fileName = args[2];
		final File testFile = new File(fileName);

		final StopWatch stopWatch = new StopWatch();

		for (int i = 0; i < numWarmUps; i++) {
			LOGGER.info("Warm up #{}", i);
			final LoggedWordCounterConfiguration wcc = new LoggedWordCounterConfiguration(numWorkerThreads, testFile);
			final Execution<?> analysis = new Execution<LoggedWordCounterConfiguration>(wcc);

			stopWatch.start();
			analysis.executeBlocking();
			stopWatch.end();

			LOGGER.info("duration: {} secs", TimeUnit.NANOSECONDS.toSeconds(stopWatch.getDurationInNs()));
		}

		LOGGER.info("Starting analysis...");
		final WordCounterConfiguration wcc = new WordCounterConfiguration(numWorkerThreads, testFile);
		final Execution<?> analysis = new Execution<WordCounterConfiguration>(wcc);

		wcc.getMonitoringThread().start();
		stopWatch.start();
		analysis.executeBlocking();
		stopWatch.end();
		wcc.getMonitoringThread().terminate();

		LOGGER.info("duration: {} milli secs", TimeUnit.NANOSECONDS.toMillis(stopWatch.getDurationInNs()));
		timings[0] = stopWatch.getDurationInNs();

		// System.out.println("exceptions: " + exceptions);

		final CountingMap<String> map = wcc.getResult();
		System.out.println("vero: " + (map.get("vero") == 3813850) + "->" + map.get("vero") + " should be " + 3813850); // NOPMD
		System.out.println("sit: " + (map.get("sit") == 7627700) + "->" + map.get("sit") + " should be " + 7627700); // NOPMD

		final File outputFile = new File("timings.txt");
		writeTimingsToFile(outputFile, timings);

		System.out.println("distributor pipes:"); // NOPMD
		for (final AbstractPort<?> port : wcc.getDistributorPorts()) {
			final IMonitorablePipe spscPipe = (IMonitorablePipe) port.getPipe();
			System.out.println("numWaits: " + spscPipe.getNumWaits()); // NOPMD
		}

		System.out.println("merger pipes:"); // NOPMD
		for (final AbstractPort<?> port : wcc.getMergerPorts()) {
			final IMonitorablePipe spscPipe = (IMonitorablePipe) port.getPipe();
			System.out.println("numWaits: " + spscPipe.getNumWaits()); // NOPMD
		}

		System.out.println("distributor waits: " // NOPMD
				+ ((NonBlockingRoundRobinStrategy) wcc.getDistributor().getStrategy()).getNumWaits());

		System.out.println(ActivationStateLogger.getInstance()); // NOPMD
		ActivationStateLogger.getInstance().logToFile();
	}
}
