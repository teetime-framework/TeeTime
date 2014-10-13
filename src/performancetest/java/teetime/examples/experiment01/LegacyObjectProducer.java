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
package teetime.examples.experiment01;

import teetime.util.ConstructorClosure;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class LegacyObjectProducer<T> {

	private long numInputObjects;
	private ConstructorClosure<T> inputObjectCreator;

	/**
	 * @since 1.10
	 */
	public LegacyObjectProducer(final long numInputObjects, final ConstructorClosure<T> inputObjectCreator) {
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	public long getNumInputObjects() {
		return this.numInputObjects;
	}

	public void setNumInputObjects(final long numInputObjects) {
		this.numInputObjects = numInputObjects;
	}

	public ConstructorClosure<T> getInputObjectCreator() {
		return this.inputObjectCreator;
	}

	public void setInputObjectCreator(final ConstructorClosure<T> inputObjectCreator) {
		this.inputObjectCreator = inputObjectCreator;
	}

	protected T execute() {
		T newObject = this.inputObjectCreator.create();
		this.numInputObjects--;

		return newObject;

	}

}
