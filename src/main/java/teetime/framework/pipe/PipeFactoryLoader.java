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

import teetime.util.classpath.FileSearcher;

public final class PipeFactoryLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PipeFactoryLoader.class);

	private PipeFactoryLoader() {
		// utility class
	}

	public static List<IPipeFactory> loadFromStream(final InputStream stream) throws IOException {
		final List<IPipeFactory> pipeFactories = new LinkedList<IPipeFactory>();

		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		try {
			String line;
			while (null != (line = bufferedReader.readLine())) {
				try {
					line = line.trim();
					if (!line.isEmpty()) {
						final Class<?> clazz = Class.forName(line);
						final Class<? extends IPipeFactory> pipeFactoryClass = clazz.asSubclass(IPipeFactory.class);
						final IPipeFactory pipeFactory = pipeFactoryClass.newInstance();
						pipeFactories.add(pipeFactory);
					}
				} catch (ClassNotFoundException e) {
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("Could not find class: " + line, e);
					}
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
		final List<IPipeFactory> list = new ArrayList<IPipeFactory>();
		for (URL url : files) {
			try {
				final InputStream is = url.openStream();
				list.addAll(loadFromStream(is));
				is.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

		}
		return list;
	}
}
