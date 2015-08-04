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
