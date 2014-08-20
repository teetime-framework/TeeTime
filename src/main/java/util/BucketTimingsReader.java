package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

public class BucketTimingsReader {

	private final static Logger LOGGER = LoggerFactory.getLogger(BucketTimingsReader.class);

	public static void main(final String[] args) throws IOException {
		String fileName = args[0];

		Long[] currentTimings = new Long[10000];
		int processedLines = 0;
		List<Long> buckets = new LinkedList<Long>();

		LOGGER.trace("Reading " + fileName);
		CharSource charSource = Files.asCharSource(new File(fileName), Charsets.UTF_8);
		BufferedReader bufferedStream = charSource.openBufferedStream();
		String line;
		while (null != (line = bufferedStream.readLine())) {
			String[] strings = line.split(";");
			Long timing = new Long(strings[1]);
			currentTimings[processedLines] = timing;
			processedLines++;
			if (currentTimings.length == processedLines) {
				// Long aggregatedTimings = StatisticsUtil.calculateQuintiles(Arrays.asList(currentTimings)).get(0.5);
				Long aggregatedTimings = StatisticsUtil.calculateAverage(Arrays.asList(currentTimings));
				buckets.add(aggregatedTimings);
				processedLines = 0;
			}
		}

		LOGGER.trace("#buckets: " + buckets.size());

		List<Long> durationsInNs = buckets.subList(buckets.size() / 2, buckets.size());

		LOGGER.trace("Calculating quantiles...");
		Map<Double, Long> quintiles = StatisticsUtil.calculateQuintiles(durationsInNs);
		LOGGER.info(StatisticsUtil.getQuantilesString(quintiles));

		long confidenceWidth = StatisticsUtil.calculateConfidenceWidth(durationsInNs);
		LOGGER.info("Confidence width: " + confidenceWidth);
	}
}
