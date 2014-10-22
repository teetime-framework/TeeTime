package teetime.framework.pipe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.FileSearcher;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

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

	public static void mergeConfigFiles(final String configFileName, final String mergedFileName) {
		File output = new File(mergedFileName);
		FileOutputStream os;
		try {
			os = new FileOutputStream(output);
		} catch (FileNotFoundException e1) {
			throw new IllegalStateException(e1);
		}
		try {
			Files.touch(output);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		List<URL> files = null;

		try {
			files = FileSearcher.loadResources(configFileName);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		for (URL url : files) {
			try {
				InputStream is = url.openStream();
				ByteStreams.copy(is, os);
				is.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

		}
	}
}
