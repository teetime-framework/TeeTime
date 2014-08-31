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
package teetime.variant.methodcallWithPorts.examples.experiment09;

import java.util.List;

import teetime.util.ConstructorClosure;
import teetime.variant.explicitScheduling.examples.throughput.TimestampObject;
import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.HeadPipeline;
import teetime.variant.methodcallWithPorts.framework.core.HeadStage;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.Pipe;
import teetime.variant.methodcallWithPorts.stage.CollectorSink;
import teetime.variant.methodcallWithPorts.stage.NoopFilter;
import teetime.variant.methodcallWithPorts.stage.ObjectProducer;
import teetime.variant.methodcallWithPorts.stage.StartTimestampFilter;
import teetime.variant.methodcallWithPorts.stage.StopTimestampFilter;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class MethodCallThroughputAnalysis9 extends Analysis {

	private int numInputObjects;
	private ConstructorClosure<TimestampObject> inputObjectCreator;
	private int numNoopFilters;
	private List<TimestampObject> timestampObjects;
	private Runnable runnable;

	@Override
	public void init() {
		super.init();
		HeadStage pipeline = this.buildPipeline();
		this.runnable = new RunnableStage(pipeline);
	}

	/**
	 * @param numNoopFilters
	 * @return
	 * @since 1.10
	 */
	private HeadPipeline<ObjectProducer<TimestampObject>, CollectorSink<TimestampObject>> buildPipeline() {
		@SuppressWarnings("unchecked")
		final NoopFilter<TimestampObject>[] noopFilters = new NoopFilter[this.numNoopFilters];
		// create stages
		final ObjectProducer<TimestampObject> objectProducer = new ObjectProducer<TimestampObject>(this.numInputObjects, this.inputObjectCreator);
		final StartTimestampFilter startTimestampFilter = new StartTimestampFilter();
		for (int i = 0; i < noopFilters.length; i++) {
			noopFilters[i] = new NoopFilter<TimestampObject>();
		}
		final StopTimestampFilter stopTimestampFilter = new StopTimestampFilter();
		final CollectorSink<TimestampObject> collectorSink = new CollectorSink<TimestampObject>(this.timestampObjects);

		final HeadPipeline<ObjectProducer<TimestampObject>, CollectorSink<TimestampObject>> pipeline = new HeadPipeline<ObjectProducer<TimestampObject>, CollectorSink<TimestampObject>>();
		pipeline.setFirstStage(objectProducer);
		pipeline.setLastStage(collectorSink);

		Pipe.connect(objectProducer.getOutputPort(), startTimestampFilter.getInputPort());
		Pipe.connect(startTimestampFilter.getOutputPort(), noopFilters[0].getInputPort());
		for (int i = 0; i < noopFilters.length - 1; i++) {
			Pipe.connect(noopFilters[i].getOutputPort(), noopFilters[i + 1].getInputPort());
		}
		Pipe.connect(noopFilters[noopFilters.length - 1].getOutputPort(), stopTimestampFilter.getInputPort());
		Pipe.connect(stopTimestampFilter.getOutputPort(), collectorSink.getInputPort());

		return pipeline;
	}

	@Override
	public void start() {
		super.start();
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
