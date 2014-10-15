/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/
package teetime.examples.experiment01.legacystage;

import java.util.List;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class LegacyCollectorSink<T> {

	// private final InputPort<T> inputPort = this.createInputPort();
	//
	// public final InputPort<T> getInputPort() {
	// return this.inputPort;
	// }

	private final List<T> elements;
	private final int threshold;

	public LegacyCollectorSink(final List<T> list, final int threshold) {
		this.elements = list;
		this.threshold = threshold;
	}

	public LegacyCollectorSink(final List<T> list) {
		this(list, 100000);
	}

	public void onIsPipelineHead() {
		System.out.println("size: " + this.elements.size());
	}

	public Object execute(final T element) {
		this.elements.add(element);

		if ((this.elements.size() % this.threshold) == 0) {
			System.out.println("size: " + this.elements.size());
		}

		return new Object();
		// if (this.elements.size() > 90000) {
		// // System.out.println("size > 90000: " + this.elements.size());
		// }
	}

}
