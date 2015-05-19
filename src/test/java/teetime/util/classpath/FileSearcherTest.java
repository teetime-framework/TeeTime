/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
		final List<URL> urls = FileSearcher.loadResources("pipe-factories.conf");
		Assert.assertEquals(false, urls.isEmpty()); 
	}

	@Test
	public void multipleFiles() throws IOException {
		final List<URL> urls = FileSearcher.loadResources("LICENSE.txt");
		Assert.assertEquals(true, urls.size() > 1); 
	}

	@Test
	public void missingFile() throws IOException {
		final List<URL> urls = FileSearcher.loadResources("filethatdoesnotexistinanyproject.nope");
		Assert.assertEquals(true, urls.isEmpty()); 
	}

}
