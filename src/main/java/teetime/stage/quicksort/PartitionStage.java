/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.quicksort;

import teetime.stage.basic.AbstractFilter;

class PartitionStage extends AbstractFilter<QuicksortTaskContext> {

	@Override
	protected void execute(final QuicksortTaskContext context) {
		int[] elements = context.getElements();
		int lowestIndex = context.getLowestIndex();
		int highestIndex = context.getHighestIndex();

		int pivotIndex = partition(elements, lowestIndex, highestIndex);

		context.setPivotIndex(pivotIndex);

		outputPort.send(context);
	}

	private int partition(final int[] elements, final int lowestIndex, final int highestIndex) {
		int x = elements[highestIndex];
		int i = (lowestIndex - 1);

		for (int j = lowestIndex; j <= highestIndex - 1; j++) {
			if (elements[j] <= x) {
				i++;
				// swap elements[i] and elements[j]
				swap(elements, i, j);
			}
		}
		// swap elements[i+1] and elements[highestIndex]
		swap(elements, i + 1, highestIndex);
		return (i + 1);
	}

	void swap(final int[] elements, final int i, final int j) {
		int t = elements[i];
		elements[i] = elements[j];
		elements[j] = t;
	}
}
