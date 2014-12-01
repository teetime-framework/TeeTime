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
		final List<URL> list = new ArrayList<URL>();

		final Enumeration<URL> systemRes = CLASS_LOADER.getResources(name);
		while (systemRes.hasMoreElements()) { // NOPMD
			list.add(systemRes.nextElement()); // NOPMD
		}
		return list;
	}
}
