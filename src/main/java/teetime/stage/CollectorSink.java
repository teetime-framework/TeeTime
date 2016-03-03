/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import java.util.ArrayList;
import java.util.List;

import teetime.framework.AbstractConsumerStage;

/**
 * @param <T>
 *            the type of the elements to be collected
 *
 * @author Christian Wulf
 *
 * @since 1.0
 */
public final class CollectorSink<T> extends AbstractConsumerStage<T> {

	private final List<T> elements;

	/**
	 * Creates a new {@link CollectorSink} with an empty {@link ArrayList}.
	 */
	public CollectorSink() {
		this(new ArrayList<T>());
	}

	public CollectorSink(final List<T> list) {
		this.elements = list;
	}

	@Override
	protected void execute(final T element) {
		this.elements.add(element);
	}

	public List<T> getElements() {
		return elements;
	}

}
