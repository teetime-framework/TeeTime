package teetime.framework;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class FileSearcher {

	public static List<URL> loadResources(final String name) throws IOException {

		final List<URL> list = new ArrayList<URL>();

		final Enumeration<URL> systemRes = ClassLoader.getSystemClassLoader().getResources(name);
		while (systemRes.hasMoreElements()) {
			list.add(systemRes.nextElement());
		}
		return list;
	}
}
