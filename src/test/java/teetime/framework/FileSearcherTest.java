package teetime.framework;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FileSearcherTest {

	@Test
	public void fileInClasspath() throws IOException {
		List<URL> list = FileSearcher.loadResources("pipe-factories.conf");
		Assert.assertEquals(false, list.isEmpty());// NOPMD
	}

	@Test
	public void multipleFiles() throws IOException {
		List<URL> list = FileSearcher.loadResources("LICENSE.txt");
		Assert.assertEquals(true, list.size() > 1);// NOPMD
	}

	@Test
	public void missingFile() throws IOException {
		List<URL> list = FileSearcher.loadResources("filethatdoesnotexistinanyproject.nope");
		Assert.assertEquals(true, list.isEmpty());// NOPMD
	}

}
