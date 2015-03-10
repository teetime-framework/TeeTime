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
package teetime.stage;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import teetime.framework.Analysis;
import teetime.stage.util.CountingMap;

public class WordCountingTest {

	// A excellent test file is src/test/resources/data/hugetext.txt.zip, but make sure to unzip it before
	private static final File testFile = new File("src/test/resources/data/hugetext.txt");

	@Test
	public void test1() throws IOException {
		int threads = 1;
		WordCountingConfiguration wcc = new WordCountingConfiguration(threads, testFile);
		Analysis analysis = new Analysis(wcc);
		analysis.start();
		CountingMap<String> map = wcc.getResult();
		// assertEquals(new Integer(525059), map.get("rsa"));
		assertEquals(new Integer(3813850), map.get("vero"));
		assertEquals(new Integer(7627700), map.get("sit"));
	}

	// private Multiset<String> wordOccurrences(final File file) throws IOException {
	// return HashMultiset.create(
	// Splitter.on(CharMatcher.WHITESPACE)
	// .trimResults()
	// .omitEmptyStrings()
	// .split(Files.asCharSource(testFile, Charsets.UTF_8).read()));
	// }

}
