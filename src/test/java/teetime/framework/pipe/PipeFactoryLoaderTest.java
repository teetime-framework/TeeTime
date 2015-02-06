/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	public PipeFactoryLoaderTest() {}

	@Test
	public void emptyConfig() throws IOException {
		final List<IPipeFactory> list = PipeFactoryLoader.loadPipeFactoriesFromClasspath("data/empty-test.conf");
		Assert.assertEquals(true, list.isEmpty());
	}

	@Test
	public void singleConfig() throws IOException {
		final List<IPipeFactory> list = PipeFactoryLoader.loadPipeFactoriesFromClasspath("pipe-factories.conf");
		final int lines = Files.readLines(new File("target/classes/pipe-factories.conf"), Charsets.UTF_8).size();
		Assert.assertEquals(lines, list.size());
	}

	@Test
	public void multipleConfigs() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		final List<URL> files = new ArrayList<URL>();
		final File pipeConfig = new File("target/classes/pipe-factories.conf");
		final File testConfig = new File("target/test-classes/data/normal-test.conf");
		files.add(testConfig.toURI().toURL());
		files.add(pipeConfig.toURI().toURL());
		final List<IPipeFactory> pipeFactories = PipeFactoryLoader.mergeFiles(files);

		final List<String> contents = Files.readLines(pipeConfig, Charsets.UTF_8);
		contents.addAll(Files.readLines(testConfig, Charsets.UTF_8));

		// Check if all read factories are contained in one of the files
		for (IPipeFactory iPipeFactory : pipeFactories) {
			Assert.assertTrue(contents.indexOf(iPipeFactory.getClass().getCanonicalName()) != -1);
		}

		// Second part of the test: PipeFactoryRegistry
		final PipeFactoryRegistry pipeRegistry = PipeFactoryRegistry.INSTANCE;
		final ClassForNameResolver<IPipeFactory> classResolver = new ClassForNameResolver<IPipeFactory>(IPipeFactory.class);

		// Look for the "normal" pipes
		for (String className : Files.readLines(pipeConfig, Charsets.UTF_8)) {
			final IPipeFactory pipeFactory = classResolver.classForName(className).newInstance();
			final IPipeFactory returnedFactory = pipeRegistry.getPipeFactory(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(),
					pipeFactory.isGrowable());
			Assert.assertEquals(pipeFactory.getClass().getCanonicalName(), returnedFactory.getClass().getCanonicalName());
		}
		// Second "and a half" part
		for (String className : Files.readLines(testConfig, Charsets.UTF_8)) {
			final IPipeFactory pipeFactory = classResolver.classForName(className).newInstance();
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
