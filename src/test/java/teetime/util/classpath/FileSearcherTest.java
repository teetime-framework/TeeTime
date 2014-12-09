package teetime.util.classpath;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import teetime.util.classpath.FileSearcher;

public class FileSearcherTest {

	public FileSearcherTest() {}

	@Test
	public void fileInClasspath() throws IOException {
		final List<URL> list = FileSearcher.loadResources("pipe-factories.conf");
		Assert.assertEquals(false, list.isEmpty()); // NOPMD
	}

	@Test
	public void multipleFiles() throws IOException {
		final List<URL> list = FileSearcher.loadResources("LICENSE.txt");
		Assert.assertEquals(true, list.size() > 1); // NOPMD
	}

	@Test
	public void missingFile() throws IOException {
		final List<URL> list = FileSearcher.loadResources("filethatdoesnotexistinanyproject.nope");
		Assert.assertEquals(true, list.isEmpty()); // NOPMD
	}

}
