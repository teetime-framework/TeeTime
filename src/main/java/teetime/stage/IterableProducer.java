/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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

import java.util.Arrays;
import java.util.List;

import teetime.framework.AbstractProducerStage;

public final class IterableProducer<T> extends AbstractProducerStage<T> {

	private Iterable<T> iter;

	public IterableProducer(final T... elements) {
		this.iter = Arrays.asList(elements);
	}

	public <O extends Iterable<T>> IterableProducer(final O iter) {
		this.iter = iter;
	}

	@Override
	protected void execute() {
		for (final T i : this.iter) {
			this.outputPort.send(i);
		}
		this.terminate();
	}

	public void setIter(final Iterable<T> iter) {
		this.iter = iter;
	}

	@Override
	public void onStarting() throws Exception {
		if (iter == null) {
			throw new IllegalArgumentException("iter must not be null");
		}
		super.onStarting();
	}

	public static void main(final String[] args) {
		// int[] array = new int[] { 0, 0, 0 };
		// new IterableProducer<Integer>(array);
		//
		// new InitialElementProducer<Integer>(array);

		Integer[] array = new Integer[] { 0, 0, 0 };
		new IterableProducer<Integer>(array);

		new IterableProducer<Integer>(0, 0, 0);

		List<Integer> iterable = Arrays.asList(0, 0, 0);
		new IterableProducer<Integer>(iterable);
	}

}
