package teetime.framework.pipe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class PipeFactoryLoaderTest {
	@Test
	public void emptyConfig() throws IOException {
		List<IPipeFactory> list = PipeFactoryLoader.loadPipeFactoriesFromClasspath("data/empty-test.conf");
		Assert.assertEquals(true, list.isEmpty());
	}

	@Test
	public void singleConfig() throws IOException {
		List<IPipeFactory> list = PipeFactoryLoader.loadPipeFactoriesFromClasspath("pipe-factories.conf");
		int lines = this.countLines(new File("conf/pipe-factories.conf"));
		Assert.assertEquals(lines, list.size());
	}

	@Test
	public void multipleConfigs() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
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

		// Second part of the test: PipeFactoryRegistry
		PipeFactoryRegistry pipeRegistry = PipeFactoryRegistry.INSTANCE;

		// Look for the "normal" pipes
		for (String string : readConf(pipeConfig)) {
			IPipeFactory pipeFactory = getClassByString(string);
			IPipeFactory returnedFactory = pipeRegistry.getPipeFactory(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(), pipeFactory.isGrowable());
			Assert.assertEquals(pipeFactory.getClass().getCanonicalName(), returnedFactory.getClass().getCanonicalName());
		}
		// Second "and a half" part
		for (String string : readConf(testConfig)) {
			IPipeFactory pipeFactory = getClassByString(string);
			// Still old factory
			IPipeFactory returnedFactory = pipeRegistry.getPipeFactory(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(), pipeFactory.isGrowable());
			Assert.assertNotEquals(pipeFactory.getClass().getCanonicalName(), returnedFactory.getClass().getCanonicalName());
			// Overload factory and check for the new one
			pipeRegistry.register(pipeFactory);
			returnedFactory = pipeRegistry.getPipeFactory(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(), pipeFactory.isGrowable());
			Assert.assertEquals(pipeFactory.getClass().getCanonicalName(), returnedFactory.getClass().getCanonicalName());
		}
	}

	private IPipeFactory getClassByString(final String string) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = Class.forName(string);
		Class<? extends IPipeFactory> pipeFactoryClass = clazz.asSubclass(IPipeFactory.class);
		return pipeFactoryClass.newInstance();
	}

	private int countLines(final File fileName) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
		int lines = 0;// NOPMD
		try {
			while (fileReader.readLine() != null) {// NOPMD
				lines = lines + 1;
			}
		} finally {
			fileReader.close();// NOPMD
		}
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
