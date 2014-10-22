package teetime.framework;

import java.io.IOException;
import java.util.StringTokenizer;

public class FileSearcher {

	public static void main(final String[] args) throws IOException {
		final String classpath = System.getProperty("java.class.path");
		final String pathSeparator = System.getProperty("path.separator");
		// System.out.println(classpath);
		// System.out.println(pathSeparator);

		StringTokenizer st = new StringTokenizer(classpath, pathSeparator);
		while (st.hasMoreTokens()) {
			System.out.println(st.nextToken());// st.nextToken());
		}
	}

}
