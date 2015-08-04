/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
package teetime.stage.taskfarm.monitoring.extraction;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

import org.junit.Test;

public class AbstractMonitoringDataExtractionTest {

	private final static String NEWLINE = System.getProperty("line.separator");

	@Test
	public void toStringTest() {
		AbstractMonitoringDataExtraction extractor = new TestExtractionImpl();
		String result = extractor.extractToString();

		String outputToBe = "foo,poo"
				+ NEWLINE
				+ "poo,foo"
				+ NEWLINE;
		assertThat(result, is(equalTo(outputToBe)));
	}

	@Test
	public void toFileTest() {
		try {
			File file = File.createTempFile("abstractmonitoringdataextractiontest", ".tmp");
			file.deleteOnExit();

			AbstractMonitoringDataExtraction extractor = new TestExtractionImpl();
			extractor.extractToFile(file);

			String result = readFileToString(file.getAbsolutePath());
			String outputToBe = "foo,poo"
					+ NEWLINE
					+ "poo,foo"
					+ NEWLINE;
			assertThat(result, is(equalTo(outputToBe)));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	public void toFileWithPathTest() {
		try {
			String file = System.getProperty("java.io.tmpdir") + "abstractmonitoringdataextractiontest.tmp";

			AbstractMonitoringDataExtraction extractor = new TestExtractionImpl();
			extractor.extractToFile(file);

			String result = readFileToString(file);
			String outputToBe = "foo,poo"
					+ NEWLINE
					+ "poo,foo"
					+ NEWLINE;
			assertThat(result, is(equalTo(outputToBe)));

			File fileToDelete = new File(file);
			if (fileToDelete.exists()) {
				fileToDelete.delete();
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static String readFileToString(final String filepath) throws IOException {
		File file = new File(filepath);
		StringBuilder builder = new StringBuilder((int) file.length());
		Scanner scanner = new Scanner(file);

		try {
			while (scanner.hasNextLine()) {
				builder.append(scanner.nextLine() + NEWLINE);
			}

			return builder.toString();
		} finally {
			scanner.close();
		}
	}

	private class TestExtractionImpl extends AbstractMonitoringDataExtraction {

		public TestExtractionImpl() {
			super(null, null);
		}

		@Override
		protected void extractToWriter(final Writer writer) {
			try {
				addCSVLineToWriter(writer, "foo", "poo");
				addCSVLineToWriter(writer, "poo", "foo");
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

	}

}
