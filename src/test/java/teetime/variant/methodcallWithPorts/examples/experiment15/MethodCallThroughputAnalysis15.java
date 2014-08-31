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
package teetime.variant.methodcallWithPorts.examples.experiment15;

import java.util.List;

import teetime.util.ConstructorClosure;
import teetime.variant.explicitScheduling.examples.throughput.TimestampObject;
import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.HeadPipeline;
import teetime.variant.methodcallWithPorts.framework.core.HeadStage;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.OrderedGrowableArrayPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.Clock;
import teetime.variant.methodcallWithPorts.stage.CollectorSink;
import teetime.variant.methodcallWithPorts.stage.NoopFilter;
import teetime.variant.methodcallWithPorts.stage.ObjectProducer;
import teetime.variant.methodcallWithPorts.stage.StartTimestampFilter;
import teetime.variant.methodcallWithPorts.stage.StopTimestampFilter;
import teetime.variant.methodcallWithPorts.stage.basic.Delay;
import teetime.variant.methodcallWithPorts.stage.basic.Sink;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class MethodCallThroughputAnalysis15 extends Analysis {
	// FIXME this analysis sometimes runs infinitely

	private static final int SPSC_INITIAL_CAPACITY = 4;

	private int numInputObjects;
	private ConstructorClosure<TimestampObject> inputObjectCreator;
	private int numNoopFilters;
	private List<TimestampObject> timestampObjects;

	private Runnable clockRunnable;
	private Runnable runnable;
	private Clock clock;

	@Override
	public void init() {
		super.init();

		HeadPipeline<Clock, Sink<Long>> clockPipeline = this.buildClockPipeline();
		this.clockRunnable = new RunnableStage(clockPipeline);

		HeadStage pipeline = this.buildPipeline(this.clock);
		this.runnable = new RunnableStage(pipeline);
	}

	private HeadPipeline<Clock, Sink<Long>> buildClockPipeline() {
		this.clock = new Clock();

		this.clock.setInitialDelayInMs(100);
		this.clock.setIntervalDelayInMs(100);

		final HeadPipeline<Clock, Sink<Long>> pipeline = new HeadPipeline<Clock, Sink<Long>>();
		pipeline.setFirstStage(this.clock);
		pipeline.setLastStage(new Sink<Long>());

		return pipeline;
	}

	/**
	 * @param numNoopFilters
	 * @return
	 * @since 1.10
	 */
	private HeadPipeline<ObjectProducer<TimestampObject>, CollectorSink<TimestampObject>> buildPipeline(final Clock clock) {
		@SuppressWarnings("unchecked")
		final NoopFilter<TimestampObject>[] noopFilters = new NoopFilter[this.numNoopFilters];
		// create stages
		final ObjectProducer<TimestampObject> objectProducer = new ObjectProducer<TimestampObject>(this.numInputObjects, this.inputObjectCreator);
		Delay<TimestampObject> delay = new Delay<TimestampObject>();
		final StartTimestampFilter startTimestampFilter = new StartTimestampFilter();
		for (int i = 0; i < noopFilters.length; i++) {
			noopFilters[i] = new NoopFilter<TimestampObject>();
		}
		final StopTimestampFilter stopTimestampFilter = new StopTimestampFilter();
		final CollectorSink<TimestampObject> collectorSink = new CollectorSink<TimestampObject>(this.timestampObjects);

		final HeadPipeline<ObjectProducer<TimestampObject>, CollectorSink<TimestampObject>> pipeline = new HeadPipeline<ObjectProducer<TimestampObject>, CollectorSink<TimestampObject>>();
		pipeline.setFirstStage(objectProducer);
		pipeline.setLastStage(collectorSink);

		SpScPipe.connect(clock.getOutputPort(), delay.getTimestampTriggerInputPort(), SPSC_INITIAL_CAPACITY);

		SingleElementPipe.connect(objectProducer.getOutputPort(), startTimestampFilter.getInputPort());
		SingleElementPipe.connect(startTimestampFilter.getOutputPort(), noopFilters[0].getInputPort());
		for (int i = 0; i < noopFilters.length - 1; i++) {
			SingleElementPipe.connect(noopFilters[i].getOutputPort(), noopFilters[i + 1].getInputPort());
		}
		SingleElementPipe.connect(noopFilters[noopFilters.length - 1].getOutputPort(), stopTimestampFilter.getInputPort());
		OrderedGrowableArrayPipe.connect(stopTimestampFilter.getOutputPort(), delay.getInputPort());

		SingleElementPipe.connect(delay.getOutputPort(), collectorSink.getInputPort());

		return pipeline;
	}

	@Override
	public void start() {
		super.start();
		Thread clockThread = new Thread(this.clockRunnable);
		clockThread.start();
		this.runnable.run();
		clockThread.interrupt();
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
