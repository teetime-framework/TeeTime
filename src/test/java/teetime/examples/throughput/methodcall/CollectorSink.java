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
package teetime.examples.throughput.methodcall;

import java.util.List;

import teetime.util.list.CommittableQueue;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class CollectorSink<T> extends ConsumerStage<T, Void> {

	private static final int THRESHOLD = 10000;

	private final List<T> elements;

	/**
	 * @since 1.10
	 */
	public CollectorSink(final List<T> list) {
		this.elements = list;
	}

	public void execute(final T element) {
		this.elements.add(element);
		if ((this.elements.size() % THRESHOLD) == 0) {
			System.out.println("size: " + this.elements.size());
		}
	}

	// @Override
	// public void execute3() {
	// T element = this.getInputPort().receive();
	//
	// this.elements.add(element);
	// if ((this.elements.size() % THRESHOLD) == 0) {
	// System.out.println("size: " + this.elements.size());
	// }
	// }

	@Override
	protected void execute4(final CommittableQueue<T> elements) {
		T element = elements.removeFromHead();
		this.elements.add(element);
		if ((this.elements.size() % THRESHOLD) == 0) {
			System.out.println("size: " + this.elements.size());
		}
	}

}
