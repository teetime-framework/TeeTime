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
package teetime.examples.throughput.methodcall.stage;

import java.util.concurrent.Callable;

import teetime.examples.throughput.methodcall.ProducerStage;
import teetime.util.list.CommittableQueue;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class ObjectProducer<T> extends ProducerStage<Void, T> {

	private long numInputObjects;
	private Callable<T> inputObjectCreator;

	/**
	 * @since 1.10
	 */
	public ObjectProducer(final long numInputObjects, final Callable<T> inputObjectCreator) {
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	@Override
	public T execute(final Object element) {
		if (this.numInputObjects == 0) {
			this.setReschedulable(false);
			return null;
		}

		try {
			final T newObject = this.inputObjectCreator.call();
			this.numInputObjects--;

			return newObject;
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public long getNumInputObjects() {
		return this.numInputObjects;
	}

	public void setNumInputObjects(final long numInputObjects) {
		this.numInputObjects = numInputObjects;
	}

	public Callable<T> getInputObjectCreator() {
		return this.inputObjectCreator;
	}

	public void setInputObjectCreator(final Callable<T> inputObjectCreator) {
		this.inputObjectCreator = inputObjectCreator;
	}

	// @Override
	// protected void execute3() {
	// if (this.numInputObjects == 0) {
	// // this.getOutputPort().send((T) END_SIGNAL);
	// return;
	// }
	//
	// try {
	// final T newObject = this.inputObjectCreator.call();
	// this.numInputObjects--;
	//
	// // this.getOutputPort().send(newObject);
	// } catch (final Exception e) {
	// throw new IllegalStateException(e);
	// }
	// }

	@Override
	protected void execute4(final CommittableQueue<Void> elements) {
		this.execute5(null);
	}

	@Override
	protected void execute5(final Void element) {
		if (this.numInputObjects == 0) {
			this.setReschedulable(false);
			return;
		}

		try {
			final T newObject = this.inputObjectCreator.call();
			this.numInputObjects--;
			// System.out.println(this.getClass().getSimpleName() + ": sending " + this.numInputObjects);
			this.send(newObject);
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
