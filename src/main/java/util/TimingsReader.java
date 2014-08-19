package util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

public class TimingsReader {

	private final static Logger LOGGER = LoggerFactory.getLogger(TimingsReader.class);

	public static void main(final String[] args) throws IOException {
		String fileName = args[0];

		LOGGER.trace("Reading " + fileName);
		CharSource charSource = Files.asCharSource(new File(fileName), Charsets.UTF_8);
		ImmutableList<String> lines = charSource.readLines();

		LOGGER.trace("#lines: " + lines.size());

		List<Long> durationsInNs = new LinkedList<Long>();

		LOGGER.trace("Extracting timings from the last half of lines...");
		int startIndex = lines.size() / 2;
		for (int i = startIndex; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] strings = line.split(";");
			Long timing = new Long(strings[1]);
			durationsInNs.add(timing);
		}

		LOGGER.trace("Calculating quantiles...");
		Map<Double, Long> quintiles = StatisticsUtil.calculateQuintiles(durationsInNs);
		LOGGER.info(StatisticsUtil.getQuantilesString(quintiles));

		long confidenceWidth = StatisticsUtil.calculateConfidenceWidth(durationsInNs);
		LOGGER.info("Confidence width: " + confidenceWidth);
	}
}
