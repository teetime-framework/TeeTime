package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipeFactoryLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PipeFactoryLoader.class);

	private PipeFactoryLoader() {
		// utility class
	}

	public static List<IPipeFactory> loadFromFile(final String fileName) throws IOException {
		List<IPipeFactory> pipeFactories = new LinkedList<IPipeFactory>();

		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		try {
			String line;
			while (null != (line = bufferedReader.readLine())) {
				try {
					line = line.trim();
					if (!line.isEmpty()) {
						Class<?> clazz = Class.forName(line);
						Class<? extends IPipeFactory> pipeFactoryClass = clazz.asSubclass(IPipeFactory.class);
						IPipeFactory pipeFactory = pipeFactoryClass.newInstance();
						pipeFactories.add(pipeFactory);
					}
				} catch (ClassNotFoundException e) {
					LOGGER.warn("Could not find class: " + line, e);
				} catch (InstantiationException e) {
					LOGGER.warn("Could not instantiate pipe factory", e);
				} catch (IllegalAccessException e) {
					LOGGER.warn("Could not instantiate pipe factory", e);
				}
			}
		} finally {
			bufferedReader.close();
		}

		return pipeFactories;
	}
}
