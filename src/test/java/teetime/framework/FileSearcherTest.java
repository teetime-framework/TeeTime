package teetime.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryLoader;
import teetime.framework.pipe.PipeFactoryRegistry;

public class FileSearcherTest {

	@Test
	public void fileInClasspath() throws IOException {
		List<URL> list = FileSearcher.loadResources("pipe-factories.conf");
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
		List<IPipeFactory> list = PipeFactoryLoader.loadPipefactoriesFromClasspath("data/empty-test.conf");
		Assert.assertEquals(true, list.isEmpty());
	}

	@Test
	public void singleConfig() throws IOException {
		List<IPipeFactory> list = PipeFactoryLoader.loadPipefactoriesFromClasspath("pipe-factories.conf");
		int lines = this.countLines(new File("conf/pipe-factories.conf"));
		Assert.assertEquals(lines, list.size());
	}

	@Test
	public void multipleConfigs() throws IOException {
		List<URL> files = new ArrayList<URL>();
		File pipeConfig = new File("conf/pipe-factories.conf");
		File testConfig = new File("src/test/resources/data/normal-test.conf");
		files.add(testConfig.toURI().toURL());
		files.add(pipeConfig.toURI().toURL());
		List<IPipeFactory> pipeFactories = PipeFactoryLoader.mergeFiles(files);

		ArrayList<String> contents = readConf(pipeConfig);
		contents.addAll(readConf(testConfig));

		// Check if all read factories are contained in one of the files
		for (IPipeFactory iPipeFactory : pipeFactories) {
			Assert.assertTrue(contents.indexOf(iPipeFactory.getClass().getCanonicalName()) != -1);
		}

		PipeFactoryRegistry pipeRegistry = PipeFactoryRegistry.INSTANCE;
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	private int countLines(final File fileName) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
		int lines = 0;
		while (fileReader.readLine() != null) { // TODO: Finally
			lines = lines + 1;
		}
		fileReader.close();
		return lines;
	}

	private ArrayList<String> readConf(final File fileName) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
		ArrayList<String> list = new ArrayList<String>();
		String line;
		while ((line = fileReader.readLine()) != null) {
			list.add(line);
		}
		fileReader.close();
		return list;
	}
}
