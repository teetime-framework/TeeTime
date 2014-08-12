package teetime.variant.methodcallWithPorts.examples.kiekerdays;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import util.StatisticsUtil;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

public class TimingsReader {

	public static void main(final String[] args) throws IOException {
		String fileName = args[0];

		CharSource charSource = Files.asCharSource(new File(fileName), Charsets.UTF_8);
		ImmutableList<String> lines = charSource.readLines();

		System.out.println("#lines: " + lines.size());

		List<Long> durationsInNs = new LinkedList<Long>();

		int startIndex = lines.size() / 2;
		for (int i = startIndex; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] strings = line.split(";");
			Long timing = new Long(strings[1]);
			durationsInNs.add(timing);
		}

		System.out.println("Calculating quantiles...");

		Map<Double, Long> quintiles = StatisticsUtil.calculateQuintiles(durationsInNs);
		System.out.println(StatisticsUtil.getQuantilesString(quintiles));
	}
}
