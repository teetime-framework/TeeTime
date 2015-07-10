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
