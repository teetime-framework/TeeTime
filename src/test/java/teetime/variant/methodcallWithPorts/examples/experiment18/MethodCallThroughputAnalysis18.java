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
package teetime.variant.methodcallWithPorts.examples.experiment18;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import teetime.util.ConstructorClosure;
import teetime.variant.explicitScheduling.examples.throughput.TimestampObject;
import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.HeadPipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.UnorderedGrowablePipe;
import teetime.variant.methodcallWithPorts.stage.CollectorSink;
import teetime.variant.methodcallWithPorts.stage.NoopFilter;
import teetime.variant.methodcallWithPorts.stage.ObjectProducer;
import teetime.variant.methodcallWithPorts.stage.Relay;
import teetime.variant.methodcallWithPorts.stage.StartTimestampFilter;
import teetime.variant.methodcallWithPorts.stage.StopTimestampFilter;
import teetime.variant.methodcallWithPorts.stage.basic.distributor.Distributor;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class MethodCallThroughputAnalysis18 extends Analysis {

	private static final int SPSC_INITIAL_CAPACITY = 4;
	private static final int NUM_WORKER_THREADS = Runtime.getRuntime().availableProcessors();

	private int numInputObjects;
	private ConstructorClosure<TimestampObject> inputObjectCreator;
	private int numNoopFilters;

	private final List<List<TimestampObject>> timestampObjectsList = new LinkedList<List<TimestampObject>>();

	private Thread producerThread;

	private Thread[] workerThreads;

	private int numWorkerThreads;

	@Override
	public void init() {
		super.init();
		HeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> producerPipeline = this.buildProducerPipeline(this.numInputObjects,
				this.inputObjectCreator);
		this.producerThread = new Thread(new RunnableStage(producerPipeline));

		this.numWorkerThreads = Math.min(NUM_WORKER_THREADS, this.numWorkerThreads);

		this.workerThreads = new Thread[this.numWorkerThreads];
		for (int i = 0; i < this.workerThreads.length; i++) {
			List<TimestampObject> resultList = new ArrayList<TimestampObject>(this.numInputObjects);
			this.timestampObjectsList.add(resultList);

			HeadPipeline<?, ?> pipeline = this.buildPipeline(producerPipeline, resultList);
			this.workerThreads[i] = new Thread(new RunnableStage(pipeline));
		}
	}

	private HeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> buildProducerPipeline(final int numInputObjects,
			final ConstructorClosure<TimestampObject> inputObjectCreator) {
		final ObjectProducer<TimestampObject> objectProducer = new ObjectProducer<TimestampObject>(numInputObjects, inputObjectCreator);
		Distributor<TimestampObject> distributor = new Distributor<TimestampObject>();

		final HeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> pipeline = new HeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>>();
		pipeline.setFirstStage(objectProducer);
		pipeline.setLastStage(distributor);

		UnorderedGrowablePipe.connect(objectProducer.getOutputPort(), distributor.getInputPort());

		return pipeline;
	}

	/**
	 * @param numNoopFilters
	 * @since 1.10
	 */
	private HeadPipeline<Relay<TimestampObject>, CollectorSink<TimestampObject>> buildPipeline(
			final HeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> previousStage,
			final List<TimestampObject> timestampObjects) {
		Relay<TimestampObject> relay = new Relay<TimestampObject>();
		@SuppressWarnings("unchecked")
		final NoopFilter<TimestampObject>[] noopFilters = new NoopFilter[this.numNoopFilters];
		// create stages
		final StartTimestampFilter startTimestampFilter = new StartTimestampFilter();
		for (int i = 0; i < noopFilters.length; i++) {
			noopFilters[i] = new NoopFilter<TimestampObject>();
		}
		final StopTimestampFilter stopTimestampFilter = new StopTimestampFilter();
		final CollectorSink<TimestampObject> collectorSink = new CollectorSink<TimestampObject>(timestampObjects);

		final HeadPipeline<Relay<TimestampObject>, CollectorSink<TimestampObject>> pipeline = new HeadPipeline<Relay<TimestampObject>, CollectorSink<TimestampObject>>();
		pipeline.setFirstStage(relay);
		pipeline.setLastStage(collectorSink);

		SpScPipe.connect(previousStage.getLastStage().getNewOutputPort(), relay.getInputPort(), SPSC_INITIAL_CAPACITY);

		UnorderedGrowablePipe.connect(relay.getOutputPort(), startTimestampFilter.getInputPort());

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

		for (Thread workerThread : this.workerThreads) {
			workerThread.start();
		}

		try {
			for (Thread workerThread : this.workerThreads) {
				workerThread.join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public List<List<TimestampObject>> getTimestampObjectsList() {
		return this.timestampObjectsList;
	}

	public int getNumWorkerThreads() {
		return this.numWorkerThreads;
	}

	public void setNumWorkerThreads(final int numWorkerThreads) {
		this.numWorkerThreads = numWorkerThreads;
	}

}
