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

import static org.junit.Assert.assertArrayEquals;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import teetime.framework.DynamicConfigurationContext;
import teetime.stage.util.QuicksortProblem;

/**
 * @author Robin Mohr
 */
public class QuicksortStageTest {

	private final int numberOfElements = 5;
	private final int low = 0;
	private final int[] arr = generateRandomNumbers(numberOfElements);
	private final int[] sortedArr = arr;

	QuicksortProblem qsp = new QuicksortProblem(low, numberOfElements, arr);
	private final List<QuicksortProblem> input = new ArrayList<QuicksortProblem>();
	private final List<QuicksortProblem> result = new ArrayList<QuicksortProblem>();
	private final DynamicConfigurationContext context = new DynamicConfigurationContext(); // FIXME need to provide context for testing environment

	@Test
	public void quicksortStageShouldSortInputArray() {
		input.add(qsp);

		final QuicksortStage qs = new QuicksortStage(context);

		quickSort(sortedArr, low, numberOfElements);

		test(qs).and().send(input).to(qs.getInputPort()).and().receive(result).from(qs.getOutputPort()).start();

		assertArrayEquals(result.get(0).getArr(), sortedArr);
	}

	private int[] generateRandomNumbers(final int n) {

		int[] arr = new int[n + 1];
		Random random = new Random();

		for (int i = 0; i <= n; i++) {
			arr[i] = (random.nextInt(n * 10));
		}

		return arr;
	}

	public static void quickSort(final int[] arr, final int low, final int high) {
		if (arr == null || arr.length == 0) {
			return;
		}

		if (low >= high) {
			return;
		}

		// pick the pivot
		int middle = low + (high - low) / 2;
		int pivot = arr[middle];

		// make left < pivot and right > pivot
		int i = low, j = high;
		while (i <= j) {
			while (arr[i] < pivot) {
				i++;
			}

			while (arr[j] > pivot) {
				j--;
			}

			if (i <= j) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
				i++;
				j--;
			}
		}

		// recursively sort two sub parts
		if (low < j) {
			quickSort(arr, low, j);
		}

		if (high > i) {
			quickSort(arr, i, high);
		}
	}
}
