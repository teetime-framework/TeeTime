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
package teetime.framework.test;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import teetime.framework.OutputPort;
import teetime.stage.CollectorSink;

class Produces<T, O extends OutputPort<T>> extends BaseMatcher<O> {

	private final Matcher<Iterable<? extends T>> matcher;

	@SafeVarargs
	public Produces(final T... expectedElements) {
		this.matcher = Matchers.contains(expectedElements);
	}

	public Produces() {
		this.matcher = Matchers.emptyIterable();
	}

	@Override
	public boolean matches(final Object item) {
		if (!(item instanceof OutputPort)) {
			String message = String.format("%s", item);
			throw new IllegalArgumentException(message);
		}
		@SuppressWarnings("unchecked")
		OutputPort<T> outputPort = (OutputPort<T>) item;
		CollectorSink<T> collectorSink = StageFactory.getSinkFromOutputPort(outputPort);

		return matcher.matches(collectorSink.getElements());
		// the following invocation does not work with List<int[]>
		// return expectedElementsList.equals(collectorSink.getElements());
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("to produce ");
		matcher.describeTo(description);
	}

	@Override
	public void describeMismatch(final Object item, final Description description) {
		if (!(item instanceof OutputPort)) {
			String message = String.format("%s", item);
			throw new IllegalArgumentException(message);
		}
		@SuppressWarnings("unchecked")
		OutputPort<T> outputPort = (OutputPort<T>) item;
		CollectorSink<T> collectorSink = StageFactory.getSinkFromOutputPort(outputPort);

		List<T> actualElements = collectorSink.getElements();
		description.appendText("has produced ").appendValueList("[", ", ", "]", actualElements);
	}

}
