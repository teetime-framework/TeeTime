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

import java.util.List;

import teetime.util.ConstructorClosure;
import teetime.util.TimestampObject;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class MethodCallThroughputAnalysis1 {

	private long numInputObjects;
	private ConstructorClosure<TimestampObject> inputObjectCreator;
	private int numNoopFilters;
	private List<TimestampObject> timestampObjects;
	private Runnable runnable;

	public void init() {
		this.runnable = this.buildPipeline();
	}

	/**
	 * @param numNoopFilters
	 * @since 1.10
	 */
	private Runnable buildPipeline() {
		@SuppressWarnings("unchecked")
		final LegacyNoopFilter<TimestampObject>[] noopFilters = new LegacyNoopFilter[this.numNoopFilters];
		// create stages
		final LegacyObjectProducer<TimestampObject> objectProducer = new LegacyObjectProducer<TimestampObject>(this.numInputObjects, this.inputObjectCreator);
		final LegacyStartTimestampFilter startTimestampFilter = new LegacyStartTimestampFilter();
		for (int i = 0; i < noopFilters.length; i++) {
			noopFilters[i] = new LegacyNoopFilter<TimestampObject>();
		}
		final LegacyStopTimestampFilter stopTimestampFilter = new LegacyStopTimestampFilter();
		final LegacyCollectorSink<TimestampObject> collectorSink = new LegacyCollectorSink<TimestampObject>(this.timestampObjects);

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					TimestampObject object = objectProducer.execute();
					if (object == null) {
						return;
					}

					object = startTimestampFilter.execute(object);
					for (final LegacyNoopFilter<TimestampObject> noopFilter : noopFilters) {
						object = noopFilter.execute(object);
					}
					object = stopTimestampFilter.execute(object);
					collectorSink.execute(object);
				}
			}
		};
		return runnable;
	}

	public void start() {
		this.runnable.run();
	}

	public void setInput(final int numInputObjects, final ConstructorClosure<TimestampObject> inputObjectCreator) {
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	public int getNumNoopFilters() {
		return this.numNoopFilters;
	}

	public void setNumNoopFilters(final int numNoopFilters) {
		this.numNoopFilters = numNoopFilters;
	}

	public List<TimestampObject> getTimestampObjects() {
		return this.timestampObjects;
	}

	public void setTimestampObjects(final List<TimestampObject> timestampObjects) {
		this.timestampObjects = timestampObjects;
	}
}
