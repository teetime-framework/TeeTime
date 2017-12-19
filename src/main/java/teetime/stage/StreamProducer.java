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

import java.util.Iterator;
import java.util.stream.BaseStream;

import teetime.framework.AbstractProducerStage;
import teetime.framework.OutputPort;

/**
 * @author Christian Wulf
 *
 * @since 3.0
 */
public class StreamProducer<T> extends AbstractProducerStage<T> {

	private final BaseStream<T, ?> stream;

	/**
	 * @param stream
	 *            a stream which creates new instances of type <code>T</code>.
	 */
	public StreamProducer(final BaseStream<T, ?> stream) {
		if (stream == null) {
			throw new IllegalArgumentException("stream may not be null");
		}
		this.stream = stream;
	}

	@Override
	protected void execute() {
		final Iterator<T> iterator = stream.iterator();
		final OutputPort<T> localOutputPort = outputPort; // NOPMD

		while (iterator.hasNext()) {
			T newObject = iterator.next();
			localOutputPort.send(newObject);
		}
		this.terminateStage();
	}

}
