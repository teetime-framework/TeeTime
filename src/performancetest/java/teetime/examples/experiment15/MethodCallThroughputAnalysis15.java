/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
package teetime.examples.experiment15;

import java.util.List;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.OldHeadPipeline;
import teetime.framework.RunnableProducerStage;
import teetime.framework.Stage;
import teetime.framework.exceptionHandling.IgnoringExceptionListener;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.OrderedGrowableArrayPipe;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.framework.pipe.SpScPipe;
import teetime.stage.Clock;
import teetime.stage.CollectorSink;
import teetime.stage.NoopFilter;
import teetime.stage.ObjectProducer;
import teetime.stage.StartTimestampFilter;
import teetime.stage.StopTimestampFilter;
import teetime.stage.basic.Delay;
import teetime.stage.basic.Sink;
import teetime.util.ConstructorClosure;
import teetime.util.TimestampObject;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class MethodCallThroughputAnalysis15 extends AnalysisConfiguration {
	// FIXME this analysis sometimes runs infinitely

	private static final int SPSC_INITIAL_CAPACITY = 4;

	private final IPipeFactory intraThreadPipeFactory;

	private int numInputObjects;
	private ConstructorClosure<TimestampObject> inputObjectCreator;
	private int numNoopFilters;
	private List<TimestampObject> timestampObjects;

	private Runnable clockRunnable;
	private Runnable runnable;
	private Clock clock;

	public MethodCallThroughputAnalysis15() {
		intraThreadPipeFactory = PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
	}

	public void init() {
		OldHeadPipeline<Clock, Sink<Long>> clockPipeline = this.buildClockPipeline();
		this.clockRunnable = new RunnableProducerStage(clockPipeline, new IgnoringExceptionListener());

		Stage pipeline = this.buildPipeline(this.clock);
		this.runnable = new RunnableProducerStage(pipeline, new IgnoringExceptionListener());
	}

	private OldHeadPipeline<Clock, Sink<Long>> buildClockPipeline() {
		this.clock = new Clock();

		this.clock.setInitialDelayInMs(100);
		this.clock.setIntervalDelayInMs(100);

		final OldHeadPipeline<Clock, Sink<Long>> pipeline = new OldHeadPipeline<Clock, Sink<Long>>();
		pipeline.setFirstStage(this.clock);
		pipeline.setLastStage(new Sink<Long>());

		return pipeline;
	}

	/**
	 * @param numNoopFilters
	 * @return
	 * @since 1.10
	 */
	private OldHeadPipeline<ObjectProducer<TimestampObject>, CollectorSink<TimestampObject>> buildPipeline(final Clock clock) {
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

		final OldHeadPipeline<ObjectProducer<TimestampObject>, CollectorSink<TimestampObject>> pipeline = new OldHeadPipeline<ObjectProducer<TimestampObject>, CollectorSink<TimestampObject>>();
		pipeline.setFirstStage(objectProducer);
		pipeline.setLastStage(collectorSink);

		SpScPipe.connect(clock.getOutputPort(), delay.getTimestampTriggerInputPort(), SPSC_INITIAL_CAPACITY);

		intraThreadPipeFactory.create(objectProducer.getOutputPort(), startTimestampFilter.getInputPort());
		intraThreadPipeFactory.create(startTimestampFilter.getOutputPort(), noopFilters[0].getInputPort());
		for (int i = 0; i < noopFilters.length - 1; i++) {
			intraThreadPipeFactory.create(noopFilters[i].getOutputPort(), noopFilters[i + 1].getInputPort());
		}
		intraThreadPipeFactory.create(noopFilters[noopFilters.length - 1].getOutputPort(), stopTimestampFilter.getInputPort());
		OrderedGrowableArrayPipe.connect(stopTimestampFilter.getOutputPort(), delay.getInputPort());

		intraThreadPipeFactory.create(delay.getOutputPort(), collectorSink.getInputPort());

		return pipeline;
	}

	public void start() {
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
