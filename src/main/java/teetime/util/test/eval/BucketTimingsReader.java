/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.util.test.eval;

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

public final class BucketTimingsReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(BucketTimingsReader.class);

	private BucketTimingsReader() {}

	public static void main(final String[] args) throws IOException {
		final String fileName = args[0];

		final Long[] currentTimings = new Long[10000];
		int processedLines = 0;
		final List<Long> buckets = new LinkedList<Long>();

		LOGGER.trace("Reading " + fileName);
		final CharSource charSource = Files.asCharSource(new File(fileName), Charsets.UTF_8);
		final BufferedReader bufferedStream = charSource.openBufferedStream();
		String line;
		while (null != (line = bufferedStream.readLine())) {
			final String[] strings = line.split(";");
			final Long timing = new Long(strings[1]);
			currentTimings[processedLines] = timing;
			processedLines++;
			if (currentTimings.length == processedLines) {
				// Long aggregatedTimings = StatisticsUtil.calculateQuintiles(Arrays.asList(currentTimings)).get(0.5);
				final Long aggregatedTimings = StatisticsUtil.calculateAverage(Arrays.asList(currentTimings));
				buckets.add(aggregatedTimings);
				processedLines = 0;
			}
		}

		LOGGER.trace("#buckets: " + buckets.size());

		final List<Long> durationsInNs = buckets.subList(buckets.size() / 2, buckets.size());

		LOGGER.trace("Calculating quantiles...");
		final Map<Double, Long> quintiles = StatisticsUtil.calculateQuintiles(durationsInNs);
		LOGGER.info(StatisticsUtil.getQuantilesString(quintiles));

		final long confidenceWidth = StatisticsUtil.calculateConfidenceWidth(durationsInNs);
		LOGGER.info("Confidence width: " + confidenceWidth);
	}
}
