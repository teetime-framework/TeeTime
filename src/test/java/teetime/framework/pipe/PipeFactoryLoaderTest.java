package teetime.framework.pipe;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import teetime.util.classpath.ClassForNameResolver;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class PipeFactoryLoaderTest {

	@Test
	public void emptyConfig() throws IOException {
		List<IPipeFactory> list = PipeFactoryLoader.loadPipeFactoriesFromClasspath("data/empty-test.conf");
		Assert.assertEquals(true, list.isEmpty());
	}

	@Test
	public void singleConfig() throws IOException {
		List<IPipeFactory> list = PipeFactoryLoader.loadPipeFactoriesFromClasspath("pipe-factories.conf");
		int lines = Files.readLines(new File("conf/pipe-factories.conf"), Charsets.UTF_8).size();
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

		List<String> contents = Files.readLines(pipeConfig, Charsets.UTF_8);
		contents.addAll(Files.readLines(testConfig, Charsets.UTF_8));

		// Check if all read factories are contained in one of the files
		for (IPipeFactory iPipeFactory : pipeFactories) {
			Assert.assertTrue(contents.indexOf(iPipeFactory.getClass().getCanonicalName()) != -1);
		}

		// Second part of the test: PipeFactoryRegistry
		PipeFactoryRegistry pipeRegistry = PipeFactoryRegistry.INSTANCE;
		ClassForNameResolver<IPipeFactory> classResolver = new ClassForNameResolver<IPipeFactory>(IPipeFactory.class);

		// Look for the "normal" pipes
		for (String className : Files.readLines(pipeConfig, Charsets.UTF_8)) {
			IPipeFactory pipeFactory = classResolver.classForName(className).newInstance();
			IPipeFactory returnedFactory = pipeRegistry.getPipeFactory(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(), pipeFactory.isGrowable());
			Assert.assertEquals(pipeFactory.getClass().getCanonicalName(), returnedFactory.getClass().getCanonicalName());
		}
		// Second "and a half" part
		for (String className : Files.readLines(testConfig, Charsets.UTF_8)) {
			IPipeFactory pipeFactory = classResolver.classForName(className).newInstance();
			// Still old factory
			IPipeFactory returnedFactory = pipeRegistry.getPipeFactory(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(), pipeFactory.isGrowable());
			Assert.assertNotEquals(pipeFactory.getClass().getCanonicalName(), returnedFactory.getClass().getCanonicalName());
			// Overload factory and check for the new one
			pipeRegistry.register(pipeFactory);
			returnedFactory = pipeRegistry.getPipeFactory(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(), pipeFactory.isGrowable());
			Assert.assertEquals(pipeFactory.getClass().getCanonicalName(), returnedFactory.getClass().getCanonicalName());
		}
	}

}
