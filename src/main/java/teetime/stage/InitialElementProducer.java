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
package teetime.stage;

import java.util.Arrays;
import java.util.Collection;

import teetime.framework.AbstractProducerStage;

/**
 * Represents a producer stage which outputs all of its elements in the first and only execution.
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            the type of the elements
 *
 * @see ResponsiveProducer
 */
public class InitialElementProducer<T> extends AbstractProducerStage<T> {

	// #281: not Iterable<T> since it would forbid to pass java.nio.Path as single object
	private final Collection<T> elements;

	@SafeVarargs
	public InitialElementProducer(final T... elements) {
		this(Arrays.asList(elements));
	}

	public InitialElementProducer(final Collection<T> elements) {
		if (elements == null) {
			throw new IllegalArgumentException("4002 - The given collection must not be null.");
		}
		if (elements.isEmpty()) {
			logger.warn("The given collection is empty! This stage will not output anything.");
		}
		this.elements = elements;
	}

	@Override
	protected void execute() {
		for (final T element : this.elements) {
			this.outputPort.send(element);
		}
		this.workCompleted();
	}

	/**
	 * @return the elements which should be sent.
	 */
	public Collection<T> getElements() {
		return elements;
	}

}
