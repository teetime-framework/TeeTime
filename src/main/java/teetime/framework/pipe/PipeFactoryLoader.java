package teetime.framework.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.FileSearcher;

public final class PipeFactoryLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PipeFactoryLoader.class);

	private PipeFactoryLoader() {
		// utility class
	}

	public static List<IPipeFactory> loadFromStream(final InputStream stream) throws IOException {
		List<IPipeFactory> pipeFactories = new LinkedList<IPipeFactory>();

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
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

	public static List<IPipeFactory> loadPipeFactoriesFromClasspath(final String configFileName) {

		List<URL> files = null;

		try {
			files = FileSearcher.loadResources(configFileName);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return mergeFiles(files);
	}

	public static List<IPipeFactory> mergeFiles(final List<URL> files) {
		ArrayList<IPipeFactory> list = new ArrayList<IPipeFactory>();
		for (URL url : files) {
			try {
				InputStream is = url.openStream();
				list.addAll(loadFromStream(is));
				is.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

		}
		return list;
	}
}
