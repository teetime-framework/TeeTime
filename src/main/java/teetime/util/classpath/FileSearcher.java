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
package teetime.util.classpath;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class FileSearcher {

	private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

	private FileSearcher() {
		// utility class
	}

	public static List<URL> loadResources(final String name) throws IOException {
		final List<URL> urls = new ArrayList<URL>();

		final Enumeration<URL> systemRes = CLASS_LOADER.getResources(name);
		while (systemRes.hasMoreElements()) { 
			urls.add(systemRes.nextElement()); 
		}
		return urls;
	}
}
