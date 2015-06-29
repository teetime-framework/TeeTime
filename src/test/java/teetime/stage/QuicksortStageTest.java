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
package teetime.stage;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertArrayEquals;
import static teetime.framework.test.StageTester.test;

import java.util.Random;

import org.junit.Test;

import teetime.framework.ConfigurationContext;

/**
 * @author Robin Mohr
 */
public class QuicksortStageTest {

	ConfigurationContext context = null; // FIXME need to provide context for testing environment

	@Test
	public void quicksortStageShouldSortInputArray() {
		final int[] input = new int[1500];

		// setup random array
		for (int i = 1; i <= 15; i++)
		{
			input[i - 1] = i;
		}

		Random rg = new Random();
		int tmp;
		for (int i = 14; i > 0; i--)
		{
			int r = rg.nextInt(i + 1);
			tmp = input[r];
			input[r] = input[i];
			input[i] = tmp;
		}

		final int[] result = input;
		final QuicksortStage qs = new QuicksortStage(context);

		test(qs).and().send(input).to(qs.getInputPort()).and().receive(result)).from(qs.getOutputPort()).start(); //FIXME maybe setupt QS stage not using arrays or modify testing methods

		assertArrayEquals(input, result);
	}
}
