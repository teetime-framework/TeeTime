package teetime.framework;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import teetime.framework.pipe.PipeFactoryLoader;

public class FileSearcher {

	public static void main(final String[] args) throws IOException {

		PipeFactoryLoader.mergeConfigFiles("LICENSE.txt", "test.txt");
	}

	public static List<URL> loadResources(final String name) throws IOException {

		final List<URL> list = new ArrayList<URL>();

		final Enumeration<URL> systemRes = ClassLoader.getSystemClassLoader().getResources(name);
		while (systemRes.hasMoreElements()) {
			list.add(systemRes.nextElement());
		}
		return list;
	}
}
