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
package teetime.examples.experiment17;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import teetime.framework.HeadPipeline;
import teetime.framework.RunnableStage;
import teetime.framework.Stage;
import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.RelayTestPipe;
import teetime.framework.pipe.UnorderedGrowablePipe;
import teetime.framework.signal.TerminatingSignal;
import teetime.stage.CollectorSink;
import teetime.stage.NoopFilter;
import teetime.stage.ObjectProducer;
import teetime.stage.Relay;
import teetime.stage.StartTimestampFilter;
import teetime.stage.StopTimestampFilter;
import teetime.stage.basic.Sink;
import teetime.stage.basic.distributor.Distributor;
import teetime.util.ConstructorClosure;
import teetime.util.TimestampObject;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class MethodCallThroughputAnalysis17 {

	private static final int SPSC_INITIAL_CAPACITY = 100100;
	private static final int NUM_WORKER_THREADS = Runtime.getRuntime().availableProcessors();

	private int numInputObjects;
	private ConstructorClosure<TimestampObject> inputObjectCreator;
	private int numNoopFilters;

	private final PipeFactoryRegistry pipeFactory = PipeFactoryRegistry.INSTANCE;
	private final List<List<TimestampObject>> timestampObjectsList = new LinkedList<List<TimestampObject>>();

	private Thread producerThread;
	private Thread[] workerThreads;

	public void init() {
		HeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> producerPipeline = this.buildProducerPipeline(this.numInputObjects,
				this.inputObjectCreator);
		this.producerThread = new Thread(new RunnableStage(producerPipeline));

		int numWorkerThreads = Math.min(NUM_WORKER_THREADS, 1); // only for testing purpose

		this.workerThreads = new Thread[numWorkerThreads];
		for (int i = 0; i < this.workerThreads.length; i++) {
			List<TimestampObject> resultList = new ArrayList<TimestampObject>(this.numInputObjects);
			this.timestampObjectsList.add(resultList);

			HeadPipeline<?, ?> pipeline = this.buildPipeline(null, resultList);
			this.workerThreads[i] = new Thread(new RunnableStage(pipeline));
		}

		// this.producerThread = new Thread(new Runnable() {
		// @Override
		// public void run() {
		// TimestampObject ts;
		// try {
		// ts = MethodCallThroughputAnalysis17.this.inputObjectCreator.call();
		// System.out.println("test" + producerPipeline + ", # filters: " + MethodCallThroughputAnalysis17.this.numNoopFilters + ", ts: "
		// + ts);
		// MethodCallThroughputAnalysis17.this.numInputObjects++;
		// System.out.println("numInputObjects: " + MethodCallThroughputAnalysis17.this.numInputObjects);
		// MethodCallThroughputAnalysis17.this.numInputObjects--;
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println("run end");
		// }
		// });

		// this.producerThread.start();
		// this.producerThread.run();
		new RunnableStage(producerPipeline).run();

		// try {
		// this.producerThread.join();
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

	}

	@SuppressWarnings("unchecked")
	private HeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> buildProducerPipeline(final int numInputObjects,
			final ConstructorClosure<TimestampObject> inputObjectCreator) {
		final ObjectProducer<TimestampObject> objectProducer = new ObjectProducer<TimestampObject>(numInputObjects, inputObjectCreator);
		Distributor<TimestampObject> distributor = new Distributor<TimestampObject>();
		Sink<TimestampObject> sink = new Sink<TimestampObject>();
		Sink<Void> endStage = new Sink<Void>();

		// UnorderedGrowablePipe.connect(objectProducer.getOutputPort(), sink.getInputPort());
		// objectProducer.getOutputPort().pipe = new UnorderedGrowablePipe<TimestampObject>();

		UnorderedGrowablePipe.connect(objectProducer.getOutputPort(), distributor.getInputPort());
		distributor.getNewOutputPort().setPipe(new DummyPipe());

		final HeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> pipeline = new HeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>>();
		pipeline.setFirstStage(objectProducer);
		// pipeline.setFirstStage(sink);
		// pipeline.setFirstStage(endStage);
		pipeline.setLastStage(distributor);
		// pipeline.setLastStage(sink);
		// pipeline.setLastStage(new EndStage<TimestampObject>());
		return pipeline;
	}

	/**
	 * @param numNoopFilters
	 * @since 1.10
	 */
	private HeadPipeline<Relay<TimestampObject>, CollectorSink<TimestampObject>> buildPipeline(final Stage previousStage,
			final List<TimestampObject> timestampObjects) {
		// create stages
		Relay<TimestampObject> relay = new Relay<TimestampObject>();
		final StartTimestampFilter startTimestampFilter = new StartTimestampFilter();
		@SuppressWarnings("unchecked")
		final NoopFilter<TimestampObject>[] noopFilters = new NoopFilter[this.numNoopFilters];
		for (int i = 0; i < noopFilters.length; i++) {
			noopFilters[i] = new NoopFilter<TimestampObject>();
		}
		final StopTimestampFilter stopTimestampFilter = new StopTimestampFilter();
		final CollectorSink<TimestampObject> collectorSink = new CollectorSink<TimestampObject>(timestampObjects);

		IPipe startPipe = new RelayTestPipe<TimestampObject>(this.numInputObjects, this.inputObjectCreator);
		startPipe.setSignal(new TerminatingSignal());

		relay.getInputPort().setPipe(startPipe);
		UnorderedGrowablePipe.connect(relay.getOutputPort(), startTimestampFilter.getInputPort());
		UnorderedGrowablePipe.connect(startTimestampFilter.getOutputPort(), noopFilters[0].getInputPort());
		for (int i = 0; i < noopFilters.length - 1; i++) {
			UnorderedGrowablePipe.connect(noopFilters[i].getOutputPort(), noopFilters[i + 1].getInputPort());
		}
		UnorderedGrowablePipe.connect(noopFilters[noopFilters.length - 1].getOutputPort(), stopTimestampFilter.getInputPort());
		UnorderedGrowablePipe.connect(stopTimestampFilter.getOutputPort(), collectorSink.getInputPort());

		final HeadPipeline<Relay<TimestampObject>, CollectorSink<TimestampObject>> pipeline = new HeadPipeline<Relay<TimestampObject>, CollectorSink<TimestampObject>>();
		pipeline.setFirstStage(relay);
		pipeline.setLastStage(collectorSink);
		return pipeline;
	}

	public void start() {

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

}
