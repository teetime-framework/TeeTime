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
package teetime.framework;

import org.slf4j.Logger;

public abstract class AbstractConsumerStage<I> extends AbstractStage {

	// Creation of this input port requires to use super and null for both parameters
	// in order to invoke the original AbstractStage.createInputPort() instead of
	// the one overridden in this stage.
	protected final InputPort<I> inputPort = super.createInputPort(null, null);

	public AbstractConsumerStage() {
		super();
	}

	/**
	 * @param logger
	 *            a custom logger (potentially shared by multiple stage instances)
	 */
	public AbstractConsumerStage(final Logger logger) {
		super(logger);
	}

	public final InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	protected final void execute() throws Exception {
		final I element = this.getInputPort().receive();
		if (null == element) {
			return;
		}

		this.execute(element);
	}

	/**
	 * Contains the logic of this stage and is invoked (possibly multiple times) by the framework.
	 *
	 * @param element
	 *            the next non-null element from the (only) input port of this stage
	 *
	 * @throws Exception
	 *             arbitrary exception triggered by the logic of this stage
	 */
	protected abstract void execute(I element) throws Exception;

	@Override
	protected <T> InputPort<T> createInputPort(final Class<T> type, final String name) {
		String message = String.format("A subtype of %s cannot have more than one input port. Extend %s instead.",
				AbstractConsumerStage.class.getName(),
				AbstractStage.class.getName());
		throw new IllegalStateException(message);
	}

}
