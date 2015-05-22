package teetime.stage.io;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.Test;

import teetime.framework.test.StageTester;
import teetime.stage.basic.merger.Merger;

public class PrinterTest {

	@Test
	public void testDefaultUsage() {
		Printer<Merger<Object>> printer = new Printer<Merger<Object>>();
		Merger<Object> testStage = new Merger<Object>();

		final StringBuilder sb = new StringBuilder(128);
		sb.append(printer.getId());
		sb.append('(').append(testStage.getClass().getSimpleName()).append(") ").append(testStage.toString());

		PrintStream systemOut = System.out;
		final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));

		StageTester.test(printer).send(Arrays.asList(testStage)).to(printer.getInputPort()).start();
		assertThat(outContent.toString(), containsString(sb.toString()));

		System.setOut(systemOut);
	}
}
