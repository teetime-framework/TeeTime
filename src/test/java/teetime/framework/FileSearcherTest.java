package teetime.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FileSearcherTest {

	@Test
	public void fileInClasspath() throws IOException {
		List<URL> list = FileSearcher.loadResources("data/input.txt");
		Assert.assertEquals(false, list.isEmpty());
	}

	@Test
	public void multipleFiles() throws IOException {
		List<URL> list = FileSearcher.loadResources("LICENSE.txt");
		Assert.assertEquals(true, list.size() > 1);
	}

	@Test
	public void missingFile() throws IOException {
		List<URL> list = FileSearcher.loadResources("filethatdoesnotexistinanyproject.nope");
		Assert.assertEquals(true, list.isEmpty());
	}

	@Test
	public void emptyConfig() throws IOException {
		// List<IPipeFactory> list = PipeFactoryLoader.mergeConfigFiles();
	}

	private int countLines(final File fileName) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(fileName));
		int lines = 0;
		while (r.readLine() != null) {
			lines++;
		}
		r.close();
		return lines;
	}
}
