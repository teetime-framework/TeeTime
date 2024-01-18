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

class QuicksortTaskContext { // NOPMD

	private final int[] elements;
	private final int[] stack;
	private int top = -1;

	private int lowestIndex;
	private int highestIndex;
	private int pivotIndex;

	public QuicksortTaskContext(final int[] elements) { // NOPMD
		this.elements = elements;
		this.stack = new int[elements.length];
	}

	public int getTop() {
		return top;
	}

	void push(final int lowestIndex, final int highestIndex) {
		stack[++top] = lowestIndex;
		stack[++top] = highestIndex;
	}

	void setRange() {
		this.highestIndex = stack[top--];
		this.lowestIndex = stack[top--];
	}

	public void setPivotIndex(final int pivotIndex) {
		this.pivotIndex = pivotIndex;
	}

	public int getLowestIndex() {
		return lowestIndex;
	}

	public int getHighestIndex() {
		return highestIndex;
	}

	public int getPivotIndex() {
		return pivotIndex;
	}

	public int[] getElements() {
		return elements;
	}
}
