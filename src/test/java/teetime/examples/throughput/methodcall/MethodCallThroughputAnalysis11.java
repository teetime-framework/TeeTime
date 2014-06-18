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

import teetime.examples.throughput.TimestampObject;
import teetime.examples.throughput.methodcall.stage.CollectorSink;
import teetime.examples.throughput.methodcall.stage.NoopFilter;
import teetime.examples.throughput.methodcall.stage.ObjectProducer;
import teetime.examples.throughput.methodcall.stage.Pipeline;
import teetime.examples.throughput.methodcall.stage.StartTimestampFilter;
import teetime.examples.throughput.methodcall.stage.StopTimestampFilter;
import teetime.framework.core.Analysis;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class MethodCallThroughputAnalysis11 extends Analysis {

	private long numInputObjects;
	private Closure<Void, TimestampObject> inputObjectCreator;
	private int numNoopFilters;
	private List<TimestampObject> timestampObjects;
	private Runnable runnable;

	@Override
	public void init() {
		super.init();
		Pipeline<Void, Object> pipeline = this.buildPipeline(this.numInputObjects, this.inputObjectCreator);
		this.runnable = new RunnableStage(pipeline);
	}

	/**
	 * @param numNoopFilters
	 * @since 1.10
	 */
	private Pipeline<Void, Object> buildPipeline(final long numInputObjects, final Closure<Void, TimestampObject> inputObjectCreator) {
		@SuppressWarnings("unchecked")
		final NoopFilter<TimestampObject>[] noopFilters = new NoopFilter[this.numNoopFilters];
		// create stages
		final ObjectProducer<TimestampObject> objectProducer = new ObjectProducer<TimestampObject>(numInputObjects, inputObjectCreator);
		final StartTimestampFilter startTimestampFilter = new StartTimestampFilter();
		for (int i = 0; i < noopFilters.length; i++) {
			noopFilters[i] = new NoopFilter<TimestampObject>();
		}
		final StopTimestampFilter stopTimestampFilter = new StopTimestampFilter();
		final CollectorSink<TimestampObject> collectorSink = new CollectorSink<TimestampObject>(this.timestampObjects);

		// Relay<TimestampObject> relay = new Relay<TimestampObject>();

		final Pipeline<Void, Object> pipeline = new Pipeline<Void, Object>();
		pipeline.setFirstStage(objectProducer);
		// pipeline.addIntermediateStage(relay);
		pipeline.addIntermediateStage(startTimestampFilter);
		pipeline.addIntermediateStages(noopFilters);
		pipeline.addIntermediateStage(stopTimestampFilter);
		pipeline.setLastStage(collectorSink);

		UnorderedGrowablePipe.connect(objectProducer.getOutputPort(), startTimestampFilter.getInputPort());
		// UnorderedGrowablePipe.connect(objectProducer.getOutputPort(), relay.getInputPort());
		// UnorderedGrowablePipe.connect(relay.getOutputPort(), startTimestampFilter.getInputPort());

		UnorderedGrowablePipe.connect(startTimestampFilter.getOutputPort(), noopFilters[0].getInputPort());
		for (int i = 0; i < noopFilters.length - 1; i++) {
			UnorderedGrowablePipe.connect(noopFilters[i].getOutputPort(), noopFilters[i + 1].getInputPort());
		}
		UnorderedGrowablePipe.connect(noopFilters[noopFilters.length - 1].getOutputPort(), stopTimestampFilter.getInputPort());
		UnorderedGrowablePipe.connect(stopTimestampFilter.getOutputPort(), collectorSink.getInputPort());

		return pipeline;
	}

	@Override
	public void start() {
		super.start();
		this.runnable.run();
	}

	public void setInput(final int numInputObjects, final Closure<Void, TimestampObject> inputObjectCreator) {
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
